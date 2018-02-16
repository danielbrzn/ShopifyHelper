import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ShopifyDestroyer {

    private BufferedReader bufferedReader;

    private void run() throws IOException, URISyntaxException {

        JsonNode rootNode;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter size: ");
        String shoeSize = bufferedReader.readLine().trim();

        System.out.println("Enter product URL: ");
        String prodLink = bufferedReader.readLine().trim();

        URLConnection pageConnection;
        while (true) {
            pageConnection = getPageSource(prodLink);

            if (pageConnection == null) {
                return;
            }

            String shopifyMeta = getProductMeta(pageConnection.getInputStream(), pageConnection.getContentEncoding());
            byte[] jsonData = shopifyMeta.getBytes();
            ObjectMapper objectMapper = new ObjectMapper();
            rootNode = objectMapper.readTree(jsonData);
            JsonNode pageNode = rootNode.path("page");

            if (pageNode.get("pageType").asText().contains("password")) {
                try {
                    System.out.println("Password page is up, waiting 1.5 seconds...");
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    System.out.println("Thread interrupted.");
                }
            } else {
                if (prodLink.contains("eflash") && !prodLink.contains("collections")) {
                    prodLink = getProductUrl(prodLink);
                    System.out.println(prodLink);
                }
                else
                    break;
            }


        }

        if (prodLink.contains("yeezysupply") && pageConnection.getURL().getPath().isEmpty()) {
            System.out.println("Parsing for product launch on YS page");
            String sizeID = kanyeString(pageConnection.getInputStream(), pageConnection.getContentEncoding(), shoeSize);
            String cartLink = "https://yeezysupply.com/cart/" + sizeID + ":1";
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(new URI(cartLink));

        } else {
            JsonNode idNode = rootNode.path("product");
            Iterator<JsonNode> elements = idNode.get("variants").elements();
            boolean foundCart = false;
            while (elements.hasNext()) {
                JsonNode curElement = elements.next();
                JsonNode productID = curElement.get("id");
                System.out.println(productID);
                if (curElement.get("public_title").asText().contains(shoeSize)) {
                    URL siteLink = new URL(prodLink);
                    String cartLink = "https://" + siteLink.getHost() + "/cart/" + productID.asText().trim() + ":1";
                    Desktop desktop = Desktop.getDesktop();
                    desktop.browse(new URI(cartLink));
                    foundCart = true;
                    break;
                }
            }

            if (!foundCart) {
                System.out.println("Unable to cart the requested size.");
            }
        }
    }

    private URLConnection getPageSource(String prodLink) {
        URL link = null;
        try {
            link = new URL(prodLink);
        } catch (MalformedURLException e) {
            System.out.println("Invalid URL");
        }

        URLConnection urlConnection = null;
        try {
            urlConnection = link.openConnection();
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36");
            return urlConnection;
        } catch (IOException e) {
            System.out.println("Unable to get page source");
        }

        return null;
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        ShopifyDestroyer inst = new ShopifyDestroyer();
        inst.run();
    }

    // Extracts product metadata from page source
    private String getProductMeta(InputStream inputStream, String encoding) throws IOException {

        if (encoding != null && encoding.contains("gzip")) {
            bufferedReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(inputStream)));
        }
        else {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        }
            String inputLine;
            StringBuilder stringBuilder = new StringBuilder();
            while ((inputLine = bufferedReader.readLine()) != null)
            {
                if (inputLine.contains("var meta")) {
                    String trimmed = inputLine.trim().substring(11);
                    System.out.println(trimmed);
                    stringBuilder.append(trimmed);
                    break;
                }
            }

            return stringBuilder.toString();
    }

    // Extracts size ID from page source
    private String kanyeString(InputStream inputStream, String encoding, String size) throws IOException {
        String inputLine, rawID = "";
        while ((inputLine = bufferedReader.readLine()) != null) {
            if (inputLine.contains("p.variants.push")) {
                while(!(inputLine.contains("option1: \"" + size + "\","))) {
                    if (inputLine.contains("id") && !inputLine.contains("parent")) {
                        rawID = inputLine;
                    }
                    inputLine = bufferedReader.readLine();

                }
                return rawID.trim().substring(4).replace(",","");
            }
        }

        return null;
    }

    // Extracts product URL for E-Flash
    private String getProductUrl(String shopifyUrl) throws IOException {
        String inputLine, toAppend;
        while ((inputLine = bufferedReader.readLine()) != null) {
            if (inputLine.contains("grid-view-item__link")) {
                Document doc = Jsoup.parse(inputLine);
                Element link = doc.select("a").first();
                toAppend = link.attr("href");
                return shopifyUrl + toAppend;
            }
        }
        return null;
    }
}

import java.awt.*;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
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

import javafx.application.Platform;

public class YezzyKitchen {

    private void run() throws IOException, URISyntaxException {

        JsonNode rootNode;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter size: ");
        String shoeSize = bufferedReader.readLine().trim();

        System.out.println("Enter product URL: ");
        String prodLink = bufferedReader.readLine().trim();

        while (true) {
            String productJSON = getPageSource(prodLink);

            if (productJSON == null) {
                return;
            }

            System.out.println(productJSON);

            byte[] jsonData = productJSON.getBytes();
            ObjectMapper objectMapper = new ObjectMapper();
            rootNode = objectMapper.readTree(jsonData);

            JsonNode idNode = rootNode.path("id");

            String cartLink = "https://yeezysupply.com/cart/" + idNode.asText().trim() + ":1";
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(new URI(cartLink));
            break;
            /*
            JsonNode pageNode = rootNode.path("page");

            if (pageNode.get("pageType").asText().contains("password")) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    System.out.println("Thread interrupted.");
                }
            } else {
                break;
            }
            */

        }

        /*
        JsonNode idNode = rootNode.path("product");
        Iterator<JsonNode> elements = idNode.get("variants").elements();
        boolean foundCart = false;
        while (elements.hasNext()) {
            JsonNode curElement = elements.next();
            JsonNode productID = curElement.get("id");
            System.out.println(productID);
            if (curElement.get("public_title").asText().contains(shoeSize)) {
                String cartLink = "https://yeezysupply.com/cart/" + productID.asText().trim() + ":1";
                Desktop desktop = Desktop.getDesktop();
                desktop.browse(new URI(cartLink));
                foundCart = true;
                break;
            }
        }

        if (!foundCart) {
            System.out.println("Unable to cart the requested size.");
        }
        */
    }

    private String getPageSource(String prodLink) {
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
            System.out.println(urlConnection.getContentEncoding());
            InputStream file = YezzyKitchen.class.getResourceAsStream("yezzysupply.html");
            return kanyeString(file, "test");
        } catch (IOException e) {
            System.out.println("Unable to get page source");
        }

        return null;
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        YezzyKitchen inst = new YezzyKitchen();
        inst.run();
    }

    // Extracts product metadata from page source
    private static String toString(InputStream inputStream, String encoding) throws IOException {
        BufferedReader bufferedReader;

        if (encoding.contains("gzip")) {
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
                }
            }

            return stringBuilder.toString();
    }

    // Extracts size JSON from page source
    private static String kanyeString(InputStream inputStream, String encoding) throws IOException {
        BufferedReader bufferedReader;

        if (encoding.contains("gzip")) {
            bufferedReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(inputStream)));
        }
        else {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        }

        String inputLine;
        StringBuilder stringBuilder = new StringBuilder("{");
        while ((inputLine = bufferedReader.readLine()) != null) {
            if (inputLine.contains("KANYE.p.variants.push")) {
                while(!(inputLine.contains("option1: \"9.5\","))) {
                    inputLine = bufferedReader.readLine();
                }
                while (!(inputLine.contains("KANYE.p.variants.push"))) {
                    inputLine = bufferedReader.readLine();
                }
                while (!(inputLine.contains("option4"))) {
                    inputLine = bufferedReader.readLine();
                    stringBuilder.append(inputLine.trim());
                }

                stringBuilder.append("}");
                break;
            }
        }

        return stringBuilder.toString();
    }

}

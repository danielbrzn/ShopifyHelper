# ShopifyDestroyer

ShopifyDestroyer aims to be a solution for quickly adding a product to cart on Shopify-based websites by avoiding the need for the user
to manually select the product parameters, saving valuable time.

## Getting Started

These instructions will get you a copy of the software up and running.

### Prerequisites

What things you need to install the software and how to install them

[Java Runtime Environment 8](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)

### Quick Start

1. Ensure that Java is installed on your system.
2. Download the latest JAR from the releases section.
3. Open a command line in the directory of the jar and type the following:
```
java -jar ShopifyDestroyer.jar
```
4. The program should run and ask you for your desired product size and product link.

5. If the product link is not behind a password page, the checkout page will open in your system's default web browser.
    
    Else, the program will refresh the provided link every 2 seconds until the password page is taken down.
    
    ```
    ShopifyDestroyer only supports direct product links and websites with live password pages. Invalid links will 
    cause the program to crash.
    ```

### Pre-defining parameters
 
It is possible to run the program with a pre-defined size and website link. 

1. Create a new file `param.in` with the following contents:
```
PRODUCT_SIZE
WEBSITE_URL
```

For example, if the desired product is a shoe in US 9 on the Dover Street Market NY E-FLASH website:
```
9
https://eflash-us.doverstreetmarket.com/
```
2. Create a new file `run.bat` with the following contents:
```
java -jar ShopifyDestroyer.jar < param.in
```
3. Click on the `run.bat` file to start.

## Built With

* [jsoup](https://jsoup.org/) - HTML parsing
* [Gradle](https://gradle.org) - Dependency Management
* [Jackson Databind](https://github.com/FasterXML/jackson-databind) - JSON parsing



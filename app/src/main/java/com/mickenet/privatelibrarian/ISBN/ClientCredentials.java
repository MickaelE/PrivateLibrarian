package com.mickenet.privatelibrarian.ISBN;

public class ClientCredentials {
    /** Value of the "API key" shown under "Simple API Access". */
    static final String API_KEY = "AIzaSyAKq7OlIIMiQ5l3q97cOBdZ2AZcCd04PTc";

    static void errorIfNotSpecified() {
        if (API_KEY.startsWith("Enter ")) {
            System.err.println(API_KEY);
            System.exit(1);
        }
    }
}

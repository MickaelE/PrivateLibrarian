package com.mickenet.privatelibrarian.ISBN;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class BookClient {
    private static final String API_BASE_URL = "http://libris.kb.se/xsearch?query=";
    private final AsyncHttpClient client;

    public BookClient() {
        this.client = new AsyncHttpClient();
    }

    private String getApiUrl(String query) {
        return API_BASE_URL + query + "&format=json";
    }

    // Method for accessing the search API
    public void getBooks(final String query, JsonHttpResponseHandler handler) {
        try {
            String url = getApiUrl(query);
            client.get(url + URLEncoder.encode(query, "utf-8"), handler);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}

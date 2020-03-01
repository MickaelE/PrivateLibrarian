package com.mickenet.privatelibrarian.ISBN;
import android.net.Uri;

import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.books.Books;
import com.google.api.services.books.BooksRequestInitializer;
import com.google.api.services.books.Books.Volumes.List;
import com.google.api.services.books.model.Volume;
import com.google.api.services.books.model.Volumes;
import com.mickenet.privatelibrarian.books.LocalBook;
import com.mickenet.privatelibrarian.database.DatabaseHandler;

import java.text.NumberFormat;
import java.util.ArrayList;

public  class GoogleBooksApi {
    private static final String APPLICATION_NAME = "";

    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance();
    private static final NumberFormat PERCENT_FORMATTER = NumberFormat.getPercentInstance();

    public static java.util.List<LocalBook> queryGoogleBooks(JsonFactory jsonFactory, String query) throws Exception {
        ClientCredentials.errorIfNotSpecified();
        java.util.List<LocalBook> localBooks = new ArrayList<>();
        LocalBook localBook = new LocalBook();
        String author= "";

        // Set up Books client.
        final Books books = new Books.Builder(new ApacheHttpTransport()
                , jsonFactory, null)
                .setApplicationName(APPLICATION_NAME)
                .setGoogleClientRequestInitializer(new BooksRequestInitializer(ClientCredentials.API_KEY))
                .build();
        // Set query string and filter only Google eBooks.
        System.out.println("Query: [" + query + "]");
        List volumesList = books.volumes().list(query);
        //volumesList.setFilter("ebooks");

        // Execute the query.
        Volumes volumes = volumesList.execute();
        if (volumes.getTotalItems() == 0 || volumes.getItems() == null) {
            System.out.println("No matches found.");
             return localBooks;
        }

        // Output results.
        for (Volume volume : volumes.getItems()) {
            Volume.VolumeInfo volumeInfo = volume.getVolumeInfo();
            Volume.SaleInfo saleInfo = volume.getSaleInfo();

            // Title.
            localBook.setTitle(volumeInfo.getTitle());
            // Author(s).
            java.util.List<String> authors = volumeInfo.getAuthors();

            if (authors != null && !authors.isEmpty()) {
                for (int i = 0; i < authors.size(); ++i) {
                    author =author + authors.get(i);
                    if (i < authors.size() - 1) {
                        author =author + ", ";
                    }
                }
                localBook.setAuthor( author);
                author="";


                if(volumeInfo.getImageLinks() !=null){
                    String getmedium = volumeInfo.getImageLinks().getThumbnail();
                    Uri image = Uri.parse(getmedium);
                    localBook.setCoverMedium(image);
                    localBook.setOpenLibraryId(query);
                }

            }
            // Description (if any).
            if (volumeInfo.getDescription() != null && volumeInfo.getDescription().length() > 0) {
                System.out.println("Description: " + volumeInfo.getDescription());
            }
            localBooks.add(localBook);

            }

        return localBooks;
    }
}

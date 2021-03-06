package com.mickenet.privatelibrarian.books;

import android.net.Uri;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;

@SuppressWarnings("Convert2Diamond")
public class LocalBook {
    private String openLibraryId;
    private String author;
    private String title;
    private Uri coverMedium;

  public LocalBook(String openLibraryId, String author, String title,Uri coverMedium) {
        this.openLibraryId = openLibraryId;
        this.author = author;
        this.title = title;
        this.coverMedium = coverMedium;
    }

    public LocalBook() {

    }

    public String getOpenLibraryId() {
        return openLibraryId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public void setOpenLibraryId(String openLibraryId) {
        this.openLibraryId = openLibraryId;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Uri getCoverMedium() {
        return coverMedium;
    }

    public void setCoverMedium(Uri coverMedium) {
        this.coverMedium = coverMedium;
    }

    // Returns a Book given the expected JSON
    private static LocalBook fromJson(JSONObject jsonObject) {
        LocalBook book = new LocalBook();
        try {
            // Deserialize json into object fields
            // Check if a cover edition is available
            if (jsonObject.has("cover_edition_key"))  {
                book.openLibraryId = jsonObject.getString("cover_edition_key");
            } else if(jsonObject.has("edition_key")) {
                final JSONArray ids = jsonObject.getJSONArray("edition_key");
                book.openLibraryId = ids.getString(0);
            }
            book.title = jsonObject.has("title_suggest") ? jsonObject.getString("title_suggest") : "";
            book.author = getAuthor(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        // Return new object
        return book;
    }

    // Return comma separated author list when there is more than one author
    private static String getAuthor(final JSONObject jsonObject) {
        try {
            final JSONArray authors = jsonObject.getJSONArray("author_name");
            int numAuthors = authors.length();
            final String[] authorStrings = new String[numAuthors];
            for (int i = 0; i < numAuthors; ++i) {
                authorStrings[i] = authors.getString(i);
            }
            return TextUtils.join(", ", authorStrings);
        } catch (JSONException e) {
            return "";
        }
    }
    // Decodes array of book json results into business model objects
    public static ArrayList<LocalBook> fromJson(JSONArray jsonArray) {
        ArrayList<LocalBook> books = new ArrayList<LocalBook>(jsonArray.length());
        // Process each result in json array, decode and convert to business
        // object
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject bookJson;
            try {
                bookJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            LocalBook book = LocalBook.fromJson(bookJson);
            if (book != null) {
                books.add(book);
            }
        }
        return books;
    }
}

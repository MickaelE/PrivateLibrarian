package com.mickenet.privatelibrarian.database;
/**
 * Handle call to sqllite database.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.widget.Toast;

import com.mickenet.privatelibrarian.books.LocalBook;

import java.util.ArrayList;
import java.util.List;

/**
 * Costructor without context
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "bookManager.db";
    private static final String TABLE_BOOKS = "books";
    private static final String openLibraryId = "openLibraryId";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_TITLE = "title";
    private static final String KEY_URI = "uri";
    private final Context context;

    /**
     * Costructor with context
     * @param context context object.
     */
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        //3rd argument to be passed is CursorFactory instance
    }
    /**
     * Initial setup of connection to sqllite database
     * @param db object of type SQLiteDatabase.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_BOOK_TABLE = "CREATE TABLE " + TABLE_BOOKS+ "("
                + openLibraryId + " TEXT PRIMARY KEY,"
                + KEY_AUTHOR + " TEXT," + KEY_TITLE + " TEXT," + KEY_URI + " TEXT" + " )";
        db.execSQL(CREATE_BOOK_TABLE);
    }

    /**
     * Overided procedure to handle of upgrade of database schema.
     * @param db  object of SQLiteDatabase
     * @param oldVersion The version of the old schema.
     * @param newVersion The version of the new schema.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);

        // Create tables again
        onCreate(db);
    }

    /**
     * Function to add a book object to the database.
     * @param books A book object.
     */
    public void addBook(LocalBook books) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(openLibraryId, books.getOpenLibraryId());
        values.put(KEY_AUTHOR, books.getAuthor());
        values.put(KEY_TITLE, books.getTitle());
        if(books.getCoverMedium() !=null)
            values.put(KEY_URI, books.getCoverMedium().toString());
        // Inserting Row
        try {
            db.insert(TABLE_BOOKS, null, values);
        } catch (Exception e) {
            db.close(); // Closing database connection
            Context context = this.context;
            CharSequence text = e.getLocalizedMessage();
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } finally {
            db.close(); // Closing database connection
        }
        //2nd argument is String containing nullColumnHack

    }

    /**
     * Function to get a special book object from the database.
     * @param id The bookobject id eg. isbm.
     * @return A book object.
     */
    LocalBook getBook(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_BOOKS, new String[] { openLibraryId,
                        KEY_AUTHOR, KEY_TITLE }, openLibraryId + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        LocalBook contact = new LocalBook(
        );
        // return contact
        assert cursor != null;
        cursor.close();
        return contact;
    }

    /**
     * code to get all books from database in a list view
     * @return A list of books objects.
     */
    public List<LocalBook> getAllBooks() {
        @SuppressWarnings("Convert2Diamond") List<LocalBook> bookList = new ArrayList<LocalBook>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_BOOKS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                LocalBook book = new LocalBook();
                book.setOpenLibraryId(cursor.getString(0));
                book.setAuthor(cursor.getString(1));
                book.setTitle(cursor.getString(2));
                String medium = cursor.getString(3);
                if(medium != null) {
                    book.setCoverMedium(Uri.parse(medium));
                } else {
                    Uri uri = Uri.parse("android.resource://"+context.getPackageName()+"/drawable/no_thumbnail");
                    book.setCoverMedium(uri);
                }
                // Adding book to list
                bookList.add(book);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // return book list
        return bookList;
    }

    /**
     *  Function to update a book object in the database.
     * @param book A edited book object.
     * @return  Success or fail.
     */
    public int updateBook(LocalBook book) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_AUTHOR, book.getAuthor());
        values.put(KEY_TITLE, book.getTitle());

        // updating row
        return db.update(TABLE_BOOKS, values, openLibraryId + " = ?",
                new String[] { String.valueOf(book.getOpenLibraryId()) });
    }

    /**
     * Delete a specific book from database.
     * @param book  The book object to delete.
     */
    public void deleteBook(LocalBook book) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BOOKS, openLibraryId + " = ?",
                new String[] { String.valueOf(book.getOpenLibraryId()) });
        db.close();
    }

    /**
     * Procedure that return the number of books in the database.
     * @return The number of books in the dabase.
     */
    public long getBooksCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_BOOKS);
        db.close();
        return count;
    }
    /**
     * Procedure that return the number of books for a isbn number in the database.
     * @return The number of books in the dabase.
     */
    public long getBooksCount(String isbn) {
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db,TABLE_BOOKS,  "openLibraryId =" + isbn);
        db.close();
        return count;
    }
}

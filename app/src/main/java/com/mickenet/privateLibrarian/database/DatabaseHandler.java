package com.mickenet.privateLibrarian.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mickenet.privateLibrarian.Books.Book;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "bookManager";
    private static final String TABLE_BOOKS = "books";
    private static final String openLibraryId = "openLibraryId";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_TITLE = "title";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_BOOK_TABLE = "CREATE TABLE " + TABLE_BOOKS+ "("
                + openLibraryId + " INTEGER PRIMARY KEY,"
                + KEY_AUTHOR + " TEXT," + KEY_TITLE + " TEXT" +" )";
        db.execSQL(CREATE_BOOK_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);

        // Create tables again
        onCreate(db);
    }

    // code to add the new books
    public void addBook(Book books) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(openLibraryId, books.getOpenLibraryId());
        values.put(KEY_AUTHOR, books.getAuthor());
        values.put(KEY_TITLE, books.getTitle());

        // Inserting Row
        db.insert(TABLE_BOOKS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get the single contact
    Book getBook(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_BOOKS, new String[] { openLibraryId,
                        KEY_AUTHOR, KEY_TITLE }, openLibraryId + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Book contact = new Book(cursor.getString(0),
                cursor.getString(1), cursor.getString(2));
        // return contact
        return contact;
    }

    // code to get all contacts in a list view
    public List<Book> getAllBooks() {
        List<Book> contactList = new ArrayList<Book>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_BOOKS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Book book = new Book();
                book.setOpenLibraryId(cursor.getString(0));
                book.setAuthor(cursor.getString(1));
                book.setTitle(cursor.getString(2));
                // Adding book to list
                contactList.add(book);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    // code to update the single book
    public int updateBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_AUTHOR, book.getAuthor());
        values.put(KEY_TITLE, book.getTitle());

        // updating row
        return db.update(TABLE_BOOKS, values, openLibraryId + " = ?",
                new String[] { String.valueOf(book.getOpenLibraryId()) });
    }

    // Deleting single book
    public void deleteBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BOOKS, openLibraryId + " = ?",
                new String[] { String.valueOf(book.getOpenLibraryId()) });
        db.close();
    }

    // Getting contacts Count
    public int getBooksCount() {
        String countQuery = "SELECT  * FROM " + TABLE_BOOKS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

}

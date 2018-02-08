package com.emmita.android.books.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.emmita.android.books.database.BookContract.BookEntry;


/**
 * BookDbHelper gestionará la creación y actualizción de la base de datos
 */

public class BookDbHelper extends SQLiteOpenHelper {

    //Nombre de la base de datos
    private static final String DATABASE_NAME = "books.db";

    //Versión de la base de datos
    private static final int DATABASE_VERSION = 1;

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Este método es llamado cuando la base de datos ha sido creada por primera vez
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Se crea un String que contiene la sentencia SQL para crear la tabla books
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME + " ("
                + BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookEntry.COLUMN_BOOK_TITULO + " TEXT NOT NULL, "
                + BookEntry.COLUMN_BOOK_AUTOR + " TEXT NOT NULL, "
                + BookEntry.COLUMN_BOOK_REVIEW + " TEXT NOT NULL);";

        //execSQL ejecuta la sentencia SQL
        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    /**
     * Este método es llamado cuando la base de datos necesita actualizarsr
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

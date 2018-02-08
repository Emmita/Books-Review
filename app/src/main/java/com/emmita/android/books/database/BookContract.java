package com.emmita.android.books.database;

import android.provider.BaseColumns;


public class BookContract {

    private BookContract() {

    }

    //Tabla books
    public static final class BookEntry implements BaseColumns {

        //Constante para el nombre de la tabla
        public final static String TABLE_NAME = "books";

        //Constante para el nombre de la primer columna id
        public final static String _ID = BaseColumns._ID;
        //Constante para el nombre de la segunda columna titulo
        public final static String COLUMN_BOOK_TITULO = "titulo";
        //Constante para el nombre de la tercer columna autor
        public final static String COLUMN_BOOK_AUTOR = "autor";
        //Constante para el nombre de la cuarta columna review
        public final static String COLUMN_BOOK_REVIEW = "review";

    }

}

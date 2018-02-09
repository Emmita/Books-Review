package com.emmita.android.books.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public class BookContract {

    private BookContract() {

    }

    /**Formando el URI
    El nombre que se le asigna al content authority es recomendable que sea el nombre del paquete de la aplicación
    ya que, es único en el proyecto*/
    public static final String CONTENT_AUTHORITY = "com.emmita.android.books.database";
    /**Concatenamos el content authority para formar la base del URI */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+ CONTENT_AUTHORITY);
    /**Dirección donde se va a buscar le información de los libros*/
    public static final String PATH_BOOKS = "books";

    //Tabla books
    public static final class BookEntry implements BaseColumns {

        //Uri para accedes a la información desde el ContentProvider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;


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

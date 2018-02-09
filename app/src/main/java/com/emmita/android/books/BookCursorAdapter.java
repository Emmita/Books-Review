package com.emmita.android.books;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.emmita.android.books.database.BookContract.BookEntry;

/**
 * Created by JESUS on 08/02/2018.
 */

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        //Infla el layout donde va a ser vinculada la información del libro
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //Encuentra los TextViews situados en el layout list item
        TextView tituloTextView = (TextView) view.findViewById(R.id.titulo);
        TextView autorTextView = (TextView) view.findViewById(R.id.autor);

        //Encuentra las columnas de los atributos del libro en que estamos interesados
        int tituloColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_TITULO);
        int autorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_AUTOR);

        //Lee los atributos del libro provenientes del Cursor para el libro actual
        String tituloLibro = cursor.getString(tituloColumnIndex);
        String autorLibro = cursor.getString(autorColumnIndex);

        //Se actualiza el valor de los TextViews con el valor de los tributos del libro actual
        tituloTextView.setText(tituloLibro);
        autorTextView.setText(autorLibro);

    }
}

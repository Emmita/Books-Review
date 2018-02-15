package com.emmita.android.books;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.emmita.android.books.database.BookContract.BookEntry;
import com.emmita.android.books.database.BookDbHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //Identificador para el cargador de datos del libro
    private static final int BOOK_LOADER = 0;

    //Adaptador para el ListView
    BookCursorAdapter mCursorAdapter;

    BookDbHelper mBookDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBookDbHelper = new BookDbHelper(this);

        //Encuentra el FloatingActionButton en el archivo xml para después asignarle
        //el método setOnClickListener
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        //Encuentra el ListView en el archivo xml
        ListView listView = (ListView) findViewById(R.id.list_view);

        //Se le asigna el adaptador al ListView
        mCursorAdapter = new BookCursorAdapter(this, null);
        listView.setAdapter(mCursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Secrea un nuevo Intent para ir a ShowActivity
                Intent intent = new Intent(MainActivity.this, ShowActivity.class);

                //Se forma el contenido de la URI que representa al elemento seleccionado
                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);

                //Se envía el contenido de la URI por medio del Intent
                intent.setData(currentBookUri);

                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    /**
     * Método para borrar todos los libros en la base de datos*/
    private void deleteAllBooks(){
        int rowsDeleted = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " filas eliminadas de la base de datos books");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Infla el respectivo archivo xml de opciones de menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Se utiliza un switch para cada caso (opcion del menú) que se de click
        switch (item.getItemId()) {
            case R.id.accion_borrar_todo:
                deleteAllBooks();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        //La actividad mostrará todos los atributos del libro
        //por lo tanto se seleccionan todas las columnas de la tabla
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_TITULO,
                BookEntry.COLUMN_BOOK_AUTOR,
                BookEntry.COLUMN_BOOK_REVIEW};

        //Se ejecuta la consulta en diferente hilo

        return new CursorLoader(this,
                BookEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        //Se actualiza el adaptador con el nuevo cursor que contiene la información actualizada
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        //Este callback es llamado cuando la información necesita ser eliminada
        mCursorAdapter.swapCursor(null);

    }
}

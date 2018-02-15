package com.emmita.android.books;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.emmita.android.books.database.BookContract.BookEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //Identificador para cargar la información de los libros
    private static final int EXISTING_BOOK_LOADER = 0;

    //Uri para el libro existente
    private Uri mCurrentBookUri;

    //EditText del titulo del libro
    private EditText mTituloEditText;

    //EditText del autor del libro
    private EditText mAutorEditText;

    //EditText para el review del libro
    private EditText mReviewEditText;

    //Variable que indica si el libro ha sido editado o no
    private boolean mBookHasChanged = false;


    /**
     * Este método ayuda a saber si el usuario ha hecho touch en la vista,
     * lo que implica que la están modificando, luego se cambia el valor de mBookHasChanged a verdadero*/
    private View.OnTouchListener mTouchListener = new View.OnTouchListener(){

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);

        //Encuentra los EditTexts en el archivo xml
        mTituloEditText = (EditText) findViewById(R.id.edit_text_titulo);
        mAutorEditText = (EditText) findViewById(R.id.edit_text_autor);
        mReviewEditText = (EditText) findViewById(R.id.edit_text_review);

        //Asigna el touchListener a cada uno de los EditTexts
        //para que indique si el usuario está modificando el campo
        //en el que ha dado touch
        mTituloEditText.setOnTouchListener(mTouchListener);
        mAutorEditText.setOnTouchListener(mTouchListener);
        mReviewEditText.setOnTouchListener(mTouchListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Infla las opciones de menú para agregarlas al app bar
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * Con este método se pueden ocultar o hacer visibles
     * algunas opciones de menús*/
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentBookUri == null){
            MenuItem menuItem = menu.findItem(R.id.accion_borrar);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            //En caso de que se presione la opción de guardar
            case R.id.accion_guardar:
                saveBook();
                finish();
                return true;
            //En caso de que se presione la opción de borrar
            case R.id.accion_borrar:
                //Se muestra el dialogo para la eliminación del libro
                showDeleteConfirmationDialog();
                return true;
            //En caso de que presione la flecha situada en la app bar
            case R.id.home:
                //Si el libro no ha cambiado, continua con la navegación entre actividades
                if (!mBookHasChanged){
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                //De otra manera si hay cambios sin guardar, se muestra un diálogo
                DialogInterface.OnClickListener discardButtonClickistener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };

                //Se muestra el dialogo
                showUnsavedChangesDialog(discardButtonClickistener);
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
        return new CursorLoader(this, mCurrentBookUri, projection, null, null, null);


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 0){
            return;
        }

        //Se mueve el cursor a la primera fila para que empiece a leer la información de esta
        if (cursor.moveToFirst()){
            //Se seleccionan las columnas en las que estamos interesados
            int tituloColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_TITULO);
            int autorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_AUTOR);
            int reviewColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_REVIEW);

            //Se obtiene el valor de la actual columna donde se encuentra el cursor
            String titulo = cursor.getString(tituloColumnIndex);
            String autor = cursor.getString(autorColumnIndex);
            String review = cursor.getString(reviewColumnIndex);

            //Se le asigna los valores de la base de datos a la vista
            mTituloEditText.setText(titulo);
            mAutorEditText.setText(autor);
            mReviewEditText.setText(review);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        //Si la información es invalida se borra la información de los campos
        mTituloEditText.setText("");
        mAutorEditText.setText("");
        mReviewEditText.setText("");
    }

    private void saveBook(){

        //Lee la información contenida en EditText
        String tituloString = mTituloEditText.getText().toString().trim();
        String autorString = mAutorEditText.getText().toString().trim();
        String reviewString = mReviewEditText.getText().toString().trim();

        //Se checa si es un nuevo libro y si todos los campos están en blanco
        if (mCurrentBookUri == null && TextUtils.isEmpty(tituloString) && TextUtils.isEmpty(autorString) && TextUtils.isEmpty(reviewString)){
            return;
        }

        //Se crea un objeto ContentValues donde se les asigna valores a las columnas de la tabla
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_TITULO, tituloString);
        values.put(BookEntry.COLUMN_BOOK_AUTOR, autorString);
        values.put(BookEntry.COLUMN_BOOK_REVIEW, reviewString);

        //Checa si el libro es nuevo o ya existe en la base de datos
        if (mCurrentBookUri == null){
            //En este caso es un nuevo libro
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            //Se muestra un Toast dependiendo si la inserción fue exitosa o no
            if (newUri == null){
                //Será un error en este caso
                Toast.makeText(this, getString(R.string.insercion_fallida), Toast.LENGTH_SHORT).show();
            }else{
                //Aqui la inserción fue exitosa
                Toast.makeText(this, getString(R.string.insercion_exitosa), Toast.LENGTH_SHORT).show();
            }
        }else{
            //En este caso es un libro existente
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

            //Se muestra un Toast dependiendo si la actualización fue exitosa o no
            if (rowsAffected == 0){
                //Si ninguna fila fue afectada, será un error
                Toast.makeText(this, getString(R.string.actualizacion_fallida), Toast.LENGTH_SHORT).show();
            }else{
                //Aquí la actualización fue exitosa
                Toast.makeText(this, getString(R.string.actualizacion_exitosa), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void deleteBook(){

        //En este caso solo se realiza la función para un libro existente
        if (mCurrentBookUri != null){
            //Se elimina el libro con el URI dado
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            //Se muestra un Toast dependiendo si la eliminación fue exitosa o no
            if (rowsDeleted == 0){
                //En este caso es un error
                Toast.makeText(this, getString(R.string.eliminacion_libro_fallida), Toast.LENGTH_SHORT).show();
            }else{
                //En este caso el libro se eliminó satisfactoriamente
                Toast.makeText(this, getString(R.string.eliminacion_libro_exitosa), Toast.LENGTH_SHORT).show();
            }

            //Se cierra la actividad
            finish();
        }

    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener){
        //Se crea un AlertDialog.Builder y se le asigna el mensaje
        //así como también se asignan las funciones a los botones positivo y negativo del diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialogo_cambios_no_guardados);
        builder.setPositiveButton(R.string.descartar, discardButtonClickListener);
        builder.setNegativeButton(R.string.seguir_editando, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Si el usuario presiona este botón, entonces se quita el dialogo
                //y se mantiene en la actividad
                if (dialog != null){
                    dialog.dismiss();
                }
            }
        });

        //Se crea y se muestra el AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog(){
        //Se crea un AlertDialog.Builder y se le asigna el mensaje
        //así como también se asignan las funciones a los botones positivo y negativo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialogo_borrar);
        builder.setPositiveButton(R.string.borrar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Como el usuario presionó "Borrar", entonces se elimina el libro
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Como el usuario descartó la acción, entonces se quita el diálogo
                //y se mantiene en la actividad
                if (dialog != null){
                    dialog.dismiss();
                }
            }


        });


        //Se crea y se muestra el AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Este método es llamado cuando se presiona el botón hacia atrás*/
    @Override
    public void onBackPressed() {
        //Si el libro no se ha editado, continua con la acción
        if (!mBookHasChanged){
            super.onBackPressed();
            return;
        }

        //De otra manera si los cambios no se han guardado, se muestra un dialogo
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Si se presiona "Descartar", se cierra la actividad
                finish();
            }
        };

        //Se muestra el dialogo
        showUnsavedChangesDialog(discardButtonClickListener);

    }
}

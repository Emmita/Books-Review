package com.emmita.android.books.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.IntentFilter;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.emmita.android.books.database.BookContract.BookEntry;


/**
 * Created by JESUS on 08/02/2018.
 */

public class BookProvider extends ContentProvider {

    public static final String LOG_TAG = BookProvider.class.getSimpleName();

    //Código que nos indica si el contenido del URI coincide para la tabla books
    private static final int BOOKS = 100;
    //Código que nos indica si el contenido del URI para un solo libro coincide in la tabla books
    private static final int BOOK_ID = 101;

    private BookDbHelper mBookDbHelper;

    //UriMatcher nos ayuda a asegurarnos que el ContentProvider no está manejando contenido que no es
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static{
        //UriMatcher para la tabla completa
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        //UriMatcher para un elemento específico
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOK_ID);

    }

    @Override
    public boolean onCreate() {

        mBookDbHelper = new BookDbHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        //Llama a getReadableDatabase() para poder consultar los elementos de la base de datos
        SQLiteDatabase database = mBookDbHelper.getReadableDatabase();

        //El cursor contiene el resultado de la consulta
        Cursor cursor;

        //Se asegura que el codigo del UriMatcher coincida con el URI dado
        int match = sUriMatcher.match(uri);

        switch (match) {
            case BOOKS:
                //En caso de que el URI coincida con este código, la consulta traerá toda la información
                //de la tabla books
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BOOK_ID:
                //En caso de que el URI coincida con este código, la consulta traerá la información
                //solamente del ID especificado
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("No se puede consultar el URI desconocido " + uri);
        }

        //Se notifica al Cursor qué contenido tendrá
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match){
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("URI desconocido " + uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final int match = sUriMatcher.match(uri);
        switch(match){
            case BOOKS:
                return insertBook(uri, values);
            default:
                throw new IllegalArgumentException("La inserción es no soportada por " +uri);
        }

    }
    //Inserta un nuevo libro en la base de datos con los valores dados
    private Uri insertBook(Uri uri, ContentValues values){

        //Checa si el titulo no es nulo
        String titulo = values.getAsString(BookEntry.COLUMN_BOOK_TITULO);
        if (titulo == null){
            throw new IllegalArgumentException("El libro require un titulo");
        }

        //Checa si el autor no es nulo
        String autor = values.getAsString(BookEntry.COLUMN_BOOK_AUTOR);
        if (autor == null){
            throw new IllegalArgumentException("El libro requiere un autor");
        }

        //Checa si el texto del review no es nulo
        String review = values.getAsString(BookEntry.COLUMN_BOOK_REVIEW);
        if (review == null){
            throw new IllegalArgumentException("El libro necesita review");
        }

        SQLiteDatabase database = mBookDbHelper.getWritableDatabase();

        //Inserta el nuevo ibro con los valores dados
        long id = database.insert(BookEntry.TABLE_NAME, null, values);

        //Si el id es igual a -1, entonces hubo error en la inserción
        if (id == -1){
            Log.e(LOG_TAG, "Fallo en la insercicón de la fila" + uri);
            return null;
        }

        //Notifica que la información ha sido cambiada por la URI del libro
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        //Manda llamar a getWritableDatabase(), ya que se va a eliminar información
        SQLiteDatabase database = mBookDbHelper.getWritableDatabase();

        //Variable donde se almacenarán las filas que sean eliminadas
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match){
            case BOOKS:
                //Elimina todas las filas que coincidan con la instruccion SQL
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                //Elimina una unica fila, dada por el ID en el URI
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("La eliminación no es compatible con " + uri);
        }

        //Si una o mas filas han sido eliminadas, se notifica que la información del URI ha sido cambiada
        if (rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        //Regresa las filas eliminadas
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        switch(match){
            case BOOKS:
                return updateBook(uri, values, selection, selectionArgs);
            case BOOK_ID:
                //Se conoce cuál fila será actualizada, puesto que el ID se extrae del URI
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("La actualización no es soportada por " + uri);
        }

    }

    private int updateBook(Uri uri, ContentValues values, String selection, String [] selectionArgs){

        //Checa que el valor del titulo no es nulo
        if (values.containsKey(BookEntry.COLUMN_BOOK_TITULO)){
            String titulo = values.getAsString(BookEntry.COLUMN_BOOK_TITULO);
            if (titulo == null){
                throw new IllegalArgumentException("El libro requiere un titulo");
            }
        }

        //Checa que el valor del autor no sea nulo
        if (values.containsKey(BookEntry.COLUMN_BOOK_AUTOR)){
            String autor = values.getAsString(BookEntry.COLUMN_BOOK_AUTOR);
            if (autor == null){
                throw new IllegalArgumentException("El libro requiere un autor");
            }
        }

        //checa que el valor del texto del review no sea nulo
        if (values.containsKey(BookEntry.COLUMN_BOOK_REVIEW)){
            String review = values.getAsString(BookEntry.COLUMN_BOOK_REVIEW);
            if (review == null){
                throw new IllegalArgumentException("El libro requiere un review");
            }
        }

        //De otra manera, manda llamar a getWritableDatabase() para poder actualizar la base de datos
        SQLiteDatabase database = mBookDbHelper.getWritableDatabase();

        //Obtener el número de filas actualizadas en la base de datos
        int rowsUpdated = database.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);

        //Si una o más filas fueron afectadas, se notifica que la información en la URI dada ha sido cambiada
        if (rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        //Regresa el número de filas actualizadas
        return rowsUpdated;

    }


}

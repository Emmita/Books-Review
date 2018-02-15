package com.emmita.android.books;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.emmita.android.books.database.BookContract.BookEntry;

public class ShowActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int EXISTING_BOOK_LOADER = 0;

    private Uri mCurrentBookUri;

    private TextView mTituloTextView;

    private TextView mAutorTextView;

    private TextView mReviewTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        Intent intent = getIntent();
        mCurrentBookUri =  intent.getData();

        getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);

        mTituloTextView = (TextView) findViewById(R.id.text_view_titulo);
        mAutorTextView = (TextView) findViewById(R.id.text_view_autor);
        mReviewTextView = (TextView) findViewById(R.id.text_view_review);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.accion_editar:
                Intent intent = new Intent(ShowActivity.this, EditorActivity.class);
                intent.setData(mCurrentBookUri);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_TITULO,
                BookEntry.COLUMN_BOOK_AUTOR,
                BookEntry.COLUMN_BOOK_REVIEW};


        return new CursorLoader(this,
                mCurrentBookUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1){
            return;
        }

        if (cursor.moveToFirst()){
            int tituloColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_TITULO);
            int autorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_AUTOR);
            int reviewColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_REVIEW);

            String titulo = cursor.getString(tituloColumnIndex);
            String autor = cursor.getString(autorColumnIndex);
            String review = cursor.getString(reviewColumnIndex);

            mTituloTextView.setText(titulo);
            mAutorTextView.setText(autor);
            mReviewTextView.setText(review);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mTituloTextView.setText("");
        mAutorTextView.setText("");
        mReviewTextView.setText("");

    }
}

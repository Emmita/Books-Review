package com.emmita.android.books;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Encuentra el FloatingActionButton en el archivo xml para después asignarle
        //el método setOnClickListener
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //Encuentra el ListView en el archivo xml
        ListView listView = (ListView) findViewById(R.id.list_view);
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
            case R.id.accion_random:
                return true;
            case R.id.accion_borrar_todo:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.example.iot2.network;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private String message = "Farm Story~";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 1, Menu.NONE, "REQUEST HTML");
        menu.add(Menu.NONE, 2, Menu.NONE, "SEARCH MOVIE");
        menu.add(Menu.NONE, 3, Menu.NONE, "SEARCH IMAGE");
        menu.add(Menu.NONE, 4, Menu.NONE, "SHOW PLANTS");
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case 4 :
                intent = new Intent(this, FarmListActivity.class);
                startActivity(intent);
                break;
            case 3 :
                intent = new Intent(this, ImageActivity.class);
                startActivity(intent);
                break;
            case 2 :
                intent = new Intent(this, MovieActivity.class);
                startActivity(intent);
                break;
            case 1 :
                intent = new Intent(this, RequestHtmlActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.example.myweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;


public class ChooseCityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_city);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
        Set<String> cities = sp.getStringSet("cities", null);
        if(cities != null) {
            String[] cityData = new String[cities.size()];
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_list_item_1, cities.toArray(cityData));
            ListView cityListView = (ListView) findViewById(R.id.city_list);
            cityListView.setAdapter(adapter);
        }
        else{
            Toast.makeText(ChooseCityActivity.this,
                    "No cities in SharedPreferences ", Toast.LENGTH_SHORT).show();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent googlePlaceIntent = new Intent(ChooseCityActivity.this, GooglePlaceActivity.class);
                startActivityForResult(googlePlaceIntent, 1);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_city, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode) {
            case 1:
                if(resultCode == RESULT_OK){
                    SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
                    Set<String> cities = sp.getStringSet("cities", null);
                    if(cities != null) {
                        String[] test = {"San Jose", "Santa Clara"};
                        String[] cityData = new String[cities.size()];
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                ChooseCityActivity.this, android.R.layout.simple_list_item_1, /*cities.toArray(cityData)*/ test);
                        ListView cityListView = (ListView) findViewById(R.id.city_list);
                        cityListView.setAdapter(adapter);
                        cityListView.setVisibility(View.VISIBLE);
                    }
                }
                else{
                    Toast.makeText(ChooseCityActivity.this,
                            "No cities in SharedPreferences ", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
}

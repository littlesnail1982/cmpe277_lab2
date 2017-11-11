package com.example.myweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class ChooseCityActivity extends AppCompatActivity {

    public static final int REQUEST_GOOGLE_PLACE = 1;
    public static final int REQUEST_DELETE_CITY = 2;
    private CityListAdapter adapter;
    private String[] cityData = null;
    private ListView cityListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_city);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        cityListView = (ListView) findViewById(R.id.city_list);

        final SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);

        RadioGroup unitGroup = (RadioGroup) findViewById(R.id.rg_unit);
        String unit = sp.getString("unit",null);
        if(unit == null) {
            unitGroup.check(R.id.bt_C);
            sp.edit().putString("unit","C").apply();
        }
        else {
            if(unit.equals("C"))
                unitGroup.check(R.id.bt_C);
            else
                unitGroup.check(R.id.bt_F);
        }
        unitGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton unitbtn = (RadioButton) findViewById(checkedId);
                sp.edit().putString("unit", unitbtn.getText().toString()).apply();
                //Doing temperature exchange work
                adapter = new CityListAdapter(cityData, ChooseCityActivity.this);
                cityListView.setAdapter(adapter);

            }
        });

        Set<String> cities = sp.getStringSet("cities", null);
        if(cities != null) {
            cityData = cities.toArray(new String[0]);
            adapter = new CityListAdapter(cityData, this);
            cityListView.setAdapter(adapter);
        }
        else{
            Toast.makeText(ChooseCityActivity.this,
                    "No cities in SharedPreferences ", Toast.LENGTH_SHORT).show();
        }


        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent showCityIntent = new Intent(ChooseCityActivity.this, CityActivity.class);
                showCityIntent.putExtra("number", position);
                startActivity(showCityIntent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent googlePlaceIntent = new Intent(ChooseCityActivity.this, GooglePlaceActivity.class);
                startActivityForResult(googlePlaceIntent, REQUEST_GOOGLE_PLACE);
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
        if (id == R.id.action_delete) {
            Intent deleteCityIntent = new Intent(ChooseCityActivity.this, DeleteCityActivity.class);
            startActivityForResult(deleteCityIntent, REQUEST_DELETE_CITY);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode) {
            case REQUEST_GOOGLE_PLACE:
                if(resultCode == RESULT_OK){
                    SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
                    Set<String> cities = sp.getStringSet("cities", null);
                    if(cities != null) {
                        cityData = cities.toArray(new String[0]);
                        adapter = new CityListAdapter(cityData, this);
                        ListView cityListView = (ListView) findViewById(R.id.city_list);
                        cityListView.setAdapter(adapter);

                    }
                    else{
                        Toast.makeText(ChooseCityActivity.this,
                                "No cities in SharedPreferences ", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case REQUEST_DELETE_CITY:
                if(resultCode == RESULT_OK) {
                    SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
                    Set<String> cities = sp.getStringSet("cities", null);
                    if (cities != null) {
                        cityData = cities.toArray(new String[0]);
                        adapter = new CityListAdapter(cityData, this);
                        ListView cityListView = (ListView) findViewById(R.id.city_list);
                        cityListView.setAdapter(adapter);
                    } else {
                        Toast.makeText(ChooseCityActivity.this,
                                "No cities in SharedPreferences ", Toast.LENGTH_SHORT).show();
                    }
                }
            default:
        }
    }
}

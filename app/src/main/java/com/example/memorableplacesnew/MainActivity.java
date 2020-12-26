package com.example.memorableplacesnew;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    static ArrayList<String> list = new ArrayList<String>();
    static ArrayList<LatLng> places = new ArrayList<LatLng>();
    static ArrayList<String> longitude = new ArrayList<String>();
    static ArrayList<String> latitude = new ArrayList<String>();
    static ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listview);

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.memorableplacesnew",MODE_PRIVATE);

        places.clear();
        list.clear();
        latitude.clear();
        longitude.clear();

        try {
            list = (ArrayList<String>) objectSerializer.deserialize(sharedPreferences.getString("list",objectSerializer.serialize(new ArrayList<String>())));
            latitude = (ArrayList<String>) objectSerializer.deserialize(sharedPreferences.getString("list",objectSerializer.serialize(new ArrayList<String>())));
            longitude = (ArrayList<String>) objectSerializer.deserialize(sharedPreferences.getString("list",objectSerializer.serialize(new ArrayList<String>())));

        } catch (IOException e) {
            e.printStackTrace();
        }

        if(list.size() > 0 && latitude.size()>0 && longitude.size() > 0){

            if(places.size() == longitude.size() && longitude.size() == latitude.size() && latitude.size() == places.size()){

                for( int i=0; i< latitude.size(); i++){

                    places.add(new LatLng(Double.parseDouble(latitude.get(i)),Double.parseDouble(longitude.get(i))));

                }
            }
        }else {
            list.add("Add new Location.... ");
            places.add(new LatLng(0, 0));
        }

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                    intent.putExtra("placeholder", position);
                    startActivity(intent);

            }
        });

    }
}
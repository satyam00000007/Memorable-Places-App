package com.example.memorableplacesnew;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationlistener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationlistener);

                Location lastlocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                centerMapLocation(lastlocation,"your location");
            }
        }
    }

    public void centerMapLocation(Location location,String title){

        LatLng userlocation = new LatLng(location.getLatitude(),location.getLongitude());

        mMap.clear();

        if(title != "your location"){

            mMap.addMarker(new MarkerOptions().position(userlocation).title(title));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userlocation,4));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("tag","onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i("tag","onMapReady");
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);

        final double[] count = {0};

        Intent intent = getIntent();

        if ( intent.getIntExtra("placeholder",0)==0)
        {
            //zoom in the users location

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            locationlistener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                if(count[0] != location.getLatitude()) {

                    Log.i("tag","onLocationChanged");

                        centerMapLocation(location, "your location");

                    count[0] =location.getLatitude();

                    }
                }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        else{

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationlistener);

            Location lastlocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            centerMapLocation(lastlocation,"last location");
        }
    } else {

            Log.i("tag", String.valueOf(MainActivity.places.get(intent.getIntExtra("placeholder",0)).latitude));
                Location placelocation = new Location(LocationManager.GPS_PROVIDER);


                placelocation.setLatitude(MainActivity.places.get(intent.getIntExtra("placeholder",0)).latitude);
                placelocation.setLongitude(MainActivity.places.get(intent.getIntExtra("placeholder",0)).longitude);
                centerMapLocation(placelocation,MainActivity.list.get(intent.getIntExtra("placeholder",0)));
            Log.i("tag", "i run");
        }
}

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapLongClick(LatLng latLng) {
            String Address = "";
            Geocoder geocoder= new Geocoder(getApplicationContext(), Locale.getDefault());

            Log.i("tag","setOnMapClickListener");
            try {
                List<Address> list = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

                if (list.get(0).getSubThoroughfare() != null) {

                    Address += list.get(0).getSubThoroughfare() + ", ";
                }
                if (list.get(0).getAdminArea() != null) {

                    Address += list.get(0).getAdminArea() + ", ";
                }
                if (list.get(0).getCountryName() != null) {

                    Address += list.get(0).getCountryName();
                }

                if (Address=="") {
                    Toast.makeText(MapsActivity.this, "address is null", Toast.LENGTH_SHORT).show();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy  HH:mm:ss", Locale.getDefault());
                    Address = sdf.format(new Date());
                }

                mMap.addMarker(new MarkerOptions().position(latLng).title(Address));
                MainActivity.list.add(Address);
                MainActivity.places.add(latLng);
                MainActivity.adapter.notifyDataSetChanged();

                Toast.makeText(MapsActivity.this, "Location Saved", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

            SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.memorableplacesnew",MODE_PRIVATE);


            ArrayList<String> latitude = new ArrayList<String>();
            ArrayList<String> longitude = new ArrayList<String>();

            for(LatLng coordinates : MainActivity.places)
            {
                latitude.add(Double.toString(coordinates.latitude));
                longitude.add(Double.toString(coordinates.longitude));
            }
            try {
                sharedPreferences.edit().putString("list",objectSerializer.serialize(MainActivity.list)).apply();
                sharedPreferences.edit().putString("latitude",objectSerializer.serialize(latitude)).apply();
                sharedPreferences.edit().putString("longitude",objectSerializer.serialize(longitude)).apply();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
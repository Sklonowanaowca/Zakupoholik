package com.example.monia.zakupoholik;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.monia.zakupoholik.data.ShopData;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    double latitude = 51.274;
    double longitude = 22.552;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Button mFindShopsNearMe = (Button) findViewById(R.id.button_map_find_shops_near_me);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFindShopsNearMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findShopsNearMe();
            }
        });
    }

    private void findShopsNearMe() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {// response from pokaz_listy.php (json array)
                if (response != null && response.length() > 0) {
                    //removeAllSignaturesFromSQLite();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray sklepy = jsonObject.getJSONArray("shops");
                        for (int i = 0; i < sklepy.length(); i++) {
                            JSONObject json_data = sklepy.getJSONObject(i);
                            double lat = json_data.getDouble("Szerokosc_geogr");
                            double lon = json_data.getDouble("Dlugosc_geogr");
                            Toast.makeText(MapActivity.this, "szer: " + lat + ", dl: " + lon, Toast.LENGTH_SHORT).show();
//                            ShopData currentData = new ShopData();
//
//                            currentData.idSklep = json_data.getInt("ID_Sklep");
//                            currentData.sygnatura = json_data.getString("Sygnatura");
                            //addShopToSQLite(currentData.getIdSklep(), currentData.getSygnatura());
                        }
                        //loadListsFromSqlite();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        FindShopsNearMeRequest findShopsNearMeRequest = new FindShopsNearMeRequest(latitude, longitude, responseListener);
        RequestQueue queue = Volley.newRequestQueue(MapActivity.this);
        queue.add(findShopsNearMeRequest);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
       // LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        //if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ///Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//            latitude = 51.274;
//            longitude = 22.552;
            LatLng lesz10 = new LatLng(latitude,longitude);
            googleMap.addMarker(new MarkerOptions().position(lesz10).title("TU JESTEŚ"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lesz10,12));
        }
}

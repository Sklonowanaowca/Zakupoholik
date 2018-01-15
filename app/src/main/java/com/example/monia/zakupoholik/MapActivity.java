package com.example.monia.zakupoholik;

import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.monia.zakupoholik.data.ShopData;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnMyLocationButtonClickListener, ActivityCompat.OnRequestPermissionsResultCallback {
    double latitude = 51.274;
    double longitude = 22.552;
    GoogleMap mGoogleMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void findShopsNearMe(final GoogleMap googleMap) {
        Response.Listener<String> responseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {// response from pokaz_listy.php (json array)
                if (response != null && response.length() > 0) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray sklepy = jsonObject.getJSONArray("shops");
                        for (int i = 0; i < sklepy.length(); i++) {
                            JSONObject json_data = sklepy.getJSONObject(i);
                            double lat = json_data.getDouble("Szerokosc_geogr");
                            double lon = json_data.getDouble("Dlugosc_geogr");
                            String nazwa = json_data.getString("Nazwa");
                            String adres = json_data.getString("Adres");
                            LatLng nesrestShop = new LatLng(lat, lon);
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(nesrestShop);
                            markerOptions.title(nazwa);
                            googleMap.addMarker(markerOptions);
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nesrestShop, 15));
                        }
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
    public void onMapReady(final GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();

//            LatLng lesz10 = new LatLng(latitude,longitude);
//            googleMap.addMarker(new MarkerOptions().position(lesz10).title("TU JESTEŚ"));
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lesz10,12));
        }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
           // PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                   // Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mGoogleMap != null) {
            // Access to the location has been granted to the app.
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

//        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
//                Manifest.permission.ACCESS_FINE_LOCATION)) {
//            // Enable the my location layer if the permission has been granted.
//            enableMyLocation();
//        } else {
//            // Display the missing permission error dialog when the fragments resume.
//            mPermissionDenied = true;
//        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
           // showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

//    private void showMissingPermissionError() {
//        PermissionUtils.PermissionDeniedDialog
//                .newInstance(true).show(getSupportFragmentManager(), "dialog");
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_find_nearest_shops:
                findShopsNearMe(mGoogleMap);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

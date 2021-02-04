package com.example.travelweatherapp.PlacesClasses;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.ZoomControls;
import com.example.travelweatherapp.Common.Common;
import com.example.travelweatherapp.ModelPlaces.MyPlaces;
import com.example.travelweatherapp.ModelPlaces.Results;
import com.example.travelweatherapp.R;
import com.example.travelweatherapp.Remote.IGoogleAPIService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int MY_PERMISSION_CODE = 1000;
    private GoogleMap mMap;
    Toolbar toolbar;
    private double latitude, longitude;
    private Location mLastLocation;
    private Marker mMarker;
    ZoomControls zoom;
    IGoogleAPIService mService;
    MyPlaces currentPlace;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    private LocationRequest mLocationRequest;
    String[] listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        zoom = (ZoomControls) findViewById(R.id.zoom_controls);

        zoom.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });

        zoom.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbarP);
        toolbar.setTitle("Travel Guide - Nearby Places");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mService = Common.getGoogleAPIService();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        listItems = getResources().getStringArray(R.array.places_item);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_market:
                        nearbyPlaces("supermarket");
                        Log.d("supermarket", "supermarket");
                        break;
                    case R.id.action_restaurant:
                        nearbyPlaces("restaurant");
                        Log.d("restaurant", "restaurant");
                        break;
                    case R.id.action_museum:
                        nearbyPlaces("museum");
                        Log.d("museum", "museum");
                        break;
                    case R.id.action_hospital:
                        nearbyPlaces("hospital");
                        Log.d("hospital", "hospital");
                        break;
                    case R.id.action_other:
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MapsActivity.this);
                        mBuilder.setTitle("Choose a place type");
                        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                               if(listItems[i].equals("Airports")){
                                   nearbyPlaces("airport");
                                   dialogInterface.dismiss();
                               }else if(listItems[i].equals("Art Galleries")){
                                   nearbyPlaces("art_gallery");
                                   dialogInterface.dismiss();
                               }else if(listItems[i].equals("ATMs")){
                                   nearbyPlaces("atm");
                                   dialogInterface.dismiss();
                               }else if(listItems[i].equals("Banks")){
                                   nearbyPlaces("bank");
                                   dialogInterface.dismiss();
                               }else if(listItems[i].equals("Bars")){
                                   nearbyPlaces("bar");
                                   dialogInterface.dismiss();
                               }else if(listItems[i].equals("Gyms")){
                                   nearbyPlaces("gym");
                                   dialogInterface.dismiss();
                               }else if(listItems[i].equals("Night Clubs")){
                                   nearbyPlaces("night_club");
                                   dialogInterface.dismiss();
                               }else if(listItems[i].equals("Gas Stations")){
                                   nearbyPlaces("gas_station");
                                   dialogInterface.dismiss();
                               }else if(listItems[i].equals("Taxi Stands")){
                                   nearbyPlaces("taxi_stand");
                                   dialogInterface.dismiss();
                               }else if(listItems[i].equals("Pharmacies")){
                                   nearbyPlaces("pharmacy");
                                   dialogInterface.dismiss();
                               }
                            }
                        });
                        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog mDialog = mBuilder.create();
                        mDialog.show();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        buildLocationCallBack();
        buildLocationRequest();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());

    }


    @Override
    protected void onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onStop();
    }

    private void buildLocationCallBack() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mLastLocation = locationResult.getLastLocation();
                if (mMarker != null) {
                    mMarker.remove();
                }
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();

                LatLng latLng = new LatLng(latitude, longitude);
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title("Your position")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                mMarker = mMap.addMarker(markerOptions);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,9),1000,null);
            }
        };
    }

    private void buildLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setSmallestDisplacement(10f);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    private void nearbyPlaces(final String placeType) {
        mMap.clear();
        String url = getUrl(latitude, longitude, placeType);
        mService.getNearbyPlaces(url).enqueue(new Callback<MyPlaces>() {
            @Override
            public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {
                currentPlace = response.body();
                if (response.isSuccessful()) {
                    for (int i = 0; i < response.body().getResults().length; i++) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        Results googlePlace = response.body().getResults()[i];
                        double lat = Double.parseDouble(googlePlace.getGeometry().getLocation().getLat());
                        double lng = Double.parseDouble(googlePlace.getGeometry().getLocation().getLng());
                        String placeName = googlePlace.getName();
                        String vicinity = googlePlace.getVicinity();
                        LatLng latLng = new LatLng(lat, lng);
                        markerOptions.position(latLng);
                        markerOptions.title(placeName + ": " + vicinity);
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                        markerOptions.snippet(String.valueOf(i));
                        mMap.addMarker(markerOptions);
                        LatLng currentPosition = new LatLng(latitude,longitude);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition,13),1000,null);
                    }

                }
            }

            @Override
            public void onFailure(Call<MyPlaces> call, Throwable t) {

            }
        });
    }

    private String getUrl(double latitude, double longitude, String placeType) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + 10000);
        googlePlacesUrl.append("&type=" + placeType);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + getResources().getString(R.string.browser_key));

        return googlePlacesUrl.toString();
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, MY_PERMISSION_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, MY_PERMISSION_CODE);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                        buildLocationCallBack();
                        buildLocationRequest();

                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
                    }
                }
            }
            break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (currentPlace != null) {
                    Common.currentResult = currentPlace.getResults()[Integer.parseInt(marker.getSnippet())];
                    startActivity(new Intent(MapsActivity.this, ViewPlaces.class));
                } else {
                    Toast.makeText(MapsActivity.this, "No informations about this place!", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }
}






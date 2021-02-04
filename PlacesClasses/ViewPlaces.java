package com.example.travelweatherapp.PlacesClasses;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.travelweatherapp.Common.Common;
import com.example.travelweatherapp.ModelPlaces.PlaceDetail;
import com.example.travelweatherapp.R;
import com.example.travelweatherapp.Remote.IGoogleAPIService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewPlaces extends AppCompatActivity {

    ImageView photo;
    IGoogleAPIService mService;
    RatingBar ratingBar;
    TextView openingHours, placeAdress, placeName, placeWebsite, placePhoneNumber;
    Button btnViewOnMap;
    Button btnAddPlaceToFavorite;
    PlaceDetail mPlace;
    Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase db;
    private DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_places);

        mService = Common.getGoogleAPIService();

        photo = (ImageView) findViewById(R.id.photo);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        placeAdress = (TextView) findViewById(R.id.place_adress);
        placeName = (TextView) findViewById(R.id.place_name);
        openingHours = (TextView) findViewById(R.id.place_openHour);
        placeWebsite = (TextView) findViewById(R.id.place_website);
        placePhoneNumber = (TextView) findViewById(R.id.place_phoneNumber);
        btnViewOnMap = (Button) findViewById(R.id.btn_showMap);
        btnAddPlaceToFavorite = (Button) findViewById(R.id.btn_addPlaceToFavorite);
        toolbar = (Toolbar) findViewById(R.id.toolbarP);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");


        toolbar.setTitle("Travel Guide - Place Details");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        placePhoneNumber.setAutoLinkMask(Linkify.PHONE_NUMBERS);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnViewOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mPlace.getResult().getUrl()));
                startActivity(mapIntent);
            }
        });


        btnAddPlaceToFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String pName = placeName.getText().toString().trim();
                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.child(firebaseAuth.getCurrentUser().getUid()).child("Favorites").child(pName).exists()) {
                            Toast.makeText(ViewPlaces.this, "Already added to favorites...", Toast.LENGTH_SHORT).show();
                        }else{
                            users.child(firebaseAuth.getCurrentUser().getUid()).child("Favorites").child(pName).setValue(pName);
                            Toast.makeText(ViewPlaces.this, "Added to favorites!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        if (Common.currentResult.getPhotos() != null && Common.currentResult.getPhotos().length > 0) {
            Picasso.get()
                    .load(getPhotoOfPlace(Common.currentResult.getPhotos()[0].getPhoto_reference(), 1000))
                    .placeholder(R.drawable.ic_image_black_24dp)
                    .error(R.drawable.ic_error_black_24dp)
                    .into(photo);
        }

        if (Common.currentResult.getRating() != null && !TextUtils.isEmpty(Common.currentResult.getRating())) {
            ratingBar.setRating(Float.parseFloat(Common.currentResult.getRating()));
        } else {
            ratingBar.setVisibility(View.GONE);
        }

        if (Common.currentResult.getOpening_hours() != null) {
            if (Common.currentResult.getOpening_hours().getOpen_now() == "true") {
                openingHours.setText("Open now: yes");
            } else if (Common.currentResult.getOpening_hours().getOpen_now() == "false") {
                openingHours.setText("Open now: no");
            }
        } else {
            openingHours.setVisibility(View.GONE);
        }


        mService.getDetailPlace(getPlaceDetailUrl(Common.currentResult.getPlace_id()))
                .enqueue(new Callback<PlaceDetail>() {
                    @Override
                    public void onResponse(Call<PlaceDetail> call, Response<PlaceDetail> response) {
                        mPlace = response.body();

                        placeAdress.setText(mPlace.getResult().getFormatted_address());
                        placeName.setText(mPlace.getResult().getName());
                        placePhoneNumber.setText(mPlace.getResult().getFormatted_phone_number());
                        placeWebsite.setText(mPlace.getResult().getWebsite());
                    }
                    @Override
                    public void onFailure(Call<PlaceDetail> call, Throwable t) {

                    }
                });
    }

    private String getPlaceDetailUrl(String place_id) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json");
        url.append("?placeid=" + place_id);
        url.append("&key=" + getResources().getString(R.string.browser_key));
        return url.toString();
    }

    private String getPhotoOfPlace(String photo_reference, int maxWidth) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo");
        url.append("?maxwidth=" + maxWidth);
        url.append("&photoreference=" + photo_reference);
        url.append("&key=" + getResources().getString(R.string.browser_key));
        return url.toString();
    }
}



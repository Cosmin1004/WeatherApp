package com.example.travelweatherapp.PlacesClasses;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.travelweatherapp.ProfileClasses.ProfileActivity;
import com.example.travelweatherapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class SavedPlaces extends AppCompatActivity {

    private ListView listView;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase db;
    TextView favoriteP;
    private DatabaseReference users;
    Toolbar toolbar;
    private ArrayList<String> placesList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_places);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Favorites");

        toolbar = (Toolbar) findViewById(R.id.toolbarP);
        listView = (ListView) findViewById(R.id.listView);
        favoriteP = (TextView) findViewById(R.id.favorite_text);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,placesList);
        listView.setAdapter(arrayAdapter);

        toolbar.setTitle("Travel Guide - Favorite Places");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String placeName = (String) ds.getKey();
                    arrayAdapter.add(placeName);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });

        final Button btnDelete = (Button) findViewById(R.id.btn_delete);


        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    btnDelete.setVisibility(View.VISIBLE);
                    btnDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SavedPlaces.this);
                            builder.setTitle("Are you sure you want to delete all your favorite places?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    users.removeValue();
                                    placesList.clear();
                                    arrayAdapter.clear();
                                    Toast.makeText(SavedPlaces.this, "Deleted...", Toast.LENGTH_SHORT).show();
                                    finish();
                                    Intent intent = new Intent(SavedPlaces.this, ProfileActivity.class);
                                    startActivity(intent);
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            builder.create().show();
                        }
                    });
                }else{
                    btnDelete.setVisibility(View.GONE);
                    favoriteP.setText("No favorite place added");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}

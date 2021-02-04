package com.example.travelweatherapp.ProfileClasses;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.travelweatherapp.PlacesClasses.SavedPlaces;
import com.example.travelweatherapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    TextView userName;
    TextView userEmail;
    Button btnShowFavoritePlaces;
    Button btnDeleteAccount;
    Button btnLogout;
    FloatingActionButton btnEditButton;
    FirebaseUser user;
    FirebaseDatabase database;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    DatabaseReference myRef;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userName = (TextView) findViewById(R.id.user_name);
        userEmail = (TextView) findViewById(R.id.user_email);
        btnShowFavoritePlaces = (Button) findViewById(R.id.btn_show_favorite_places);
        btnDeleteAccount = (Button) findViewById(R.id.btn_delete);
        btnLogout = (Button) findViewById(R.id.btn_logout);
        firebaseAuth = FirebaseAuth.getInstance();
        btnEditButton = (FloatingActionButton) findViewById(R.id.floatingButton);
        user = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(this);
        myRef = database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                userName.setText(name);
                userEmail.setText(email);
                toolbar = (Toolbar) findViewById(R.id.toolbarP);
                toolbar.setTitle("User profile - " + name);
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        btnShowFavoritePlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, SavedPlaces.class));
                finish();
            }
        });

        btnEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String options[] = {"Edit Name", "Edit Password"};
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Choose an option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (i == 0) {
                            AlertDialog.Builder builderName = new AlertDialog.Builder(ProfileActivity.this);
                            builderName.setTitle("Change name");
                            LinearLayout linearLayout = new LinearLayout(ProfileActivity.this);
                            final EditText newNameEt = new EditText(ProfileActivity.this);
                            newNameEt.setHint("Enter your new name!");
                            linearLayout.addView(newNameEt);
                            linearLayout.setPadding(10, 10, 10, 10);
                            builderName.setView(linearLayout);
                            builderName.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final String newName = newNameEt.getText().toString().trim();
                                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            dataSnapshot.getRef().child("name").setValue(newName);
                                            finish();
                                            startActivity(getIntent());
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            });
                            builderName.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builderName.create().show();
                        } else if (i == 1) {
                            AlertDialog.Builder builderPass = new AlertDialog.Builder(ProfileActivity.this);
                            builderPass.setTitle("Change your password");
                            LinearLayout linearLayout = new LinearLayout(ProfileActivity.this);
                            final EditText oldPasswordEt = new EditText(ProfileActivity.this);
                            oldPasswordEt.setHint("Enter your actually password");
                            oldPasswordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            linearLayout.addView(oldPasswordEt);
                            linearLayout.setPadding(10, 10, 10, 10);
                            builderPass.setView(linearLayout);
                            builderPass.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if ((TextUtils.isEmpty(oldPasswordEt.getText().toString()))) {
                                        Toast.makeText(ProfileActivity.this, "Please enter your actually password!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    AlertDialog.Builder builderPass = new AlertDialog.Builder(ProfileActivity.this);
                                    builderPass.setTitle("Change Password");
                                    LinearLayout linearLayout = new LinearLayout(ProfileActivity.this);
                                    final EditText newPasswordEt = new EditText(ProfileActivity.this);
                                    newPasswordEt.setHint("Enter a new password!");
                                    newPasswordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                    linearLayout.addView(newPasswordEt);
                                    linearLayout.setPadding(10, 10, 10, 10);
                                    builderPass.setView(linearLayout);
                                    builderPass.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (TextUtils.isEmpty(newPasswordEt.getText().toString())) {
                                                Toast.makeText(ProfileActivity.this, "Please enter a new password!", Toast.LENGTH_SHORT).show();
                                                return;
                                            }else if (newPasswordEt.getText().toString().length() < 6) {
                                                Toast.makeText(ProfileActivity.this, "New password too short!", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            progressDialog.setMessage("Changing password in progress...");
                                            progressDialog.show();
                                            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPasswordEt.getText().toString());
                                            if (user != null) {
                                                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        user.updatePassword(newPasswordEt.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(ProfileActivity.this, "Password updated...", Toast.LENGTH_SHORT).show();
                                                                    /*firebaseAuth.signOut();
                                                                    finish();
                                                                    Intent i = new Intent(ProfileActivity.this,LoginActivity.class);
                                                                    startActivity(i);*/
                                                                } else {
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(ProfileActivity.this, "Password cannot be updated...", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    });
                                    builderPass.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    builderPass.create().show();
                                }
                            });

                            builderPass.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builderPass.create().show();
                        }
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


        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Are you sure you want to delete this account?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteUser();
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

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Are you sure you want to log out?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
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
    }


    public void deleteUser() {
        AlertDialog.Builder builderPass = new AlertDialog.Builder(ProfileActivity.this);
        builderPass.setTitle("Delete your account");
        LinearLayout linearLayout = new LinearLayout(ProfileActivity.this);
        final EditText passwordEt = new EditText(ProfileActivity.this);
        passwordEt.setHint("Enter your password!");
        passwordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        linearLayout.addView(passwordEt);
        linearLayout.setPadding(10, 10, 10, 10);
        builderPass.setView(linearLayout);
        builderPass.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if ((TextUtils.isEmpty(passwordEt.getText().toString()))) {
                    Toast.makeText(ProfileActivity.this, "Please enter your password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(),passwordEt.getText().toString());
                if (user != null) {
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        myRef.removeValue();
                                        Toast.makeText(ProfileActivity.this, "Account deleted...", Toast.LENGTH_SHORT).show();
                                        finish();
                                        Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
                                        startActivity(i);
                                    } else {
                                        Toast.makeText(ProfileActivity.this, "Account cannot be deleted...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });

        builderPass.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderPass.create().show();
    }
}

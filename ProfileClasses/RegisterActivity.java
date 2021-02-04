package com.example.travelweatherapp.ProfileClasses;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.travelweatherapp.OtherClasses.MainActivity;
import com.example.travelweatherapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextName;
    private Button btnRegister;
    private TextView textViewSignin;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase db;
    private DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        progressDialog = new ProgressDialog(this);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextName=(EditText)findViewById(R.id.editTextName);
        textViewSignin = (TextView) findViewById(R.id.textViewSignin);
        btnRegister.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");

        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
        }

    }

    public void onBackPressed() {
        return;
    }

    private void registerUser() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if ((TextUtils.isEmpty(email) && (TextUtils.isEmpty(password)) && (TextUtils.isEmpty(name)))) {
            Toast.makeText(this, "Please enter your email, password & name!", Toast.LENGTH_SHORT).show();
            return;
        } else if ((TextUtils.isEmpty(email) && (TextUtils.isEmpty(password)))) {
            Toast.makeText(this, "Please enter your email and a password!", Toast.LENGTH_SHORT).show();
            return;
        } else if ((TextUtils.isEmpty(email) && (TextUtils.isEmpty(name)))) {
            Toast.makeText(this, "Please enter your email and your name!", Toast.LENGTH_SHORT).show();
            return;
        } else if ((TextUtils.isEmpty(password) && (TextUtils.isEmpty(name)))) {
            Toast.makeText(this, "Please enter your name and a password!", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email!", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter a password!", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter your name!", Toast.LENGTH_SHORT).show();
            return;
        } else if (editTextPassword.getText().toString().length() < 6) {
            Toast.makeText(this, "Password too short!", Toast.LENGTH_SHORT).show();
            return;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Invalid Email!");
        }

        progressDialog.setMessage("Registering user...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    User user = new User();
                    user.setEmail(editTextEmail.getText().toString());
                    user.setName(editTextName.getText().toString());

                    users.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(RegisterActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btnRegister) {
            registerUser();
        }
        if (v == textViewSignin) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}

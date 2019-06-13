package com.example.chatlite;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private EditText username;

    private DatabaseReference mDataBaseReferene;
    private ProgressDialog mydialoge;


    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        mydialoge = new ProgressDialog(RegistrationActivity.this);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email_signup);
        password = findViewById(R.id.password_signup);
        username = findViewById(R.id.username_signup);
        Button register = findViewById(R.id.button_signup);


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String semail = email.getText().toString().trim();
                String spassword = password.getText().toString().trim();
                String suser = username.getText().toString().trim();
                mydialoge.setMessage("Processing..");
                mydialoge.show();
                registrarion(suser,semail,spassword);
            }
        });
    }
    private void registrarion(final String user, String email , String password){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = currentuser.getUid();

                            //Setting default values
                            mDataBaseReferene = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                            HashMap<String , String> hashMap = new HashMap<String,String>();
                            hashMap.put("name",user);
                            hashMap.put("status","Hi There!");
                            hashMap.put("image","default");
                            hashMap.put("thumb_image","default");

                            mDataBaseReferene.setValue(hashMap);



                            Toast.makeText(RegistrationActivity.this,"Registration Success!",Toast.LENGTH_SHORT).show();
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "createUserWithEmail:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                            Intent i = new Intent(RegistrationActivity.this,HomeActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            mydialoge.dismiss();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            mydialoge.dismiss();
                            //updateUI(null);
                        }
                    }
                });
    }
}

package com.example.chatlite;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private EditText newStatus;
    private Button newButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        newStatus = findViewById(R.id.change_status);
        newButton = findViewById(R.id.save_status);

        progressDialog = new ProgressDialog(StatusActivity.this);

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = newStatus.getText().toString().trim();
                FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();
                String uid = currentuser.getUid();
                databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                progressDialog.setMessage("Updating..");
                progressDialog.show();
                databaseReference.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            progressDialog.dismiss();
                            Toast.makeText(StatusActivity.this,"Status Updated!",Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                });


            }
        });

    }
}

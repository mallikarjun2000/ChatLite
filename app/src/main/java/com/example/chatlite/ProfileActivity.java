package com.example.chatlite;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseReference,mDatabaseRequestReferance;
    private FirebaseUser currentUser;
    private String user;
    private TextView nameView,statusView;
    private Button requestButton;
    private String current_state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        nameView = findViewById(R.id.pname);
        statusView = findViewById(R.id.pstatus);
        requestButton = findViewById(R.id.friendrequest);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        current_state = "not friends";
        final String uid = getIntent().getStringExtra("string_uid");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        mDatabaseRequestReferance = FirebaseDatabase.getInstance().getReference().child("Request_Data");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                nameView.setText(name);
                statusView.setText(status);

                mDatabaseRequestReferance.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(uid))
                        {
                            String req_type = dataSnapshot.child(uid).child("request_type").getValue().toString();
                            if(req_type.equals("recieved")){
                                current_state = "request recieved";
                                requestButton.setText("Accept Request");
                            }
                            else{
                                if(req_type.equals("sent")){
                                    current_state = "request sent";
                                    requestButton.setText("Cancel Request");
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestButton.setEnabled(false);

                //------------- NOT FRIENDS STATE ---------------------//


                if(current_state.equals("not friends")){

                    mDatabaseRequestReferance.child(currentUser.getUid())
                            .child(uid).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                mDatabaseRequestReferance.child(uid)
                                        .child(currentUser.getUid()).child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ProfileActivity.this,"Request Sent",Toast.LENGTH_SHORT).show();
                                        current_state = "request sent";
                                        requestButton.setEnabled(true);
                                        requestButton.setText("cancel request");
                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(ProfileActivity.this,"Request Not Sent succesfully",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                //-------------------- CANCEL FRIEND REQUEST ----------------------//

                if(current_state.equals("request sent"))
                {
                    mDatabaseRequestReferance.child(currentUser.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mDatabaseRequestReferance.child(uid).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    requestButton.setEnabled(true);
                                    current_state = "not friends";
                                    requestButton.setText("SENT REQUEST");
                                }
                            });
                        }
                    });
                }

            }
        });
    }
}

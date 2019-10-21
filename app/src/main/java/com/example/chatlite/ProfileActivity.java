package com.example.chatlite;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseReference,mDatabaseRequestReferance,mFriendsReference;
    private FirebaseUser currentUser;
    private String user;
    private TextView nameView,statusView;
    private ImageView imageView;
    private Button requestButton;
    private String current_state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        nameView = findViewById(R.id.pname);
        statusView = findViewById(R.id.pstatus);
        requestButton = findViewById(R.id.friendrequest);
        imageView = findViewById(R.id.pimage);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        current_state = "not friends";
        final String uid = getIntent().getStringExtra("string_uid");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        mDatabaseRequestReferance = FirebaseDatabase.getInstance().getReference().child("Request_Data");
        mFriendsReference = FirebaseDatabase.getInstance().getReference().child("Friends data");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                if(image.equals("default"))
                {
                    Picasso.get().load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQdkaBEEeZk4IMpWUQsCqc7jXuX-4-qT7-V6fyF_79lCyanLr5OfA").into(imageView);
                }else
                    Picasso.get().load(image).into(imageView);
                nameView.setText(name);
                statusView.setText(status);

                //------------------FRIENDS LIST / REQUEST FEATURE----------------------//

                mDatabaseRequestReferance.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(uid))
                        {
                            String req_type = dataSnapshot.child(uid).child("request_type").getValue().toString();
                            if(req_type.equals("received")){
                                current_state = "request received";
                                requestButton.setText("Accept Request");
                            }
                            else{
                                if(req_type.equals("sent")){
                                    current_state = "request sent";
                                    requestButton.setText("Cancel Request");
                                }
                            }
                        }
                        else
                        {
                            mFriendsReference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(uid))
                                    {
                                        current_state = "friends";
                                        requestButton.setText(" UnFriend ");
                                    }
                                    else
                                    {
                                        current_state="not friends";
                                        requestButton.setText("SEND REQUEST");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
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
                                requestButton.setEnabled(true);
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
                                    requestButton.setText("SEND REQUEST");
                                }
                            });
                        }
                    });
                }
                //------------------- REQUEST RECECEIVED STATE----------------------//
                if(current_state.equals("request received")){
                    final String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                    mFriendsReference.child(currentUser.getUid()).child(uid).setValue(date)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendsReference.child(uid).child(currentUser.getUid()).setValue(date).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mDatabaseRequestReferance.child(currentUser.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mDatabaseRequestReferance.child(uid).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            requestButton.setEnabled(true);
                                                            current_state = "friends";
                                                            requestButton.setText(" UnFriend ");
                                                            Toast.makeText(ProfileActivity.this,"Friend Added",Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                }
                //----------------------- Already Friends State --------------------------//
                if(current_state.equals("friends")){
                    mFriendsReference.child(currentUser.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendsReference.child(uid).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    requestButton.setEnabled(true);
                                    current_state="not friends";
                                    requestButton.setText("SEND REQUEST");
                                }
                            });
                        }
                    });
                }

            }
        });
    }
}

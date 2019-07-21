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

    private TextView userName;
    private TextView userStatus;
    private DatabaseReference mDatabaseReferane;
    private DatabaseReference mFriendReqDataBaseReference;
    private FirebaseUser CurrentUser;
    private Button button = findViewById(R.id.send_request_button);
    private String request;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String name = getIntent().getStringExtra("user_id");
        userName = findViewById(R.id.profile_name);
        userStatus=findViewById(R.id.profle_status);
        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReferane = FirebaseDatabase.getInstance().getReference().child("Users").child(name);
        mFriendReqDataBaseReference = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        request = "not friends";

        mDatabaseReferane.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                userName.setText(name);
                userStatus.setText(status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this,"No Data Found",Toast.LENGTH_SHORT).show();
            }
        });

        /*button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(request.equals("not friends"))
                {
                    mFriendReqDataBaseReference.child(CurrentUser.getUid()).child(name).child("req_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                mFriendReqDataBaseReference.child(name).child(CurrentUser.getUid()).child("req_type").setValue("recieved").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ProfileActivity.this,"Request Recieved Sucessfully",Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(ProfileActivity.this,"Request Failed Successfully",Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            }
        });*/
    }
}

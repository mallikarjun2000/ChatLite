package com.example.chatlite;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

public class AllUsersActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseReference;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private FirebaseRecyclerOptions<User> options;
    private FirebaseRecyclerAdapter<User , MyViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor("#3949AB"));

        String udi = FirebaseAuth.getInstance().getUid();

        mToolbar = findViewById(R.id.all_users_toolbar);
        mRecyclerView=findViewById(R.id.recycler_view);


        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");//.child(udi);


        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All users");
        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {

        options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(mDatabaseReference, User.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, MyViewHolder>(options) {

            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.singleuserlayout, parent, false);

                return new MyViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(MyViewHolder usersViewHolder, int i,User users) {
                usersViewHolder.setName(users.getName());
                usersViewHolder.setStatus(users.getStatus());
                final String uid = getRef(i).getKey();
                usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(AllUsersActivity.this,ProfileActivity.class);
                        i.putExtra("string_uid",uid);
                        startActivity(i);
                    }
                });
            }
        };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);


        if(firebaseRecyclerAdapter!=null) {
            firebaseRecyclerAdapter.startListening();
        }
        super.onStart();


    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{

        View mView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName (String name){
            TextView userNameView = (TextView) mView.findViewById(R.id.single_user_name);
            userNameView.setText(name);
        }
        public void setStatus(String status){
            TextView userStatus = mView.findViewById(R.id.single_user_status);
            userStatus.setText(status);
        }
    }
    }


    /* @Override
    protected void onStart() {
        super.onStart();


        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users");

        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(query, new SnapshotParser<User>() {
                            @NonNull
                            @Override
                            public User parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new User(snapshot.child("name").getValue().toString(),
                                        snapshot.child("status").getValue().toString(),
                                        snapshot.child("image").getValue().toString(),
                                        snapshot.child("thumb_image").getValue().toString());
                            }
                        })
                        .build();

         adapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User model) {

                holder.setLName(model.getName());
                holder.setLStatus(model.getStatus());
            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.singleuserlayout, viewGroup, false);
                return new UserViewHolder(view);
            }
        };

        mRecyclerView.setAdapter(adapter);

        adapter.startListening();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{

        private RelativeLayout relativeLayout;
        private TextView name ;
        private TextView status;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.relative_layout_1);
            name=itemView.findViewById(R.id.single_user_name);
            status=itemView.findViewById(R.id.single_user_status);
        }
        public void setLName(String string) {
            name.setText(string);
        }
        public void setLStatus(String string) {
            status.setText(string);
        }
    }*/


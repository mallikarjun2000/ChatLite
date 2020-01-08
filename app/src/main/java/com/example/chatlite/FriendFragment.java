package com.example.chatlite;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.chatlite.AllUsersActivity.MyViewHolder;
import com.google.firebase.database.ValueEventListener;

import java.sql.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView mFriendsRecycler;
    private DatabaseReference mFreindsRef;
    private FirebaseRecyclerOptions<Friend> options;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Friend, FriendViewHolder> adapter;

    public FriendFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendFragment newInstance(String param1, String param2) {
        FriendFragment fragment = new FriendFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_friend, container, false);
        mFriendsRecycler = mView.findViewById(R.id.friendsrecycler);
        FirebaseUser cur = FirebaseAuth.getInstance().getCurrentUser();
        String uid = cur.getUid();

        mFreindsRef = FirebaseDatabase.getInstance().getReference().child("Friends Data").child(uid);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mFriendsRecycler.setVisibility(View.VISIBLE);
        mFriendsRecycler.setHasFixedSize(true);
        mFriendsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        return mView;
    }

    @Override
    public void onStart() {
        options =
                new FirebaseRecyclerOptions.Builder<Friend>()
                        .setQuery(mFreindsRef , Friend.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Friend, FriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendViewHolder holder, int position, @NonNull Friend model) {
                final String list_user_id = getRef(position).getKey();
                mDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        holder.setName(dataSnapshot.child("name").getValue().toString());
                        Log.i("LOCT",""+dataSnapshot.child("name").getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.singleuserlayout, viewGroup , false);
                return new FriendViewHolder(view);
            }
        };
        mFriendsRecycler.setAdapter(adapter);
        mFriendsRecycler.setVisibility(View.VISIBLE);
        if(adapter!=null) {
            adapter.startListening();
        }
        super.onStart();
    }

    // view holder for each view
    public class FriendViewHolder extends RecyclerView.ViewHolder {
        View mView;
        FriendViewHolder(View view){
            super(view);
            this.mView = view;
        }
        public void setName (String name){
            TextView userNameView = (TextView) mView.findViewById(R.id.single_user_name);
            userNameView.setText(name);
        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

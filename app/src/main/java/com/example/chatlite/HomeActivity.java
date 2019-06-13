package com.example.chatlite;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Set;

public class HomeActivity extends AppCompatActivity implements RequestFragment.OnFragmentInteractionListener,ChatFragment.OnFragmentInteractionListener,FriendFragment.OnFragmentInteractionListener{

    private FirebaseAuth mAuth;
    private Toolbar mToolBar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private SectionsPageAdapter mSectionsPAgeAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor("#3949AB"));

        mAuth = FirebaseAuth.getInstance();

        mToolBar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("ChatLite");
        mToolBar.setTitleTextColor(getResources().getColor(android.R.color.white));


        mViewPager = findViewById(R.id.tab_pager);


        mSectionsPAgeAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPAgeAdapter);

        mTabLayout = findViewById(R.id.main_tab);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setSelectedTabIndicatorColor(Color.WHITE);
       // mTabLayout.setSelectedTabIndicatorHeight((int) (5 * getResources().getDisplayMetrics().density));
        mTabLayout.setTabTextColors(Color.WHITE,Color.WHITE);//.parseColor("#727272"), Color.parseColor("#ffffff"));
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null)
        {
            sendToStart();
        }

    }

    private void sendToStart(){
        startActivity(new Intent(HomeActivity.this,MainActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.account_settings)
        {
            startActivity(new Intent(HomeActivity.this, SettingsActivity.class));

        }
        if(item.getItemId() == R.id.all_users)
        {
            startActivity(new Intent(HomeActivity.this,AllUsersActivity.class));
        }
        if(item.getItemId() == R.id.logout)
        {
            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

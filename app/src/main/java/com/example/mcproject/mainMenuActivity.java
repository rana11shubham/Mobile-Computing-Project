package com.example.mcproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.mcproject.sendNotification.Token;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class mainMenuActivity extends AppCompatActivity {
    BottomNavigationView bmv;
    Boolean menuSet=true;
    LottieAnimationView lottieAnimationView;
    MeowBottomNavigation meowBottomNavigation;
    private static final String SHARED_PREF_NAME = "MCpref";
    private static final String KEY_UID = "UID";
    private static final String KEY_USERNAME = "USERNAME";
    private SharedPreferences sharedPreferences;
    private CustomLocationHandler locationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        locationHandler = new CustomLocationHandler(this);
        Boolean tempb = getIntent().getBooleanExtra("callFromNotification", false);
        Log.d("GG", "onCreate: "+tempb);

        UpdateToken();


        meowBottomNavigation = findViewById(R.id.bottomNavigation);

        meowBottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic__home_new));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.ic__profile_new));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.ic__554319801578287702));

        meowBottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                switch (item.getId()){
                    case 1:
                        if(!menuSet){
                            generateHomeFragment();
                        }
                        break;
                    case 2:
                        generateProfileFragment();
                        menuSet=false;
                        break;
                    case 3:
                        generatenotificationListFragment();
                        menuSet=false;
                        break;
                    default: throw new IllegalStateException("Unexpected value: " + item.getId());
                }
            }
        });

        meowBottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                String name;
                switch (item.getId()) {
                    case 1:
                        name = "Home";
                        break;
                    case 2:
                        name = "Profile";
                        break;
                    case 3:
                        name = "Settings";
                        break;
                    default:
                        name = "";
                        break;
                }
            }
        });

        if(tempb != false) {
            meowBottomNavigation.show(3,true);
            generatenotificationListFragment();
        }
        else{
            meowBottomNavigation.show(1,true);
            getSupportFragmentManager().beginTransaction().replace(R.id.mainMenuContainer,new homeScreen()).commit();
        }

        meowBottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {
                Toast.makeText(getApplicationContext(), "Already Pressed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2){
            if (resultCode == 0){
                locationHandler.turnOnGPS();
            }
        }
    }
    private void generateHomeFragment(){
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_right,R.anim.enter_from_right,R.anim.exit_to_right).replace(R.id.mainMenuContainer,new homeScreen()).commit();
    }
    private void generateProfileFragment(){
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_right,R.anim.enter_from_right,R.anim.exit_to_right).replace(R.id.mainMenuContainer,new profileScreen()).commit();
    }
    private void generatenotificationListFragment(){
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_right,R.anim.enter_from_right,R.anim.exit_to_right).replace(R.id.mainMenuContainer,new notificationListFragment()).commit();
    }

    private void UpdateToken(){
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        Token token= new Token(refreshToken);
        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("token").setValue(refreshToken);


    }
}
package com.txy822.android_personality_based_dating_app.view.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.txy822.android_personality_based_dating_app.R;
import com.txy822.android_personality_based_dating_app.view.authentication.signup.SignUp;
import com.txy822.android_personality_based_dating_app.view.authentication.login.Login;

/**
 * Main class to login existing user and new user to register
 */
public class Main extends AppCompatActivity {
    private FirebaseAuth mAuth;
    /**
     * Creates main activity
     * @param savedInstanceState saved Instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
    }

    /**
     * Check if user is already sign in
     */
    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()!=null){
            Intent intent = new Intent(Main.this, Home.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Sign  in activity
     * @param view view
     */
    public void login(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    /**
     * Sign up activity
     * @param view view
     */
    public void SignUp(View view) {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }

}

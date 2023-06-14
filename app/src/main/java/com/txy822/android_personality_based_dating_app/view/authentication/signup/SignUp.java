package com.txy822.android_personality_based_dating_app.view.authentication.signup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.txy822.android_personality_based_dating_app.R;
import com.txy822.android_personality_based_dating_app.utils.NetworkManager;
import com.txy822.android_personality_based_dating_app.view.authentication.login.Login;
import com.txy822.android_personality_based_dating_app.view.main.Main;
import com.txy822.android_personality_based_dating_app.view.profile.EditProfileFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * The SignUp class is for registering new users to database
 */
public class SignUp extends AppCompatActivity {
    /**
     * mAuth used to keep track of authorised users
     * mStore clause based firebase database to store users detail
     * progressDialog dialog while loading the system
     */
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private EditText full_name;
    private EditText password;
    private EditText confirm_password;
    private EditText email;
    private ProgressDialog progressDialog;

    private FrameLayout fragmentContainer;
    private ConstraintLayout componentsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        componentsContainer = findViewById(R.id.sign_up_container);
        fragmentContainer = findViewById(R.id.fragment_container);

        mAuth = FirebaseAuth.getInstance();
        full_name = findViewById(R.id.enter_full_name);
        password = findViewById(R.id.enter_password);
        confirm_password = findViewById(R.id.enter_password2);
        email = findViewById(R.id.enterEmail);

        progressDialog = new ProgressDialog(getApplicationContext());
        progressDialog.setTitle("Logging in ...");
        progressDialog.setMessage("Please wait!");
        mStore = FirebaseFirestore.getInstance();
    }

    /**
     * Registers new users using full name, email and password
     *
     * @param view view
     */
    public void signup(View view) {
        //create  a user in FirebaseFirestore  by password and email
        String full_name_ = full_name.getText().toString().trim();
        String email_ = email.getText().toString().trim();
        String password_ = password.getText().toString();
        String confirm_password_ = confirm_password.getText().toString();
        if (!email_.isEmpty() && !password_.isEmpty() && !full_name_.isEmpty() && password_.equals(confirm_password_)) {
            if (NetworkManager.isNetworkAvailable(this)) {
                createAccount(mAuth, mStore, full_name_, email_, password_);            }
            else {
                Toast.makeText(this, "No internet connection available", Toast.LENGTH_SHORT).show();
            }
        }
        if (full_name_.isEmpty() || email_.isEmpty() || password_.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Your Full Name or Email or Password Field  is empty!", Toast.LENGTH_SHORT).show();
        } else if (!password_.equals(confirm_password_)) {
            Toast.makeText(getApplicationContext(), "Password  do not match!", Toast.LENGTH_SHORT).show();
        } else {
            Log.i("TAG", "Check your  Internet");
        }
    }

    /**
     * Cancels the registration and go back to main class
     *
     * @param view view
     */
    public void cancel(View view) {
        Intent intent = new Intent(this, Main.class);
        startActivity(intent);
    }

    /**
     * Switches to login or sign in activity
     *
     * @param view view
     */
    public void login(View view) {

        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    public FirebaseUser createAccount(FirebaseAuth mAuth_, FirebaseFirestore mStore_, String fullName_, String email, String password) {

        mAuth_.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //Hash map to track of user detail
                Map<String, Object> map = new HashMap<>();
                map.put("fullName", fullName_);
                //set up User collection with current user UID as document Id to store user detail in the field of document
//                    storeAccount(mAuth_,mStore_,full_name_,map).
                mStore_.collection("Users").document(mAuth_.getCurrentUser().getUid()).set(map).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        progressDialog.dismiss();
                        //once creation of document is successful it switches to login activity
                        Toast.makeText(getBaseContext(), "Account Created", Toast.LENGTH_SHORT).show();

                        // Replace the fragment container with YourFragment
                        EditProfileFragment fragment = new EditProfileFragment();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        componentsContainer.setVisibility(View.GONE);

                    } else {
                        Toast.makeText(getBaseContext(), "Failed to Create Account!" + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                progressDialog.dismiss();
                Toast.makeText(getBaseContext(), "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        FirebaseUser user = mAuth.getCurrentUser();
        return user;
    }
}
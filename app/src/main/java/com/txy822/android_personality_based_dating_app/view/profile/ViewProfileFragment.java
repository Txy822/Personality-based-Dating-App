package com.txy822.android_personality_based_dating_app.view.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.txy822.android_personality_based_dating_app.R;

//import android.app.FragmentManager;
//import android.app.FragmentTransaction;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;

/**
 * ViewProfileFragment To view full user fragment_show_profile
 */
public class ViewProfileFragment extends Fragment {
    /**
     * Result Load code for uploading image
     */
    private static final int RESULT_LOAD_IMG = 123;
    /**
     * Image view  for fragment_show_profile picture view
     */
    private ImageView profile_img;
    /**
     * User details to view on fragment_show_profile
     */
    private TextView full_name;
    private TextView personality_type;
    private TextView location;
    private TextView date_of_birth;
    private TextView age_range_pref;
    private TextView age;
    private TextView summary;

    /**
     * Calander to add dates on user date of birth
     */
    final Calendar myCalendar = Calendar.getInstance();
    /**
     * Edit fragment_show_profile button to edit2 the detail of user
     */
    private Button edit_profile_btn;
    private Button cancel;

    /**
     * Declare the firebase cloud database and authentication instances
     */
    private Uri url = null;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private StorageReference mStorage;

    /**
     * Creates view of view fragment_show_profile fragment
     *
     * @param inflater           LayoutInflater inflater
     * @param container          ViewGroup container
     * @param savedInstanceState Bundle
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /*
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_view_profile, container, false);
        profile_img=(ImageView)view.findViewById(R.id.fragment_show_profile);
        full_name=view.findViewById(R.id.fulName_id);
        personality_type=(TextView)view.findViewById(R.id.personality_type);
        location=(TextView)view.findViewById(R.id.location);
        date_of_birth=(TextView)view.findViewById(R.id.date_of_birth);
        age_range_pref=(TextView)view.findViewById(R.id.age_range);
        summary=(TextView)view.findViewById(R.id.summary);
        edit_profile_btn=view.findViewById(R.id.edit2);
        */
        View view = inflater.inflate(R.layout.fragment_show_profile, container, false);
        profile_img = (ImageView) view.findViewById(R.id.profileImage);
        full_name = view.findViewById(R.id.fullName);
        personality_type = (TextView) view.findViewById(R.id.personalityType);
        location = (TextView) view.findViewById(R.id.locationText);
        date_of_birth = (TextView) view.findViewById(R.id.dateOfBirth);
        age = (TextView) view.findViewById(R.id.ageText);

        age_range_pref = (TextView) view.findViewById(R.id.ageRangeText);
        summary = (TextView) view.findViewById(R.id.hobbiesContent);
        edit_profile_btn = view.findViewById(R.id.editButton);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();

        getProfileData();

        // switch to update fragment_show_profile fragment to edit2 and update fragment_show_profile
        edit_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = new UpdateProfileFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                // fragmentTransaction.replace(R.id.viewProfileFragmentLayout, fragment);
                fragmentTransaction.replace(R.id.profile_frame_layout, fragment);

                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });
        return view;
    }

    /**
     * Fetches fragment_show_profile from database and display on the fragment view
     */
    private void getProfileData() {
        //get the user detail fro the Users collection

        if (mAuth.getCurrentUser() != null) {
            mStore.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        String full_name_set = task.getResult().getString(("fullName"));
                        String personality_type_set = task.getResult().getString("personalityType");
                        String date_of_birth_set = task.getResult().getString("dateOfBirth");
                        Long age_set = task.getResult().getLong("age");

                        String location_set = task.getResult().getString("location");

                        String summary_set = task.getResult().getString("summary");

                        String age_preference_set = task.getResult().getString("ageRangePreference");

                        String img_url_set = task.getResult().getString("img_url");
                        if (full_name_set != null) {
                            full_name.setText(full_name_set);
                        }
                        if (personality_type_set != null) {
                            personality_type.setText(personality_type_set);
                        }
                        if (age_preference_set != null) {
                            location.setText(location_set);
                        }
                        if (age_preference_set != null) {
                            age_range_pref.setText("Range: "+age_preference_set);
                        }
                        if (date_of_birth_set != null) {
                            date_of_birth.setText(date_of_birth_set);
                        }
                        if (summary_set != null) {
                            summary.setText(summary_set);
                        }
                        if(age_set!=null) {
                            age.setText("Age: " + age_set.toString());
                        }
                        if (img_url_set != null) {
                            Glide.with(requireContext()).load(img_url_set).placeholder(R.drawable.place_holder_profile).into(profile_img);
                            //profile_img.setImageURI(Uri.parse(img_url_set));
                        }

                    } else {
                        Log.i("TAG", "onComplete Error: fragment_show_profile data not fetched");
                    }

                }

            });
        } else {
            Log.i("TAG", "User don't sign in");

        }


    }

}
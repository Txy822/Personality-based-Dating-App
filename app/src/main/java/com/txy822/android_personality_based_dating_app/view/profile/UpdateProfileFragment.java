package com.txy822.android_personality_based_dating_app.view.profile;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
//import android.app.FragmentManager;
//import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.txy822.android_personality_based_dating_app.BuildConfig;
import com.txy822.android_personality_based_dating_app.R;
import com.txy822.android_personality_based_dating_app.view.main.Home;
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * UpdateProfileFragment Class updates the user fragment_show_profile
 */
public class UpdateProfileFragment extends Fragment {
    private static final int RESULT_LOAD_IMG = 123;
    private static final int RESULT_LOAD_PLACE = 122;

    private ImageView profile_img;
    private EditText full_name;
    private EditText personality_type;
    private EditText location;
    private EditText date_of_birth;
    private TextView age_range_pref;
    private TextView minAgeText;
    private TextView maxAgeText;
    private EditText summary;
    private int age;
    final Calendar myCalendar = Calendar.getInstance();

    private Button save_profile;
    private Button cancel;
    private Button signIn;

    private Uri url = null;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private StorageReference mStorage;
    private DatabaseReference dbRef;

    private double currentUserLatitude;
    private double currentUserLongitude;
    private String currentUserPlace;
    private RangeSeekBar rangeSeekbar;
    private int minAge =18;
    private int maxAge =100;


    List<String> type = new ArrayList<>(Arrays.asList("ESFJ", "ESTJ", "ENFJ", "ENTJ", "ESTP", "ESFP", "ENFP", "ENTP", "ISTP", "ISFP", "ISFP", "INFP", "INTP", "ISTJ", "ISFJ", "INFJ", "INTJ"));

    /**
     * Creates View for  fragment of update fragment_show_profile
     *
     * @param inflater           LayoutInflater inflater
     * @param container          ViewGroup container
     * @param savedInstanceState Bundle saved instances state
     * @return View
     */
    @SuppressLint("CutPasteId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        profile_img = (ImageView) view.findViewById(R.id.profileImage);
        full_name = view.findViewById(R.id.fullNameEditText);
        personality_type = (EditText) view.findViewById(R.id.personalityTypeEditText);
        location = (EditText) view.findViewById(R.id.locationEditText);
        cancel = view.findViewById(R.id.cancelButton);
        age_range_pref = (TextView) view.findViewById(R.id.ageRangeText);
        summary = (EditText) view.findViewById(R.id.hobbiesEditText);
        save_profile = view.findViewById(R.id.saveButton);
        date_of_birth = (EditText) view.findViewById(R.id.dateOfBirthEditText);

        minAgeText = view.findViewById(R.id.minAgeText);
        maxAgeText = view.findViewById(R.id.maxAgeText);

        //google places api access key
        Places.initialize(getActivity().getApplicationContext(), BuildConfig.PLACE_API_KEY);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            //calendar calculator
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                String myFormat = "MM/dd/yy";
                SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.UK);
                date_of_birth.setText(dateFormat.format(myCalendar.getTime()));
                Calendar today = Calendar.getInstance();
                age = (today.get(Calendar.YEAR) - (myCalendar.get(Calendar.YEAR)));

            }
        };
        //listener for date of birth
        date_of_birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
//        mStorage=FirebaseStorage.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        //displays the existing data
        if (mAuth.getCurrentUser() != null) {
            getProfileData();
        }
//upload image for fragment_show_profile
        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //for uploading fragment_show_profile images
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //initialize place  field list
                List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG);
                //create intent for place selector
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(getActivity().getApplicationContext());
                startActivityForResult(intent, RESULT_LOAD_PLACE);
            }

        });
        if (mAuth.getCurrentUser() != null) {
            save_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //random number  for name to save images
                    Long tsLong = System.currentTimeMillis() / 1000;
                    String ts = tsLong.toString();
                    //check if the image url is not added
                    if (url != null) {
                        mStorage.child(ts + "/").putFile(url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> res = taskSnapshot.getStorage().getDownloadUrl();
                                res.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        //download image uri
                                        String downloadUrl = uri.toString();
                                        //create hash map to store user fragment_show_profile
                                        Map<String, Object> map1 = new HashMap<>();
                                        map1.put("fullName", full_name.getText().toString());
                                        if (type.contains(personality_type.getText().toString())) {
                                            map1.put("personalityType", personality_type.getText().toString());
                                        } else {
                                            Toast.makeText(getContext(), "Personality type must be 16 MBTI", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        map1.put("location", location.getText().toString());
                                        map1.put("dateOfBirth", date_of_birth.getText().toString());
                                      //  map1.put("ageRangePreference", age_range_pref.getText().toString());

                                        map1.put("ageRangePreference",  minAge +" - "+ maxAge);
                                        map1.put("summary", summary.getText().toString());
                                        map1.put("img_url", downloadUrl);
                                        map1.put("age", age );
                                        map1.put("latitude", currentUserLatitude);
                                        map1.put("longitude", currentUserLongitude);

                                        //update the existing user data or fragment_show_profile under users collection with document id of each user UID
                                        mStore.collection("Users").document(mAuth.getCurrentUser().getUid()).set(map1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getContext(), "Error Not Updated", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }

                                });
                            }
                        });

                    } else {

                        //if image is not uploaded earlier or if image url id empty create new one
                        Map<String, Object> map = new HashMap<>();
                        map.put("fullName", full_name.getText().toString());
                        String placeHolder_image = "https://firebasestorage.googleapis.com/v0/b/datingapp-5b017.appspot.com/o/1678810263?alt=media&token=dd14e47e-fb89-4700-9bb2-8e1686e6bf17";
                        map.put("img_url", placeHolder_image);
                        if (type.contains(personality_type.getText().toString())) {
                            map.put("personalityType", personality_type.getText().toString());
                        } else {
                            Toast.makeText(getContext(), "Must be 16 MBTI eg. ISTJ, ESTP, ENFJ, INTP...", Toast.LENGTH_LONG).show();
                            return;
                        }
                        map.put("location", location.getText().toString());
                        map.put("dateOfBirth", date_of_birth.getText().toString());
//                        map.put("ageRangePreference", age_range_pref.getText().toString());
                        map.put("ageRangePreference",  minAge +" - "+ maxAge);                        map.put("summary", summary.getText().toString());
                        map.put("age", age);
                        map.put("latitude", currentUserLatitude);
                        map.put("longitude", currentUserLongitude);

                        //set user collection with user data using document id of current user UID
                        mStore.collection("Users").document(mAuth.getCurrentUser().getUid()).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Error Not Updated", Toast.LENGTH_LONG).show();

                                }
                            }
                        });
                    }

                    switchFragment();
                }
            });
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.editProfileFragmentLayout, new ViewProfileFragment());
//                fragmentTransaction.replace(R.id.updateProfileFragmentLayout, new ViewProfileFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();


            }
        });

        rangeSeekbar = (RangeSeekBar) view.findViewById(R.id.rangeSeekbar);
        rangeSeekbar.setRangeValues(18, 120);
        rangeSeekbar.setNotifyWhileDragging(true);
        rangeSeekbar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
               // Toast.makeText(getContext(), "Min Value- " + minValue + " & " + "Max Value- " + maxValue, Toast.LENGTH_LONG).show();
            }
        });
        rangeSeekbar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                minAge = (Integer)minValue;
                maxAge = (Integer)maxValue;
            }
        });
        return view;
    }

    private void switchFragment() {
                Intent intent = new Intent(getContext(), Home.class);
                startActivity(intent);

    }

    /**
     * Loads fragment_show_profile data to fragment view
     */
    private void getProfileData() {
        mStore.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    String full_name_set = task.getResult().getString(("fullName"));
                    String personality_type_set = task.getResult().getString("personalityType");
                    String date_of_birth_set = task.getResult().getString("dateOfBirth");

                    String location_set = task.getResult().getString("location");

                    String summary_set = task.getResult().getString("summary");

                    String age_preference_set = task.getResult().getString("ageRangePreference");

                    String[] numbers = {"",""};
                    if(age_preference_set !=null) {
                        numbers = age_preference_set.split("-");

                        int firstNumber = Integer.parseInt(numbers[0].trim());
                        int secondNumber = Integer.parseInt(numbers[1].trim());

                        rangeSeekbar.setSelectedMinValue(firstNumber);
                        rangeSeekbar.setSelectedMaxValue(secondNumber);
                    }

                    String img_url_set = task.getResult().getString("img_url");

                    if (full_name_set != null) {
                        full_name.setText(full_name_set);
                    }
                    if (personality_type_set != null) {
                        personality_type.setText(personality_type_set);
                    }
                    if (location_set != null) {
                        location.setText(location_set);
                    }
                    if (date_of_birth_set != null) {
                        date_of_birth.setText(date_of_birth_set);
                    }
                    if (summary_set != null) {
                        summary.setText(summary_set);
                    }

                    if (img_url_set != null) {
                        Glide.with(requireContext()).load(img_url_set).placeholder(R.drawable.place_holder_profile).into(profile_img);
                        // profile_img.setImageURI(Uri.parse(img_url_set));
                    } else {
                        Toast.makeText(requireContext(), "Please upload fragment_show_profile photo", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Log.i("TAG", "onComplete Error: fragment_show_profile data not fetched");
                }

            }

        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        try {
            //switch to identify the place selector and image updater onActivityResult based on request code
            switch (requestCode) {
                //for place selector or update
                case RESULT_LOAD_PLACE:
                    if (resultCode == RESULT_OK) {
                        //create place with autocomplete from the data
                        Place place = Autocomplete.getPlaceFromIntent(data);
//                     currentUserLocation.setText(place.getAddress());
                        //current user place like London,UK
                        currentUserPlace = place.getAddress();
                        //extract latitude and longitude value of places
                        String sSource = String.valueOf(place.getLatLng());
                        sSource = sSource.replaceAll("lat/lng:", "");
                        sSource = sSource.replace("(", "");
                        sSource = sSource.replace(")", "");
                        String[] split = sSource.split(",");
                        //extract latitude vale
                        currentUserLatitude = Double.parseDouble(split[0]);
                        //extract longitude value
                        currentUserLongitude = Double.parseDouble(split[1]);
                        //set location view by current user place
                        location.setText(currentUserPlace);

                    }
                    break;
                //case of uploading fragment_show_profile
                case RESULT_LOAD_IMG:
                    if (resultCode == RESULT_OK) {
                        //get daata
                        final Uri imageUri = data.getData();
                        // assign image url value
                        url = imageUri;
//                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
//                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        //display image
                        Glide.with(requireContext()).load(imageUri).placeholder(R.drawable.place_holder_profile).into(profile_img);
                        // Picasso.with(this).load(url).into(profile_img);
                        //profile_img.setImageURI(url);

                    } else {
                        Toast.makeText(getContext(), "You haven't picked Image", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        } catch (Exception e) {
            Log.i("Tag", e.getMessage());
        }
    }
}
package com.txy822.android_personality_based_dating_app.fragment;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.txy822.android_personality_based_dating_app.Home;
import com.txy822.android_personality_based_dating_app.Login;
import com.txy822.android_personality_based_dating_app.UpdateLoginDetail;

import java.util.Objects;

public class MyAlertDialogFragment extends DialogFragment {

    private final String TAG = "";
    private Uri url = null;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    //    private FirebaseStorage mStorage;
    private StorageReference mStorage;
    private DatabaseReference dbRef;

    private Button mUpdate;
    private Button logout;
    private Button deleteAccount;
    private Button updateLogin;

    // Empty constructor required for DialogFragment
    public MyAlertDialogFragment() {
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
    }

    public static MyAlertDialogFragment newInstance(String title, int buttonId) {
        MyAlertDialogFragment frag = new MyAlertDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("buttonId", buttonId);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        int buttonId = getArguments().getInt("buttonId");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage("Are you sure?");
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // on success
                if (buttonId == 1) {
                    logout();
                }
                if (buttonId == 2) {
                    updateLogin();
                }
                if (buttonId == 3) {
                    deleteUser();
                    logout();
                }
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                if (dialog != null && dialog.isShowing()) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }

        });

        return alertDialogBuilder.create();
    }

    public void deleteUser() {
        // [START delete_user]
        FirebaseUser user = mAuth.getInstance().getCurrentUser();
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User account deleted.");
                        }
                    }
                });
        // [END delete_user]
    }

    public void logout() {
        if (mAuth.getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getContext(), Login.class));
        }
    }

    public void updateLogin() {
        Intent intent = new Intent(getContext(), UpdateLoginDetail.class);
        startActivity(intent);
    }

    public void update() {
        Intent intent = new Intent(getContext(), Home.class);
//                Intent intent = new Intent(getContext(), AppNotification.class);
        startActivity(intent);
    }
}
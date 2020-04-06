package com.booksyndy.academics.android.ui.switchMode;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.booksyndy.academics.android.MainActivity;
import com.booksyndy.academics.android.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SwitchModeFragment extends Fragment {

    private SharedPreferences userPref;
    private SharedPreferences.Editor editor;
    private boolean preferGeneral;
    private String switchToMode,phoneNumber;
    private FirebaseFirestore mFirestore;
    private ProgressDialog progressDialog;



    private SwitchModeViewModel switchModeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        switchModeViewModel =
                ViewModelProviders.of(this).get(SwitchModeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_shareapp, container, false);

        userPref = getActivity().getSharedPreferences(getString(R.string.UserPref), 0);
        preferGeneral = userPref.getBoolean(getString(R.string.preferGeneral),false);
        phoneNumber = userPref.getString(getString(R.string.p_userphone), "");

        editor = userPref.edit();

        mFirestore = FirebaseFirestore.getInstance();


        if (preferGeneral) {
            switchToMode = "Academics Mode?";
        }
        else {
            switchToMode = "General Mode?";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Switch to "+switchToMode);
        if (preferGeneral) {
            builder.setMessage("You will see academics-related books on your home screen. Everything else will remain the same, and you can switch back anytime.");
        }
        else {
            builder.setMessage("You will see non-academic books on your home screen. Everything else will remain the same, and you can switch back anytime.");
        }
        builder.setPositiveButton("Switch", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Just a moment...");
                progressDialog.setTitle("Switching mode");
                progressDialog.setCancelable(false);
                progressDialog.show();
                DocumentReference userReference = mFirestore.collection("users").document(phoneNumber);
                userReference.update("preferGeneral",!preferGeneral).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        editor.putBoolean(getString(R.string.preferGeneral),!preferGeneral);
                        progressDialog.dismiss();
                        startActivity(new Intent(getActivity(), MainActivity.class));
                        getActivity().finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Failed to switch mode", Toast.LENGTH_SHORT).show();
                        startActivity(getActivity().getIntent());
                        getActivity().finish();
                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                startActivity(getActivity().getIntent());
                getActivity().finish();
            }
        });

        builder.show();
        return root;
    }
}
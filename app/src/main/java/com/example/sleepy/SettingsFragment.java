package com.example.sleepy;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsFragment extends Fragment {
    private View view;
    private Button cameraButton;
    Button btn_logout;
    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    GoogleSignInClient mGoogleSignInClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        //logout
        btn_logout = view.findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signout();
            }
        });
        //camera
        cameraButton = view.findViewById(R.id.CameraButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),CameraActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    // logout
    private void signout() {
        mFirebaseAuth.signOut();
        startActivity(new Intent(getActivity(), LoginActivity.class));
        //startActivity(new Intent(getActivity(), MainActivity.class));
    }
}
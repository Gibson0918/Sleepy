package com.example.sleepy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SettingsFragment extends Fragment {
    private View view;
    private Button cameraButton;
    Button btn_logout;
    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userEmail = currentUser.getEmail();
    //private GoogleSignInClient mGoogleSignInClient;
    SharedPreferences sharedpreferences;
    public static final String Email = "emailKey";// creating constant keys for shared preferences.
    public static final String SHARED_PREFS = "shared_prefs";
    String email;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        sharedpreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        email = sharedpreferences.getString(Email, null);
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
        if (isConnected()) {
            cameraButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.collection(userEmail).document("Embeddings").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Intent intent = new Intent(getActivity(), AddFaceEmbedding.class);
                            startActivity(intent);
                        }
                    });
                }
            });
        } else {
            Toast.makeText(getContext(), "Please try again! Ensure that wifi is enabled!", Toast.LENGTH_LONG).show();
        }
        return view;

    }

    // logout
    private void signout() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
        editor.commit();

        mFirebaseAuth.signOut();

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id2))
                .requestEmail()
                .build();

        GoogleSignInClient googleClient = GoogleSignIn.getClient(this.getActivity(), options);

        googleClient.signOut();
        startActivity(new Intent(getActivity(), LoginActivity.class));
        //startActivity(new Intent(getActivity(), MainActivity.class));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetwork() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

}
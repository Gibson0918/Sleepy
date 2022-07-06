package com.example.sleepy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private SignInButton googleSignInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    int RC_SIGN_IN = 10;

    SharedPreferences sharedpreferences;

    public static final String Email = "emailKey";// creating constant keys for shared preferences.
    public static final String SHARED_PREFS = "shared_prefs";
    String sharedemail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        sharedemail = sharedpreferences.getString(Email, null);


        googleSignInButton = findViewById(R.id.sign_in_button);
        //Toast.makeText(this, getUserName(), Toast.LENGTH_SHORT).show();
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id2))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                e.printStackTrace();
                Log.w("signInError", e.toString());
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(this, authResult -> {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection(getusermail()).document("Alarms");
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser currUser = firebaseAuth.getCurrentUser();
                    assert currUser != null;
                    String emailAddr = currUser.getEmail();
                    DocumentReference dbRef = FirebaseFirestore.getInstance().collection(emailAddr).document("Embeddings");
                    dbRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()) {

                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.clear();
                                    editor.putString(Email, emailAddr);
                                    editor.apply();
                                    editor.commit();

                                    Log.e("email", emailAddr );

                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(LoginActivity.this);
                                    builder.setTitle("Facial Recognition required!");
                                    builder.setMessage("Please proceed to add your face!");
                                    builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //Intent intent = new Intent(LoginActivity.this, AddFaceEmbedding.class);

                                            //remove afterwards
                                            SharedPreferences.Editor editor = sharedpreferences.edit();
                                            editor.clear();
                                            editor.putString(Email, emailAddr);
                                            editor.apply();
                                            editor.commit();

                                            Log.e("email", emailAddr );

                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                    builder.show();
                                }
                            }
                        }
                    });
                })
                .addOnFailureListener(this, e -> Toast.makeText(LoginActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show());
    }

    private String getusermail() {
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if(user != null) {
            return  user.getEmail();
        }
        return "ANONYMOUS" ;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(sharedemail != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
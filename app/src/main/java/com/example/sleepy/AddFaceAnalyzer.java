package com.example.sleepy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AddFaceAnalyzer extends AddFaceEmbedding {

    private FirebaseVisionImage fbImage;
    private FaceNet faceNet;
    private Activity context;
    private Button addBtn;


    public AddFaceAnalyzer(FaceNet faceNet, Button addBtn, Activity context) {
        this.faceNet = faceNet;
        this.context = context;
        this.addBtn = addBtn;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initDetector(ImageProxy image) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //Initialize the face detector
        FirebaseVisionFaceDetectorOptions detectorOptions = new FirebaseVisionFaceDetectorOptions
                .Builder()
                .enableTracking()
                .build();
        FirebaseVisionFaceDetector faceDetector = FirebaseVision.getInstance().getVisionFaceDetector(detectorOptions);
        //retrieve a list of faces detected (firebaseVisionFaces) and perform facial recognition
        faceDetector.detectInImage(fbImage).addOnSuccessListener(firebaseVisionFaces -> {

            if (firebaseVisionFaces.size() == 1) {
                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseVisionFace face = firebaseVisionFaces.get(0);
                        Bitmap croppedFaceBitmap = getFaceBitmap(face);
                        if(croppedFaceBitmap != null) {
                            faceNet.addFace(currentUser.getDisplayName(), croppedFaceBitmap, db, currentUser.getEmail());
                            Intent intent = new Intent(context, MainActivity.class);
                            context.startActivity(intent);
                            context.finish();
                        }
                    }
                });
            }
        }).addOnCompleteListener(new OnCompleteListener<List<FirebaseVisionFace>>() {
            @Override
            public void onComplete(@NonNull Task<List<FirebaseVisionFace>> task) {
                image.close();
            }
        });

    }

    private Bitmap getFaceBitmap(FirebaseVisionFace face) {
        Bitmap originalFrame = fbImage.getBitmap();
        Bitmap faceBitmap = null;
        try {
            faceBitmap = Bitmap.createBitmap(originalFrame, face.getBoundingBox().left, face.getBoundingBox().top, face.getBoundingBox().right - face.getBoundingBox().left, face.getBoundingBox().bottom - face.getBoundingBox().top);
        } catch (IllegalArgumentException e) {
        }
        return faceBitmap;
    }

    private int degreesToFirebaseRotation(int degrees) {
        switch (degrees) {
            case 0:
                return FirebaseVisionImageMetadata.ROTATION_0;
            case 90:
                return FirebaseVisionImageMetadata.ROTATION_90;
            case 180:
                return FirebaseVisionImageMetadata.ROTATION_180;
            case 270:
                return FirebaseVisionImageMetadata.ROTATION_270;
            default:
                throw new IllegalArgumentException("Rotation must be 0, 90, 180, or 270.");
        }
    }
}

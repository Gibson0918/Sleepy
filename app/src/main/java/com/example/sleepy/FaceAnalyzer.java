package com.example.sleepy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

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

public class FaceAnalyzer extends CameraActivity implements ImageAnalysis.Analyzer {

    private FirebaseVisionImage fbImage;
    private FaceNet faceNet;
    private FaceRecognition faceRecognition;
    private Activity context;
    private Button addBtn;

    public FaceAnalyzer(FaceNet faceNet, FaceRecognition faceRecognition, Activity context) {
        this.faceNet = faceNet;
        this.faceRecognition = faceRecognition;
        this.context = context;
    }

    public FaceAnalyzer(FaceNet faceNet, Button addBtn, Activity context) {
        this.faceNet = faceNet;
        this.context = context;
        this.addBtn = addBtn;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void analyze(ImageProxy image, int rotationDegrees) {
        if (image == null || image.getImage() == null) {
            return;
        }
        int rotation = degreesToFirebaseRotation(rotationDegrees);
        fbImage = FirebaseVisionImage.fromMediaImage(image.getImage(), rotation);
        initDetector(image);
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
            if (addBtn == null && firebaseVisionFaces.size() == 1) {
                //comparing face embeddings here
                new Thread(() -> {
                    try {
                        Boolean result = processFaces(firebaseVisionFaces);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (result) {
                                    Toast.makeText(context, "User verified!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(context, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    context.startActivity(intent);
                                    context.finish();
                                }
                            }
                        });
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();

            }

            if (addBtn != null && firebaseVisionFaces.size() == 1) {
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
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private synchronized boolean processFaces(List<FirebaseVisionFace> faces) throws ExecutionException, InterruptedException {
        Bitmap croppedFaceBitmap = getFaceBitmap((faces.get(0)));
        if (croppedFaceBitmap == null) {
            return false;
        }
        else {
            Future<Boolean> faceRecognitionFuture = faceNet.recognizeFace(croppedFaceBitmap, faceRecognition);
            return faceRecognitionFuture.get();
        }
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

package com.example.sleepy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.camera2.Camera2Config;
import androidx.camera.core.CameraInfoUnavailableException;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Size;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddFaceEmbedding extends AppCompatActivity {

    private int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.RECORD_AUDIO"};
    private TextureView textureView;
    private FaceNet faceNet;
    private CameraX.LensFacing lens = CameraX.LensFacing.FRONT;
    private Button addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_face_embedding);
        bindDisplayItem();

        // <<Load the facial recognition model  >>
        faceNet = null;
        try {
            faceNet = new FaceNet(getAssets());
        } catch (Exception e) {
            Toast.makeText(AddFaceEmbedding.this, "Model not loaded successfully", Toast.LENGTH_SHORT).show();
        }

        //check permission for camera
        if (allPermissionsGranted()) {
            textureView.post(this::startCamera); //start camera if permission has been granted by user
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    //remove the loaded face recognition model from memory
    @Override
    protected void onDestroy() {
        super.onDestroy();
        faceNet.close();
    }

    @SuppressLint("RestrictedApi")
    private void startCamera() {
        try {
            // Only bind use cases if we can query a camera with this orientation
            CameraX.getCameraWithLensFacing(lens);
            initCamera();
        } catch (CameraInfoUnavailableException e) {
            e.printStackTrace();
        }
    }

    //method to start up camera and set up camera preview
    private void initCamera() {
        CameraX.unbindAll();

        @SuppressLint("RestrictedApi") PreviewConfig.Builder pc = new PreviewConfig
                .Builder()
                .setTargetResolution(new Size(textureView.getWidth(), textureView.getHeight()))
                .setLensFacing(lens);

        Preview preview = new Preview(pc.build());
        preview.setOnPreviewOutputUpdateListener(output -> {
            ViewGroup vg = (ViewGroup) textureView.getParent();
            vg.removeView(textureView);
            vg.addView(textureView, 0);
            textureView.setSurfaceTexture(output.getSurfaceTexture());
        });

        ImageAnalysisConfig imageAnalysisConfig = new ImageAnalysisConfig
                .Builder()
                .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
                .setTargetResolution(new Size(textureView.getWidth(),textureView.getHeight()))
                .setLensFacing(lens).build();

        ImageAnalysis imageAnalysis = new ImageAnalysis(imageAnalysisConfig);
        imageAnalysis.setAnalyzer(Runnable::run,
                new FaceAnalyzer(faceNet, addBtn, AddFaceEmbedding.this));
        CameraX.bindToLifecycle(this, preview, imageAnalysis);
    }

    private boolean allPermissionsGranted(){
        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }

    private void bindDisplayItem() {
        textureView = findViewById(R.id.textureView);
        addBtn = findViewById(R.id.addBtn);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AddFaceEmbedding.this);
        builder.setTitle("Facial recognition!");
        builder.setMessage("Tap on the camera button to capture your face!");
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Do Nothing
            }
        });
        builder.show();
    }
}
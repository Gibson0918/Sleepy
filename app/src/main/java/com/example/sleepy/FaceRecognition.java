package com.example.sleepy;

import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

public class FaceRecognition {

    @PropertyName("Embeddings")
    private  float [][] Embedding;
    @PropertyName("Name")
    private String Name;

    public FaceRecognition() {}

    public FaceRecognition(String Name, @Nullable float[][] Embedding) {
        this.Name = Name;
        this.Embedding = Embedding;
    }

    public String getName() {
        return Name;
    }

    public float[][] getEmbedding() {
        return Embedding;
    }

}
package com.example.sleepy;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

import java.util.List;

//This class is simply used to retrieve details from Firestore
public class Face {

    @PropertyName("Embeddings")
    private List<Float> Embeddings;
    @PropertyName("Name")
    private String Name;

    public Face() {}

    public Face(String Name, List<Float> Embeddings) {
        this.Name = Name;
        this.Embeddings = Embeddings;
    }

    @PropertyName("Embeddings")
    public List<Float> getEmbeddings() {
        return Embeddings;
    }

    @PropertyName("Name")
    public String getName() {
        return Name;
    }

}


package com.example.smombie

import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File

class QuestionDownloader {
    private val storageRef = Firebase.storage.reference
    // Create a child reference
    // imagesRef now points to "images"
    var csvRef: StorageReference? = storageRef.child("Question_storage.csv")

    //todo:ダウンロードの処理書く
    fun showFileDetail(){
        Log.d("STORAGE","${csvRef?.path}_${csvRef?.name}")
        val localFile = File.createTempFile("ccc.csv","csv")
        csvRef?.getFile(localFile)?.addOnSuccessListener {
            // Local temp file has been created
            Log.d("STORAGE","$it")
        }?.addOnFailureListener {
            // Handle any errors
            Log.d("STORAGE","$it")
        }
    }

}
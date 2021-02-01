package com.example.smombie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

class MainActivity : AppCompatActivity() {
    private val TAG = this::class.java.simpleName
    private lateinit var startBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startBtn = findViewById(R.id.start_btn)

        Metadata.setLabel("oh my got!!")
        
        Log.d(TAG,Metadata.getLabel())
        startBtn.setOnClickListener{
            val measurementIntent = Intent(applicationContext, MeasurementActivity::class.java)
            startActivity(measurementIntent)
        }

    }

}
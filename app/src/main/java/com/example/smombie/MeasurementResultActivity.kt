package com.example.smombie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MeasurementResultActivity : AppCompatActivity() {
    private val TAG = this::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measurement_result)

        val resultData = intent.getStringArrayExtra("resultData")
        Log.d(TAG, "$resultData")
    }
}
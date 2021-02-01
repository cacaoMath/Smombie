package com.example.smombie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.opencsv.CSVIterator
import com.opencsv.CSVReader
import java.io.StringReader

class MeasurementActivity : AppCompatActivity() {
    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measurement)
        Log.d(TAG,Metadata.getLabel()+"2")
    }


}
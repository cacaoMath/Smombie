package com.example.smombie

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import java.time.LocalDateTime

class MeasurementResultActivity : AppCompatActivity() {
    private val TAG = this::class.java.simpleName
    private var dataManager : DataFileManager? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measurement_result)

        val rightData = intent.extras?.getString("RightData") ?:""
        val answeredData = intent.extras?.getString("AnsweredData") ?:""
        val answerTimeData = intent.extras?.getString("AnswerTimeData") ?:""
        Log.d(TAG, "$rightData")

        //データをcsvに保存
        dataManager = DataFileManager(this.applicationContext)
        dataManager?.saveData("${answeredData},${rightData},${answerTimeData},${Metadata.getNote()},${Metadata.getPattern()},${Metadata.getLabel()},${LocalDateTime.now()}")
    }
}
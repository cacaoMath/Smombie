package com.example.smombie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MeasurementResultActivity : AppCompatActivity() {
    private val TAG = this::class.java.simpleName
    private var dataManager : DataFileManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measurement_result)

        val rightData = intent.extras?.getString("RightData") ?:""
        val answeredData = intent.extras?.getString("AnsweredData") ?:""
        val answerTimeData = intent.extras?.getString("AnswerTimeData") ?:""
        Log.d(TAG, "$rightData")

        //データをcsvに保存
        dataManager = DataFileManager(this.applicationContext)
        dataManager?.saveData("${answeredData},${rightData},${answerTimeData},${Metadata.getNote()},${Metadata.getPattern()},${Metadata.getLabel()}")
    }
}
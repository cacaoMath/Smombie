package com.example.smombie

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.annotation.RequiresApi
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime

class MeasurementResultActivity : AppCompatActivity() {
    private val TAG = this::class.java.simpleName
    private var dataManager: DataFileManager? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measurement_result)

        val finishBtn: Button = findViewById(R.id.finishBtn)
        val restartBtn: Button = findViewById(R.id.restartBtn)

        val rightData = intent.extras?.getString("RightData") ?: ""
        val answeredData = intent.extras?.getString("AnsweredData") ?: ""
        val answerTimeData = intent.extras?.getString("AnswerTimeData") ?: ""
        Log.d(TAG, "$rightData")

        //データをcsvに保存
        dataManager = DataFileManager(this.applicationContext)
        runBlocking {
            dataManager?.saveData("${answeredData},${rightData},${answerTimeData},${Metadata.getNote()},${Metadata.getPattern()},${Metadata.getLabel()},${LocalDateTime.now()}")
            Log.d("Block", "fileBlock")
        }


        restartBtn.setOnClickListener {
            val measurementIntent = Intent(applicationContext, MeasurementActivity::class.java)
            startActivity(measurementIntent)
            finish()
        }

        finishBtn.setOnClickListener {
            val mainIntent = Intent(applicationContext, MainActivity::class.java)
            //メタデータをリセットする
            Metadata.resetValues()
            Log.d("Block", "finish")
            startActivity(mainIntent)
            finish()
        }

    }
}
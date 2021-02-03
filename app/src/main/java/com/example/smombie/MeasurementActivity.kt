package com.example.smombie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.opencsv.CSVIterator
import com.opencsv.CSVReader
import com.opencsv.bean.CsvToBeanBuilder
import java.io.*

class MeasurementActivity : AppCompatActivity() {
    private val TAG = this::class.java.simpleName
    private lateinit var questionList : MutableList<Question>   //出題する問題のリスト

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measurement)

        questionList = questionReader(getAssets().open("test.csv"))

        Log.d(TAG,Metadata.getLabel()+"2")
        
        Log.d(TAG, "" + questionList)
    }

    //csvから問題を読み取り，リストにする
    private fun questionReader(fileName : InputStream):MutableList<Question>{
        var questionList = mutableListOf<Question>()
        try{
            val csvStream = BufferedReader(InputStreamReader(fileName))
            val beanList = CsvToBeanBuilder<Question>(csvStream)
                .withType(Question::class.java)
                .build()
                .parse()

            for (q in beanList) {
                questionList.add(q)
            }
        }catch(e :Exception){
            Log.d(TAG,"ファイルがないよ！！")
        }
        return questionList
    }


}
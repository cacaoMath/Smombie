package com.example.smombie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.opencsv.CSVIterator
import com.opencsv.CSVReader
import com.opencsv.bean.CsvToBeanBuilder
import java.io.BufferedReader
import java.io.FileReader
import java.io.InputStreamReader
import java.io.StringReader

class MeasurementActivity : AppCompatActivity() {
    private val TAG = this::class.java.simpleName
    private val questionList = questionReader()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measurement)
        Log.d(TAG,Metadata.getLabel()+"2")
        
        Log.d(TAG, "" +questionReader())
    }

    fun questionReader():MutableList<Question>{
            var questionList = mutableListOf<Question>()
            try{
                val csvStream = BufferedReader(InputStreamReader(getAssets().open("test.csv")))
                val beanList = CsvToBeanBuilder<Question>(csvStream)
                    .withType(Question::class.java)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse()

                for (q in beanList) {
                    questionList.add(q)
                }
            }catch(e :Exception){
                Log.d(TAG,"e.toString()")
            }
            return questionList
        }


}
package com.example.smombie

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.opencsv.bean.CsvToBeanBuilder
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {
    private val TAG = this::class.java.simpleName
    private lateinit var startBtn: Button
    private lateinit var labelSpinner: Spinner
    private lateinit var questionSpinner: Spinner
    private lateinit var labelList: Array<String>
    private lateinit var patternList: Array<String>
    private lateinit var noteEt: EditText


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startBtn = findViewById(R.id.start_btn)
        labelSpinner = findViewById(R.id.labelSpn)
        questionSpinner = findViewById(R.id.qstSpn)
        noteEt = findViewById(R.id.etMeta)


        //spinnerの動作用設定
        labelSpinner.isFocusable = false
        questionSpinner.isFocusable = false
        labelList = resources.getStringArray(R.array.labelList)
        patternList = resources.getStringArray(R.array.patternList)
        labelSpinner.onItemSelectedListener = SpinnerActivity(labelList)
        questionSpinner.onItemSelectedListener = SpinnerActivity(patternList)

        //csvから問題を取り込んでおく
        Metadata.setAllQuestionList(questionReader("JLPT_questions.csv"))


        Log.d(TAG, Metadata.getLabel())
        startBtn.setOnClickListener {
            Metadata.setNote(noteEt.text?.toString() ?: "")

            //メタデータを何かしら選択したか
            if (Metadata.getLabel() != "" && Metadata.getPattern() != "") {
                val measurementIntent = Intent(applicationContext, MeasurementActivity::class.java)
                startActivity(measurementIntent)
            } else {
                Toast.makeText(applicationContext, "メタデータを入力してください", Toast.LENGTH_LONG).show()
            }

        }

        //todo:今後firebase storageから問題をダウンロードして使えるようにする用
        val qdl = QuestionDownloader()
        //qdl.showFileDetail()

    }

    //csvから問題を読み取り，リストにする
    private fun questionReader(fileName: String): MutableList<Question> {
        val questionList = mutableListOf<Question>()
        try {
            val csvStream = BufferedReader(InputStreamReader(assets.open(fileName)))
            Log.d(TAG, "${assets.open(fileName)}")
            val beanList = CsvToBeanBuilder<Question>(csvStream)
                .withType(Question::class.java)
                .build()
                .parse()

            for (q in beanList) {
                questionList.add(q)
            }

        } catch (e: Exception) {
            Log.d(TAG, "ファイルが存在しません")
        }
        return questionList
    }

    //オプションメニュー表示用
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_option, menu)

        return super.onCreateOptionsMenu(menu)
    }

    //オプションメニュー選択時処理
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settingMenu -> {
                val settingIntent = Intent(applicationContext, SettingActivity::class.java)
                startActivity(settingIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //スピナーの動きを決めるやつ
    class SpinnerActivity(list: Array<String>) : Activity(), AdapterView.OnItemSelectedListener {

        private val TAG = this::class.java.simpleName
        private var arrayList: Array<String>? = null

        init {
            this.arrayList = list
        }

        override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {

            //アクティビティ起動時に値がとられないようにする．
            if (!parent.isFocusable) {
                parent.isFocusable = true
                return
            }
            when (parent.id) {
                R.id.labelSpn -> {
                    Log.d(TAG, "${arrayList?.get(id.toInt())}")
                    arrayList?.get(id.toInt())?.let { Metadata.setLabel(it) }
                }
                R.id.qstSpn -> {
                    Log.d(TAG, "${arrayList?.get(id.toInt())}")
                    arrayList?.get(id.toInt())?.let { Metadata.setPattern(it) }
                }
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
            // Another interface callback
        }
    }

}
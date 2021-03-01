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

class MainActivity : AppCompatActivity() {
    private val TAG = this::class.java.simpleName
    lateinit var startBtn: Button
    lateinit var labelSpinner: Spinner
    lateinit var questionSpinner: Spinner
    lateinit var labelList: Array<String>
    lateinit var patternList: Array<String>
    lateinit var noteEt: EditText


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

        
        Log.d(TAG,Metadata.getLabel())
        startBtn.setOnClickListener{
            Metadata.setNote(noteEt.text?.toString() ?: "")
            
            if(!labelSpinner.isFocusable || !questionSpinner.isFocusable ){
                val measurementIntent = Intent(applicationContext, MeasurementActivity::class.java)
                startActivity(measurementIntent)
            }else{
                Toast.makeText(applicationContext,"メタデータを入力してください", Toast.LENGTH_LONG)
            }

        }

    }


    //オプションメニュー表示用
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_option, menu)

        return super.onCreateOptionsMenu(menu)
    }

    //オプションメニュー選択時処理
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
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
        private var arrayList : Array<String>? = null

        init {
            this.arrayList = list
        }
        override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {

            if(!parent.isFocusable) {
                parent.isFocusable = true
                return
            }
            when(parent.id){
                R.id.labelSpn ->{
                    Log.d(TAG,"${arrayList?.get(id.toInt())}")
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
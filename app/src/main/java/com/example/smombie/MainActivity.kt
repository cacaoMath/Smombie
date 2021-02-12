package com.example.smombie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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

}
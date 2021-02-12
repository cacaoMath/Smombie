package com.example.smombie

import android.content.Context
import android.os.Environment
import android.util.Log
import com.opencsv.CSVWriter
import java.io.File
import java.io.StringWriter
import java.nio.charset.StandardCharsets
import java.util.*

class DataFileManager(context: Context) {
    private val TAG = this::class.java.simpleName
    private var dataFile: File? = null
    private var context :Context? = null

    init{
        this.context = context
    }

    fun saveData(dataStr : String){
        checkFile()
        dataFile?.appendText("$dataStr,\n", StandardCharsets.UTF_8)
    }

    private fun checkFile(){
        val dataDir = File(this.context?.getExternalFilesDir(null), "measuredData")

        if(!isExternalStorageWritable()){
            Log.d(TAG,"ストレージ使用不可")
            return
        }
        if(!(dataDir.exists() and dataDir.isDirectory)){
            if(!dataDir.mkdir()) return
        }

        dataFile = File(dataDir, "measurement_${System.currentTimeMillis()}.csv")
    }

    //ストレージが使用可能かをチェック
    private fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }
}
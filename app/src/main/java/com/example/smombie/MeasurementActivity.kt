package com.example.smombie

import Sensing
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT
import com.opencsv.bean.CsvToBeanBuilder
import java.io.*

class MeasurementActivity : AppCompatActivity() {
    private val TAG = this::class.java.simpleName

    //センシング用
    lateinit var sensing: Sensing

    //選択肢のボタン
    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button
    private lateinit var btn4: Button

    //回答時間記録用
    private var ansStartTime : Long = 0

    //結果(正答番号)データ保持用
    private var rightData  = mutableListOf<String>()

    //回答した問題番号保存用
    private var answeredData  = mutableListOf<String>()

    //各問題の回答時間
    private var answerTimeData = mutableListOf<Long>()

    //問題文表示用
    private lateinit var qTv: TextView
    //問題をランダム順に処理したもの
    private lateinit var shuffledQList: MutableList<Question>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measurement)

        //センシング
        sensing = Sensing(this, Metadata.getLabel() )

        //全問題文のリスト
        val questionList = questionReader("Question_p.csv")

        btn1 = findViewById<Button>(R.id.button1)
        btn2 = findViewById<Button>(R.id.button2)
        btn3 = findViewById<Button>(R.id.button3)
        btn4 = findViewById<Button>(R.id.button4)
        qTv = findViewById<TextView>(R.id.questinoTv)

        //選択肢ボタンのリスナー
        val selectBtnListener = SelectButtonListener()
        btn1.setOnClickListener(selectBtnListener)
        btn2.setOnClickListener(selectBtnListener)
        btn3.setOnClickListener(selectBtnListener)
        btn4.setOnClickListener(selectBtnListener)

        Log.d(TAG,Metadata.getLabel())


        if(questionList.isNotEmpty()){
            shuffledQList = questionList.shuffled().toMutableList()

            //センシングの開始
            sensing.start(Metadata.getLabel())

            //初回の問題，ボタン選択肢表示用
            setSelectBtnAns(shuffledQList[0])
            setQuestion(shuffledQList[0])
        }else{
            //手違いでassets内にcsvがないときの処理
            Log.d(TAG,"Question-> $questionList")
        }


    }

    private fun getTime(): Long {
        return System.currentTimeMillis()
    }

    //問題の選択肢を4つのボタンにランダムに設定する
    private fun setSelectBtnAns(question : Question){
        val btnList = mutableListOf(btn1,btn2,btn3,btn4).shuffled()

        btnList[0].text = "${question.correctAns}"
        btnList[1].text = "${question.incorrectAns1}"
        btnList[2].text = "${question.incorrectAns2}"
        btnList[3].text = "${question.incorrectAns3}"
    }

    //問題文表示
    fun setQuestion(question : Question) {
        qTv.text = HtmlCompat.fromHtml("${question.question}", FROM_HTML_MODE_COMPACT)
        //問題文を出したタイミングを回答時間のスタートとする
        ansStartTime = getTime()
    }

    //csvから問題を読み取り，リストにする
    private fun questionReader(fileName : String):MutableList<Question>{
        var questionList = mutableListOf<Question>()
        try{
            val csvStream = BufferedReader(InputStreamReader(assets.open(fileName)))
            Log.d(TAG,"${assets.open(fileName)}")
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

    private inner class SelectButtonListener: View.OnClickListener {
        override fun onClick(view: View){
            //回答時間を求める
            val answerTime = getTime() - ansStartTime


            //ユーザが選択した答え
            val selectText = findViewById<Button>(view.id).text
            //正解の答え
            val correctText = shuffledQList[0].correctAns

            //出題した問題番号
            answeredData.add("${shuffledQList[0].number}")
            //各問題の回答時間を保存
            answerTimeData.add(answerTime)
            //正誤判定
            if(correctText == selectText){
                Log.d(TAG,"正解!!")
                rightData.add("${shuffledQList[0].number}")
                Log.d(TAG,"$rightData")
            }else{
                Log.d(TAG, "残念!!${correctText}です．")
            }

            shuffledQList.removeAt(0)
            if(shuffledQList.isEmpty()){
                Log.d(TAG,"終了！！")
                //計測結果画面へ移動
                val measurementResultIntent = Intent(applicationContext, MeasurementResultActivity::class.java)
                measurementResultIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                //センシング終了
                sensing.stop()

                //次のアクティビティデータを引き継ぐ
                // 回答した問題番号（回答内容も？）,正答した問題番号,それぞれの回答時間，ユーザーID，問題パターン
                measurementResultIntent.putExtra("RightData", rightData.joinToString(prefix = "\"", postfix = "\""))
                measurementResultIntent.putExtra("AnsweredData", answeredData.joinToString(prefix = "\"", postfix = "\""))
                measurementResultIntent.putExtra("AnswerTimeData", answerTimeData.joinToString(prefix = "\"", postfix = "\""))

                startActivity(measurementResultIntent)
            }else{
                setQuestion(shuffledQList[0])
                setSelectBtnAns(shuffledQList[0])
            }


        }
    }


}
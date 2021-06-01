package com.example.smombie

import Sensing
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime

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
    private var ansStartTime: Long = 0

    //結果(正答番号)データ保持用
    private var rightData = mutableListOf<String>()

    //回答した問題番号保存用
    private var answeredData = mutableListOf<String>()

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
        sensing = Sensing(this, Metadata.getLabel())

        //全問題文のリスト,csvから取り込む
        val allQuestionList = Metadata.getAllQuestionList()
        Log.d(TAG, "${allQuestionList.size}")
        //選択パターン分の問題リスト
        val questionList = setQuestionPattern(
            allQuestionList,
            Metadata.getPattern()
        )//todo:ここでセットしてると処理により空白の時間が生まれるため，上手く処理を変えたい．

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

        Log.d(TAG, Metadata.getLabel())


        if (questionList.isNotEmpty()) {
            shuffledQList = questionList.shuffled().toMutableList()

            //センシングの開始
            sensing.start(Metadata.getLabel())

            //初回の問題，ボタン選択肢表示用
            setSelectBtnAns(shuffledQList[0])
            setQuestion(shuffledQList[0])
        } else {
            //手違いでassets内にcsvがないときの処理
            Log.d(TAG, "Question-> $questionList")
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("確認")
            .setMessage("計測を終了しますか")
            .setPositiveButton("はい") { dialog, id ->
                dataSave()
                finish()
            }
            .setNegativeButton("いいえ") { dialog, id ->
            }.show()

    }

    private fun getTime(): Long {
        return System.currentTimeMillis()
    }

    //問題の選択肢を4つのボタンにランダムに設定する
    private fun setSelectBtnAns(question: Question) {
        val btnList = mutableListOf(btn1, btn2, btn3, btn4).shuffled()

        btnList[0].text = "${question.correctAns}"
        btnList[1].text = "${question.incorrectAns1}"
        btnList[2].text = "${question.incorrectAns2}"
        btnList[3].text = "${question.incorrectAns3}"
    }

    //問題文表示
    fun setQuestion(question: Question) {
        qTv.text = HtmlCompat.fromHtml("${question.question}", FROM_HTML_MODE_COMPACT)
        //問題文を出したタイミングを回答時間のスタートとする
        ansStartTime = getTime()
    }


    //メタデータに入力された問題パターンに従って，出題する問題を出力
    private fun setQuestionPattern(
        allQuestionList: MutableList<Question>,
        pattern: String
    ): MutableList<Question> {
        val questionList: MutableList<Question>
        when (pattern) {
            "A" -> {
                questionList = allQuestionList.filter {
                    (it.number.toInt() in 1..50)
                }.toMutableList()
            }
            "B" -> {
                questionList = allQuestionList.filter {
                    (it.number.toInt() in 51..100)
                }.toMutableList()
            }
            "C" -> {
                questionList = allQuestionList.filter {
                    (it.number.toInt() in 101..150)
                }.toMutableList()
            }
            "D" -> {
                questionList = allQuestionList.filter {
                    (it.number.toInt() in 151..200)
                }.toMutableList()
            }
            else -> {
                questionList = allQuestionList.filter {
                    (it.number.toInt() in 201..allQuestionList.size)
                }.toMutableList()
            }
        }
        return questionList
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun dataSave() {
        var dataManager: DataFileManager? = null
        //データをcsvに保存
        dataManager = DataFileManager(this.applicationContext)
        runBlocking {
            dataManager?.saveData("${answeredData},${rightData},${answerTimeData},${Metadata.getNote()},${Metadata.getPattern()},${Metadata.getLabel()},${LocalDateTime.now()}")
            Log.d("Block", "fileBlock")
        }
    }

    private inner class SelectButtonListener : View.OnClickListener {
        //ボタン2重連打防止用
        private var isBtnEventEnable = true
        override fun onClick(view: View) {
            if (!isBtnEventEnable) return
            else {
                isBtnEventEnable = false
                view.postDelayed({
                    isBtnEventEnable = true
                }, 400L)
            }

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
            if (correctText == selectText) {
                Log.d(TAG, "正解!!")
                rightData.add("${shuffledQList[0].number}")
                Log.d(TAG, "$rightData")
            } else {
                Log.d(TAG, "残念!!${correctText}です．")
            }

            shuffledQList.removeAt(0)
            if (shuffledQList.isEmpty()) {
                Log.d(TAG, "終了！！")
                //計測結果画面へ移動
                val measurementResultIntent =
                    Intent(applicationContext, MeasurementResultActivity::class.java)
                measurementResultIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                //センシング終了
                sensing.stop()

                //次のアクティビティデータを引き継ぐ
                // 回答した問題番号（回答内容も？）,正答した問題番号,それぞれの回答時間，ユーザーID，問題パターン
                measurementResultIntent.putExtra(
                    "RightData",
                    rightData.joinToString(prefix = "\"", postfix = "\"")
                )
                measurementResultIntent.putExtra(
                    "AnsweredData",
                    answeredData.joinToString(prefix = "\"", postfix = "\"")
                )
                measurementResultIntent.putExtra(
                    "AnswerTimeData",
                    answerTimeData.joinToString(prefix = "\"", postfix = "\"")
                )

                startActivity(measurementResultIntent)
            } else {
                setQuestion(shuffledQList[0])
                setSelectBtnAns(shuffledQList[0])
            }


        }
    }


}
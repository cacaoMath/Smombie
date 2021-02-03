package com.example.smombie

import com.opencsv.bean.CsvBindByName

data class Question(
    @CsvBindByName(column = "問題番号", required = true)
    val num: String = "",

    @CsvBindByName(column = "問題文", required = true)
    val question: String = "",

    @CsvBindByName(column = "正解", required = true)
    val collectAns: String = "",

    @CsvBindByName(column = "回答1", required = true)
    val otherAns1: String = "",

    @CsvBindByName(column = "回答2", required = true)
    val otherAns2: String = "",

    @CsvBindByName(column = "回答3", required = true)
    val otherAns3: String = ""

)

package com.example.smombie

import com.opencsv.bean.CsvBindByName

data class Question(
        @CsvBindByName(column = "NUM", required = true)
    val number: String = "",

        @CsvBindByName(column = "QUESTION", required = true)
    val question: String = "",

        @CsvBindByName(column = "CORRECT_ANSWER", required = true)
    val correctAns: String = "",

        @CsvBindByName(column = "INCORRECT_ANSWER1", required = true)
    val incorrectAns1: String = "",

        @CsvBindByName(column = "INCORRECT_ANSWER2", required = true)
    val incorrectAns2: String = "",

        @CsvBindByName(column = "INCORRECT_ANSWER3", required = true)
    val incorrectAns3: String = ""

)

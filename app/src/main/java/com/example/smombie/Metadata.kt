package com.example.smombie

object Metadata {
    private var label  = ""
    private var pattern  = ""
    private var note  = ""

    fun resetValues(){
        this.label = ""
        this.pattern = ""
        this.note = ""
    }

    fun setLabel(txt : String){
        this.label = txt
    }

    fun getLabel(): String{
        return this.label
    }

    fun setPattern(txt : String){
        this.pattern = txt
    }

    fun getPattern(): String{
        return this.pattern
    }

    fun setNote(txt : String){
        this.note = txt
    }

    fun getNote(): String{
        return this.note
    }

}
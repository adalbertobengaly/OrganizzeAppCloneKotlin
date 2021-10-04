package com.android.my.organizze.helper

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

object DateCustom {

    @SuppressLint("SimpleDateFormat")
    fun dataAtual(): String {
        val data = System.currentTimeMillis()
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        return simpleDateFormat.format(data)
    }

    fun mesAnoDataEscolhida(data: String): String {
        val retornoData = data.split("/")
        val mes = retornoData[1] //mês
        val ano = retornoData[2] //ano

        return "$mes$ano"
    }

}
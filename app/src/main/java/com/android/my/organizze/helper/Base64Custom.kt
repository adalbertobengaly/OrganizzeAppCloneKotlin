package com.android.my.organizze.helper

import android.util.Base64

object Base64Custom {
    fun codificarBase64(texto: String): String {
        return Base64.encodeToString(texto.encodeToByteArray() , Base64.NO_WRAP )
    }

    fun decodificarBase64(textoCodificado: String): String {
        return Base64.decode(textoCodificado, Base64.DEFAULT).toString()
    }
}
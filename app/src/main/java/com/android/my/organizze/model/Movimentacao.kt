package com.android.my.organizze.model

import com.android.my.organizze.helper.Base64Custom
import com.android.my.organizze.helper.ConfiguracaoFirebase
import com.android.my.organizze.helper.DateCustom

class Movimentacao {

    var data: String = ""
    var categoria: String = ""
    var descricao: String = ""
    var tipo: String = ""
    var valor: Double = 0.00
    var key: String = ""

    fun salvar( dataEscolhida: String ) {
        val autentificacao = ConfiguracaoFirebase.getFirebaseAutentification()
        val idUsuario = Base64Custom.codificarBase64( autentificacao.currentUser?.email!! )
        val mesAno = DateCustom.mesAnoDataEscolhida( dataEscolhida )
        val firebase = ConfiguracaoFirebase.getFirebaseDatabase()
        firebase.child( "movimentacao" )
            .child( idUsuario )
            .child( mesAno )
            .push()
            .setValue( this )
    }

}
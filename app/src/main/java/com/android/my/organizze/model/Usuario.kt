package com.android.my.organizze.model

import com.android.my.organizze.helper.ConfiguracaoFirebase
import com.google.firebase.database.Exclude

class Usuario {
    var nome: String = ""
    var email: String = ""
    var receitaTotal: Double = 0.00
    var despesaTotal: Double = 0.00
    @get:Exclude var idUsuario: String = ""
    @get:Exclude var senha: String = ""

    fun salvar() {
        val firebase = ConfiguracaoFirebase.getFirebaseDatabase()
        firebase.child("usuarios")
                .child(this.idUsuario)
                .setValue(this)
    }
}
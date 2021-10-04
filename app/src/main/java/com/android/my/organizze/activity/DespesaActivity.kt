package com.android.my.organizze.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.my.organizze.databinding.ActivityDespesaBinding
import com.android.my.organizze.helper.Base64Custom
import com.android.my.organizze.helper.ConfiguracaoFirebase
import com.android.my.organizze.helper.DateCustom
import com.android.my.organizze.model.Movimentacao
import com.android.my.organizze.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class DespesaActivity : AppCompatActivity() {

    private lateinit var dBinding: ActivityDespesaBinding
    private lateinit var movimentacao: Movimentacao
    private val firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase()
    private val autentificacao = ConfiguracaoFirebase.getFirebaseAutentification()
    private var despesaTotal: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dBinding = ActivityDespesaBinding.inflate(layoutInflater)
        setContentView(dBinding.root)

        //Preenche o campo data com a data atual
        dBinding.editData.setText( DateCustom.dataAtual() )
        recuperarDespesaTotal()

        dBinding.fabSalvar.setOnClickListener {

            if ( validarCamposDespesa() ) {
                movimentacao = Movimentacao()
                val valorRecuperado = dBinding.editValor.text.toString().toDouble()
                movimentacao.valor = valorRecuperado
                movimentacao.categoria = dBinding.editCategoria.text.toString()
                movimentacao.descricao = dBinding.editDescricao.text.toString()

                val data = dBinding.editData.text.toString()
                movimentacao.data = data
                movimentacao.tipo = "d"

                val despesaAtualizada = despesaTotal + valorRecuperado
                atualizarDespesa( despesaAtualizada )

                movimentacao.salvar( data )
                finish()
            }
        }

    }

    private fun validarCamposDespesa(): Boolean {
        val textoValor = dBinding.editValor.text.toString()
        val textoData = dBinding.editData.text.toString()
        val textoCategoria = dBinding.editCategoria.text.toString()
        val textoDescricao = dBinding.editDescricao.text.toString()

        return when {
            textoValor.isEmpty() -> {
                Toast.makeText(applicationContext,
                    "Preencha o valor!",
                    Toast.LENGTH_SHORT).show()
                false
            }
            textoData.isEmpty() -> {
                Toast.makeText(applicationContext,
                    "Preencha a data!",
                    Toast.LENGTH_SHORT).show()
                false
            }
            textoCategoria.isEmpty() -> {
                Toast.makeText(applicationContext,
                    "Preencha a categoria!",
                    Toast.LENGTH_SHORT).show()
                false
            }
            textoDescricao.isEmpty() -> {
                Toast.makeText(applicationContext,
                    "Preencha a descrição!",
                    Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun recuperarDespesaTotal() {

        val emailUsuario = autentificacao.currentUser?.email!!
        val idUsuario = Base64Custom.codificarBase64( emailUsuario )
        val usuarioRef = firebaseRef.child("usuarios").child( idUsuario )

        usuarioRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usuario = snapshot.getValue( Usuario::class.java )
                despesaTotal = usuario!!.despesaTotal
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun atualizarDespesa( despesa: Double ) {
        val emailUsuario = autentificacao.currentUser?.email!!
        val idUsuario = Base64Custom.codificarBase64( emailUsuario )
        val usuarioRef = firebaseRef.child("usuarios").child( idUsuario )

        usuarioRef.child("despesaTotal").setValue( despesa )
    }
}
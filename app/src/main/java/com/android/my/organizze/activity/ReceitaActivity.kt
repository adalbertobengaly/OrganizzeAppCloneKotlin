package com.android.my.organizze.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.my.organizze.databinding.ActivityReceitaBinding
import com.android.my.organizze.helper.Base64Custom
import com.android.my.organizze.helper.ConfiguracaoFirebase
import com.android.my.organizze.helper.DateCustom
import com.android.my.organizze.model.Movimentacao
import com.android.my.organizze.model.Usuario
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ReceitaActivity : AppCompatActivity() {

    private lateinit var rBinding: ActivityReceitaBinding
    private lateinit var movimentacao: Movimentacao
    private val firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase()
    private val autentificacao = ConfiguracaoFirebase.getFirebaseAutentification()
    private var receitaTotal: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rBinding = ActivityReceitaBinding.inflate(layoutInflater)
        setContentView(rBinding.root)

        //Preenche o campo data com a data atual
        rBinding.editData.setText( DateCustom.dataAtual() )
        recuperarReceitaTotal()

        rBinding.fabSalvar.setOnClickListener {

            if ( validarCamposReceita() ) {
                movimentacao = Movimentacao()
                val valorRecuperado = rBinding.editValor.text.toString().toDouble()
                movimentacao.valor = valorRecuperado
                movimentacao.categoria = rBinding.editCategoria.text.toString()
                movimentacao.descricao = rBinding.editDescricao.text.toString()

                val data = rBinding.editData.text.toString()
                movimentacao.data = data
                movimentacao.tipo = "r"

                val receitaAtualizada = receitaTotal + valorRecuperado
                atualizarReceita( receitaAtualizada )

                movimentacao.salvar( data )
                finish()
            }
        }

    }

    private fun validarCamposReceita(): Boolean {
        val textoValor = rBinding.editValor.text.toString()
        val textoData = rBinding.editData.text.toString()
        val textoCategoria = rBinding.editCategoria.text.toString()
        val textoDescricao = rBinding.editDescricao.text.toString()

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

    private fun recuperarReceitaTotal() {
        val emailUsuario = autentificacao.currentUser?.email!!
        val idUsuario = Base64Custom.codificarBase64( emailUsuario )
        val usuarioRef = firebaseRef.child("usuarios").child( idUsuario )

        usuarioRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usuario = snapshot.getValue( Usuario::class.java )
                receitaTotal = usuario!!.receitaTotal
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun atualizarReceita(receita: Double) {
        val emailUsuario = autentificacao.currentUser?.email!!
        val idUsuario = Base64Custom.codificarBase64( emailUsuario )
        val usuarioRef = firebaseRef.child("usuarios").child( idUsuario )

        usuarioRef.child("receitaTotal").setValue( receita )
    }
}
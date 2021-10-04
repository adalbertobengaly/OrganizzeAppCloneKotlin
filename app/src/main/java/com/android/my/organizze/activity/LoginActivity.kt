package com.android.my.organizze.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Toast
import com.android.my.organizze.R
import com.android.my.organizze.databinding.ActivityLoginBinding
import com.android.my.organizze.model.Usuario
import com.google.firebase.auth.*

class LoginActivity : AppCompatActivity() {

    private lateinit var lBinding: ActivityLoginBinding
    private lateinit var usuario: Usuario
    private lateinit var autentificacao: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(lBinding.root)

        lBinding.imagePasswordVisibility.setOnClickListener {
            val visibleSenha = lBinding.editSenha
            val typeVisible = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            val typeInvisible = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            when(visibleSenha.inputType) {
                typeVisible -> {
                    Log.i("passWord","typeVisible")
                    visibleSenha.inputType = typeInvisible
                    lBinding.imagePasswordVisibility.setImageResource(R.drawable.ic_visibility_off)
                }
                typeInvisible -> {
                    Log.i("passWord","typeInvisible")
                    visibleSenha.inputType = typeVisible
                    lBinding.imagePasswordVisibility.setImageResource(R.drawable.ic_visibility)
                }
            }
        }

        // Validar se os campos foram preenchidos
        lBinding.buttonEntrar.setOnClickListener {
            val textoEmail = lBinding.editEmail.text.toString()
            val textoSenha = lBinding.editSenha.text.toString()

            if ( textoEmail.isNotEmpty() ) {
                if ( textoSenha.isNotEmpty() ) {
                    usuario = Usuario()
                    usuario.email = textoEmail
                    usuario.senha = textoSenha
                    validarLogin()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Preencha a senha!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "Preencha o email!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun validarLogin() {
        autentificacao = FirebaseAuth.getInstance()
        autentificacao.signInWithEmailAndPassword(
            usuario.email,
            usuario.senha
        ).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                abrirTelaPrincipal()
            } else {

                // Para tratamento das exceções:
                // https://firebase.google.com/docs/reference/kotlin/com/google/firebase/auth/FirebaseAuth.html?authuser=0

                var excecao = ""
                try {
                    throw task.exception!!
                } catch (e: FirebaseAuthInvalidUserException) {
                    excecao = "Usuário não cadastrado!"
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    excecao = "Email e senha sem correspondência!"
                } catch (e: Exception) {
                    excecao = "Falha na tentativa de login: ${e.message}"
                    e.printStackTrace()
                }

                Toast.makeText(
                    applicationContext,
                    excecao,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun abrirTelaPrincipal() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
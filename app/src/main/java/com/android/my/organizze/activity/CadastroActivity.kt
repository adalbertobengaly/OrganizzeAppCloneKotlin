package com.android.my.organizze.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Toast
import com.android.my.organizze.R
import com.android.my.organizze.databinding.ActivityCadastroBinding
import com.android.my.organizze.helper.Base64Custom
import com.android.my.organizze.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlin.Exception

class CadastroActivity : AppCompatActivity() {

    private lateinit var cBinding: ActivityCadastroBinding
    private lateinit var autentificacao: FirebaseAuth
    private lateinit var usuario: Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cBinding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(cBinding.root)

        cBinding.imagePasswordVisibility.setOnClickListener {
            val visibleSenha = cBinding.editSenha
            val typeVisible = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            val typeInvisible = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            when(visibleSenha.inputType) {
                    typeVisible -> {
                        Log.i("passWord","typeVisible")
                        visibleSenha.inputType = typeInvisible
                        cBinding.imagePasswordVisibility.setImageResource(R.drawable.ic_visibility_off)
                    }
                    typeInvisible -> {
                        Log.i("passWord","typeInvisible")
                        visibleSenha.inputType = typeVisible
                        cBinding.imagePasswordVisibility.setImageResource(R.drawable.ic_visibility)
                    }
                }
        }


        cBinding.buttonCadastrar.setOnClickListener {

            val textoNome = cBinding.editNome.text.toString()
            val textoEmail = cBinding.editEmail.text.toString()
            val textoSenha = cBinding.editSenha.text.toString()

            // Validar se os campos foram preenchidos
           if ( textoNome.isNotEmpty() ) {
                if ( textoEmail.isNotEmpty() ) {
                    if ( textoSenha.isNotEmpty() ) {
                        usuario = Usuario()
                        usuario.nome = textoNome
                        usuario.email = textoEmail
                        usuario.senha = textoSenha
                        cadastrarUsuario()
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
            } else {
                Toast.makeText(
                    applicationContext,
                    "Preencha o nome!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun cadastrarUsuario() {
        autentificacao = FirebaseAuth.getInstance()
        autentificacao.createUserWithEmailAndPassword(usuario.email, usuario.senha)
            .addOnCompleteListener { task ->
            if ( task.isSuccessful ) {
                val idUsuario = Base64Custom.codificarBase64(usuario.email)
                usuario.idUsuario = idUsuario
                usuario.salvar()
                finish()
            } else {
                // Para tratamento das exceções:
                // https://firebase.google.com/docs/reference/kotlin/com/google/firebase/auth/FirebaseAuth.html?authuser=0

                var excecao = ""
                try {
                    throw task.exception!!
                } catch (e: FirebaseAuthWeakPasswordException) {
                    excecao = "Digite uma senha mais forte!"
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    excecao = "Por favor! Digite uma email válido."
                } catch (e: FirebaseAuthUserCollisionException) {
                    excecao = "Esta conta já existe!"
                } catch (e: Exception) {
                    excecao = "Erro ao cadastrar o usuário: ${e.message}"
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
}
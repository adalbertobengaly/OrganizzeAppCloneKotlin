package com.android.my.organizze.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.android.my.organizze.R
import com.android.my.organizze.databinding.ActivityIntroBinding
import com.android.my.organizze.helper.ConfiguracaoFirebase
import com.github.appintro.AppIntro2
import com.github.appintro.AppIntroCustomLayoutFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class IntroActivity : AppIntro2() {

    private  lateinit var userAuth: FirebaseAuth
    private lateinit var binding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        //setContentView(binding.root)

        // Change Indicator Color
        setIndicatorColor(
            selectedIndicatorColor = Color.LTGRAY,
            unselectedIndicatorColor = Color.BLACK
        )

        isButtonsEnabled = false

        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.intro_1))
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.intro_2))
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.intro_3))
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.intro_4))
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.intro_cadastro))
    }

    private fun verificarUsuarioLogado() {
        userAuth = ConfiguracaoFirebase.getFirebaseAutentification()
        if (userAuth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        verificarUsuarioLogado()
    }

    fun btEntrar( view: View ) {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    fun btCadastrar ( view: View ) {
        startActivity(Intent(this, CadastroActivity::class.java))
    }
}
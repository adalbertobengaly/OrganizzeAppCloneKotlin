package com.android.my.organizze.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.my.organizze.R
import com.android.my.organizze.adapter.RecyclerAdapter
import com.android.my.organizze.databinding.ActivityMainBinding
import com.android.my.organizze.helper.Base64Custom
import com.android.my.organizze.helper.ConfiguracaoFirebase
import com.android.my.organizze.model.Movimentacao
import com.android.my.organizze.model.Usuario
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase()
    private var userAuth = ConfiguracaoFirebase.getFirebaseAutentification()

    private var receitaTotal: Double = 0.00
    private var despesaTotal: Double = 0.00
    private var resumoTotal: Double = 0.00

    private lateinit var usuarioRef: DatabaseReference
    private lateinit var valueEventListenerUsuario: ValueEventListener
    private lateinit var valueEventListenerMovimentacoes: ValueEventListener
    private val movimentacoes = mutableListOf<Movimentacao>()
    private lateinit var movimentacao: Movimentacao
    private lateinit var movimentacaoRef: DatabaseReference
    private lateinit var mesAnoSelecionado: String
    private lateinit var adapterMovimentacao: RecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = "Organizze"
        setSupportActionBar(binding.toolbar)
        configuraCalendarView()
        recyclerWithSwipe()

        binding.menuDespesa.setOnClickListener {
            startActivity(Intent(this, DespesaActivity::class.java))
        }

        binding.menuReceita.setOnClickListener {
            startActivity(Intent(this, ReceitaActivity::class.java))
        }
    }

    private fun recyclerWithSwipe() {

        // Adapter do RecyclerMovimentos
        adapterMovimentacao = RecyclerAdapter( movimentacoes, this )

        // Configuração RecyclerMovimentos
        val recyclerMovimentos = binding.content.recyclerMovimentos
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        recyclerMovimentos.layoutManager = layoutManager
        recyclerMovimentos.setHasFixedSize(true)
        recyclerMovimentos.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
        recyclerMovimentos.adapter = adapterMovimentacao

        val itemTouch = object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
            ): Int {
                val dragFlags = ItemTouchHelper.ACTION_STATE_IDLE
                val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
                return makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean { TODO("Not yet implemented") }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                excluirMovimentacao( viewHolder )
            }
        }

        ItemTouchHelper( itemTouch ).attachToRecyclerView( recyclerMovimentos )
    }

    private fun excluirMovimentacao(viewHolder: RecyclerView.ViewHolder) {
        // configura AlertDialog
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Excluir Movimentação da Conta")
        alertDialog.setMessage("Você tem certeza que deseja realmente excluir esta movimentação?")
        alertDialog.setCancelable(false)

        alertDialog.setPositiveButton("Confirmar", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                val position = viewHolder.adapterPosition
                movimentacao = movimentacoes[position]

                val emailUsuario = userAuth.currentUser?.email!!
                val idUsuario = Base64Custom.codificarBase64( emailUsuario )
                movimentacaoRef = firebaseRef.child("movimentacao")
                    .child( idUsuario )
                    .child( mesAnoSelecionado )

                movimentacaoRef.child(movimentacao.key).removeValue()
                adapterMovimentacao.notifyItemRemoved( position )
                atualizarSaldo()
            }
        })

        alertDialog.setNegativeButton("Cancelar", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                Toast.makeText(
                    applicationContext,
                    "Cancelado",
                    Toast.LENGTH_SHORT
                ).show()
                adapterMovimentacao.notifyDataSetChanged()
            }
        })

        val alert = alertDialog.create()
        alert.show()
    }

    private fun atualizarSaldo() {
        val emailUsuario = userAuth.currentUser?.email!!
        val idUsuario = Base64Custom.codificarBase64( emailUsuario )
        usuarioRef = firebaseRef.child("usuarios").child( idUsuario )

        when (movimentacao.tipo) {
            "r" -> {
                receitaTotal -= movimentacao.valor
                usuarioRef.child("receitaTotal").setValue(receitaTotal)
            }
            "d" -> {
                despesaTotal -= movimentacao.valor
                usuarioRef.child("despesaTotal").setValue(despesaTotal)
            }
        }
    }

    private fun recuperarMovimentacoes() {
        val emailUsuario = userAuth.currentUser?.email!!
        val idUsuario = Base64Custom.codificarBase64( emailUsuario )
        movimentacaoRef = firebaseRef.child("movimentacao")
                                    .child( idUsuario )
                                    .child( mesAnoSelecionado )

        valueEventListenerMovimentacoes = movimentacaoRef.addValueEventListener(
            object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    movimentacoes.clear()
                    for ( dados in snapshot.children) {
                        val movimentacao = dados.getValue( Movimentacao::class.java )
                        movimentacao!!.key = dados.key.toString()
                        movimentacoes.add( movimentacao )
                    }

                    adapterMovimentacao.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) { TODO("Not yet implemented") }

            })
    }

    private fun recuperarResumo() {
        val emailUsuario = userAuth.currentUser?.email!!
        val idUsuario = Base64Custom.codificarBase64( emailUsuario )
        usuarioRef = firebaseRef.child("usuarios").child( idUsuario )

        valueEventListenerUsuario = usuarioRef.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usuario = snapshot.getValue( Usuario::class.java )

                despesaTotal = usuario!!.despesaTotal
                receitaTotal = usuario.receitaTotal
                resumoTotal = receitaTotal - despesaTotal


                val textoSaldo = binding.content.textSaldo
                val textoSaudacao = binding.content.textSaudacao

                textoSaudacao.text = "Olá, ${usuario.nome}"
                textoSaldo.text = "R$ %.2f".format( resumoTotal )

            }

            override fun onCancelled(error: DatabaseError) { TODO("Not yet implemented") }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menuSair -> {
                userAuth.signOut()
                startActivity( Intent (this, IntroActivity::class.java) )
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun configuraCalendarView() {
        val calendarView = binding.content.calendarView
        val meses = arrayOf("Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro")
        calendarView.setTitleMonths( meses )

        val dataAtual = calendarView.currentDate
        val mesSelecionado = "%02d".format(dataAtual.month + 1)
        mesAnoSelecionado = "$mesSelecionado${dataAtual.year}"

        calendarView.setOnMonthChangedListener { _, date ->
            val mesSelecionadoDate = "%02d".format(date.month + 1)
            mesAnoSelecionado = "$mesSelecionadoDate${date.year}"

            movimentacaoRef.removeEventListener( valueEventListenerMovimentacoes )
            recuperarMovimentacoes()
        }
    }

    override fun onStart() {
        super.onStart()
        recuperarResumo()
        recuperarMovimentacoes()
    }

    override fun onStop() {
        super.onStop()
        usuarioRef.removeEventListener( valueEventListenerUsuario )
        movimentacaoRef.removeEventListener( valueEventListenerMovimentacoes )
    }
}
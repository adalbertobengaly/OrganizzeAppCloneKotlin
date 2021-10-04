package com.android.my.organizze.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.my.organizze.R.*
import com.android.my.organizze.databinding.RecyclerItemBinding
import com.android.my.organizze.model.Movimentacao

class RecyclerAdapter(private val movimentacoes: List<Movimentacao>, private val context: Context) : RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>() {

    inner class MyViewHolder(
        private val binding: RecyclerItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movimentacao: Movimentacao) {
            binding.textDescricao.text = movimentacao.descricao
            binding.textCategoria.text = movimentacao.categoria
            binding.textValor.text = movimentacao.valor.toString()
            binding.textValor.setTextColor(context.resources.getColor( color.colorPrimaryReceita ))
            if(movimentacao.tipo == "d") {
                binding.textValor.setTextColor(context.resources.getColor( color.colorSecondaryDespesa ))
                binding.textValor.text = "- ${movimentacao.valor}"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemList = RecyclerItemBinding
            .inflate(LayoutInflater.from(parent.context),
                parent,
                false)
        return(MyViewHolder(itemList))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(movimentacoes[position])
    }

    override fun getItemCount(): Int {
        return movimentacoes.size
    }

}
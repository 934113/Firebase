package com.example.firebase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GastoAdapter(
    private val listaGastos: List<Gasto>,
    private val onItemClick: (Gasto) -> Unit // Callback para el click
) : RecyclerView.Adapter<GastoAdapter.GastoViewHolder>() {

    class GastoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitulo: TextView = itemView.findViewById(R.id.tvTitulo)
        val tvCategoria: TextView = itemView.findViewById(R.id.tvCategoria)
        val tvMonto: TextView = itemView.findViewById(R.id.tvMonto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GastoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gasto, parent, false)
        return GastoViewHolder(view)
    }

    override fun onBindViewHolder(holder: GastoViewHolder, position: Int) {
        val gasto = listaGastos[position]
        holder.tvTitulo.text = gasto.titulo
        holder.tvCategoria.text = gasto.categoria
        holder.tvMonto.text = "$${gasto.monto}"

        holder.itemView.setOnClickListener {
            onItemClick(gasto)
        }
    }

    override fun getItemCount(): Int = listaGastos.size
}
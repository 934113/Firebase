package com.example.firebase

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase.databinding.ItemGastoBinding
import java.util.*

class GastoAdapter(
    private val listaGastos: List<Gasto>,
    private val onItemClick: (Gasto) -> Unit
) : RecyclerView.Adapter<GastoAdapter.GastoViewHolder>() {

    class GastoViewHolder(val binding: ItemGastoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GastoViewHolder {
        val binding = ItemGastoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GastoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GastoViewHolder, position: Int) {
        val gasto = listaGastos[position]
        with(holder.binding) {
            tvTitulo.text = gasto.titulo
            tvCategoria.text = gasto.categoria
            tvMonto.text = String.format(Locale.getDefault(), "$%.2f", gasto.monto)
            
            val context = root.context
            val colorRes = if (gasto.tipo == "Fijo") {
                android.R.color.holo_red_dark
            } else {
                android.R.color.holo_orange_dark
            }
            tvMonto.setTextColor(ContextCompat.getColor(context, colorRes))

            root.setOnClickListener { onItemClick(gasto) }
        }
    }

    override fun getItemCount(): Int = listaGastos.size
}

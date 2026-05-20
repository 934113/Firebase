package com.example.firebase


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase.Gasto
import com.example.firebase.R

class GastoAdapter(
    private val listaGastos: List<Gasto>
) : RecyclerView.Adapter<GastoAdapter.GastoViewHolder>() {

    class GastoViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView) {

        val tvTitulo: TextView =
            itemView.findViewById(R.id.tvTitulo)

        val tvCategoria: TextView =
            itemView.findViewById(R.id.tvCategoria)

        val tvMonto: TextView =
            itemView.findViewById(R.id.tvMonto)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GastoViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gasto, parent, false)

        return GastoViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: GastoViewHolder,
        position: Int
    ) {

        val gasto = listaGastos[position]

        holder.tvTitulo.text = gasto.titulo
        holder.tvCategoria.text = gasto.categoria
        holder.tvMonto.text = "$${gasto.monto}"
    }

    override fun getItemCount(): Int {
        return listaGastos.size
    }
}
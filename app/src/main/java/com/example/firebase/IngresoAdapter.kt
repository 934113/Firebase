package com.example.firebase

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase.databinding.ItemIngresoBinding
import java.text.SimpleDateFormat
import java.util.*

class IngresoAdapter(
    private val listaIngresos: List<Ingreso>,
    private val onItemClick: (Ingreso) -> Unit
) : RecyclerView.Adapter<IngresoAdapter.IngresoViewHolder>() {

    class IngresoViewHolder(val binding: ItemIngresoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngresoViewHolder {
        val binding = ItemIngresoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IngresoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngresoViewHolder, position: Int) {
        val ingreso = listaIngresos[position]
        with(holder.binding) {
            tvTitulo.text = ingreso.descripcion
            tvFecha.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(ingreso.fecha))
            tvMonto.text = String.format(Locale.getDefault(), "+ $%.2f", ingreso.monto)
            tvMonto.setTextColor(ContextCompat.getColor(root.context, android.R.color.holo_green_dark))

            root.setOnClickListener { onItemClick(ingreso) }
        }
    }

    override fun getItemCount(): Int = listaIngresos.size
}

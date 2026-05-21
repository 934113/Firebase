package com.example.firebase

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase.databinding.ActivityInicioBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*

class InicioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInicioBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    
    private lateinit var adapter: GastoAdapter
    private var listaGastos = mutableListOf<Gasto>()
    private var presupuestoMensual: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setupUI()
        setupRecyclerView()
        configurarSwipeParaEliminar()
    }

    private fun setupUI() {
        binding.tvUserEmail.text = auth.currentUser?.email ?: "Usuario"

        binding.btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }

        binding.btnAgregarGasto.setOnClickListener {
            startActivity(Intent(this, AgregarGastoActivity::class.java))
        }

        binding.btnIrIngresos.setOnClickListener {
            startActivity(Intent(this, IngresosActivity::class.java))
        }

        binding.btnIrEstadisticas.setOnClickListener {
            startActivity(Intent(this, EstadisticasActivity::class.java))
        }
        
        // Al hacer click en el balance o el ahorro, permitir editar presupuesto
        binding.tvBalanceTotal.setOnClickListener {
            mostrarDialogoPresupuesto()
        }
    }

    private fun setupRecyclerView() {
        adapter = GastoAdapter(listaGastos) { gasto ->
            val intent = Intent(this, AgregarGastoActivity::class.java).apply {
                putExtra("documentId", gasto.documentId)
                putExtra("titulo", gasto.titulo)
                putExtra("monto", gasto.monto)
                putExtra("categoria", gasto.categoria)
                putExtra("tipo", gasto.tipo)
            }
            startActivity(intent)
        }
        binding.recyclerGastos.layoutManager = LinearLayoutManager(this)
        binding.recyclerGastos.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        cargarDatosDashboard()
    }

    private fun cargarDatosDashboard() {
        val uid = auth.currentUser?.uid ?: return

        // 1. Cargar Presupuesto
        db.collection("presupuestos").document(uid).get()
            .addOnSuccessListener { doc ->
                presupuestoMensual = doc.getDouble("limite") ?: 0.0
                
                // 2. Cargar Gastos Recientes
                db.collection("gastos")
                    .whereEqualTo("usuarioId", uid)
                    .orderBy("fecha", Query.Direction.DESCENDING)
                    .limit(10)
                    .get()
                    .addOnSuccessListener { result ->
                        listaGastos.clear()
                        for (documento in result) {
                            val gasto = documento.toObject(Gasto::class.java)
                            gasto.documentId = documento.id
                            listaGastos.add(gasto)
                        }
                        adapter.notifyDataSetChanged()
                        
                        // 3. Calcular Totales para el Dashboard
                        calcularTotales(uid)
                    }
                    .addOnFailureListener {
                        // Si falla el ordenamiento por fecha, es posible que falte el índice en Firestore
                        // Intentamos cargar sin ordenamiento como fallback
                        db.collection("gastos")
                            .whereEqualTo("usuarioId", uid)
                            .get()
                            .addOnSuccessListener { resultFallback ->
                                listaGastos.clear()
                                for (doc in resultFallback) {
                                    val gasto = doc.toObject(Gasto::class.java)
                                    gasto.documentId = doc.id
                                    listaGastos.add(gasto)
                                }
                                adapter.notifyDataSetChanged()
                                calcularTotales(uid)
                            }
                    }
            }
    }

    private fun calcularTotales(uid: String) {
        // Obtenemos todos los gastos
        db.collection("gastos").whereEqualTo("usuarioId", uid).get()
            .addOnSuccessListener { gastosResult ->
                var totalGastos = 0.0
                var fijos = 0.0
                var libres = 0.0
                for (doc in gastosResult) {
                    val m = doc.getDouble("monto") ?: 0.0
                    val t = doc.getString("tipo")
                    totalGastos += m
                    if (t == "Fijo") fijos += m else libres += m
                }

                // Obtenemos todos los ingresos
                db.collection("ingresos").whereEqualTo("usuarioId", uid).get()
                    .addOnSuccessListener { ingresosResult ->
                        var totalIngresos = 0.0
                        for (doc in ingresosResult) {
                            totalIngresos += doc.getDouble("monto") ?: 0.0
                        }

                        actualizarDashboardUI(totalIngresos, totalGastos, fijos, libres)
                    }
            }
    }

    private fun actualizarDashboardUI(ingresos: Double, gastos: Double, fijos: Double, libres: Double) {
        val balance = ingresos - gastos
        binding.tvBalanceTotal.text = String.format(Locale.getDefault(), "$%.2f", balance)
        binding.tvTotalIngresos.text = String.format(Locale.getDefault(), "$%.2f", ingresos)
        binding.tvTotalGastos.text = String.format(Locale.getDefault(), "$%.2f", gastos)
        
        // Los textviews de gastos fijos/libres pueden ser nulos en el layout land si se definieron como View
        if (binding.tvGastosFijos is android.widget.TextView) {
            (binding.tvGastosFijos as android.widget.TextView).text = String.format(Locale.getDefault(), "$%.2f", fijos)
        }
        if (binding.tvGastosLibres is android.widget.TextView) {
            (binding.tvGastosLibres as android.widget.TextView).text = String.format(Locale.getDefault(), "$%.2f", libres)
        }

        // Alerta de presupuesto
        if (presupuestoMensual > 0 && gastos > presupuestoMensual) {
            binding.tvTotalGastos.setTextColor(getColor(android.R.color.holo_red_light))
            Toast.makeText(this, "¡Has superado tu presupuesto!", Toast.LENGTH_SHORT).show()
        } else {
            binding.tvTotalGastos.setTextColor(getColor(android.R.color.white))
        }

        if (ingresos > 0) {
            val ahorro = if (balance > 0) balance else 0.0
            val porcentaje = (ahorro / ingresos * 100).toInt()
            binding.tvPorcentajeAhorro.text = "$porcentaje%"
            binding.progressAhorro.progress = porcentaje
        } else {
            binding.tvPorcentajeAhorro.text = "0%"
            binding.progressAhorro.progress = 0
        }
    }

    private fun mostrarDialogoPresupuesto() {
        val input = EditText(this)
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        input.setText(presupuestoMensual.toString())

        AlertDialog.Builder(this)
            .setTitle("Límite de Gasto Mensual")
            .setMessage("Define cuánto planeas gastar este mes:")
            .setView(input)
            .setPositiveButton("Guardar") { _, _ ->
                val monto = input.text.toString().toDoubleOrNull() ?: 0.0
                val uid = auth.currentUser?.uid ?: return@setPositiveButton
                db.collection("presupuestos").document(uid).set(mapOf("limite" to monto))
                    .addOnSuccessListener {
                        Toast.makeText(this, "Presupuesto actualizado", Toast.LENGTH_SHORT).show()
                        cargarDatosDashboard()
                    }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun configurarSwipeParaEliminar() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                confirmarEliminacion(viewHolder.bindingAdapterPosition)
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.recyclerGastos)
    }

    private fun confirmarEliminacion(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Gasto")
            .setMessage("¿Eliminar este registro?")
            .setPositiveButton("Sí") { _, _ -> eliminarGasto(position) }
            .setNegativeButton("No") { _, _ -> adapter.notifyItemChanged(position) }
            .show()
    }

    private fun eliminarGasto(position: Int) {
        val id = listaGastos[position].documentId ?: return
        db.collection("gastos").document(id).delete().addOnSuccessListener {
            listaGastos.removeAt(position)
            adapter.notifyItemRemoved(position)
            cargarDatosDashboard()
        }
    }

    private fun cerrarSesion() {
        auth.signOut()
        GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut().addOnCompleteListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}

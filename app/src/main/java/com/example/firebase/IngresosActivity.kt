package com.example.firebase

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebase.databinding.ActivityIngresosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class IngresosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIngresosBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val listaIngresos = mutableListOf<Ingreso>()
    private lateinit var adapter: IngresoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIngresosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        setupRecyclerView()

        binding.btnGuardarIngreso.setOnClickListener {
            guardarIngreso()
        }

        cargarIngresos()
    }

    private fun setupRecyclerView() {
        adapter = IngresoAdapter(listaIngresos) { ingreso ->
            // Opcional: eliminar ingreso con long click o botón
        }
        binding.recyclerIngresos.layoutManager = LinearLayoutManager(this)
        binding.recyclerIngresos.adapter = adapter
    }

    private fun guardarIngreso() {
        val descripcion = binding.etDescripcionIngreso.text.toString().trim()
        val montoTexto = binding.etMontoIngreso.text.toString().trim()

        if (descripcion.isEmpty() || montoTexto.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val monto = montoTexto.toDoubleOrNull() ?: 0.0
        val usuarioId = auth.currentUser?.uid ?: return

        val nuevoIngreso = Ingreso(
            descripcion = descripcion,
            monto = monto,
            usuarioId = usuarioId,
            fecha = System.currentTimeMillis()
        )

        db.collection("ingresos")
            .add(nuevoIngreso)
            .addOnSuccessListener {
                Toast.makeText(this, "Ingreso guardado", Toast.LENGTH_SHORT).show()
                binding.etDescripcionIngreso.text?.clear()
                binding.etMontoIngreso.text?.clear()
                cargarIngresos()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cargarIngresos() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("ingresos")
            .whereEqualTo("usuarioId", uid)
            .orderBy("fecha", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { resultado ->
                listaIngresos.clear()
                for (doc in resultado) {
                    val ingreso = doc.toObject(Ingreso::class.java)
                    ingreso.documentId = doc.id
                    listaIngresos.add(ingreso)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

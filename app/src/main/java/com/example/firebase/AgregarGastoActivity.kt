package com.example.firebase

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebase.databinding.ActivityAgregarGastoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AgregarGastoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgregarGastoBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var documentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarGastoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        setupSpinner()

        // Verificar si es edición
        intent.extras?.let {
            documentId = it.getString("documentId")
            binding.etTitulo.setText(it.getString("titulo"))
            binding.etMonto.setText(it.getDouble("monto").toString())
            
            val cat = it.getString("categoria")
            val adapter = binding.spCategoria.adapter as ArrayAdapter<String>
            val pos = adapter.getPosition(cat)
            binding.spCategoria.setSelection(pos)

            val tipo = it.getString("tipo")
            if (tipo == "Fijo") binding.rbFijo.isChecked = true
            else if (tipo == "Libre") binding.rbLibre.isChecked = true

            binding.btnGuardar.text = "Actualizar Gasto"
            binding.tvTituloPantalla.text = "Editar Gasto"
        }

        binding.btnGuardar.setOnClickListener {
            guardarGasto()
        }
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.categorias,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spCategoria.adapter = adapter
    }

    private fun guardarGasto() {
        val titulo = binding.etTitulo.text.toString().trim()
        val montoTxt = binding.etMonto.text.toString().trim()
        val categoria = binding.spCategoria.selectedItem.toString()
        val tipo = when {
            binding.rbFijo.isChecked -> "Fijo"
            binding.rbLibre.isChecked -> "Libre"
            else -> ""
        }

        if (titulo.isEmpty() || montoTxt.isEmpty() || tipo.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val monto = montoTxt.toDoubleOrNull() ?: 0.0
        val uid = auth.currentUser?.uid ?: return

        val gastoMap = mutableMapOf<String, Any>(
            "titulo" to titulo,
            "monto" to monto,
            "categoria" to categoria,
            "tipo" to tipo,
            "usuarioId" to uid
        )

        if (documentId == null) {
            gastoMap["fecha"] = System.currentTimeMillis()
            db.collection("gastos").add(gastoMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Gasto guardado", Toast.LENGTH_SHORT).show()
                    finish()
                }
        } else {
            db.collection("gastos").document(documentId!!).update(gastoMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Gasto actualizado", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }
    }
}

package com.example.firebase

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AgregarGastoActivity : AppCompatActivity() {

    private lateinit var etTitulo: EditText
    private lateinit var etMonto: EditText
    private lateinit var spCategoria: Spinner
    private lateinit var rbFijo: RadioButton
    private lateinit var rbLibre: RadioButton
    private lateinit var btnGuardar: Button
    private lateinit var tvTituloPantalla: TextView

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var documentId: String? = null // Para saber si estamos editando

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_gasto)

        // Firebase
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // UI Components
        etTitulo = findViewById(R.id.etTitulo)
        etMonto = findViewById(R.id.etMonto)
        spCategoria = findViewById(R.id.spCategoria)
        rbFijo = findViewById(R.id.rbFijo)
        rbLibre = findViewById(R.id.rbLibre)
        btnGuardar = findViewById(R.id.btnGuardar)
        // Nota: Asegúrate de que R.id.tvTituloPantalla existe en tu XML, 
        // o usa el título de la Activity si no existe un TextView específico.
        // tvTituloPantalla = findViewById(R.id.tvTituloPantalla) 

        // Configurar Spinner
        val spinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.categorias,
            android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCategoria.adapter = spinnerAdapter

        // Verificar si recibimos datos para EDITAR
        intent.extras?.let {
            documentId = it.getString("documentId")
            etTitulo.setText(it.getString("titulo"))
            etMonto.setText(it.getDouble("monto").toString())
            
            val categoria = it.getString("categoria")
            val pos = spinnerAdapter.getPosition(categoria)
            spCategoria.setSelection(pos)

            val tipo = it.getString("tipo")
            if (tipo == "Fijo") rbFijo.isChecked = true
            else if (tipo == "Libre") rbLibre.isChecked = true

            btnGuardar.text = "Actualizar Gasto"
            title = "Editar Gasto"
        }

        btnGuardar.setOnClickListener {
            guardarOActualizarGasto()
        }
    }

    private fun guardarOActualizarGasto() {
        val titulo = etTitulo.text.toString().trim()
        val montoTexto = etMonto.text.toString().trim()
        val categoria = spCategoria.selectedItem.toString()
        val tipo = when {
            rbFijo.isChecked -> "Fijo"
            rbLibre.isChecked -> "Libre"
            else -> ""
        }

        if (titulo.isEmpty() || montoTexto.isEmpty() || tipo.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val monto = montoTexto.toDouble()
        val usuarioId = auth.currentUser?.uid ?: ""

        val gastoData = hashMapOf(
            "titulo" to titulo,
            "monto" to monto,
            "categoria" to categoria,
            "tipo" to tipo,
            "usuarioId" to usuarioId
        )

        if (documentId != null) {
            // ACTUALIZAR
            db.collection("gastos").document(documentId!!)
                .update(gastoData as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(this, "Gasto actualizado correctamente", Toast.LENGTH_SHORT).show()
                    finish() // Regresar a la pantalla anterior
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
        } else {
            // CREAR NUEVO
            db.collection("gastos")
                .add(gastoData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Gasto guardado correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
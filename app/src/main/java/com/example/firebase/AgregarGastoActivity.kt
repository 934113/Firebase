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

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_gasto)

        // Firebase
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Inputs
        etTitulo = findViewById(R.id.etTitulo)
        etMonto = findViewById(R.id.etMonto)
        spCategoria = findViewById(R.id.spCategoria)
        rbFijo = findViewById(R.id.rbFijo)
        rbLibre = findViewById(R.id.rbLibre)
        btnGuardar = findViewById(R.id.btnGuardar)

        // Spinner categorías
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.categorias,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spCategoria.adapter = adapter

        // Guardar gasto
        btnGuardar.setOnClickListener {

            guardarGasto()
        }
    }

    private fun guardarGasto() {

        val titulo = etTitulo.text.toString().trim()
        val montoTexto = etMonto.text.toString().trim()
        val categoria = spCategoria.selectedItem.toString()

        val tipo = when {
            rbFijo.isChecked -> "Fijo"
            rbLibre.isChecked -> "Libre"
            else -> ""
        }

        // Validaciones
        if (titulo.isEmpty() ||
            montoTexto.isEmpty() ||
            tipo.isEmpty()
        ) {

            Toast.makeText(
                this,
                "Completa todos los campos",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val monto = montoTexto.toDouble()

        val usuarioId = auth.currentUser?.uid ?: ""

        // Crear objeto gasto
        val gasto = hashMapOf(
            "titulo" to titulo,
            "monto" to monto,
            "categoria" to categoria,
            "tipo" to tipo,
            "usuarioId" to usuarioId
        )

        // Guardar en Firestore
        db.collection("gastos")
            .add(gasto)
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Gasto guardado correctamente",
                    Toast.LENGTH_LONG
                ).show()

                limpiarCampos()
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Error al guardar",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun limpiarCampos() {

        etTitulo.text.clear()
        etMonto.text.clear()

        rbFijo.isChecked = false
        rbLibre.isChecked = false

        spCategoria.setSelection(0)
    }
}
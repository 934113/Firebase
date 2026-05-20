package com.example.firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase.Gasto
import com.example.firebase.GastoAdapter
import com.google.firebase.firestore.FirebaseFirestore


class InicioActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var btnAgregarGasto: Button
    private lateinit var recyclerGastos: RecyclerView

    private lateinit var adapter: GastoAdapter

    private var listaGastos = mutableListOf<Gasto>()

    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_inicio)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // RecyclerView
        recyclerGastos = findViewById(R.id.recyclerGastos)

        adapter = GastoAdapter(listaGastos)

        recyclerGastos.layoutManager =
            LinearLayoutManager(this)

        recyclerGastos.adapter = adapter

        cargarGastos()

        // Botón agregar gasto
        btnAgregarGasto = findViewById(R.id.btnAgregarGasto)

        btnAgregarGasto.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    AgregarGastoActivity::class.java
                )
            )
        }

        val UserEmail =
            findViewById<TextView>(R.id.UserEmail)

        val btnCerrarSesion =
            findViewById<Button>(R.id.btnCerrarSesion)

        // Mostrar correo usuario
        UserEmail.text =
            auth.currentUser?.email ?: "Usuario"

        btnCerrarSesion.setOnClickListener {

            auth.signOut()

            val gso = GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
            ).build()

            val googleClient =
                GoogleSignIn.getClient(this, gso)

            googleClient.signOut()

            startActivity(
                Intent(this, MainActivity::class.java)
            )

            finish()
        }
    }
    private fun cargarGastos() {

        val uid = FirebaseAuth.getInstance()
            .currentUser?.uid

        db.collection("gastos")
            .whereEqualTo("usuarioId", uid)
            .get()
            .addOnSuccessListener { resultado ->

                listaGastos.clear()

                for (documento in resultado) {

                    val gasto =
                        documento.toObject(Gasto::class.java)

                    listaGastos.add(gasto)
                }

                adapter.notifyDataSetChanged()
            }
    }
}
package com.example.firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
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

        // Inicializar RecyclerView
        recyclerGastos = findViewById(R.id.recyclerGastos)
        
        // Configurar el adapter con el callback de click para EDITAR
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
        
        recyclerGastos.layoutManager = LinearLayoutManager(this)
        recyclerGastos.adapter = adapter

        // Configurar Swipe to Delete
        configurarSwipeParaEliminar()

        // Botón agregar gasto
        btnAgregarGasto = findViewById(R.id.btnAgregarGasto)
        btnAgregarGasto.setOnClickListener {
            startActivity(Intent(this, AgregarGastoActivity::class.java))
        }

        val userEmail = findViewById<TextView>(R.id.UserEmail)
        val btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesion)

        userEmail.text = auth.currentUser?.email ?: "Usuario"

        btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }
    }

    override fun onStart() {
        super.onStart()
        // Cargar/Refrescar gastos cada vez que la pantalla se vuelve visible
        cargarGastos()
    }

    private fun configurarSwipeParaEliminar() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                confirmarEliminacion(position)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerGastos)
    }

    private fun confirmarEliminacion(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Gasto")
            .setMessage("¿Estás seguro de que deseas eliminar este gasto?")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarGastoDeFirestore(position)
            }
            .setNegativeButton("Cancelar") { _, _ ->
                adapter.notifyItemChanged(position)
            }
            .setCancelable(false)
            .show()
    }

    private fun eliminarGastoDeFirestore(position: Int) {
        val gasto = listaGastos[position]
        val docId = gasto.documentId

        if (docId != null) {
            db.collection("gastos").document(docId)
                .delete()
                .addOnSuccessListener {
                    listaGastos.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    Toast.makeText(this, "Gasto eliminado correctamente", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    adapter.notifyItemChanged(position)
                    Toast.makeText(this, "Error al eliminar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Error: No se pudo encontrar el ID del gasto", Toast.LENGTH_SHORT).show()
            adapter.notifyItemChanged(position)
        }
    }

    private fun cargarGastos() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("gastos")
            .whereEqualTo("usuarioId", uid)
            .get()
            .addOnSuccessListener { resultado ->
                listaGastos.clear()
                for (documento in resultado) {
                    val gasto = documento.toObject(Gasto::class.java)
                    gasto.documentId = documento.id
                    listaGastos.add(gasto)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar gastos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cerrarSesion() {
        auth.signOut()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val googleClient = GoogleSignIn.getClient(this, gso)
        googleClient.signOut().addOnCompleteListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
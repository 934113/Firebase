package com.example.firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth


class InicioActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var btnAgregarGasto: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        auth = FirebaseAuth.getInstance()

        btnAgregarGasto = findViewById(R.id.btnAgregarGasto)
        btnAgregarGasto.setOnClickListener {
            startActivity(Intent(this, AgregarGastoActivity::class.java))
        }


        val UserEmail = findViewById<TextView>(R.id.UserEmail)
        val btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesion)

        // Mostrar el correo del usuario actual
        UserEmail.text = auth.currentUser?.email ?: "Usuario"

        btnCerrarSesion.setOnClickListener {
            // Cerrar sesión de Firebase
            auth.signOut()

            // Cerrar sesión de Google también
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            val googleClient = GoogleSignIn.getClient(this, gso)
            googleClient.signOut()

            // Volver al Login
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
package com.example.firebase

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RegistroActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        auth = FirebaseAuth.getInstance()

        val Email = findViewById<EditText>(R.id.Email)
        val Password = findViewById<EditText>(R.id.Password)
        val ConfirmPassword = findViewById<EditText>(R.id.ConfirmPassword)
        val btnRegistro = findViewById<Button>(R.id.btnRegistro)
        val Login = findViewById<TextView>(R.id.Login)

        btnRegistro.setOnClickListener {
            val email = Email.text.toString().trim()
            val password = Password.text.toString().trim()
            val confirm = ConfirmPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirm) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        val errorMessage = when {
                            task.exception?.message?.contains("already in use") == true ->
                                "Este correo ya está siendo utilizado, intenta con otro"

                            task.exception?.message?.contains("badly formatted") == true ->
                                "El formato del correo no es válido, verifica que esté bien escrito"

                            task.exception?.message?.contains("too many") == true ->
                                "Demasiados intentos, intenta más tarde"

                            else -> "Error al registrarse, verifica tus datos"
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
        }
        Login.setOnClickListener {
            finish()
        }
    }
}
package com.example.firebase

import com.google.firebase.firestore.Exclude

data class Gasto(
    @get:Exclude var documentId: String? = null,
    var titulo: String = "",
    var monto: Double = 0.0,
    var categoria: String = "",
    var tipo: String = "", // "Fijo" o "Libre"
    var usuarioId: String = "",
    var fecha: Long = System.currentTimeMillis()
)

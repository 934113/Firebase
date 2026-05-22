package com.example.firebase

import com.google.firebase.firestore.Exclude

data class Ingreso(
    @get:Exclude var documentId: String? = null,
    var descripcion: String = "",
    var monto: Double = 0.0,
    var usuarioId: String = "",
    var fecha: Long = System.currentTimeMillis()
)

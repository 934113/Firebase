package com.example.firebase

import android.R
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity


class AgregarGastoActivity : AppCompatActivity() {
    var spCategoria: Spinner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_gasto)

        spCategoria = findViewById<Spinner?>(R.id.spCategoria)

        val adapter =
            ArrayAdapter.createFromResource(
                this,
                R.array.categorias,
                R.layout.simple_spinner_item
            )

        adapter.setDropDownViewResource(
            R.layout.simple_spinner_dropdown_item
        )

        spCategoria!!.setAdapter(adapter)
    }
}
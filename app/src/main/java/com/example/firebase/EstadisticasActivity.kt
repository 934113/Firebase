package com.example.firebase

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebase.databinding.ActivityEstadisticasBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EstadisticasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEstadisticasBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEstadisticasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        cargarEstadisticas()
    }

    private fun cargarEstadisticas() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("gastos")
            .whereEqualTo("usuarioId", uid)
            .get()
            .addOnSuccessListener { result ->
                val gastosPorCategoria = mutableMapOf<String, Double>()
                var totalGastos = 0.0

                for (doc in result) {
                    val monto = doc.getDouble("monto") ?: 0.0
                    val cat = doc.getString("categoria") ?: "Otros"
                    gastosPorCategoria[cat] = (gastosPorCategoria[cat] ?: 0.0) + monto
                    totalGastos += monto
                }

                setupPieChart(gastosPorCategoria)

                db.collection("ingresos")
                    .whereEqualTo("usuarioId", uid)
                    .get()
                    .addOnSuccessListener { ingresosResult ->
                        var totalIngresos = 0.0
                        for (doc in ingresosResult) {
                            totalIngresos += doc.getDouble("monto") ?: 0.0
                        }
                        setupBarChart(totalIngresos, totalGastos)
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar estadísticas", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupPieChart(datos: Map<String, Double>) {
        val entries = ArrayList<PieEntry>()
        for ((cat, monto) in datos) {
            entries.add(PieEntry(monto.toFloat(), cat))
        }

        val dataSet = PieDataSet(entries, "Categorías")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextSize = 14f
        dataSet.valueTextColor = Color.WHITE
        
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(binding.pieChartCategorias))

        binding.pieChartCategorias.apply {
            this.data = data
            description.isEnabled = false
            isDrawHoleEnabled = true
            setUsePercentValues(true)
            setEntryLabelColor(Color.BLACK)
            centerText = "Gastos"
            animateY(1000)
            invalidate()
        }
    }

    private fun setupBarChart(ingresos: Double, gastos: Double) {
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f, ingresos.toFloat()))
        entries.add(BarEntry(1f, gastos.toFloat()))

        val dataSet = BarDataSet(entries, "Comparativa")
        dataSet.colors = listOf(Color.GREEN, Color.RED)
        dataSet.valueTextSize = 12f

        val data = BarData(dataSet)
        binding.barChartComparacion.apply {
            this.data = data
            description.isEnabled = false
            setDrawGridBackground(false)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            axisRight.isEnabled = false
            animateY(1000)
            invalidate()
        }
    }
}

package com.example.appmedica

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appmedica.com.example.appmedica.Consulta
import java.util.Calendar
import com.google.firebase.firestore.FirebaseFirestore



class Consultas : AppCompatActivity() {

     private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_consultas)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btn: Button = findViewById(R.id.btn_regresar)
        btn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val editTextTime = findViewById<EditText>(R.id.CHoraConsul)
        editTextTime.setOnClickListener { showTimePickerDialog() }

        val editTextFecha = findViewById<EditText>(R.id.CFechaConsul)
        editTextFecha.setOnClickListener { showDatePickerDialog() }

        val btnSendFeedback = findViewById<Button>(R.id.btn_alta)
        btnSendFeedback.setOnClickListener{
            sendFeedback()
        }

    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerFragment { day, month, year -> onDateSelected(day, month, year) }
        datePicker.show(supportFragmentManager, "datePicker")
    }
    @SuppressLint("SetTextI18n")
    private fun onDateSelected(day: Int, month: Int, year: Int) {
        val editTextFecha = findViewById<EditText>(R.id.CFechaConsul)
        editTextFecha.setText("$year-$month-$day")
    }

    private fun showTimePickerDialog() {
        val timePicker = TimePickerFragment { onTimeSelected(it) }
        timePicker.show(supportFragmentManager, "timePicker")
    }

    private fun onTimeSelected(time: String) {
        val editTextTime = findViewById<EditText>(R.id.CHoraConsul)
        editTextTime.setText("$time")
    }

    private fun sendFeedback() {
        val identificador = findViewById<EditText>(R.id.CIdenti).text.toString()
        val fecha = findViewById<EditText>(R.id.CFechaConsul).text.toString()
        val hora = findViewById<EditText>(R.id.CHoraConsul).text.toString()
        val clinica = findViewById<EditText>(R.id.CClinica).text.toString()
        var nomdoc = findViewById<EditText>(R.id.CDoc).text.toString()
        var teldoc = findViewById<EditText>(R.id.CNumDoc).text.toString()
        // Verificar que los campos no estén vacíos
        if (fecha.isEmpty() || hora.isEmpty() || clinica.isEmpty() || identificador.isEmpty()) {
            Toast.makeText(this, "Por favor, complete los campos con (*)", Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (nomdoc.isEmpty()) {
            nomdoc = "N/A"
        }
        if (teldoc.isEmpty()) {
            teldoc = "N/A"
        }
        val timestamp = System.currentTimeMillis()
        db.collection("consultas")
            .add(hashMapOf(
                "idcons" to identificador,
                "date" to fecha,
                "time" to hora,
                "clinic" to clinica,
                "doctor" to nomdoc,
                "contactdoc" to teldoc,
                "timestamp" to timestamp,
                "estado" to "pendiente"
            ))
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Consulta agendada con exito!!", Toast.LENGTH_SHORT).show()
                fetchLastDocument()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar!!", Toast.LENGTH_SHORT).show()
            }
    }
    private fun fetchLastDocument() {
        val collectionPath = "consultas"

        db.collection(collectionPath)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val document = result.documents[0]
                    val consulta = document.toObject(Consulta::class.java)
                    if (consulta != null){
                        val intent = Intent(this, MostrarConsulta::class.java).apply {
                            putExtra("id", consulta.idcons)
                            putExtra("fecha", consulta.date)
                            putExtra("hora", consulta.time)
                            putExtra("clinica", consulta.clinic)
                            putExtra("doctor", consulta.doctor)
                            putExtra("cont_doc", consulta.contactdoc)
                        }
                        startActivity(intent)
                    }
                }
            }
    }
}
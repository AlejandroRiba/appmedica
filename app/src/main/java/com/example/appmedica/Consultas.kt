package com.example.appmedica

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appmedica.com.example.appmedica.AlarmUtils
import com.example.appmedica.com.example.appmedica.Consulta
import com.example.appmedica.com.example.appmedica.Utilidades
import com.example.appmedica.utils.FirebaseHelper
import com.google.firebase.Timestamp
import java.util.Calendar


class Consultas : AppCompatActivity() {

     private var ultimoRequestCode = 0
     private lateinit var firebaseHelper: FirebaseHelper
    private lateinit var fechaTimestamp: Timestamp
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_consultas)
        firebaseHelper = FirebaseHelper(this)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btn: Button = findViewById(R.id.btn_regresar)
        btn.setOnClickListener{
            finish() //cierra el activity y pasa al anterior
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
        // Crear un objeto Calendar para construir el Timestamp
        val calendar = Calendar.getInstance().apply {
            set(year, month - 1, day, 0, 0, 0) // Resta 1 al mes porque Calendar.MONTH es cero-indexado
            set(Calendar.MILLISECOND, 0)
        }
        fechaTimestamp = Timestamp(calendar.time) // Crea el Timestamp

        val editTextFecha = findViewById<EditText>(R.id.CFechaConsul)
        // Formato de fecha: YYYY-MM-DD
        editTextFecha.setText("$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}")
    }

    private fun showTimePickerDialog() {
        val timePicker = TimePickerFragment { onTimeSelected(it) }
        timePicker.show(supportFragmentManager, "timePicker")
    }

    private fun onTimeSelected(time: String) {
        val editTextTime = findViewById<EditText>(R.id.CHoraConsul)
        editTextTime.setText(time)
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

        val datosCita = mapOf(
            "idcons" to identificador,
            "date" to fecha,
            "time" to hora,
            "clinic" to clinica,
            "doctor" to nomdoc,
            "contactdoc" to teldoc,
            "timestamp" to fechaTimestamp,
            "estado" to "pendiente"
        )
        firebaseHelper.agregarCita(
            datosCita = datosCita
        ).addOnSuccessListener { result ->
            val (exito, citaId) = result // Descomponemos el Pair
            if (exito) {
                // La cita se creó correctamente
                if (citaId != null) {
                    fetchLastDocument(citaId)
                } // Llama a fetchLastDocument()
            } else {
                // No se pudo crear la cita
                Toast.makeText(this, "No se pudo agendar la cita.", Toast.LENGTH_SHORT).show()
                finish()//regresa al activity anterior
            }
        }.addOnFailureListener {
            // Manejo de cualquier otro error
            Toast.makeText(this, "Ocurrió un error al intentar crear la cita.", Toast.LENGTH_SHORT).show()
            finish()//regresa al activity anterior
        }

    }
    private fun fetchLastDocument(citaId: String) {
        // Llama a leerCita y maneja el resultado
        firebaseHelper.leerCita(citaId).addOnSuccessListener { consulta ->
            if (consulta != null) {
                val intent = Intent(this, MostrarConsulta::class.java).apply {
                    putExtra("id", consulta.idcons)
                    putExtra("fecha", consulta.date)
                    putExtra("hora", consulta.time)
                    putExtra("clinica", consulta.clinic)
                    putExtra("doctor", consulta.doctor)
                    putExtra("cont_doc", consulta.contactdoc)
                }
                recordatorios(consulta) // Asegúrate de que este método exista
                finish() // Cierra la actividad actual
                startActivity(intent)
            } else {
                // Manejo del caso en que consulta es null
                Toast.makeText(this, "No se encontró la cita.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            // Manejo de errores en la lectura de la cita
            Log.e("FirebaseHelper", "Error al leer la cita: ${e.message}")
            Toast.makeText(this, "Error al cargar la cita.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun generateUniqueRequestCode(consulta: Consulta): Int {
        return consulta.idcons.hashCode() // Esto generará un requestCode único basado en el ID de la consulta
    }
    private fun recordatorios(consulta: Consulta) {
        val fecha = consulta.date
        val hora = consulta.time
        val mismodia1 = Pair("CITA PENDIENTE AHORA MISMO !", "Tu cita ${consulta.idcons} está registrada para hoy a esta hora.\n Click aquí para ver detalles.")
        val mismodia = Pair("CITA PENDIENTE HOY !", "Tu cita ${consulta.idcons} está registrada para hoy a las ${consulta.time} en ${consulta.clinic}.\n Click aquí para ver detalles.")
        val mismodia2 = Pair("CITA PENDIENTE EN 2 HRS !", "Tu cita ${consulta.idcons} está registrada para hoy a las ${consulta.time} en ${consulta.clinic}.\n Click aquí para ver detalles.")
        val tresantes = Pair("CITA PENDIENTE EN TRES DÍAS !", "Tu cita ${consulta.idcons} está registrada para ${consulta.date} a las ${consulta.time} en ${consulta.clinic}.\n Click aquí para ver detalles.")
        val ayer = Pair("CITA PENDIENTE MAÑANA !", "Tu cita ${consulta.idcons} está registrada para mañana a las ${consulta.time} en ${consulta.clinic}.\n Click aquí para ver detalles.")
        val notifdia = fecha?.let { fecha ->
            hora?.let { hora ->
                Utilidades.obtenerFechaHora(fecha, hora)
            }
        }
        val notifdia5 = fecha?.let { fecha ->
            hora?.let { hora ->
                Utilidades.restarCincoMinutos(fecha, hora)
            }
        }
        val notifantes = fecha?.let { fecha ->
            hora?.let { hora ->
                Utilidades.restarTresDias(fecha, hora)
            }
        }
        val notifantes2 = fecha?.let { fecha ->
            hora?.let { hora ->
                Utilidades.restarDosHoras(fecha, hora)
            }
        }

        val notifayer = fecha?.let { fecha ->
            hora?.let { hora ->
                Utilidades.restarDia(fecha, hora)
            }
        }
        val requestCodeBase = generateUniqueRequestCode(consulta)
        notifantes?.let { AlarmUtils.scheduleNotification(this,it, tresantes, requestCodeBase + 1) }
        notifantes2?.let { AlarmUtils.scheduleNotification(this,it, mismodia2, requestCodeBase + 2) }
        notifayer?.let { AlarmUtils.scheduleNotification(this,it, ayer, requestCodeBase + 3) }
        notifdia?.let { AlarmUtils.scheduleNotification(this,it, mismodia1, requestCodeBase + 4) }
        notifdia5?.let { AlarmUtils.scheduleNotification(this,it, mismodia, requestCodeBase + 5) }
    }

}
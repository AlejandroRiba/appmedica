package com.example.appmedica

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appmedica.com.example.appmedica.AlarmUtils
import com.example.appmedica.com.example.appmedica.Consulta
import com.example.appmedica.com.example.appmedica.Utilidades
import com.example.appmedica.utils.ColorSpinnerAdapter
import com.example.appmedica.utils.FirebaseHelper
import com.example.appmedica.utils.KeyboardUtils
import com.google.firebase.Timestamp
import java.util.Calendar


@Suppress("NAME_SHADOWING")
class Consultas : AppCompatActivity() {

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private lateinit var spinnercolor: Spinner
    private lateinit var selectedcolor: String

    private lateinit var firebaseHelper: FirebaseHelper
    private lateinit var fechaTimestamp: Timestamp

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_consultas)
        firebaseHelper = FirebaseHelper(this)

        // Referencia al ScrollView
        val scrollView = findViewById<ScrollView>(R.id.scrollform) // Asegúrate de tener un ID para el ScrollView
        scrollView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                // Oculta el teclado al tocar cualquier parte del ScrollView
                val view = currentFocus
                if (view != null) {
                    KeyboardUtils.hideKeyboard(this, view)
                    view.clearFocus() // Opcional: quitar el foco del EditText
                }
            }
            false // Retornar false para permitir que otros eventos se manejen
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        spinnercolor = findViewById(R.id.color_spinner)
        selectedcolor = ""
        //Asignacion de opciones y listeners
        inicializaSpinners()

        val textViews = listOf<TextView>(
            findViewById(R.id.lblident),
            findViewById(R.id.lblfecha),
            findViewById(R.id.lblhora),
            findViewById(R.id.lbllugar)
        )
        val redColor = Color.parseColor("#8b1515") // Color rojo en hexadecimal
        for (textView in textViews) {
            // Obtén el texto desde el TextView
            val labelText = textView.text.toString()
            val spannableString = SpannableString(labelText)

            // Aplicar el color rojo al asterisco
            val start = labelText.indexOf("*")
            if (start != -1) { // Verifica si el asterisco existe
                val end = start + 1 // La posición después del asterisco
                spannableString.setSpan(
                    ForegroundColorSpan(redColor), // Color rojo
                    start, // Inicio del asterisco
                    end, // Fin del asterisco
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            // Asignar el SpannableString al TextView
            textView.text = spannableString
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
            if (isInternetAvailable(this)) {
                btnSendFeedback.isEnabled = false //deshabilito el botón para evitar sobrellamadas
                sendFeedback()
            } else {
                Toast.makeText(this, "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun inicializaSpinners() {
        // Obtiene el array de colores desde strings.xml
        val colors = resources.getStringArray(R.array.color_items)
        // Configura el adaptador personalizado
        val adapter = ColorSpinnerAdapter(this, colors.toList())
        spinnercolor.adapter = adapter

        //Spinner color
        spinnercolor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedcolor = parent?.getItemAtPosition(position).toString()
                view?.setBackgroundColor(Color.parseColor(selectedcolor))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedcolor = parent?.getItemAtPosition(0).toString()
            }

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
            "selectedcolor" to selectedcolor,
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
                recordatorios(consulta, citaId) // Asegúrate de que este método exista
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



    private fun recordatorios(consulta: Consulta, citaId: String) {
        val fecha = consulta.date
        val hora = consulta.time


        val fechayreminder = fecha?.let { fecha ->
            hora?.let { hora ->
                Utilidades.obtenerFechaRecordatorio(fecha, hora) //Busca la fecha para el primer recordatorio
            }
        }

        val notifantes = fechayreminder?.first // Accedes al Calendar
        val actualReminder = fechayreminder?.second // Accedes al String

        val mensaje = Utilidades.obtenerMensajeCita(actualReminder?:"ahora", consulta.idcons?:"", consulta.clinic?:"", hora?:"00:00", fecha?:"")

        val requestCodeBase = Utilidades.generateUniqueRequestCode(citaId)

        notifantes?.let {
            AlarmUtils.scheduleNotification(this@Consultas,it, mensaje, requestCodeBase,
                consulta.idcons ?: "", // Consulta id
                consulta.clinic ?: "", // Consulta clínica
                hora ?: "00:00",
                fecha)
        }
    }

}
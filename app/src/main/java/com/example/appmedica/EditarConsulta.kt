package com.example.appmedica

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
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
import android.widget.ImageButton
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

class EditarConsulta: AppCompatActivity()  {

    private lateinit var spinnercolor: Spinner
    private lateinit var selectedcolor: String

    private lateinit var firebaseHelper: FirebaseHelper
    private lateinit var fechaTimestamp: Timestamp

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editarcita)
        firebaseHelper = FirebaseHelper(this)

        // Referencia al ScrollView
        val scrollView =
            findViewById<ScrollView>(R.id.scrollform) // Asegúrate de tener un ID para el ScrollView
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

        //recibir los datos de la actividad de llamadad
        val consulta = intent.getStringExtra("motivo")
        val fecha = intent.getStringExtra("fecha")
        val hora = intent.getStringExtra("hora")
        val clinica = intent.getStringExtra("clinica")
        val doc = intent.getStringExtra("doctor")
        val contacto = intent.getStringExtra("cont_doc")
        val id = intent.getStringExtra("id")
        selectedcolor = intent.getStringExtra("selectedcolor") ?: "#E5B6D7"
        val campo1 = findViewById<EditText>(R.id.CIdenti)
        val editTextTime = findViewById<EditText>(R.id.CHoraConsul)
        val editTextFecha = findViewById<EditText>(R.id.CFechaConsul)
        val campo4 = findViewById<EditText>(R.id.CClinica)
        val campo5 = findViewById<EditText>(R.id.CDoc)
        val campo6 = findViewById<EditText>(R.id.CNumDoc)

        spinnercolor = findViewById(R.id.color_spinner)
        //Asignacion de opciones y listeners
        inicializaSpinners()

        campo1.setText(consulta)
        editTextFecha.setText(fecha)
        editTextTime.setText(hora)
        campo4.setText(clinica)
        if(doc != "N/A"){
            campo5.setText(doc)
        }
        if(contacto != "N/A"){
            campo6.setText(contacto)
        }

        val textViews = listOf<TextView>(
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


        val btn = findViewById<ImageButton>(R.id.btn_regresar)
        btn.setOnClickListener {
            val intent = Intent(this, ListaConsultas::class.java) //para recargar lista de consultas
            finish()//finalizamos la edición
            startActivity(intent) //iniciamos la lista de consultas
        }

        //Definimos que al tocar el editText de la hora se abre el time picker
        editTextTime.setOnClickListener { showTimePickerDialog() }

        //Definimos que al tocar el editText de la fecha se abre el date picker
        editTextFecha.setOnClickListener { showDatePickerDialog() }

        val btnSendFeedback = findViewById<ImageButton>(R.id.btn_alta)
        btnSendFeedback.setOnClickListener{
            sendFeedback(id!!)
        }


        val navCitas = findViewById<ImageButton>(R.id.navCitas)
        navCitas.setOnClickListener{
            val intent = Intent(this, ListaConsultas::class.java)
            finish()
            startActivity(intent)
        }

        val navMedicinas = findViewById<ImageButton>(R.id.navMedicinas)
        navMedicinas.setOnClickListener{
            val intent = Intent(this, ListaMedicamentos::class.java)
            finish()
            startActivity(intent)
        }

        val navPerfil = findViewById<ImageButton>(R.id.navPerfil)
        navPerfil.setOnClickListener{
            val intent = Intent(this, PerfilActivity::class.java)
            finish()
            startActivity(intent)
        }
    }

    private fun sendFeedback(docId: String) {
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
            "selectedcolor" to selectedcolor,
            "contactdoc" to teldoc
        )
        firebaseHelper.actualizarCita(
            identificador = docId,
            nuevosDatos = datosCita
        ).addOnSuccessListener { result ->
            if (result != null) {
                // La cita se actualizo correctamente
                val requestCode = Utilidades.generateUniqueRequestCode(docId)
                AlarmUtils.deleteReminder(this, requestCode)
                recordatorios(result, docId) //Se actualizan los recordatorios
                val intent = Intent(this, ListaConsultas::class.java) //para recargar lista de consultas
                finish()//finalizamos la edición
                Toast.makeText(this, "Cita actualizada con éxito!!.", Toast.LENGTH_SHORT).show()
                startActivity(intent) //iniciamos la lista de consultas
            } else {
                // No se pudo crear la cita
                Toast.makeText(this, "No se pudo actualizar la cita.", Toast.LENGTH_SHORT).show()
                finish()//regresa al activity anterior
            }
        }.addOnFailureListener {
            // Manejo de cualquier otro error
            Toast.makeText(this, "Ocurrió un error al intentar actualizar la cita.", Toast.LENGTH_SHORT).show()
            finish()//regresa al activity anterior
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

    private fun inicializaSpinners() {
        // Obtiene el array de colores desde strings.xml
        val colors = resources.getStringArray(R.array.color_items)
        // Configura el adaptador personalizado
        val adapter = ColorSpinnerAdapter(this, colors.toList())
        spinnercolor.adapter = adapter

        //seleccionado el color original
        val defaultIndex = colors.indexOf(selectedcolor)
        spinnercolor.setSelection(defaultIndex)

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

    private fun showTimePickerDialog() {
        val timePicker = TimePickerFragment { onTimeSelected(it) }
        timePicker.show(supportFragmentManager, "timePicker")
    }

    private fun onTimeSelected(time: String) {
        val editTextTime = findViewById<EditText>(R.id.CHoraConsul)
        editTextTime.setText(time)
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
            AlarmUtils.scheduleNotification(this,it, mensaje, requestCodeBase,
                consulta.idcons ?: "", // Consulta id
                consulta.clinic ?: "", // Consulta clínica
                hora ?: "00:00",
                fecha)
        }
    }

}
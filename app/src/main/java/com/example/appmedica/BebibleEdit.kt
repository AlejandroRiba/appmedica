package com.example.appmedica

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appmedica.com.example.appmedica.AlarmUtils
import com.example.appmedica.com.example.appmedica.Utilidades
import com.example.appmedica.com.example.appmedica.utils.MedicationRepository
import com.example.appmedica.utils.ColorSpinnerAdapter
import com.example.appmedica.utils.FirebaseHelper
import com.example.appmedica.utils.KeyboardUtils
import com.example.appmedica.utils.Medicine
import java.text.SimpleDateFormat
import java.util.Locale

class BebibleEdit : AppCompatActivity() {

    private lateinit var spinnercantidad: Spinner
    private lateinit var spinnerfrecuencia: Spinner
    private lateinit var spinnerprimertoma: Spinner
    private lateinit var spinnerduracion: Spinner
    private lateinit var spinnercolor: Spinner
    private lateinit var spinnermedida: Spinner

    private lateinit var othercantidad: EditText
    private lateinit var otherfrecuencia: EditText
    private lateinit var othermedida: EditText
    private lateinit var otherprimertoma: EditText
    private lateinit var otherduracion: EditText
    private lateinit var nombreEdtext: EditText

    private lateinit var nombre: String
    private lateinit var frecuencia: String
    private lateinit var duracion: String
    private lateinit var cantidad: String
    private lateinit var primertoma: String
    private lateinit var selectedcolor: String
    private lateinit var medidaadmin: String
    private lateinit var id: String

    private lateinit var db : FirebaseHelper
    private lateinit var medRepo : MedicationRepository

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bebible_edit)

        // Detectar toques en la pantalla para ocultar el teclado
        val layout = findViewById<ScrollView>(R.id.scrollform)  // Cambia a tu layout principal
        layout.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                KeyboardUtils.hideKeyboard(this, v)
            }
            false
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)){ v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = FirebaseHelper(this)
        medRepo = MedicationRepository(this)

        // Obtener referencia al Spinner frecuencia
        spinnerfrecuencia = findViewById(R.id.frecuencia)
        // Obtener referencia al Spinner duración
        spinnerduracion = findViewById(R.id.duracion)
        // Obtener referencia al Spinner cantidad/dosis
        spinnercantidad = findViewById(R.id.cantidad)
        // Obtener referencia al Spinner primertoma
        spinnerprimertoma = findViewById(R.id.primertoma)
        //Obtener referencia al Spinner color
        spinnercolor = findViewById(R.id.color_spinner)
        //Obtener referencia al Spinner de medidad de administración
        spinnermedida = findViewById(R.id.medida)

        //Referencia la campo nombre
        nombreEdtext = findViewById(R.id.nombremedicamento)

        //Referencia al campo other de cantidad
        othercantidad = findViewById(R.id.edtext_other_cantidad)
        othercantidad.visibility = View.GONE
        //Referencia al campo other de frecuencia
        otherfrecuencia = findViewById(R.id.edtext_other_frecuencia)
        otherfrecuencia.visibility = View.GONE
        //Referencia al campo other de primertoma
        otherprimertoma = findViewById(R.id.edtext_other_primertoma)
        otherprimertoma.visibility = View.GONE
        //Referencia al campo other de duración
        otherduracion = findViewById(R.id.edtext_other_duracion)
        otherduracion.visibility = View.GONE
        //Referencia al campo other de medida de administración
        othermedida = findViewById(R.id.edtext_other_medida)
        othermedida.visibility = View.GONE

        //CONJUNTO DE DATOS PARA EL SENDFEEDBACK
        id = intent.getStringExtra("id") ?: ""
        nombre = intent.getStringExtra("nombre") ?: ""
        frecuencia = intent.getStringExtra("frecuencia") ?: "* Seleccione una opción"
        duracion = intent.getStringExtra("duracion") ?: "* Seleccione una opción"
        cantidad = intent.getStringExtra("cantidad") ?: "* Seleccione una opción"
        primertoma = intent.getStringExtra("primertoma") ?: "* Seleccione una opción"
        selectedcolor = intent.getStringExtra("selectedcolor") ?: "#E5B6D7"
        medidaadmin = intent.getStringExtra("medida") ?: "* Seleccione una opción"

        nombreEdtext.setText(nombre)

        //Asignacion de opciones y listeners
        inicializaSpinners()

        //Obtener referencia al botón de cancelar
        val cancelar: Button = findViewById(R.id.btn_cancelar)
        //Definimos la acción del botón (regresamos al menú de medicamentos)
        cancelar.setOnClickListener{
            finish()
        }

        //Obtener referencia al botón de guardar
        val guardar: Button = findViewById(R.id.btn_guardar)
        guardar.setOnClickListener { sendFeedback(id) }
    }

    private fun sendFeedback(id: String){
        // Verificar que los campos no estén vacíos
        nombre = findViewById<EditText>(R.id.nombremedicamento).text.toString()
        if (nombre.isEmpty() || cantidad.isEmpty() || frecuencia.isEmpty() || primertoma.isEmpty()|| duracion.isEmpty() || medidaadmin.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        //Si los campos no están vacios
        //variable cantidad
        var cantidadReal: String = cantidad
        if(cantidad == "other"){
            cantidadReal = othercantidad.text.toString().trim() //para eliminar ceros, convertir a entero y luego a string
            cantidadReal = cantidadReal.toInt().toString() + " ml"
        }

        //Para la medida
        var medidaReal: String = medidaadmin
        if(medidaadmin == "other"){
            medidaReal = othermedida.text.toString()
        }

        //Extraemos las horas de la frecuencia
        var frecuenciaReal: String? = frecuencia
        Log.d("MedActivity", "frecuencia recibida $frecuencia")
        if(frecuencia == "other"){
            frecuenciaReal = otherfrecuencia.text.toString().trim() //para eliminar ceros, convertir a entero y luego a string
            frecuenciaReal = frecuenciaReal.toInt().toString()
        } else if(frecuencia != "Con cada comida" && frecuencia != "Con el desayuno" && frecuencia != "Con la comida" && frecuencia != "Con la cena" && frecuencia != "Al despertar" && frecuencia != "Antes de dormir"){
            frecuenciaReal = Utilidades.extractHours(frecuencia)
        }

        db.obtenerUsuario().addOnSuccessListener { usuario ->
            var primertomaReal = ""
            if (usuario != null) {
                Log.d("MedActivity", "Usuario no nulo")
                //Extraemos primer toma de forma asíncrona
                if (primertoma == "other") {
                    primertomaReal = Utilidades.convertirHora(otherprimertoma.text.toString())
                } else if (primertoma == "Ahora mismo") {
                    primertomaReal = Utilidades.obtenerFechaActual()
                } else {
                    when (primertoma) {
                        "Al despertar" -> primertomaReal = Utilidades.convertirHora(usuario.h1.toString())
                        "Antes de dormir" -> primertomaReal = Utilidades.convertirHora(usuario.h2.toString())
                        "Con el desayuno" -> primertomaReal = Utilidades.convertirHora(usuario.h3.toString())
                        "Con la comida" -> primertomaReal = Utilidades.convertirHora(usuario.h4.toString())
                        "Con la cena" -> primertomaReal = Utilidades.convertirHora(usuario.h5.toString())
                    }
                }

                //Extraemos el número de días de la duración
                var duracionReal: String? = duracion
                Log.d("MedActivity", "duración recibida $duracion")
                if(duracion == "other") {
                    duracionReal = otherduracion.text.toString().trim()
                    duracionReal = Utilidades.agregarDias(primertomaReal, duracionReal.toInt())
                }else if(duracion != "Tratamiento continuo"){
                    duracionReal = Utilidades.extractDays(duracion) //Verifico si entra en el formato '## días'
                    duracionReal = Utilidades.agregarDias(primertomaReal, duracionReal.toInt())
                }

                val medicineData = Medicine(
                    nombre = nombre,
                    tipo =  "bebible",
                    dosis =  cantidadReal,
                    frecuencia = frecuenciaReal!!,
                    primertoma = primertomaReal,
                    duracion = duracionReal!!,
                    color = selectedcolor,
                    medida = medidaReal
                )

                Log.d("MedActivity", medicineData.toString())
                medRepo.updateMedicamento(
                    id,
                    medicineData
                ).addOnSuccessListener { result ->
                    if (result != null) {
                        // El medicamento se actualizo correctamente
                        val requestCode = Utilidades.generateUniqueRequestCode(id)
                        AlarmUtils.deleteMedReminder(this, requestCode)
                        recordatorios(result, id) //Se actualizan los recordatorios
                        val intent = Intent(this, ListaMedicamentos::class.java) //para recargar lista de consultas
                        finish()//finalizamos la edición
                        Toast.makeText(this, "Medicamento actualizada con éxito!!.", Toast.LENGTH_SHORT).show()
                        startActivity(intent) //iniciamos la lista de consultas
                    } else {
                        // No se pudo crear la cita
                        Toast.makeText(this, "No se pudo actualizar la cita.", Toast.LENGTH_SHORT).show()
                        finish()//regresa al activity anterior
                    }
                }.addOnFailureListener{
                    // Manejo de cualquier otro error
                    Toast.makeText(this, "Ocurrió un error al intentar actualizar el registro.", Toast.LENGTH_SHORT).show()
                    finish()//regresa al activity anterior
                }

            }

        }
    }

    private fun recordatorios(medication: Medicine, medId: String) {
        val requestCodeBase = Utilidades.generateUniqueRequestCode(medId)
        val tituloNotificacion = "TOMA DE JARABE (${medication.nombre}) PENDIENTE!"
        val mensajeNotificacion = Utilidades.genMensajeMed("bebible", medication.dosis, medication.nombre)
        val calendar = Utilidades.stringToCalendar(medication.primertoma)
        AlarmUtils.scheduleNotificationMedic(this, calendar, tituloNotificacion, mensajeNotificacion, requestCodeBase, medication.frecuencia, medication.duracion, medication.tipo)
    }

    private fun showTimePickerDialog(editText: EditText) {
        val timePicker = TimePickerFragment { onTimeSelected(it, editText) }
        timePicker.show(supportFragmentManager, "timePicker")
    }

    private fun onTimeSelected(time: String, editText: EditText) {
        editText.setText(time)
    }

    private fun inicializaSpinners(){
        // Crear un ArrayAdapter usando el string-array y un layout predeterminado para el spinner 1
        ArrayAdapter.createFromResource(
            this,
            R.array.frecuencia,
            R.layout.spinner_sangres
        ).also { adapter ->
            // Especificar el layout que se utilizará cuando las opciones aparezcan desplegadas
            adapter.setDropDownViewResource(R.layout.spinner_sangres)
            // Aplicar el adaptador al Spinner
            spinnerfrecuencia.adapter = adapter
        }
        //valor por default capturado del extra
        val itemsFrecuencia = resources.getStringArray(R.array.frecuencia)
        val indexFrecuencia = itemsFrecuencia.indexOf(frecuencia)
        if (indexFrecuencia != -1) {
            spinnerfrecuencia.setSelection(indexFrecuencia) // Seleccionar el índice encontrado
        }else{
            Log.i("MedEdit", frecuencia)
            when(frecuencia){
                "8" -> { spinnerfrecuencia.setSelection(1) }
                "12" -> { spinnerfrecuencia.setSelection(2) }
                "24" -> { spinnerfrecuencia.setSelection(3) }
                else -> { spinnerfrecuencia.setSelection(10)
                            otherfrecuencia.setText(frecuencia)}
            }
        }
        // Crear un ArrayAdapter usando el string-array y un layout predeterminado para el spinner 2
        ArrayAdapter.createFromResource(
            this,
            R.array.duracion,
            R.layout.spinner_sangres
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_sangres)
            spinnerduracion.adapter = adapter
        }
        //valor por default capturado del extra
        val itemsDuracion = resources.getStringArray(R.array.duracion)
        val indexDuracion = itemsDuracion.indexOf(duracion)
        if (indexDuracion != -1) {
            spinnerduracion.setSelection(indexDuracion) // Seleccionar el índice encontrado "Tratamiento continuo" si es el caso
        }else{
            val diasaumentados = Utilidades.calcularDiferenciaDias(primertoma, duracion)
            Log.i("MedEdit", "Dias aumentados: $diasaumentados")
            when(diasaumentados){
                3 -> { spinnerduracion.setSelection(1) }
                7 -> { spinnerduracion.setSelection(2) }
                14  -> { spinnerduracion.setSelection(3) }
                30 -> { spinnerduracion.setSelection(4) }
                else -> { spinnerduracion.setSelection(6)
                        otherduracion.setText("$diasaumentados")
                    }
            }
        }
        // Crear un ArrayAdapter usando el string-array y un layout predeterminado para el spinner 3
        ArrayAdapter.createFromResource(
            this,
            R.array.dosis_beb,
            R.layout.spinner_sangres
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_sangres)
            spinnercantidad.adapter = adapter
        }
        //valor por default capturado del extra
        val itemsDosis = resources.getStringArray(R.array.dosis_beb)
        val indexDosis = itemsDosis.indexOf(cantidad)
        if (indexDosis != -1) {
            spinnercantidad.setSelection(indexDosis)
        }else{
            val dosisPersonalizada = Utilidades.extraerNumeros(cantidad)
            Log.i("MedEdit", "Dosis personalizada: $dosisPersonalizada")
            spinnercantidad.setSelection(5) //opción other
            othercantidad.setText("$dosisPersonalizada")
        }
        // Crear un ArrayAdapter usando el string-array y un layout predeterminado para el spinner 4 primer toma
        ArrayAdapter.createFromResource(
            this,
            R.array.primertoma,
            R.layout.spinner_sangres
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_sangres)
            spinnerprimertoma.adapter = adapter
        }
        //valor por default capturado del extra
        val itemsPrimtoma = resources.getStringArray(R.array.primertoma)
        val primerhoraEstimada = Utilidades.buscarHora(primertoma, this)
        Log.i("MedEdit", "Hora extraida: $primerhoraEstimada")
        val indexPrimtoma = itemsPrimtoma.indexOf(primerhoraEstimada)
        if (indexPrimtoma != -1) {
            spinnerprimertoma.setSelection(indexPrimtoma) // Seleccionar el índice encontrado
        }else{
            spinnerprimertoma.setSelection(7) //Otro
            otherprimertoma.setText(primerhoraEstimada)
        }
        // Crear un ArrayAdapter usando el string-array y un layout predeterminado para el spinner 3
        ArrayAdapter.createFromResource(
            this,
            R.array.medida_beb,
            R.layout.spinner_sangres
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_sangres)
            spinnermedida.adapter = adapter
        }
        //valor por default capturado del extra
        val itemsMedida = resources.getStringArray(R.array.medida_beb)
        val indexMedida = itemsMedida.indexOf(medidaadmin)
        Log.i("MedEdit", "Medida: $medidaadmin")
        if (indexMedida != -1) {
            spinnermedida.setSelection(indexMedida) // Seleccionar el índice encontrado
        }else{
            spinnermedida.setSelection(4) //Otro
            othermedida.setText(medidaadmin)
        }
        // Crear un ArrayAdapter para los colores
        // Obtiene el array de colores desde strings.xml
        val colors = resources.getStringArray(R.array.color_items)
        // Configura el adaptador personalizado
        val adapter = ColorSpinnerAdapter(this, colors.toList())
        spinnercolor.adapter = adapter
        //seleccionado el color original
        val defaultIndexcolor = colors.indexOf(selectedcolor)
        spinnercolor.setSelection(defaultIndexcolor)

        //Crear los listeners para detectar cuando la opción es "otro"
        //Spinner cantidad
        spinnercantidad.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (val selectedItem = parent?.getItemAtPosition(position).toString()) {
                    "Otro (ingrese)" -> {
                        othercantidad.visibility = View.VISIBLE
                        cantidad = "other"
                    }
                    "* Seleccione una opción" -> {
                        cantidad = ""
                    }
                    else -> {
                        othercantidad.visibility = View.GONE
                        cantidad = selectedItem
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                othercantidad.visibility = View.GONE //Me aseguro de que no se vea
                cantidad = ""
            }

        }
        //Spinner frecuencia
        spinnerfrecuencia.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (val selectedItem = parent?.getItemAtPosition(position).toString()) {
                    "Otro (ingrese)" -> {
                        otherfrecuencia.visibility = View.VISIBLE
                        frecuencia = "other"
                    }
                    "* Seleccione una opción" -> {
                        frecuencia = ""
                    }
                    else -> {
                        otherfrecuencia.visibility = View.GONE
                        frecuencia = selectedItem
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                otherfrecuencia.visibility = View.GONE //Me aseguro de que no se vea
                frecuencia = ""
            }

        }
        //Spinner primer toma
        otherprimertoma.setOnClickListener { showTimePickerDialog(otherprimertoma) }
        spinnerprimertoma.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (val selectedItem = parent?.getItemAtPosition(position).toString()) {
                    "Otro (ingrese)" -> {
                        otherprimertoma.visibility = View.VISIBLE
                        primertoma = "other"
                    }
                    "* Seleccione una opción" -> {
                        primertoma = ""
                    }
                    else -> {
                        otherprimertoma.visibility = View.GONE
                        primertoma = selectedItem
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                otherprimertoma.visibility = View.GONE //Me aseguro de que no se vea
                primertoma = ""
            }

        }
        //Spinner duracion
        spinnerduracion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (val selectedItem = parent?.getItemAtPosition(position).toString()) {
                    "Otro (ingrese)" -> {
                        otherduracion.visibility = View.VISIBLE
                        duracion = "other"
                    }
                    "* Seleccione una opción" -> {
                        duracion = ""
                    }
                    else -> {
                        otherduracion.visibility = View.GONE
                        duracion = selectedItem
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                otherduracion.visibility = View.GONE //Me aseguro de que no se vea
                duracion = ""
            }

        }
        //Spinner medida de administración
        spinnermedida.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (val selectedItem = parent?.getItemAtPosition(position).toString()) {
                    "Otro (ingrese)" -> {
                        othermedida.visibility = View.VISIBLE
                        medidaadmin = "other"
                    }
                    "* Seleccione una opción" -> {
                        medidaadmin = ""
                    }
                    else -> {
                        othermedida.visibility = View.GONE
                        medidaadmin = selectedItem
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                othermedida.visibility = View.GONE //Me aseguro de que no se vea
                medidaadmin = ""
            }

        }
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

}
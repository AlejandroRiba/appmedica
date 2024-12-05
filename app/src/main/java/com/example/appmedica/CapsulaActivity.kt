package com.example.appmedica

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appmedica.com.example.appmedica.Utilidades
import com.example.appmedica.utils.FirebaseHelper
import com.example.appmedica.utils.KeyboardUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CapsulaActivity : AppCompatActivity() {
    @SuppressLint("ClickableViewAccessibility")

    private lateinit var spinner: Spinner
    private lateinit var spinner2: Spinner
    private lateinit var spinner3: Spinner
    private lateinit var spinner4: Spinner

    private lateinit var othercantidad: EditText
    private lateinit var otherfrecuencia: EditText
    private lateinit var otherprimertoma: EditText
    private lateinit var otherduracion: EditText

    private lateinit var nombre: String
    private lateinit var frecuencia: String
    private lateinit var duracion: String
    private lateinit var cantidad: String
    private lateinit var primertoma: String

    private lateinit var db : FirebaseHelper

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_capsula)

        // Detectar toques en la pantalla para ocultar el teclado
        val layout = findViewById<ScrollView>(R.id.scrollform)  // Cambia a layout principal
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

        // Obtener referencia al Spinner frecuencia
        spinner = findViewById(R.id.frecuencia)
        // Obtener referencia al Spinner duración
        spinner2 = findViewById(R.id.duracion)
        // Obtener referencia al Spinner cantidad/dosis
        spinner3 = findViewById(R.id.cantidad)
        // Obtener referencia al Spinner primertoma
        spinner4 = findViewById(R.id.primertoma)

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

        //CONJUNTO DE DATOS PARA EL SENDFEEDBACK
        nombre = ""
        frecuencia = ""
        duracion = ""
        cantidad = ""
        primertoma = ""

        //Asignacion de opciones y listeners
        inicializaSpinners()

        //Obtener referencia al botón de cancelar
        val cancelar: Button = findViewById(R.id.btn_cancelar)
        //Definimos la acción del botón (regresamos al menú de medicamentos)
        cancelar.setOnClickListener{
            finish() //regresa a la activity anterior
        }

        //Obtener referencia al botón de guardar
        val guardar: Button = findViewById(R.id.btn_guardar)
        guardar.setOnClickListener { sendFeedback() }
    }

    private fun sendFeedback() {
        // Verificar que los campos no estén vacíos
        nombre = findViewById<EditText>(R.id.nombremedicamento).text.toString()
        if (nombre.isEmpty() || cantidad.isEmpty() || frecuencia.isEmpty() || primertoma.isEmpty()|| duracion.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        //Si los campos no están vacios
        //variable cantidad
        var cantidadReal: String = cantidad
        if(cantidad == "other"){
            cantidadReal = othercantidad.text.toString().trim() //para eliminar ceros, convertir a entero y luego a string
            cantidadReal = cantidadReal.toInt().toString() + " cápsulas"
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

                val medicineData = mapOf(
                    "nombre" to nombre,
                    "tipo" to "capsula",
                    "cantidad" to cantidadReal,
                    "frecuencia" to frecuenciaReal,
                    "primertoma" to primertomaReal,
                    "duracion" to duracionReal
                )

                Log.d("MedActivity", medicineData.toString())

            }

        }

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
            spinner.adapter = adapter
        }
        // Crear un ArrayAdapter usando el string-array y un layout predeterminado para el spinner 2
        ArrayAdapter.createFromResource(
            this,
            R.array.duracion,
            R.layout.spinner_sangres
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_sangres)
            spinner2.adapter = adapter
        }
        // Crear un ArrayAdapter usando el string-array y un layout predeterminado para el spinner 3
        ArrayAdapter.createFromResource(
            this,
            R.array.dosis,
            R.layout.spinner_sangres
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_sangres)
            spinner3.adapter = adapter
        }
        // Crear un ArrayAdapter usando el string-array y un layout predeterminado para el spinner 4 primer toma
        ArrayAdapter.createFromResource(
            this,
            R.array.primertoma,
            R.layout.spinner_sangres
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_sangres)
            spinner4.adapter = adapter
        }

        //Crear los listeners para detectar cuando la opción es "otro"
        //Spinner cantidad
        spinner3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
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
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
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
        spinner4.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
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
        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
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
    }

}
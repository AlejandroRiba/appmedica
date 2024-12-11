package com.example.appmedica

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appmedica.com.example.appmedica.Utilidades
import com.example.appmedica.utils.ColorSpinnerAdapter
import com.example.appmedica.utils.FirebaseHelper
import com.example.appmedica.utils.KeyboardUtils

class GotasActivity : AppCompatActivity() {

    private lateinit var spinnercantidad: Spinner
    private lateinit var spinnerfrecuencia: Spinner
    private lateinit var spinnerprimertoma: Spinner
    private lateinit var spinnerduracion: Spinner
    private lateinit var spinnercolor: Spinner
    private lateinit var spinnerlugar: Spinner

    private lateinit var othercantidad: EditText
    private lateinit var otherfrecuencia: EditText
    private lateinit var otherprimertoma: EditText
    private lateinit var otherduracion: EditText

    private lateinit var radioTipoTratamiento: RadioGroup

    private lateinit var nombre: String
    private lateinit var frecuencia: String
    private lateinit var duracion: String
    private lateinit var cantidad: String
    private lateinit var tipoTratamiento: String
    private lateinit var lugarAplicacion: String
    private lateinit var primertoma: String
    private lateinit var selectedcolor: String

    private lateinit var db : FirebaseHelper

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gotas)

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

        //Obtener referencia al Spinner de lugar de administración
        spinnerlugar = findViewById(R.id.lugarDeTratamiendo)

        //Obtener referencia al RadioGroup de tipo de tratamiento
        radioTipoTratamiento = findViewById(R.id.tipoTratamiento)

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
        selectedcolor = ""
        tipoTratamiento = ""
        lugarAplicacion = ""


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
        guardar.setOnClickListener { sendFeedback() }
    }


    private fun sendFeedback() {
        // Verificar que los campos no estén vacíos
        nombre = findViewById<EditText>(R.id.nombremedicamento).text.toString()
        //Obtener el texto del radio button seleccionado
        val selectedRadioButtonId = radioTipoTratamiento.checkedRadioButtonId
        tipoTratamiento = findViewById<RadioButton>(selectedRadioButtonId).text.toString()

        lugarAplicacion = spinnerlugar.selectedItem.toString()

        if (nombre.isEmpty() || cantidad.isEmpty() || frecuencia.isEmpty() || primertoma.isEmpty()|| duracion.isEmpty() || tipoTratamiento.isEmpty() || lugarAplicacion.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        //Si los campos no están vacios
        //variable cantidad
        var cantidadReal: String = cantidad
        if(cantidad == "other"){
            cantidadReal = othercantidad.text.toString().trim() //para eliminar ceros, convertir a entero y luego a string
            cantidadReal = cantidadReal.toInt().toString() + " gotas"
        }



        //Extraemos las horas de la frecuencia
        var frecuenciaReal: String? = frecuencia
        Log.d("MedGotasActivity", "frecuencia recibida $frecuencia")
        if(frecuencia == "other"){
            frecuenciaReal = otherfrecuencia.text.toString().trim() //para eliminar ceros, convertir a entero y luego a string
            frecuenciaReal = frecuenciaReal.toInt().toString()
        } else if(frecuencia != "Con cada comida" && frecuencia != "Con el desayuno" && frecuencia != "Con la comida" && frecuencia != "Con la cena" && frecuencia != "Al despertar" && frecuencia != "Antes de dormir"){
            frecuenciaReal = Utilidades.extractHours(frecuencia)
        }

        db.obtenerUsuario().addOnSuccessListener { usuario ->
            var primertomaReal = ""
            if (usuario != null) {
                Log.d("MedGotasActivity", "Usuario no nulo")
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
                Log.d("MedGotasActivity", "duración recibida $duracion")
                if(duracion == "other") {
                    duracionReal = otherduracion.text.toString().trim()
                    duracionReal = Utilidades.agregarDias(primertomaReal, duracionReal.toInt())
                }else if(duracion != "Tratamiento continuo"){
                    duracionReal = Utilidades.extractDays(duracion) //Verifico si entra en el formato '## días'
                    duracionReal = Utilidades.agregarDias(primertomaReal, duracionReal.toInt())
                }

                val medicineData = mapOf(
                    "nombre" to nombre,
                    "tipo" to "gotas",
                    "cantidad" to cantidadReal,
                    "frecuencia" to frecuenciaReal,
                    "primertoma" to primertomaReal,
                    "duracion" to duracionReal,
                    "selectedcolor" to selectedcolor,
                    "tipoTratamiento" to tipoTratamiento,
                    "lugarAplicacion" to lugarAplicacion

                )

                Log.d("MedGotasActivity", medicineData.toString())

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
            spinnerfrecuencia.adapter = adapter
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
        // Crear un ArrayAdapter usando el string-array y un layout predeterminado para el spinner 3
        ArrayAdapter.createFromResource(
            this,
            R.array.dosis_gotas,
            R.layout.spinner_sangres
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_sangres)
            spinnercantidad.adapter = adapter
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

        // Crear un ArrayAdapter usando el string-array y un layout predeterminado para el spinner de lugar de administración
        ArrayAdapter.createFromResource(
            this,
            R.array.lugar_admin_gotas,
            R.layout.spinner_sangres
            ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_sangres)
            spinnerlugar.adapter = adapter
        }

        // Crear un ArrayAdapter para los colores
        // Obtiene el array de colores desde strings.xml
        val colors = resources.getStringArray(R.array.color_items)
        // Configura el adaptador personalizado
        val adapter = ColorSpinnerAdapter(this, colors.toList())
        spinnercolor.adapter = adapter


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
package com.example.appmedica

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appmedica.databinding.ActivityEditarDatosBinding
import com.example.appmedica.utils.KeyboardUtils
import com.google.firebase.firestore.FirebaseFirestore

class EditarDatos : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivityEditarDatosBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditarDatosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val databaseHandler = DatabaseHandler(applicationContext)
        val usuario = databaseHandler.consultaDatos()
        val nombre = binding.CampoName1
        val edadText = binding.CampoEdad1
        val contactoText = binding.CampoContacto1
        val time1 = binding.CampoHo1
        val time2 = binding.CampoHo2
        val time3 = binding.CampoHo3
        val time4 = binding.CampoHo4
        val time5 = binding.CampoHo5
        nombre.setText(usuario.name)
        edadText.setText(usuario.age.toString())
        contactoText.setText(usuario.contact.toString())
        time1.setText(usuario.h1)
        time2.setText(usuario.h2)
        time3.setText(usuario.h3)
        time4.setText(usuario.h4)
        time5.setText(usuario.h5)

        // Obtener referencia al Spinner
        val spinner: Spinner = findViewById(R.id.tipo_sangre)

        // Crear un ArrayAdapter usando el string-array y un layout predeterminado para el spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.sangres,
            R.layout.spinner_sangres
        ).also { adapter ->
            // Especificar el layout que se utilizará cuando las opciones aparezcan desplegadas
            adapter.setDropDownViewResource(R.layout.spinner_sangres)
            // Aplicar el adaptador al Spinner
            spinner.adapter = adapter
        }

        // Verificar si el valor de sangre de la base de datos esta en el array está en el string-array
        val options = resources.getStringArray(R.array.sangres)
        val index = options.indexOf(usuario.blood)

        if (index != -1) {
            // Si se encontró el índice, selecciona la opción en el Spinner
            spinner.setSelection(index)
        }

        time1.setOnClickListener { showTimePickerDialog( time1) }
        time2.setOnClickListener { showTimePickerDialog( time2) }
        time3.setOnClickListener { showTimePickerDialog( time3) }
        time4.setOnClickListener { showTimePickerDialog( time4) }
        time5.setOnClickListener { showTimePickerDialog( time5) }

        // Referencia al ScrollView
        val scrollView = findViewById<ScrollView>(R.id.scroll_view) // Asegúrate de tener un ID para el ScrollView
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

        val btnSendFeedback = findViewById<Button>(R.id.enviardatosEdit)
        btnSendFeedback.setOnClickListener{
            sendFeedback()
        }
        val btnCancel = findViewById<Button>(R.id.cancelOperacion)
        btnCancel.setOnClickListener{
            finish() //cerramos EditarDatos, envia al activity anterior
        }
    }

    private fun showTimePickerDialog(editText: EditText) {
        val timePicker = TimePickerFragment { onTimeSelected(it, editText) }
        timePicker.show(supportFragmentManager, "timePicker")
    }

    private fun onTimeSelected(time: String, editText: EditText) {
        editText.setText(time)
    }

    private fun sendFeedback(){
        val nombre = findViewById<EditText>(R.id.CampoName1).text.toString()
        val edadText = findViewById<EditText>(R.id.CampoEdad1).text.toString()
        val contactoText = findViewById<EditText>(R.id.CampoContacto1).text.toString()
        // Obtener valores de las horas
        val time1 = findViewById<EditText>(R.id.CampoHo1).text.toString()
        val time2 = findViewById<EditText>(R.id.CampoHo2).text.toString()
        val time3 = findViewById<EditText>(R.id.CampoHo3).text.toString()
        val time4 = findViewById<EditText>(R.id.CampoHo4).text.toString()
        val time5 = findViewById<EditText>(R.id.CampoHo5).text.toString()
        val spinner: Spinner = findViewById(R.id.tipo_sangre)
        val sangretyp = spinner.selectedItem.toString()

        // Verificar que los campos no estén vacíos
        if (nombre.isEmpty() || edadText.isEmpty() || contactoText.isEmpty() || time5.isEmpty()|| time4.isEmpty()|| time3.isEmpty()|| time2.isEmpty()|| time1.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val edad = edadText.toInt()

        // Verificar que se haya seleccionado al menos una opción de sangre
        if(sangretyp == "* Seleccione una opción"){
            Toast.makeText(this, "Por favor, seleccione un tipo de sangre.", Toast.LENGTH_SHORT).show()
            return
        }
        val databaseHandler = DatabaseHandler(applicationContext)
        val usuarioAnt = databaseHandler.consultaAdulto()
        changeDocumentId(usuarioAnt,nombre)
        // Llamar a la función de actualización
        val success = databaseHandler.actualizarUsuario(usuarioAnt, nombre, edad, sangretyp, contactoText, time1, time2, time3, time4, time5)

        if (success) {
            Toast.makeText(this, "Edición exitosa", Toast.LENGTH_SHORT).show()
            finish() // Regresar a la actividad anterior
        } else {
            Toast.makeText(this, "Error al actualizar los datos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun changeDocumentId(oldUserId: String, newUserId: String) {
        val consultasRef = db.collection("usuarios").document(oldUserId).collection("citas")
        val destinationRef = db.collection("usuarios").document(newUserId).collection("citas")

        consultasRef.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    // Copiar el documento a la nueva ubicación
                    destinationRef.document(document.id).set(document.data)
                        .addOnSuccessListener {
                            // Documento copiado con éxito, ahora eliminar el original
                            consultasRef.document(document.id).delete()
                                .addOnFailureListener { e ->
                                    // Manejar errores al eliminar el documento original
                                    Toast.makeText(this, "Error al eliminar consulta original: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                        .addOnFailureListener { e ->
                            // Manejar errores al copiar el documento
                            Toast.makeText(this, "Error al copiar consulta: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                // Manejar errores al obtener las consultas originales
                Toast.makeText(this, "Error al obtener las consultas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}



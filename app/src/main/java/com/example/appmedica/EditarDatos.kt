package com.example.appmedica

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appmedica.databinding.ActivityEditarDatosBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class EditarDatos : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivityEditarDatosBinding

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
        initDate()
        if (usuario.blood == "A+") {
            binding.op2.isChecked = true
        } else if(usuario.blood == "A-"){
            binding.op1.isChecked = true
        }else if(usuario.blood == "B+"){
            binding.op4.isChecked = true
        }else if(usuario.blood == "B-"){
            binding.op3.isChecked = true
        }else if(usuario.blood == "O+"){
            binding.op6.isChecked = true
        }else if(usuario.blood == "O-"){
            binding.op5.isChecked = true
        }else if(usuario.blood == "AB+"){
            binding.op8.isChecked = true
        }else if(usuario.blood == "AB-"){
            binding.op7.isChecked = true
        }

        time1.setOnClickListener { showTimePickerDialog( time1) }
        time2.setOnClickListener { showTimePickerDialog( time2) }
        time3.setOnClickListener { showTimePickerDialog( time3) }
        time4.setOnClickListener { showTimePickerDialog( time4) }
        time5.setOnClickListener { showTimePickerDialog( time5) }

        val btnSendFeedback = findViewById<Button>(R.id.enviardatosEdit)
        btnSendFeedback.setOnClickListener{
            sendFeedback()
        }
        val btnCancel = findViewById<Button>(R.id.cancelOperacion)
        btnCancel.setOnClickListener{
            val intent = Intent(this, Ajustes::class.java)
            startActivity(intent)
        }
    }

    private fun initDate(){
        checkBox()
    }

    private fun checkBox(){
        binding.op1.setOnCheckedChangeListener { _: CompoundButton, _:Boolean ->
            if(binding.op1.isChecked){
                binding.op2.isEnabled =false
                binding.op3.isEnabled =false
                binding.op4.isEnabled =false
                binding.op5.isEnabled =false
                binding.op6.isEnabled =false
                binding.op7.isEnabled =false
                binding.op8.isEnabled =false
            }
            else if(!binding.op1.isChecked){
                binding.op2.isEnabled = true
                binding.op3.isEnabled = true
                binding.op4.isEnabled = true
                binding.op5.isEnabled = true
                binding.op6.isEnabled = true
                binding.op7.isEnabled = true
                binding.op8.isEnabled = true
            }
        }
        binding.op2.setOnCheckedChangeListener { _: CompoundButton, _:Boolean ->
            if(binding.op2.isChecked){
                binding.op1.isEnabled =false
                binding.op3.isEnabled =false
                binding.op4.isEnabled =false
                binding.op5.isEnabled =false
                binding.op6.isEnabled =false
                binding.op7.isEnabled =false
                binding.op8.isEnabled =false
            }
            else if(!binding.op2.isChecked){
                binding.op1.isEnabled = true
                binding.op3.isEnabled = true
                binding.op4.isEnabled = true
                binding.op5.isEnabled = true
                binding.op6.isEnabled = true
                binding.op7.isEnabled = true
                binding.op8.isEnabled = true
            }
        }
        binding.op3.setOnCheckedChangeListener { _: CompoundButton, _:Boolean ->
            if(binding.op3.isChecked){
                binding.op2.isEnabled =false
                binding.op1.isEnabled =false
                binding.op4.isEnabled =false
                binding.op5.isEnabled =false
                binding.op6.isEnabled =false
                binding.op7.isEnabled =false
                binding.op8.isEnabled =false
            }
            else if(!binding.op3.isChecked){
                binding.op2.isEnabled = true
                binding.op1.isEnabled = true
                binding.op4.isEnabled = true
                binding.op5.isEnabled = true
                binding.op6.isEnabled = true
                binding.op7.isEnabled = true
                binding.op8.isEnabled = true
            }
        }
        binding.op4.setOnCheckedChangeListener { _: CompoundButton, _:Boolean ->
            if(binding.op4.isChecked){
                binding.op2.isEnabled =false
                binding.op3.isEnabled =false
                binding.op1.isEnabled =false
                binding.op5.isEnabled =false
                binding.op6.isEnabled =false
                binding.op7.isEnabled =false
                binding.op8.isEnabled =false
            }
            else if(!binding.op4.isChecked){
                binding.op2.isEnabled = true
                binding.op3.isEnabled = true
                binding.op1.isEnabled = true
                binding.op5.isEnabled = true
                binding.op6.isEnabled = true
                binding.op7.isEnabled = true
                binding.op8.isEnabled = true
            }
        }
        binding.op5.setOnCheckedChangeListener { _: CompoundButton, _:Boolean ->
            if(binding.op5.isChecked){
                binding.op2.isEnabled =false
                binding.op3.isEnabled =false
                binding.op4.isEnabled =false
                binding.op1.isEnabled =false
                binding.op6.isEnabled =false
                binding.op7.isEnabled =false
                binding.op8.isEnabled =false
            }
            else if(!binding.op5.isChecked){
                binding.op2.isEnabled = true
                binding.op3.isEnabled = true
                binding.op4.isEnabled = true
                binding.op1.isEnabled = true
                binding.op6.isEnabled = true
                binding.op7.isEnabled = true
                binding.op8.isEnabled = true
            }
        }
        binding.op6.setOnCheckedChangeListener { _: CompoundButton, _:Boolean ->
            if(binding.op6.isChecked){
                binding.op2.isEnabled =false
                binding.op3.isEnabled =false
                binding.op4.isEnabled =false
                binding.op5.isEnabled =false
                binding.op1.isEnabled =false
                binding.op7.isEnabled =false
                binding.op8.isEnabled =false
            }
            else if(!binding.op6.isChecked){
                binding.op2.isEnabled = true
                binding.op3.isEnabled = true
                binding.op4.isEnabled = true
                binding.op5.isEnabled = true
                binding.op1.isEnabled = true
                binding.op7.isEnabled = true
                binding.op8.isEnabled = true
            }
        }
        binding.op7.setOnCheckedChangeListener { _: CompoundButton, _:Boolean ->
            if(binding.op7.isChecked){
                binding.op2.isEnabled =false
                binding.op3.isEnabled =false
                binding.op4.isEnabled =false
                binding.op5.isEnabled =false
                binding.op6.isEnabled =false
                binding.op1.isEnabled =false
                binding.op8.isEnabled = false

            }
            else if(!binding.op7.isChecked){
                binding.op2.isEnabled = true
                binding.op3.isEnabled = true
                binding.op4.isEnabled = true
                binding.op5.isEnabled = true
                binding.op6.isEnabled = true
                binding.op1.isEnabled = true
                binding.op8.isEnabled = true
            }
        }
        binding.op8.setOnCheckedChangeListener { _: CompoundButton, _:Boolean ->
            if(binding.op8.isChecked){
                binding.op2.isEnabled =false
                binding.op3.isEnabled =false
                binding.op4.isEnabled =false
                binding.op5.isEnabled =false
                binding.op6.isEnabled =false
                binding.op7.isEnabled =false
                binding.op1.isEnabled =false
            }
            else if(!binding.op8.isChecked){
                binding.op2.isEnabled = true
                binding.op3.isEnabled = true
                binding.op4.isEnabled = true
                binding.op5.isEnabled = true
                binding.op6.isEnabled = true
                binding.op7.isEnabled = true
                binding.op1.isEnabled = true
            }
        }
    }

    fun onSendFeedbackButtonClicked(view: View) {
      sendFeedback()
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
        val checkBoxOption1 = findViewById<CheckBox>(R.id.op1)
        val checkBoxOption2 = findViewById<CheckBox>(R.id.op2)
        val checkBoxOption3 = findViewById<CheckBox>(R.id.op3)
        val checkBoxOption4 = findViewById<CheckBox>(R.id.op4)
        val checkBoxOption5 = findViewById<CheckBox>(R.id.op5)
        val checkBoxOption6 = findViewById<CheckBox>(R.id.op6)
        val checkBoxOption7 = findViewById<CheckBox>(R.id.op7)
        val checkBoxOption8 = findViewById<CheckBox>(R.id.op8)
        var sangretyp = ""

        // Verificar que los campos no estén vacíos
        if (nombre.isEmpty() || edadText.isEmpty() || contactoText.isEmpty() || time5.isEmpty()|| time4.isEmpty()|| time3.isEmpty()|| time2.isEmpty()|| time1.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val edad = edadText.toInt()
        val contacto = contactoText.toInt()

        // Verificar que se haya seleccionado al menos una opción de sangre
        if(checkBoxOption1.isChecked){
            sangretyp = "A-"
        } else if(checkBoxOption2.isChecked){
            sangretyp = "A+"
        } else if(checkBoxOption3.isChecked){
            sangretyp = "B-"
        } else if(checkBoxOption4.isChecked){
            sangretyp = "B+"
        } else if(checkBoxOption5.isChecked){
            sangretyp = "O-"
        } else if(checkBoxOption6.isChecked){
            sangretyp = "O+"
        } else if(checkBoxOption7.isChecked){
            sangretyp = "AB-"
        } else if(checkBoxOption8.isChecked) {
            sangretyp = "AB+"
        } else{
            Toast.makeText(this, "Por favor, seleccione una opción", Toast.LENGTH_SHORT).show()
            return
        }
        val databaseHandler = DatabaseHandler(applicationContext)
        val usuarioAnt = databaseHandler.consultaAdulto()
        databaseHandler.eliminarTodosLosUsuarios()
        changeDocumentId(usuarioAnt,nombre)
        databaseHandler.agregarPersona(nombre,edad,contacto,sangretyp,time1,time2,time3,time4,time5)
        Toast.makeText(this, "Edición con exito!!", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, Ajustes::class.java)
        startActivity(intent)
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



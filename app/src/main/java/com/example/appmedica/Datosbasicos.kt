package com.example.appmedica

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.CompoundButton
import android.util.Log
import android.widget.EditText
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.appmedica.databinding.ActivityDatosbasicosBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import androidx.appcompat.app.AppCompatActivity as AppCompatActivity1

class Datosbasicos : AppCompatActivity1() {

    private val pickMedia =  registerForActivityResult(ActivityResultContracts.PickVisualMedia()){ uri ->
        if(uri!=null){
            //Se selecciono una imagen
            Log.i("aria", "Seleccionado")
            imagePeril.setImageURI(uri)
            // Convertir URI a Bitmap
            val bitmap = uriToBitmap(uri)

            // Guardar el Bitmap en los archivos internos
            val filename = "imagen_perfil.png"
            saveImageToInternalStorage(this, bitmap, filename)
        }else{
            //no se selecciono nada
            Log.i("aria", "NO seleccionado")
        }
    }

    private lateinit var binding: ActivityDatosbasicosBinding
    private lateinit var imagePeril: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDatosbasicosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDate()

        imagePeril = findViewById(R.id.fotodeusuario)
        imagePeril.setOnClickListener{
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        val editTextTime1 = findViewById<EditText>(R.id.CampoHora1)
        val editTextTime2 = findViewById<EditText>(R.id.CampoHora2)
        val editTextTime3 = findViewById<EditText>(R.id.CampoHora3)
        val editTextTime4 = findViewById<EditText>(R.id.CampoHora4)
        val editTextTime5 = findViewById<EditText>(R.id.CampoHora5)

        editTextTime1.setOnClickListener { showTimePickerDialog( editTextTime1) }
        editTextTime2.setOnClickListener { showTimePickerDialog( editTextTime2) }
        editTextTime3.setOnClickListener { showTimePickerDialog( editTextTime3) }
        editTextTime4.setOnClickListener { showTimePickerDialog( editTextTime4) }
        editTextTime5.setOnClickListener { showTimePickerDialog( editTextTime5) }

        val btnSendFeedback = findViewById<Button>(R.id.enviardatos)
        btnSendFeedback.setOnClickListener{
                sendFeedback()
        }
    }

    private fun initDate(){
        checkBox()
    }

    private fun showTimePickerDialog(editText: EditText) {
        val timePicker = TimePickerFragment { onTimeSelected(it, editText) }
        timePicker.show(supportFragmentManager, "timePicker")
    }

    private fun onTimeSelected(time: String, editText: EditText) {
        editText.setText(time)
    }

    private fun checkBox(){
        binding.option1.setOnCheckedChangeListener { _: CompoundButton, _:Boolean ->
            if(binding.option1.isChecked){
                binding.option2.isEnabled =false
                binding.option3.isEnabled =false
                binding.option4.isEnabled =false
                binding.option5.isEnabled =false
                binding.option6.isEnabled =false
                binding.option7.isEnabled =false
                binding.option8.isEnabled =false
            }
            else if(!binding.option1.isChecked){
                binding.option2.isEnabled = true
                binding.option3.isEnabled = true
                binding.option4.isEnabled = true
                binding.option5.isEnabled = true
                binding.option6.isEnabled = true
                binding.option7.isEnabled = true
                binding.option8.isEnabled = true
            }
        }
        binding.option2.setOnCheckedChangeListener { _: CompoundButton, _:Boolean ->
            if(binding.option2.isChecked){
                binding.option1.isEnabled =false
                binding.option3.isEnabled =false
                binding.option4.isEnabled =false
                binding.option5.isEnabled =false
                binding.option6.isEnabled =false
                binding.option7.isEnabled =false
                binding.option8.isEnabled =false
            }
            else if(!binding.option2.isChecked){
                binding.option1.isEnabled = true
                binding.option3.isEnabled = true
                binding.option4.isEnabled = true
                binding.option5.isEnabled = true
                binding.option6.isEnabled = true
                binding.option7.isEnabled = true
                binding.option8.isEnabled = true
            }
        }
        binding.option3.setOnCheckedChangeListener { _: CompoundButton, _:Boolean ->
            if(binding.option3.isChecked){
                binding.option2.isEnabled =false
                binding.option1.isEnabled =false
                binding.option4.isEnabled =false
                binding.option5.isEnabled =false
                binding.option6.isEnabled =false
                binding.option7.isEnabled =false
                binding.option8.isEnabled =false
            }
            else if(!binding.option3.isChecked){
                binding.option2.isEnabled = true
                binding.option1.isEnabled = true
                binding.option4.isEnabled = true
                binding.option5.isEnabled = true
                binding.option6.isEnabled = true
                binding.option7.isEnabled = true
                binding.option8.isEnabled = true
            }
        }
        binding.option4.setOnCheckedChangeListener { _: CompoundButton, _:Boolean ->
            if(binding.option4.isChecked){
                binding.option2.isEnabled =false
                binding.option3.isEnabled =false
                binding.option1.isEnabled =false
                binding.option5.isEnabled =false
                binding.option6.isEnabled =false
                binding.option7.isEnabled =false
                binding.option8.isEnabled =false
            }
            else if(!binding.option4.isChecked){
                binding.option2.isEnabled = true
                binding.option3.isEnabled = true
                binding.option1.isEnabled = true
                binding.option5.isEnabled = true
                binding.option6.isEnabled = true
                binding.option7.isEnabled = true
                binding.option8.isEnabled = true
            }
        }
        binding.option5.setOnCheckedChangeListener { _: CompoundButton, _:Boolean ->
            if(binding.option5.isChecked){
                binding.option2.isEnabled =false
                binding.option3.isEnabled =false
                binding.option4.isEnabled =false
                binding.option1.isEnabled =false
                binding.option6.isEnabled =false
                binding.option7.isEnabled =false
                binding.option8.isEnabled =false
            }
            else if(!binding.option5.isChecked){
                binding.option2.isEnabled = true
                binding.option3.isEnabled = true
                binding.option4.isEnabled = true
                binding.option1.isEnabled = true
                binding.option6.isEnabled = true
                binding.option7.isEnabled = true
                binding.option8.isEnabled = true
            }
        }
        binding.option6.setOnCheckedChangeListener { _: CompoundButton, _:Boolean ->
            if(binding.option6.isChecked){
                binding.option2.isEnabled =false
                binding.option3.isEnabled =false
                binding.option4.isEnabled =false
                binding.option5.isEnabled =false
                binding.option1.isEnabled =false
                binding.option7.isEnabled =false
                binding.option8.isEnabled =false
            }
            else if(!binding.option6.isChecked){
                binding.option2.isEnabled = true
                binding.option3.isEnabled = true
                binding.option4.isEnabled = true
                binding.option5.isEnabled = true
                binding.option1.isEnabled = true
                binding.option7.isEnabled = true
                binding.option8.isEnabled = true
            }
        }
        binding.option7.setOnCheckedChangeListener { _: CompoundButton, _:Boolean ->
            if(binding.option7.isChecked){
                binding.option2.isEnabled =false
                binding.option3.isEnabled =false
                binding.option4.isEnabled =false
                binding.option5.isEnabled =false
                binding.option6.isEnabled =false
                binding.option1.isEnabled =false
                binding.option8.isEnabled =false
            }
            else if(!binding.option7.isChecked){
                binding.option2.isEnabled = true
                binding.option3.isEnabled = true
                binding.option4.isEnabled = true
                binding.option5.isEnabled = true
                binding.option6.isEnabled = true
                binding.option1.isEnabled = true
                binding.option8.isEnabled = true
            }
        }
        binding.option8.setOnCheckedChangeListener { _: CompoundButton, _:Boolean ->
            if(binding.option8.isChecked){
                binding.option2.isEnabled =false
                binding.option3.isEnabled =false
                binding.option4.isEnabled =false
                binding.option5.isEnabled =false
                binding.option6.isEnabled =false
                binding.option7.isEnabled =false
                binding.option1.isEnabled =false
            }
            else if(!binding.option8.isChecked){
                binding.option2.isEnabled = true
                binding.option3.isEnabled = true
                binding.option4.isEnabled = true
                binding.option5.isEnabled = true
                binding.option6.isEnabled = true
                binding.option7.isEnabled = true
                binding.option1.isEnabled = true
            }
        }
    }

    private fun sendFeedback(){
        val nombre = findViewById<EditText>(R.id.CampoName).text.toString()
        val edadText = findViewById<EditText>(R.id.CampoEdad).text.toString()
        val contactoText = findViewById<EditText>(R.id.CampoContacto).text.toString()
        // Obtener valores de las horas
        val time1 = findViewById<EditText>(R.id.CampoHora1).text.toString()
        val time2 = findViewById<EditText>(R.id.CampoHora2).text.toString()
        val time3 = findViewById<EditText>(R.id.CampoHora3).text.toString()
        val time4 = findViewById<EditText>(R.id.CampoHora4).text.toString()
        val time5 = findViewById<EditText>(R.id.CampoHora5).text.toString()
        val checkBoxOption1 = findViewById<CheckBox>(R.id.option1)
        val checkBoxOption2 = findViewById<CheckBox>(R.id.option2)
        val checkBoxOption3 = findViewById<CheckBox>(R.id.option3)
        val checkBoxOption4 = findViewById<CheckBox>(R.id.option4)
        val checkBoxOption5 = findViewById<CheckBox>(R.id.option5)
        val checkBoxOption6 = findViewById<CheckBox>(R.id.option6)
        val checkBoxOption7 = findViewById<CheckBox>(R.id.option7)
        val checkBoxOption8 = findViewById<CheckBox>(R.id.option8)
        var sangretyp = ""

        // Verificar que los campos no estén vacíos
        if (nombre.isEmpty() || edadText.isEmpty() || contactoText.isEmpty() || time5.isEmpty()|| time4.isEmpty()|| time3.isEmpty()|| time2.isEmpty()|| time1.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val edad = edadText.toInt()

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
        databaseHandler.agregarPersona(nombre,edad,contactoText,sangretyp,time1,time2,time3,time4,time5)
        Toast.makeText(this, "Registro con exito!!", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, Ajustes::class.java)
        startActivity(intent)
    }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(contentResolver, uri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun saveImageToInternalStorage(context: Context, bitmap: Bitmap?, filename: String): Boolean {
        if (bitmap == null) return false

        return try {
            // Crear o abrir el archivo en los archivos internos
            val file = File(context.filesDir, filename)
            val outputStream = FileOutputStream(file)

            // Comprimir y guardar el bitmap en el archivo como PNG
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

            outputStream.flush()
            outputStream.close()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}
package com.example.appmedica

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Button
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.appmedica.databinding.ActivityDatosbasicosBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appmedica.utils.FirebaseHelper
import com.example.appmedica.utils.KeyboardUtils

class Datosbasicos : AppCompatActivity() {

    private val pickMedia =  registerForActivityResult(ActivityResultContracts.PickVisualMedia()){ uri ->
        if(uri!=null){
            //Se selecciono una imagen
            Log.i("aria", "Seleccionado")
            imagePeril.setImageURI(uri)
            // Convertir URI a Bitmap
            val bitmap = uriToBitmap(uri)

            // Guardar el Bitmap en los archivos internos
            saveImageToInternalStorage(this, bitmap)
        }else{
            //no se selecciono nada
            Log.i("aria", "NO seleccionado")
        }
    }

    private lateinit var binding: ActivityDatosbasicosBinding
    private lateinit var imagePeril: ImageView
    private val filename = "imagen_perfil.png"

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDatosbasicosBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imagePeril = findViewById(R.id.fotodeusuario)
        imagePeril.setOnClickListener{
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

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

    private fun showTimePickerDialog(editText: EditText) {
        val timePicker = TimePickerFragment { onTimeSelected(it, editText) }
        timePicker.show(supportFragmentManager, "timePicker")
    }

    private fun onTimeSelected(time: String, editText: EditText) {
        editText.setText(time)
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
        databaseHandler.agregarPersona(nombre,edad,contactoText,sangretyp,time1,time2,time3,time4,time5)
        val firebaseHelper = FirebaseHelper(this)
        val usuarioData = mapOf(
            "name" to nombre,
            "age" to edad,
            "contact" to contactoText,
            "blood" to sangretyp,
            "h1" to time1,
            "h2" to time2,
            "h3" to time3,
            "h4" to time4,
            "h5" to time5
        )
        firebaseHelper.agregarUsuario(usuarioData)
        Toast.makeText(this, "Registro con exito!!", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() //cerramos Datosbasicos
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

    private fun saveImageToInternalStorage(context: Context, bitmap: Bitmap?): Boolean {
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
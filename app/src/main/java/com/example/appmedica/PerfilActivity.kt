package com.example.appmedica

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appmedica.R.id.main
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PerfilActivity : ComponentActivity() {

    private val pickMedia =  registerForActivityResult(ActivityResultContracts.PickVisualMedia()){ uri ->
        if(uri!=null){
            //Se selecciono una imagen
            Log.i("aria", "Seleccionado")
            foto.setImageURI(uri)
            // Convertir URI a Bitmap
            val bitmap = uriToBitmap(uri)

            // Guardar el Bitmap en los archivos internos
            saveImageToInternalStorage(this, bitmap)
        }else{
            //no se selecciono nada
            Log.i("aria", "NO seleccionado")
        }
    }

    private lateinit var imagePeril: Button
    private lateinit var foto: ImageView
    private val filename = "imagen_perfil.png"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Recuperar la imagen desde los archivos internos
        val bitmap = loadImageFromInternalStorage()

        // Mostrar la imagen en el ImageView
        foto = findViewById(R.id.fotodeusuario)
        bitmap?.let {
            foto.setImageBitmap(it)
        }

        imagePeril = findViewById(R.id.editarFoto)
        imagePeril.setOnClickListener{
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        cargarDatosUsuario() //hacemos la consulta a la base y los insertamos

        //Referencia al botón de editar datoa
        val editBtn = findViewById<Button>(R.id.editarDatos)
        editBtn.setOnClickListener {
            showDialog {
                val intent = Intent(this, EditarDatos::class.java)
                startActivity(intent)
                //finish() //para que no se guarde la activity con los datos viejos
            }
        }
        //Referencia al botón de regresar
        val btnback = findViewById<Button>(R.id.btn_regresar)
        btnback.setOnClickListener {
            finish() //cierra la actividad actual y regresa a la anterior
        }

    }

    override fun onResume() {
        super.onResume()
        cargarDatosUsuario() //refrescamos los datos
    }

    private fun cargarDatosUsuario(){
        //Obtenemos los datos del usuario
        val databaseHandler = DatabaseHandler(applicationContext)
        val usuario = databaseHandler.consultaDatos()
        //Referencias a los campos en el layout
        val nombre = findViewById<TextView>(R.id.textViewNombre)
        val edadText = findViewById<TextView>(R.id.textViewEdad)
        val sangreText = findViewById<TextView>(R.id.textViewSangre)
        val contactoText = findViewById<TextView>(R.id.textViewContacto)

        // Asignar los textos con colores diferentes
        nombre.text = createColoredText("Nombre", usuario.name.toString())
        edadText.text = createColoredText("Edad", usuario.age.toString())
        sangreText.text = createColoredText("Sangre", usuario.blood.toString())
        contactoText.text = createColoredText("Contacto", usuario.contact.toString())
    }

    // Función para crear un texto con dos partes de colores diferentes
    fun createColoredText(label: String, content: String): SpannableString {
        // Color negro (puedes cambiarlo por un color de tu elección)
        val blackColor = ContextCompat.getColor(this, android.R.color.black)

        val fullText = "$label: $content"
        val spannableString = SpannableString(fullText)

        // Cambia el color del contenido (lo que va después de los dos puntos)
        spannableString.setSpan(
            ForegroundColorSpan(blackColor),
            fullText.indexOf(":") + 2, // Inicio del texto después de los dos puntos
            fullText.length, // Hasta el final del texto
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return spannableString
    }

    @SuppressLint("SetTextI18n")
    private fun showDialog(listener: () -> Unit) {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_confirm, null)
        builder.setView(view)

        val textMessage = view.findViewById<TextView>(R.id.text_message)
        textMessage.text = "Editará sus datos.\n¿Está seguro?"

        val btnCancel = view.findViewById<Button>(R.id.btn_cancel)
        val btnConfirm = view.findViewById<Button>(R.id.btn_confirm)

        val dialog = builder.create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            listener.invoke()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun loadImageFromInternalStorage(): Bitmap? {
        return try {
            // Crear la ruta del archivo donde se guardó la imagen
            val file = File(filesDir, filename)
            if (file.exists()) {
                // Decodificar el archivo en un Bitmap
                BitmapFactory.decodeFile(file.absolutePath)
            } else {
                null // El archivo no existe
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
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



package com.example.appmedica

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appmedica.com.example.appmedica.AlarmUtils
import com.example.appmedica.utils.FirebaseHelper
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File

class Ajustes : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ajustes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val databaseHandler = DatabaseHandler(applicationContext)
        val usuarioActual = databaseHandler.consultaAdulto()
        val firebaseHelper = FirebaseHelper(this)
        //Referencia al botón de regreso
        val btn = findViewById<ImageButton>(R.id.btn_regresar)
        btn.setOnClickListener{
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
            finish()
        }

        val btnShowDialog: LinearLayout = findViewById(R.id.row4)
        btnShowDialog.setOnClickListener {
            showDialog("Borrará el registro creado.\n" +
                    "¿Está seguro?") {
                showDialog("¿Está completamente seguro?") {
                    databaseHandler.eliminarTodosLosUsuarios()
                    firebaseHelper.eliminarUsuario()
                    AlarmUtils.cancelAllAlarms(this) //cancela los recordatorios
                    AlarmUtils.cancelAllMedAlarms(this)
                    val filename = "imagen_perfil.png"
                    deleteImageFromInternalStorage(this, filename) //eliminar foto de perfil
                    val intent = Intent(this, ListaConsultas::class.java)
                    startActivity(intent)
                    finish() //cerramos la actividad de ajustes para que entre directo al main
                }
            }
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
    private fun showDialog(message: String, listener: () -> Unit) {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_confirm, null)
        builder.setView(view)

        val textMessage = view.findViewById<TextView>(R.id.text_message)
        textMessage.text = message

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

    private fun deleteImageFromInternalStorage(context: Context, filename: String): Boolean { //eliminar la foto de perfil
        return try {
            // Crear la ruta del archivo donde se guardó la imagen
            val file = File(context.filesDir, filename)
            if (file.exists()) {
                // Eliminar el archivo
                file.delete()
            } else {
                // El archivo no existe
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
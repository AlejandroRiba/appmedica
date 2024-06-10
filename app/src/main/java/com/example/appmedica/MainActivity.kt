package com.example.appmedica

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.os.Build
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appmedica.com.example.appmedica.AlarmNotification
import com.example.appmedica.com.example.appmedica.AlarmNotification.Companion.NOTIFICATION_ID
import com.example.appmedica.com.example.appmedica.MyApp
import com.example.appmedica.com.example.appmedica.Utilidades
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.RemoteMessage
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setTheme(R.style.Theme_AppMedica);
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val databaseHandler = DatabaseHandler(applicationContext)
        val client = databaseHandler.consultaAdulto()
        if(client == "Usuario"){
            val intent = Intent(this, Datosbasicos::class.java)
            startActivity(intent)
            finish()
        }

        val textView: TextView = findViewById(R.id.bienvenida)
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        if (client != null) {
            val firstName = client.split(" ")[0]
            val bienvenida: String = when {
                hour < 12 -> "Buenos días, \n$firstName"
                hour < 18 -> "Buenas tardes, \n$firstName"
                else -> "Buenas noches, \n$firstName"
            }
            textView.text = bienvenida
        }


        val btn: Button = findViewById(R.id.agrmed)
        btn.setOnClickListener {
            val intent = Intent(this, MedicamentosActivity::class.java)
            startActivity(intent)
        }

        val btn2: Button = findViewById(R.id.agrconsul)
        btn2.setOnClickListener {
            val intent = Intent(this, Consultas::class.java)
            startActivity(intent)
        }
        val btn3: ImageButton = findViewById(R.id.imageButton6)
        btn3.setOnClickListener {
            val intent = Intent(this, Ajustes::class.java)
            startActivity(intent)
        }
        val btn4: Button = findViewById(R.id.button2)
        btn4.setOnClickListener {
            val intent = Intent(this, ListaConsultas::class.java)
            startActivity(intent)
        }

        verificarRegistros()

    }


    private fun verificarRegistros() {
        val databaseHandler = DatabaseHandler(applicationContext)
        val nombre = databaseHandler.consultaAdulto()
        val consulta = db.collection("usuarios")
            .document(nombre)
            .collection("citas")
            .whereEqualTo("estado","pendiente")
        consulta.get()
            .addOnSuccessListener { result ->
                val hayRegistros = !result.isEmpty
                val textoBoton = if (hayRegistros) "TIENES CITAS PENDIENTES \uD83D\uDEA8" else "SIN PROXIMAS CITAS ✅"
                // Obtener referencia al botón y establecer el texto
                val boton = findViewById<Button>(R.id.button2)
                boton.text = textoBoton
            }
    }

}
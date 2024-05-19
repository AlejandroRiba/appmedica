package com.example.appmedica

import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class MainActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
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
            val bienvenida: String = when {
                hour < 12 -> "Buenos días, $client"
                hour < 18 -> "Buenas tardes, $client"
                else -> "Buenas noches, $client"
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
        val consulta = db.collection("consultas").whereEqualTo("estado","pendiente")
        consulta.get()
            .addOnSuccessListener { result ->
                val hayRegistros = !result.isEmpty
                val textoBoton = if (hayRegistros) "TIENES CITAS PENDIENTES \uD83D\uDEA8" else "SIN PROXIMAS CITAS"
                // Obtener referencia al botón y establecer el texto
                val boton = findViewById<Button>(R.id.button2)
                boton.text = textoBoton
            }
    }
}
package com.example.appmedica

import com.example.appmedica.R
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
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
                hour < 12 -> "Buenos días, \n$firstName ☀\uFE0F"
                hour < 18 -> "Buenas tardes, \n$firstName \uD83C\uDF25\uFE0F"
                else -> "Buenas noches, \n$firstName \uD83C\uDF19"
            }
            textView.text = bienvenida
        }

        val cardViewconsulta = findViewById<CardView>(R.id.agrconsul)
        // Hacer el CardView clickeable
        cardViewconsulta.isClickable = true
        cardViewconsulta.isFocusable = true
        cardViewconsulta.setOnClickListener {
            val intent = Intent(this, Consultas::class.java)
            startActivity(intent)
        }

        val cardViewmed = findViewById<CardView>(R.id.agrmed)
        // Hacer el CardView clickeable
        cardViewmed.isClickable = true
        cardViewmed.isFocusable = true
        cardViewmed.setOnClickListener {
            val intent = Intent(this, MedicamentosActivity::class.java)
            startActivity(intent)
        }

        val cardViewajustes = findViewById<CardView>(R.id.ajustes)
        // Hacer el CardView clickeable
        cardViewajustes.isClickable = true
        cardViewajustes.isFocusable = true
        cardViewajustes.setOnClickListener {
            val intent = Intent(this, Ajustes::class.java)
            startActivity(intent)
        }

        val cardViewrecordatorio = findViewById<CardView>(R.id.recordatorios)
        // Hacer el CardView clickeable
        cardViewrecordatorio.isClickable = true
        cardViewrecordatorio.isFocusable = true
        cardViewrecordatorio.setOnClickListener {
            val intent = Intent(this, ListaConsultas::class.java)
            startActivity(intent)
        }

        val cardViewperfil = findViewById<CardView>(R.id.perfil)
        // Hacer el CardView clickeable
        cardViewperfil.isClickable = true
        cardViewperfil.isFocusable = true
        cardViewperfil.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }

        //verificarRegistros()

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
                val boton = findViewById<Button>(R.id.recordatorios)
                boton.text = textoBoton
            }
    }

}
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
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    }
}
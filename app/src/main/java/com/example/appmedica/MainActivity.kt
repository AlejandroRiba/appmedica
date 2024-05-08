package com.example.appmedica

import android.os.Bundle
import android.widget.Button
import android.content.Intent
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

        val textView: TextView = findViewById(R.id.bienvenida)

        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        val texto: String = when {
            hour < 12 -> "Buenos días, [Nombre]"
            hour < 18 -> "Buenas tardes, [Nombre]"
            else -> "Buenas noches, [Nombre]"
        }
        textView.text = texto

        val btn: Button = findViewById(R.id.agrmed)
        btn.setOnClickListener{
            val intent = Intent(this, MedicamentosActivity::class.java)
            startActivity(intent)
        }

        val btn2: Button = findViewById(R.id.agrconsul)
        btn2.setOnClickListener{
            val intent = Intent(this, datosbasicos::class.java)
            startActivity(intent)
        }
    }
}
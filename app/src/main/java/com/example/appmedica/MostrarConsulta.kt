package com.example.appmedica

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MostrarConsulta : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mostrar_consulta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Codigo
        val t1 = findViewById<TextView>(R.id.tex1)
        val t2 = findViewById<TextView>(R.id.tex2)
        val t3 = findViewById<TextView>(R.id.tex3)
        val t4 = findViewById<TextView>(R.id.tex4)
        val t5 = findViewById<TextView>(R.id.tex5)
        val t6 = findViewById<TextView>(R.id.tex6)
        val consulta = intent.getStringExtra("id")
        val fecha = intent.getStringExtra("fecha")
        val hora = intent.getStringExtra("hora")
        val clinica = intent.getStringExtra("clinica")
        val doc = intent.getStringExtra("doctor")
        val contacto = intent.getStringExtra("cont_doc")

        t1.setText(consulta)
        t2.setText(fecha)
        t3.setText(hora)
        t4.setText(clinica)
        t5.setText(doc)
        t6.setText(contacto)

        val btn: ImageButton = findViewById(R.id.back2)
        btn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
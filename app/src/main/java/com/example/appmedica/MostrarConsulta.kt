package com.example.appmedica

import android.content.Intent
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
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

        // Define las etiquetas y los datos
        val etiquetas = arrayOf("Motivo", "Fecha", "Hora", "Clínica", "Nombre del Doctor", "Contacto del Doctor")
        val datos = arrayOf("$consulta", "$fecha", "$hora", "$clinica", "$doc", "$contacto")

        // Itera sobre todos los TextViews y aplica el formato
        val textViews = arrayOf(t1, t2, t3, t4, t5, t6)

        textViews.forEachIndexed { index, textView ->
            val etiqueta = etiquetas[index]
            val dato = datos[index]
            textView.text = createColoredText(etiqueta, dato)
        }

        val btn: Button = findViewById(R.id.back2)
        btn.setOnClickListener {
            val intent = Intent(this, ListaConsultas::class.java) //para recargar lista de consultas
            finish()
            startActivity(intent)
        }

    }

    // Función para crear un texto con dos partes de colores diferentes
    fun createColoredText(label: String, content: String): SpannableString {
        // Color negro (puedes cambiarlo por un color de tu elección)
        val custom = Color.parseColor("#1F2D3A")

        val fullText = "$label: $content"
        val spannableString = SpannableString(fullText)

        // Cambia el color del contenido (lo que va después de los dos puntos)
        spannableString.setSpan(
            ForegroundColorSpan(custom),
            fullText.indexOf(":") + 2, // Inicio del texto después de los dos puntos
            fullText.length, // Hasta el final del texto
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return spannableString
    }

}

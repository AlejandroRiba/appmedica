package com.example.appmedica

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MostrarMedicamento : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mostrar_medicamento)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Codigo
        val t1 = findViewById<TextView>(R.id.tex1) //Nombre del medicamento
        val t2 = findViewById<TextView>(R.id.tex2) //Dosis del medicamento
        val t3 = findViewById<TextView>(R.id.tex3) //Frecuencia de toma
        val t4 = findViewById<TextView>(R.id.tex4) //Primer toma
        val t5 = findViewById<TextView>(R.id.tex5) //Tipo de medicamento
        val t6 = findViewById<TextView>(R.id.tex6) //Duración del tratamiento
        val t7 = findViewById<TextView>(R.id.text7) //Zona de aplicación (solo si es el caso)
        val t8 = findViewById<TextView>(R.id.text8) //Medida de administración
        val t9 = findViewById<TextView>(R.id.text9) //Color
        val nombre = intent.getStringExtra("nombre") ?: ""
        val dosis = intent.getStringExtra("dosis") ?: ""
        val frecuencia = intent.getStringExtra("frecuencia") ?: ""
        val primertoma = intent.getStringExtra("primertoma") ?: ""
        val tipo = intent.getStringExtra("tipo") ?: ""
        val duracion = intent.getStringExtra("duracion") ?: ""
        val zonaApl = intent.getStringExtra("zonaApl") ?: ""
        val medidaAdmin = intent.getStringExtra("medidaAdmin") ?: ""
        val color = intent.getStringExtra("color") ?: ""

        if(zonaApl.isEmpty())
            t7.visibility = View.GONE

        if(medidaAdmin.isEmpty())
            t8.visibility = View.GONE

        // Define las etiquetas y los datos
        val etiquetas = arrayOf("Nombre", "Dosis", "Frecuencia de toma", "Primer toma", "Tipo de medicamento", "Duración del tratamiento", "Zona de aplicación", "Medida de administración", "Color")
        val datos = arrayOf(nombre, dosis, frecuencia, primertoma, tipo, duracion, zonaApl, medidaAdmin, color)

        // Itera sobre todos los TextViews y aplica el formato
        val textViews = arrayOf(t1, t2, t3, t4, t5, t6, t7, t8, t9)

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
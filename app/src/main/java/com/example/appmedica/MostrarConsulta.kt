package com.example.appmedica

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import android.graphics.Typeface
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
        val etiquetas = arrayOf("Motivo:", "Fecha:", "Hora:", "Clínica:", "Nombre del Doctor:", "Contacto del Doctor:")
        val datos = arrayOf("$consulta", "$fecha", "$hora", "$clinica", "$doc", "$contacto")

        // Itera sobre todos los TextViews y aplica el formato
        val textViews = arrayOf(t1, t2, t3, t4, t5, t6)

        textViews.forEachIndexed { index, textView ->
            val etiqueta = etiquetas[index]
            val dato = datos[index]

            if (!(dato == "N/A" && (index == 4 || index == 5))) {
                // Crea un SpannableStringBuilder para construir el texto con diferentes estilos
                val builder = SpannableStringBuilder()

                // Agrega la etiqueta con estilo normal y color negro
                builder.append(etiqueta)
                builder.setSpan(ForegroundColorSpan(Color.BLACK), 0, etiqueta.length, 0)

                // Agrega el dato con estilo cursiva y color #4C4D4E
                builder.append(" $dato")
                builder.setSpan(
                    StyleSpan(Typeface.ITALIC),
                    builder.length - dato.length,
                    builder.length,
                    0
                )
                builder.setSpan(
                    ForegroundColorSpan(Color.parseColor("#4C4D4E")),
                    builder.length - dato.length,
                    builder.length,
                    0
                )

                // Asigna el texto formateado al TextView correspondiente
                textView.text = builder
            } else {
                // Si el dato es "n/a" y es el nombre del doctor o el contacto del doctor, oculta el TextView correspondiente
                textView.visibility = TextView.GONE
            }
        }

            val btn: Button = findViewById(R.id.back2)
            btn.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
    }
}

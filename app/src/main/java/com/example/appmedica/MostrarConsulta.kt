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
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appmedica.com.example.appmedica.AlarmUtils
import com.example.appmedica.com.example.appmedica.Utilidades
import com.example.appmedica.utils.FirebaseHelper

class MostrarConsulta : AppCompatActivity() {
    private lateinit var firebaseHelper: FirebaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mostrar_consulta)
        firebaseHelper = FirebaseHelper(this)
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
        val docID = intent.getStringExtra("docID")
        val color = intent.getStringExtra("selectedcolor")

        // Define las etiquetas y los datos
        val datos = arrayOf("$consulta", "$hora", "$clinica", "$doc", "$contacto")

        // Itera sobre todos los TextViews y aplica el formato
        val textViews = arrayOf(t1, t3, t4, t5, t6)

        textViews.forEachIndexed { index, textView ->
            textView.text = datos[index]
        }

        t2.text = dateFormatting("$fecha")

        val regViewContainer = findViewById<View>(R.id.colorRef)
        val regViewBG = regViewContainer.background
        try {
            regViewBG.setTint(Color.parseColor("$color"))
        } catch (e: IllegalArgumentException) {
            // Si el color no es válido, usa un color por defecto
            regViewBG.setTint(Color.parseColor("#FFFFFF")) // Blanco por defecto
        }

        val btn = findViewById<ImageButton>(R.id.back)
        btn.setOnClickListener {
            val intent = Intent(this, ListaConsultas::class.java) //para recargar lista de consultas
            finish()
            startActivity(intent)
        }

        val btnAsistido = findViewById<Button>(R.id.btn_asistido)
        val btnDelete = findViewById<Button>(R.id.btn_delete)
        val btnEditar = findViewById<ImageButton>(R.id.edit)

        btnAsistido.setOnClickListener {
            firebaseHelper.marcarAsistido("$docID")
            val intent = Intent(this, ListaConsultas::class.java)
            finish()//finalizamos la lista de consultas
            startActivity(intent) //la volvemos a abrir para que se actualice
        }

        val requestCode = Utilidades.generateUniqueRequestCode("$docID")
        btnDelete.setOnClickListener {
            AlarmUtils.deleteReminder(this, requestCode)
            firebaseHelper.eliminarCita("$docID", this)
            val intent = Intent(this, ListaConsultas::class.java) //para recargar lista de consultas
            finish()//finalizamos la lista de consultas
            startActivity(intent) //la volvemos a abrir para que se actualice
        }

        btnEditar.setOnClickListener {
            val intent = Intent(this, EditarConsulta::class.java).apply {
                putExtra("motivo", "$consulta")
                putExtra("fecha", "$fecha")
                putExtra("hora", "$hora")
                putExtra("clinica", "$clinica")
                putExtra("doctor", "$doc")
                putExtra("cont_doc", "$contacto")
                putExtra("id", "$docID")
                putExtra("selectedcolor", "$color")
            }
            finish() //finalizamos la lista de consultas
            startActivity(intent)
        }

        val navCitas = findViewById<ImageButton>(R.id.navCitas)
        navCitas.setOnClickListener{
            val intent = Intent(this, ListaConsultas::class.java)
            startActivity(intent)
        }

        val navMedicinas = findViewById<ImageButton>(R.id.navMedicinas)
        navMedicinas.setOnClickListener{
            val intent = Intent(this, ListaMedicamentos::class.java)
            startActivity(intent)
        }

        val navPerfil = findViewById<ImageButton>(R.id.navPerfil)
        navPerfil.setOnClickListener{
            val intent = Intent(this, PerfilActivity::class.java)
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

    // Cambia el formato de las fechas
    fun dateFormatting(date: String): String {
        var meses = arrayOf("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")

        var anio = date.substring(0, 4)
        var mes = meses[(date.substring(5, 7).toInt()) - 1]
        var dia = date.substring(8)

        return "$dia - $mes - $anio"
    }

}

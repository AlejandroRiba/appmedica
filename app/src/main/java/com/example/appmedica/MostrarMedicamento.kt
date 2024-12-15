package com.example.appmedica

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appmedica.com.example.appmedica.AlarmUtils
import com.example.appmedica.com.example.appmedica.Utilidades
import com.example.appmedica.com.example.appmedica.utils.MedicationRepository
import com.example.appmedica.utils.FirebaseHelper
import com.example.appmedica.utils.Medicine

class MostrarMedicamento : AppCompatActivity() {
    private lateinit var firebaseHelper: FirebaseHelper
    private lateinit var medRepo: MedicationRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mostrar_medicamento)
        firebaseHelper = FirebaseHelper(this)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        medRepo = MedicationRepository(this)

        //Codigo
        val t1 = findViewById<TextView>(R.id.tex1) //Nombre del medicamento
        val t2 = findViewById<TextView>(R.id.tex2) //Dosis del medicamento
        val t3 = findViewById<TextView>(R.id.tex3) //Frecuencia de toma
        val t4 = findViewById<TextView>(R.id.tex4) //Primer toma
        val t5 = findViewById<TextView>(R.id.tex5) //Tipo de medicamento
        val t6 = findViewById<TextView>(R.id.tex6) //Duración del tratamiento
        val t7 = findViewById<TextView>(R.id.text7) //Zona de aplicación (solo si es el caso)
        val t8 = findViewById<TextView>(R.id.text8) //Medida de administración

        val zonaTitle = findViewById<TextView>(R.id.tex7title)
        val zonaDiv = findViewById<View>(R.id.zonaDiv)
        val medidaTitle = findViewById<TextView>(R.id.tex8title)
        val medidaDiv = findViewById<View>(R.id.medidaDiv)

        val nombre = intent.getStringExtra("nombre") ?: ""
        val dosis = intent.getStringExtra("dosis") ?: ""
        val frecuencia = intent.getStringExtra("frecuencia") ?: ""
        val primertoma = intent.getStringExtra("primertoma") ?: ""
        val tipo = intent.getStringExtra("tipo") ?: ""
        val duracion = intent.getStringExtra("duracion") ?: ""
        val zonaApl = intent.getStringExtra("zonaApl") ?: ""
        val medidaAdmin = intent.getStringExtra("medidaAdmin") ?: ""
        val color = intent.getStringExtra("color") ?: ""
        val docID = intent.getStringExtra("docID")
        val medicamento = intent.getStringExtra("medicamentoDado")

        if(zonaApl.isEmpty()) {
            t7.visibility = View.GONE
            zonaTitle.visibility = View.GONE
            zonaDiv.visibility = View.GONE
        }

        if(medidaAdmin.isEmpty()) {
            t8.visibility = View.GONE
            medidaTitle.visibility = View.GONE
            medidaDiv.visibility = View.GONE
        }

        // Define las etiquetas y los datos
        val datos = arrayOf(nombre, dosis, frecuencia, primertoma, duracion, tipo, zonaApl, medidaAdmin, color)

        // Itera sobre todos los TextViews y aplica el formato
        val textViews = arrayOf(t1, t2, t3, t4, t5, t6, t7, t8)

        textViews.forEachIndexed { index, textView ->
            val dato = datos[index]
            textView.text = dato
        }


        val regViewContainer = findViewById<View>(R.id.colorRef)
        val regViewBG = regViewContainer.background
        try {
            regViewBG.setTint(Color.parseColor("$color"))
        } catch (e: IllegalArgumentException) {
            // Si el color no es válido, usa un color por defecto
            regViewBG.setTint(Color.parseColor("#FFFFFF")) // Blanco por defecto
        }

        val btn = findViewById<ImageButton>(R.id.back2)
        btn.setOnClickListener {
            val intent = Intent(this, ListaMedicamentos::class.java) //para recargar lista de consultas
            finish()
            startActivity(intent)
        }

        val btnEditar = findViewById<ImageButton>(R.id.edit)
        btnEditar.setOnClickListener {
            Log.d("AbrirActividad", "$medicamento")
            abrirActividad(tipo, "$docID", datos)
        }

        val requestCode = Utilidades.generateUniqueRequestCode("$docID")
        val btnEliminar = findViewById<Button>(R.id.btn_delete)
        btnEliminar.setOnClickListener {
            AlarmUtils.deleteMedReminder(this, requestCode)
            medRepo.eliminarMedicamento("$docID", this)
            val intent = Intent(this, ListaMedicamentos::class.java) //para recargar lista de consultas
            finish()//finalizamos la lista de consultas
            startActivity(intent) //la volvemos a abrir para que se actualice
        }

        val navCitas = findViewById<ImageButton>(R.id.navCitas)
        navCitas.setOnClickListener{
            val intent = Intent(this, ListaConsultas::class.java)
            finish()
            startActivity(intent)
        }

        val navMedicinas = findViewById<ImageButton>(R.id.navMedicinas)
        navMedicinas.setOnClickListener{
            val intent = Intent(this, ListaMedicamentos::class.java)
            finish()
            startActivity(intent)
        }

        val navPerfil = findViewById<ImageButton>(R.id.navPerfil)
        navPerfil.setOnClickListener{
            val intent = Intent(this, PerfilActivity::class.java)
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

    // Función auxiliar para abrir actividades
    // ORDEN: nombre, dosis, frecuencia, primertoma, duracion, tipo, zonaApl, medidaAdmin, color
    private fun abrirActividad(tipo: String, medId: String, datos: Array<String>) {
        Log.d("AbrirActividad", "Tipo recibido: $tipo")
        val intent = when (tipo) {
            "Cápsula" -> Intent(this, CapsulaEdit::class.java).apply {
                putExtra("id", medId)
                putExtra("nombre", datos[0])
                putExtra("frecuencia", datos[2])
                putExtra("duracion", datos[4])
                putExtra("cantidad", datos[1])
                putExtra("selectedcolor", datos[8])
                putExtra("primertoma", datos[3])
            }
            "Tableta" -> Intent(this, TabletaEdit::class.java).apply {
                putExtra("id", medId)
                putExtra("nombre", datos[0])
                putExtra("frecuencia", datos[2])
                putExtra("duracion", datos[4])
                putExtra("cantidad", datos[1])
                putExtra("selectedcolor", datos[8])
                putExtra("primertoma", datos[3])
            }
            "Bebible" -> Intent(this, BebibleEdit::class.java).apply {
                putExtra("id", medId)
                putExtra("nombre", datos[0])
                putExtra("frecuencia", datos[2])
                putExtra("duracion", datos[4])
                putExtra("cantidad", datos[1])
                putExtra("selectedcolor", datos[8])
                putExtra("primertoma", datos[3])
                putExtra("medida", datos[7])
            }
            "Gotas" -> Intent(this, GotasEdit::class.java).apply {
                putExtra("id", medId)
                putExtra("nombre", datos[0])
                putExtra("frecuencia", datos[2])
                putExtra("duracion", datos[4])
                putExtra("cantidad", datos[1])
                putExtra("selectedcolor", datos[8])
                putExtra("primertoma", datos[3])
                putExtra("lugarApl", datos[6])
            }
            "Inyectable" -> Intent(this, InyectableEdit::class.java).apply {
                putExtra("id", medId)
                putExtra("nombre", datos[0])
                putExtra("frecuencia", datos[2])
                putExtra("duracion", datos[4])
                putExtra("cantidad", datos[1])
                putExtra("selectedcolor", datos[8])
                putExtra("primertoma", datos[3])
                putExtra("lugarApl", datos[6])
            }
            else -> {
                Log.e("AbrirActividad", "Tipo no reconocido: $tipo")
                null
            }
        }

        if (intent != null) {
            Log.d("AbrirActividad", "Iniciando actividad para tipo: $tipo")
            startActivity(intent)
        } else {
            Log.e("AbrirActividad", "No se pudo iniciar actividad. Tipo desconocido.")
        }
    }
}
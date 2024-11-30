package com.example.appmedica

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appmedica.com.example.appmedica.AlarmUtils
import com.example.appmedica.com.example.appmedica.Consulta
import com.example.appmedica.com.example.appmedica.Utilidades
import com.example.appmedica.utils.FirebaseHelper

class ListaConsultas : AppCompatActivity() {
    private lateinit var container: LinearLayout
    private lateinit var firebaseHelper: FirebaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lista_consultas)
        firebaseHelper = FirebaseHelper(this)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        container = findViewById(R.id.container_layout)

        // Consultar los registros y mostrarlos

        fetchDataFromFirestore()

        val btn: Button = findViewById(R.id.btn_regresar)
        btn.setOnClickListener{
            finish()
        }
        val btn2 : Button = findViewById(R.id.btn_nuevaconsul)
        btn2.setOnClickListener {
            val intent = Intent(this, Consultas::class.java)
            finish()//Finaliza la lista de consultas actual
            startActivity(intent)
        }

    }
    @Suppress("SameParameterValue")
    private fun convertirStringADP(valor: String): Int {
        val dpRegex = Regex(pattern = "([0-9]+)dp")
        val matchResult = dpRegex.find(valor)
        val dpValue = matchResult?.groups?.get(1)?.value?.toIntOrNull() ?: 0
        return dpToPx(dpValue)
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }
    @SuppressLint("InflateParams", "SetTextI18n")
    private fun fetchDataFromFirestore() {
        val progressBar = findViewById<ImageView>(R.id.imageViewLoading)
        progressBar.visibility = View.VISIBLE
        progressBar.layoutParams.width = convertirStringADP("100dp")
        progressBar.layoutParams.height = convertirStringADP("100dp")
        firebaseHelper.obtenerCitas()
            .addOnSuccessListener { citas ->
                // Aquí tienes la lista de citas
                if (citas.isNotEmpty()) {
                    progressBar.visibility = View.INVISIBLE
                    container.removeAllViews() // Limpia el contenedor antes de agregar nuevas vistas

                    for ((cita, documentId) in citas) {
                        try {
                            val registroView = layoutInflater.inflate(R.layout.item_registro, container, false)
                            val textViewNombre = registroView.findViewById<TextView>(R.id.textViewNombre)
                            val textViewFecha = registroView.findViewById<TextView>(R.id.textViewFecha)
                            val textViewHora = registroView.findViewById<TextView>(R.id.textViewHora)
                            val textViewClinica = registroView.findViewById<TextView>(R.id.textViewClinica)
                            val textViewDoctor = registroView.findViewById<TextView>(R.id.textViewDoctor)
                            val textViewContacto = registroView.findViewById<TextView>(R.id.textViewContacto)

                            textViewNombre.text = "${cita.idcons}"
                            textViewFecha.text = createColoredText("Fecha", cita.date!!)
                            textViewHora.text = createColoredText("Hora", cita.time!!)
                            textViewClinica.text = createColoredText("Clínica", cita.clinic!!)
                            textViewDoctor.text = createColoredText("Doctor", cita.doctor!!)
                            textViewContacto.text = createColoredText("Contacto", cita.contactdoc!!)

                            container.addView(registroView)
                            val btnAjustes = registroView.findViewById<ImageButton>(R.id.btnOptions)
                            btnAjustes.setOnClickListener {
                                showDialog(documentId, cita)
                            }
                            val mostrarCon = registroView.findViewById<LinearLayout>(R.id.consulta)
                            mostrarCon.setOnClickListener {
                                val intent = Intent(this, MostrarConsulta::class.java).apply {
                                    putExtra("id", cita.idcons)
                                    putExtra("fecha", cita.date)
                                    putExtra("hora", cita.time)
                                    putExtra("clinica", cita.clinic)
                                    putExtra("doctor", cita.doctor)
                                    putExtra("cont_doc", cita.contactdoc)
                                }
                                finish()
                                startActivity(intent)
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this, "Error al procesar la cita.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "No se encontraron consultas.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al obtener consultas.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDialog(idcons: String, cita: Consulta) {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_ajustes_consulta, null)
        builder.setView(view)

        val btnCancel = view.findViewById<Button>(R.id.btn_cancel)
        val btnAsistido = view.findViewById<Button>(R.id.btn_asistido)
        val btnDelete = view.findViewById<Button>(R.id.btn_delete)
        val btnEditar = view.findViewById<Button>(R.id.btn_editar)

        val dialog = builder.create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnAsistido.setOnClickListener {
            firebaseHelper.marcarAsistido(idcons)
            dialog.dismiss()
            val intent = Intent(this, ListaConsultas::class.java)
            finish()//finalizamos la lista de consultas
            startActivity(intent) //la volvemos a abrir para que se actualice
        }

        val requestCode = Utilidades.generateUniqueRequestCode(idcons)
        btnDelete.setOnClickListener {
            AlarmUtils.deleteReminder(this, requestCode)
            firebaseHelper.eliminarCita(idcons, this)
            dialog.dismiss()
            val intent = Intent(this, ListaConsultas::class.java) //para recargar lista de consultas
            finish()//finalizamos la lista de consultas
            startActivity(intent) //la volvemos a abrir para que se actualice
        }

        btnEditar.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, EditarConsulta::class.java).apply {
                putExtra("motivo", cita.idcons)
                putExtra("fecha", cita.date)
                putExtra("hora", cita.time)
                putExtra("clinica", cita.clinic)
                putExtra("doctor", cita.doctor)
                putExtra("cont_doc", cita.contactdoc)
                putExtra("id", idcons)
            }
            finish() //finalizamos la lista de consultas
            startActivity(intent)
        }

        dialog.show()
    }

    // Función para crear un texto con dos partes de colores diferentes
    fun createColoredText(label: String, content: String): SpannableString {
        // Color negro (puedes cambiarlo por un color de tu elección)
        val custom = ContextCompat.getColor(this, R.color.texto)

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
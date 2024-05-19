package com.example.appmedica

import android.app.AlertDialog
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.FirebaseFirestore

class ListaConsultas : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var container: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lista_consultas)
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
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        val btn2 : Button = findViewById(R.id.btn_nuevaconsul)
        btn2.setOnClickListener {
            val intent = Intent(this, Consultas::class.java)
            startActivity(intent)
        }

    }
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
    private fun fetchDataFromFirestore() {
        val progressBar = findViewById<ImageView>(R.id.imageViewLoading)
        progressBar.visibility = View.VISIBLE
        progressBar.layoutParams.width = convertirStringADP("100dp")
        progressBar.layoutParams.height = convertirStringADP("100dp")
        val consulta = db.collection("consultas")// Filtra por estado = "pendiente"
            .orderBy("date", Query.Direction.ASCENDING)
        consulta.get()
            .addOnSuccessListener { result ->
                progressBar.visibility = View.INVISIBLE
                progressBar.layoutParams.width = convertirStringADP("0dp")
                progressBar.layoutParams.height = convertirStringADP("0dp")
                for (document in result) {
                    val estado = document.getString("estado")
                    if(estado == "pendiente"){
                        val idcons = document.getString("idcons")
                        val fecha = document.getString("date")
                        val hora = document.getString("time")
                        val clinica = document.getString("clinic")
                        val doc = document.getString("doctor")
                        val num = document.getString("contactdoc")
                        val registroView = layoutInflater.inflate(R.layout.item_registro, null)
                        val textViewNombre = registroView.findViewById<TextView>(R.id.textViewNombre)
                        val textViewFecha = registroView.findViewById<TextView>(R.id.textViewFecha)
                        val textViewHora = registroView.findViewById<TextView>(R.id.textViewHora)
                        val textViewClinica = registroView.findViewById<TextView>(R.id.textViewClinica)
                        val textViewDoctor = registroView.findViewById<TextView>(R.id.textViewDoctor)
                        val textViewContacto = registroView.findViewById<TextView>(R.id.textViewContacto)
                        textViewNombre.text = "$idcons"
                        textViewFecha.text = "Fecha: $fecha"
                        textViewHora.text = "Hora: $hora"
                        textViewClinica.text = "Clinica: $clinica"
                        textViewDoctor.text = "Doctor: $doc"
                        textViewContacto.text = "Contacto: $num"
                        container.addView(registroView)
                        val btnAjustes = registroView.findViewById<ImageButton>(R.id.btnOptions)
                        btnAjustes.setOnClickListener {
                            showDialog()
                        }
                    }
                }
            }
            .addOnFailureListener {
                progressBar.visibility = View.INVISIBLE
                progressBar.layoutParams.width = convertirStringADP("0dp")
                progressBar.layoutParams.height = convertirStringADP("0dp")
                val registroView = layoutInflater.inflate(R.layout.item_registro, null)
                val textViewNombre = registroView.findViewById<TextView>(R.id.textViewNombre)
                val textViewFecha = registroView.findViewById<TextView>(R.id.textViewFecha)
                val textViewHora = registroView.findViewById<TextView>(R.id.textViewHora)
                val textViewClinica = registroView.findViewById<TextView>(R.id.textViewClinica)
                val textViewDoctor = registroView.findViewById<TextView>(R.id.textViewDoctor)
                val textViewContacto = registroView.findViewById<TextView>(R.id.textViewContacto)
                textViewNombre.text = "Consulta"
                textViewFecha.text = "Fecha: "
                textViewHora.text = "Hora: "
                textViewClinica.text = "Clinica: "
                textViewDoctor.text = "Doctor: "
                textViewContacto.text = "Contacto: "
                container.addView(registroView)
                val btnAjustes = registroView.findViewById<ImageButton>(R.id.btnOptions)
                btnAjustes.setOnClickListener {
                    showDialog()
                }
            }
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_ajustes_consulta, null)
        builder.setView(view)

        val btnCancel = view.findViewById<Button>(R.id.btn_cancel)

        val dialog = builder.create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
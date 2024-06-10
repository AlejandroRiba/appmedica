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
        val databaseHandler = DatabaseHandler(applicationContext)
        val nombre = databaseHandler.consultaAdulto()
        val consulta = db.collection("usuarios")
            .document(nombre)
            .collection("citas")
            .orderBy("date", Query.Direction.ASCENDING)
        consulta.get()
            .addOnSuccessListener { result ->
                val hayRegistros = !result.isEmpty
                if (hayRegistros){
                    for (document in result) {
                        val estado = document.getString("estado")
                        if(estado == "pendiente"){
                            progressBar.visibility = View.INVISIBLE
                            progressBar.layoutParams.width = convertirStringADP("0dp")
                            progressBar.layoutParams.height = convertirStringADP("0dp")
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
                                showDialog(idcons.toString())
                            }
                        }
                    }
                }

            }
            .addOnFailureListener {
                Toast.makeText(this, "No se encontraron consultas.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDialog(idcons: String) {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_ajustes_consulta, null)
        builder.setView(view)

        val btnCancel = view.findViewById<Button>(R.id.btn_cancel)
        val btnAsistido = view.findViewById<Button>(R.id.btn_asistido)

        val dialog = builder.create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnAsistido.setOnClickListener {
            marcarAsistido(idcons)
            dialog.dismiss()
            val intent = Intent(this, ListaConsultas::class.java)
            startActivity(intent)
        }

        dialog.show()
    }
    private fun marcarAsistido(idcons: String) {
        val databaseHandler = DatabaseHandler(applicationContext)
        val userName = databaseHandler.consultaAdulto()
        val consultasRef = db.collection("usuarios").document(userName).collection("citas").document(idcons)

        // Realizar una consulta para buscar el documento con el campo "idcons" igual a idcons
        consultasRef.update("estado", "asistido")
            .addOnSuccessListener {
                // La actualización fue exitosa
                Toast.makeText(this, "Estado actualizado con éxito", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                // Manejar errores al actualizar el documento
                Toast.makeText(this, "No se pudo ejecutar la acción.", Toast.LENGTH_SHORT).show()
            }
    }

}
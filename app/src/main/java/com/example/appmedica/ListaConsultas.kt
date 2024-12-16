package com.example.appmedica

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appmedica.com.example.appmedica.AlarmUtils
import com.example.appmedica.com.example.appmedica.Consulta
import com.example.appmedica.com.example.appmedica.Utilidades
import com.example.appmedica.utils.FirebaseHelper
import com.example.appmedica.utils.PreferenceManager

class ListaConsultas : AppCompatActivity() {
    private lateinit var container: LinearLayout
    private lateinit var firebaseHelper: FirebaseHelper
    private lateinit var preferenceManager: PreferenceManager
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

        preferenceManager = PreferenceManager(this)

        // Si no existe un UUID, generamos uno y lo guardamos
        if (preferenceManager.getUniqueId() == null) {
            preferenceManager.generateAndSaveUniqueId()
        }

        val databaseHandler = DatabaseHandler(applicationContext)
        val client = databaseHandler.consultaAdulto()
        if (client == "Usuario") {
            val intent = Intent(this, Datosbasicos::class.java)
            startActivity(intent)
            finish()
        }

        container = findViewById(R.id.container_layout)

        // Consultar los registros y mostrarlos

        fetchDataFromFirestore()

//        val btn: Button = findViewById(R.id.btn_regresar)
//        btn.setOnClickListener{
//            finish()
//        }

        val anadirCitas: View = findViewById(R.id.fabAnadeCitas)
        anadirCitas.setOnClickListener {
            val intent = Intent(this, Consultas::class.java)
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
        firebaseHelper.obtenerCitas()
            .addOnSuccessListener { citas ->
                // Aquí tienes la lista de citas
                if (citas.isNotEmpty()) {
                    container.removeAllViews() // Limpia el contenedor antes de agregar nuevas vistas

                    for ((cita, documentId) in citas) {
                        try {
                            val registroView = layoutInflater.inflate(R.layout.item_registro, container, false)
                            val regViewContainer = registroView.findViewById<LinearLayout>(R.id.contenedorConsulta)
                            val regViewBG = regViewContainer.background
                            try {
                                regViewBG.setTint(Color.parseColor(cita.selectedcolor))
                            } catch (e: IllegalArgumentException) {
                                // Si el color no es válido, usa un color por defecto
                                regViewBG.setTint(Color.parseColor("#FFFFFF")) // Blanco por defecto
                            }
                            val textViewNombre = registroView.findViewById<TextView>(R.id.textViewNombre)
                            val textViewFecha = registroView.findViewById<TextView>(R.id.textViewFecha)
                            val textViewHora = registroView.findViewById<TextView>(R.id.textViewHora)

                            textViewNombre.text = "${cita.idcons}"
                            textViewFecha.text = dateFormatting("${cita.date}")
                            textViewHora.text = "${cita.time}"

                            container.addView(registroView)

                            regViewContainer.setOnClickListener {
                                val intent = Intent(this, MostrarConsulta::class.java).apply {
                                    putExtra("id", cita.idcons)
                                    putExtra("fecha", cita.date)
                                    putExtra("hora", cita.time)
                                    putExtra("clinica", cita.clinic)
                                    putExtra("doctor", cita.doctor)
                                    putExtra("cont_doc", cita.contactdoc)
                                    putExtra("docID", documentId)
                                    putExtra("selectedcolor", cita.selectedcolor.toString())
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
                putExtra("selectedcolor", cita.selectedcolor)
            }
            //finish() //finalizamos la lista de consultas
            startActivity(intent)
        }

        dialog.show()
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
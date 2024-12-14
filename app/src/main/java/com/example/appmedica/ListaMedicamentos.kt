package com.example.appmedica

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appmedica.com.example.appmedica.AlarmUtils
import com.example.appmedica.com.example.appmedica.Consulta
import com.example.appmedica.com.example.appmedica.Utilidades
import com.example.appmedica.com.example.appmedica.utils.MedicationRepository
import com.example.appmedica.utils.FirebaseHelper
import com.example.appmedica.utils.Medicine

class ListaMedicamentos : AppCompatActivity() {

    private lateinit var container: LinearLayout
    private lateinit var medRepo: MedicationRepository
    val TIPO_CAPSULA = "capsula"
    val TIPO_TABLETA = "tableta"
    val TIPO_BEBIBLE = "bebible"
    val TIPO_GOTAS = "gotas"
    val TIPO_INYECTABLE = "inyectable"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lista_medicamentos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        medRepo = MedicationRepository(this)

        container = findViewById(R.id.container_layout)

        // Consultar los registros y mostrarlos

        fetchDataFromFirestore()

        val anadirMedicamento: View = findViewById(R.id.fabAnadeMedicamento)
        anadirMedicamento.setOnClickListener {
            val intent = Intent(this, MedicamentosActivity::class.java)
            startActivity(intent)
        }

        val navCitas = findViewById<ImageButton>(R.id.navCitas)
        navCitas.setOnClickListener{
            val intent = Intent(this, ListaConsultas::class.java)
            startActivity(intent)
        }

        val navPerfil = findViewById<ImageButton>(R.id.navPerfil)
        navPerfil.setOnClickListener{
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }

    }

    @SuppressLint("InflateParams", "SetTextI18n")
    private fun fetchDataFromFirestore() {
        medRepo.obtenerMedicamentos()
            .addOnSuccessListener { medicamentos ->
                // Aquí tienes la lista de medicamentos
                if (medicamentos.isNotEmpty()) {
                    container.removeAllViews() // Limpia el contenedor antes de agregar nuevas vistas

                    for ((medicamento, documentId) in medicamentos) {
                        try {
                            val registroView = layoutInflater.inflate(R.layout.item_registro, container, false)
                            val regViewContainer = registroView.findViewById<LinearLayout>(R.id.contenedorConsulta)
                            val regViewBG = regViewContainer.background
                            try {
                                regViewBG.setTint(Color.parseColor(medicamento.color))
                            } catch (e: IllegalArgumentException) {
                                // Si el color no es válido, usa un color por defecto
                                regViewBG.setTint(Color.parseColor("#FFFFFF")) // Blanco por defecto
                            }
                            val textViewNombre = registroView.findViewById<TextView>(R.id.textViewNombre)
                            val textViewsigToma= registroView.findViewById<TextView>(R.id.textViewFecha)
                            val textViewDosis = registroView.findViewById<TextView>(R.id.textViewHora)
                            val requestCode = Utilidades.generateUniqueRequestCode(documentId)
                            textViewNombre.text = medicamento.nombre
                            val sigToma = AlarmUtils.getMedReminderByRequestCode(this, requestCode)

                            textViewsigToma.text = sigToma

                            textViewDosis.text = "${medicamento.dosis} por toma."

                            container.addView(registroView)
                            val btnAjustes = registroView.findViewById<ImageView>(R.id.btnOptions)
                            btnAjustes.setOnClickListener {
                                showDialog(documentId, medicamento, requestCode)
                            }

                            val mostrarCon = registroView.findViewById<GridLayout>(R.id.consulta)
                            mostrarCon.setOnClickListener {
                                val intent = Intent(this, MostrarMedicamento::class.java).apply {
                                    putExtra("nombre", medicamento.nombre)
                                    putExtra("frecuencia", medicamento.frecuencia)
                                    putExtra("primertoma", medicamento.primertoma)
                                    putExtra("duracion", medicamento.duracion)
                                    putExtra("color", medicamento.color)
                                    putExtra("dosis", medicamento.dosis)
                                    putExtra("zonaApl", medicamento.zonaAplicacion)
                                    putExtra("medida", medicamento.medida)
                                    when(medicamento.tipo){
                                        "capsula" -> {putExtra("tipo", "Cápsula")}
                                        "tableta" -> {putExtra("tipo", "Tableta")}
                                        "bebible" -> {putExtra("tipo", "Bebible")}
                                        "gotas" -> {putExtra("tipo", "Gotas")}
                                        "inyectable" -> {putExtra("tipo", "Inyectable")}
                                        else -> {putExtra("tipo", "")}
                                    }

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

    fun esNumero(input: String): Boolean {
        return input.all { it.isDigit() }
    }

    private fun showDialog(medId: String, medicamento: Medicine, requestCode: Int) {
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


        btnDelete.setOnClickListener {
            AlarmUtils.deleteMedReminder(this, requestCode)
            medRepo.eliminarMedicamento(medId, this)
            dialog.dismiss()
            val intent = Intent(this, ListaMedicamentos::class.java) //para recargar lista de consultas
            finish()//finalizamos la lista de consultas
            startActivity(intent) //la volvemos a abrir para que se actualice
        }

        btnEditar.setOnClickListener {
            Log.d("AbrirActividad", medicamento.toString())
            abrirActividad(medicamento.tipo, medId, medicamento, dialog)
        }

        dialog.show()
    }

    // Función auxiliar para abrir actividades
    private fun abrirActividad(tipo: String, medId: String, medicamento: Medicine, dialog: AlertDialog) {
        Log.d("AbrirActividad", "Tipo recibido: $tipo")
        val intent = when (tipo) {
            TIPO_CAPSULA -> Intent(this, CapsulaEdit::class.java)
            TIPO_TABLETA -> Intent(this, TabletaEdit::class.java)
            TIPO_BEBIBLE -> Intent(this, BebibleEdit::class.java).apply {
                putExtra("id", medId)
                putExtra("nombre", medicamento.nombre)
                putExtra("frecuencia", medicamento.frecuencia)
                putExtra("duracion", medicamento.duracion)
                putExtra("cantidad", medicamento.dosis)
                putExtra("selectedcolor", medicamento.color)
                putExtra("primertoma", medicamento.primertoma)
                putExtra("medida", medicamento.medida)
            }
            TIPO_GOTAS -> Intent(this, GotasEdit::class.java)
            TIPO_INYECTABLE -> Intent(this, InyectableEdit::class.java)
            else -> {
                Log.e("AbrirActividad", "Tipo no reconocido: $tipo")
                null
            }
        }

        if (intent != null) {
            dialog.dismiss()
            Log.d("AbrirActividad", "Iniciando actividad para tipo: $tipo")
            startActivity(intent)
        } else {
            dialog.dismiss()
            Log.e("AbrirActividad", "No se pudo iniciar actividad. Tipo desconocido.")
        }
    }

}
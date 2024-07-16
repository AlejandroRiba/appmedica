package com.example.appmedica

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appmedica.com.example.appmedica.AlarmUtils
import com.google.firebase.firestore.FirebaseFirestore

class Ajustes : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ajustes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val databaseHandler = DatabaseHandler(applicationContext)
        val usuarioActual = databaseHandler.consultaAdulto()
        val btn: ImageButton = findViewById(R.id.back1)
        btn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val btnEdit: LinearLayout = findViewById(R.id.row3)
        val btnShowDialog: LinearLayout = findViewById(R.id.row4)
        btnShowDialog.setOnClickListener {
            showDialog("Borrará el registro creado.\n" +
                    "¿Está seguro?") {
                showDialog("¿Está completamente seguro?") {
                    databaseHandler.eliminarTodosLosUsuarios()
                    deleteAllConsultas(usuarioActual)
                    AlarmUtils.cancelAllAlarms(this)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        btnEdit.setOnClickListener {
            showDialog("Editará sus datos.\n" +
                    "¿Está seguro?") {
                val intent = Intent(this, EditarDatos::class.java)
                startActivity(intent)
            }
        }
}
    private fun showDialog(message: String, listener: () -> Unit) {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_confirm, null)
        builder.setView(view)

        val textMessage = view.findViewById<TextView>(R.id.text_message)
        textMessage.text = message

        val btnCancel = view.findViewById<Button>(R.id.btn_cancel)
        val btnConfirm = view.findViewById<Button>(R.id.btn_confirm)

        val dialog = builder.create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            listener.invoke()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun deleteAllConsultas(userName: String) {
        val consultasRef = db.collection("usuarios").document(userName).collection("citas")

        consultasRef.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    consultasRef.document(document.id).delete()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener las consultas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
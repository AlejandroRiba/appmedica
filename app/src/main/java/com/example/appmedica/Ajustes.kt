package com.example.appmedica

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Ajustes : AppCompatActivity() {

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
        val btn: ImageButton = findViewById(R.id.back1)
        btn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val btnShowDialog: Button = findViewById(R.id.btn_show_dialog)
        btnShowDialog.setOnClickListener {
            showDialog("Borrará el registro creado.\n" +
                    "¿Está seguro?") {
                showDialog("¿Está completamente seguro?") {
                    databaseHandler.eliminarTodosLosUsuarios()
                }
            }
        }
        val btnEdit: Button = findViewById(R.id.button3)
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
}
package com.example.appmedica

import android.content.Intent
import android.os.Bundle
//import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MedicamentosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_medicamentos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btn: ImageButton = findViewById(R.id.back1)
        btn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        //Referencia al botón de capsula
        val btn1: ImageButton = findViewById(R.id.imageButton)
        btn1.setOnClickListener{
            val intent = Intent(this, CapsulaActivity::class.java)
            startActivity(intent)
        }
        //Referencia al botón de tableta
        val btn2: ImageButton = findViewById(R.id.imageButton2)
        btn2.setOnClickListener{
            val intent = Intent(this, TabletaActivity::class.java)
            startActivity(intent)
        }
        //Referencia al botón de bebible
        val btn3: ImageButton = findViewById(R.id.imageButton3)
        btn3.setOnClickListener{
            val intent = Intent(this, BebibleActivity::class.java)
            startActivity(intent)
        }
        //Referencia al botón de inyectable
        val btn4: ImageButton = findViewById(R.id.imageButton4)
        btn4.setOnClickListener{
            val intent = Intent(this, InyectableActivity::class.java)
            startActivity(intent)
        }
        //Referencia al botón de gotas
        val btn5: ImageButton = findViewById(R.id.imageButton5)
        btn5.setOnClickListener{
            val intent = Intent(this, GotasActivity::class.java)
            startActivity(intent)
        }
    }
}
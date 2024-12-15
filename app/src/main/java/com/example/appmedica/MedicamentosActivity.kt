package com.example.appmedica

import android.content.Intent
import android.os.Bundle
//import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
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
            finish()
        }
        //Referencia al botón de capsula
        val btn1 = findViewById<LinearLayout>(R.id.capsulaContainer)
        btn1.setOnClickListener{
            val intent = Intent(this, CapsulaActivity::class.java)
            startActivity(intent)
        }
        //Referencia al botón de tableta
        val btn2 = findViewById<LinearLayout>(R.id.tabletaContainer)
        btn2.setOnClickListener{
            val intent = Intent(this, TabletaActivity::class.java)
            startActivity(intent)
        }
        //Referencia al botón de bebible
        val btn3 = findViewById<LinearLayout>(R.id.bebibleContainer)
        btn3.setOnClickListener{
            val intent = Intent(this, BebibleActivity::class.java)
            startActivity(intent)
        }
        //Referencia al botón de inyectable
        val btn4 = findViewById<LinearLayout>(R.id.inyectableContainer)
        btn4.setOnClickListener{
            val intent = Intent(this, InyectableActivity::class.java)
            startActivity(intent)
        }
        //Referencia al botón de gotas
        val btn5 = findViewById<LinearLayout>(R.id.gotasContainer)
        btn5.setOnClickListener{
            val intent = Intent(this, GotasActivity::class.java)
            startActivity(intent)
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
}
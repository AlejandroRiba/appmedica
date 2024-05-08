package com.example.appmedica

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.CompoundButton
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.appmedica.databinding.ActivityDatosbasicosBinding

class datosbasicos : AppCompatActivity() {

    private lateinit var binding: ActivityDatosbasicosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDatosbasicosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDate()

        val editTextTime1 = findViewById<EditText>(R.id.CampoHora1)
        val editTextTime2 = findViewById<EditText>(R.id.CampoHora2)
        val editTextTime3 = findViewById<EditText>(R.id.CampoHora3)
        val editTextTime4 = findViewById<EditText>(R.id.CampoHora4)
        val editTextTime5 = findViewById<EditText>(R.id.CampoHora5)

        applyTimeFormat(editTextTime1)
        applyTimeFormat(editTextTime2)
        applyTimeFormat(editTextTime3)
        applyTimeFormat(editTextTime4)
        applyTimeFormat(editTextTime5)

        val btnSendFeedback = findViewById<Button>(R.id.enviardatos)
        btnSendFeedback.setOnClickListener {
            sendFeedback()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initDate(){
        checkBox()
    }

    private fun checkBox(){
        binding.option1.setOnCheckedChangeListener { _: CompoundButton, _:Boolean ->
            if(binding.option1.isChecked){
                binding.option2.isEnabled =false
                binding.option3.isEnabled =false
                binding.option4.isEnabled =false
                binding.option5.isEnabled =false
                binding.option6.isEnabled =false
                binding.option7.isEnabled =false
                binding.option8.isEnabled =false
            }
            else if(!binding.option1.isChecked){
                binding.option2.isEnabled = true
                binding.option3.isEnabled = true
                binding.option4.isEnabled = true
                binding.option5.isEnabled = true
                binding.option6.isEnabled = true
                binding.option7.isEnabled = true
                binding.option8.isEnabled = true
            }
        }
        binding.option2.setOnCheckedChangeListener { _: CompoundButton, _:Boolean ->
            if(binding.option2.isChecked){
                binding.option1.isEnabled =false
                binding.option3.isEnabled =false
                binding.option4.isEnabled =false
                binding.option5.isEnabled =false
                binding.option6.isEnabled =false
                binding.option7.isEnabled =false
                binding.option8.isEnabled =false
            }
            else if(!binding.option2.isChecked){
                binding.option1.isEnabled = true
                binding.option3.isEnabled = true
                binding.option4.isEnabled = true
                binding.option5.isEnabled = true
                binding.option6.isEnabled = true
                binding.option7.isEnabled = true
                binding.option8.isEnabled = true
            }
        }
        binding.option3.setOnCheckedChangeListener { _: CompoundButton, _:Boolean ->
            if(binding.option3.isChecked){
                binding.option2.isEnabled =false
                binding.option1.isEnabled =false
                binding.option4.isEnabled =false
                binding.option5.isEnabled =false
                binding.option6.isEnabled =false
                binding.option7.isEnabled =false
                binding.option8.isEnabled =false
            }
            else if(!binding.option3.isChecked){
                binding.option2.isEnabled = true
                binding.option1.isEnabled = true
                binding.option4.isEnabled = true
                binding.option5.isEnabled = true
                binding.option6.isEnabled = true
                binding.option7.isEnabled = true
                binding.option8.isEnabled = true
            }
        }
        binding.option4.setOnCheckedChangeListener { _: CompoundButton, _:Boolean ->
            if(binding.option4.isChecked){
                binding.option2.isEnabled =false
                binding.option3.isEnabled =false
                binding.option1.isEnabled =false
                binding.option5.isEnabled =false
                binding.option6.isEnabled =false
                binding.option7.isEnabled =false
                binding.option8.isEnabled =false
            }
            else if(!binding.option4.isChecked){
                binding.option2.isEnabled = true
                binding.option3.isEnabled = true
                binding.option1.isEnabled = true
                binding.option5.isEnabled = true
                binding.option6.isEnabled = true
                binding.option7.isEnabled = true
                binding.option8.isEnabled = true
            }
        }
        binding.option5.setOnCheckedChangeListener { _: CompoundButton, _:Boolean ->
            if(binding.option5.isChecked){
                binding.option2.isEnabled =false
                binding.option3.isEnabled =false
                binding.option4.isEnabled =false
                binding.option1.isEnabled =false
                binding.option6.isEnabled =false
                binding.option7.isEnabled =false
                binding.option8.isEnabled =false
            }
            else if(!binding.option5.isChecked){
                binding.option2.isEnabled = true
                binding.option3.isEnabled = true
                binding.option4.isEnabled = true
                binding.option1.isEnabled = true
                binding.option6.isEnabled = true
                binding.option7.isEnabled = true
                binding.option8.isEnabled = true
            }
        }
        binding.option6.setOnCheckedChangeListener { _: CompoundButton, _:Boolean ->
            if(binding.option6.isChecked){
                binding.option2.isEnabled =false
                binding.option3.isEnabled =false
                binding.option4.isEnabled =false
                binding.option5.isEnabled =false
                binding.option1.isEnabled =false
                binding.option7.isEnabled =false
                binding.option8.isEnabled =false
            }
            else if(!binding.option6.isChecked){
                binding.option2.isEnabled = true
                binding.option3.isEnabled = true
                binding.option4.isEnabled = true
                binding.option5.isEnabled = true
                binding.option1.isEnabled = true
                binding.option7.isEnabled = true
                binding.option8.isEnabled = true
            }
        }
        binding.option7.setOnCheckedChangeListener { _: CompoundButton, _:Boolean ->
            if(binding.option7.isChecked){
                binding.option2.isEnabled =false
                binding.option3.isEnabled =false
                binding.option4.isEnabled =false
                binding.option5.isEnabled =false
                binding.option6.isEnabled =false
                binding.option1.isEnabled =false
                binding.option8.isEnabled =false
            }
            else if(!binding.option7.isChecked){
                binding.option2.isEnabled = true
                binding.option3.isEnabled = true
                binding.option4.isEnabled = true
                binding.option5.isEnabled = true
                binding.option6.isEnabled = true
                binding.option1.isEnabled = true
                binding.option8.isEnabled = true
            }
        }
        binding.option8.setOnCheckedChangeListener { _: CompoundButton, _:Boolean ->
            if(binding.option8.isChecked){
                binding.option2.isEnabled =false
                binding.option3.isEnabled =false
                binding.option4.isEnabled =false
                binding.option5.isEnabled =false
                binding.option6.isEnabled =false
                binding.option7.isEnabled =false
                binding.option1.isEnabled =false
            }
            else if(!binding.option8.isChecked){
                binding.option2.isEnabled = true
                binding.option3.isEnabled = true
                binding.option4.isEnabled = true
                binding.option5.isEnabled = true
                binding.option6.isEnabled = true
                binding.option7.isEnabled = true
                binding.option1.isEnabled = true
            }
        }
    }

    //fun onSendFeedbackButtonClicked(view: View) {
      //  sendFeedback()
    //}
    private fun applyTimeFormat(editText: EditText){
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.length == 2 && !s.toString().contains(":")) {
                    editText.setText(s.toString() + ":")
                    editText.setSelection(editText.text.length)
                }
            }
        })
    }

    private fun sendFeedback(){
        val nombre = findViewById<EditText>(R.id.CampoName).text.toString()
        val edad = findViewById<EditText>(R.id.CampoEdad).text.toString()
        val checkBoxOption1 = findViewById<CheckBox>(R.id.option1)
        val checkBoxOption2 = findViewById<CheckBox>(R.id.option2)
        val checkBoxOption3 = findViewById<CheckBox>(R.id.option3)
        val checkBoxOption4 = findViewById<CheckBox>(R.id.option4)
        val checkBoxOption5 = findViewById<CheckBox>(R.id.option5)
        val checkBoxOption6 = findViewById<CheckBox>(R.id.option6)
        val checkBoxOption7 = findViewById<CheckBox>(R.id.option7)
        val checkBoxOption8 = findViewById<CheckBox>(R.id.option8)
        var sangretyp = ""
        val time1 = findViewById<EditText>(R.id.CampoHora1).text.toString()
        val time2 = findViewById<EditText>(R.id.CampoHora2).text.toString()
        val time3 = findViewById<EditText>(R.id.CampoHora3).text.toString()
        val time4 = findViewById<EditText>(R.id.CampoHora4).text.toString()
        val time5 = findViewById<EditText>(R.id.CampoHora5).text.toString()
        if(checkBoxOption1.isChecked){
            sangretyp = "A-"
        } else if(checkBoxOption2.isChecked){
            sangretyp = "A+"
        } else if(checkBoxOption3.isChecked){
            sangretyp = "B-"
        } else if(checkBoxOption4.isChecked){
            sangretyp = "B+"
        } else if(checkBoxOption5.isChecked){
            sangretyp = "O-"
        } else if(checkBoxOption6.isChecked){
            sangretyp = "O+"
        } else if(checkBoxOption7.isChecked){
            sangretyp = "AB-"
        } else if(checkBoxOption8.isChecked) {
            sangretyp = "AB+"
        }
        // Construye el mensaje a mostrar
        val message = "SANGRE: $sangretyp\n" +
                "NAME: $nombre\n" +
                "EDAD: $edad\n" +
                "Time 4: $time4\n" +
                "Time 5: $time5"

        // Muestra el mensaje en un Toast
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
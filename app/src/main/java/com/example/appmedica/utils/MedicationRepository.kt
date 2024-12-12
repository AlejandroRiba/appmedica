package com.example.appmedica.com.example.appmedica.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.appmedica.com.example.appmedica.Consulta
import com.example.appmedica.utils.Medicine
import com.example.appmedica.utils.PreferenceManager
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.firestore.FirebaseFirestore

class MedicationRepository(private val context: Context){
    private val preferenceManager: PreferenceManager = PreferenceManager(context)
    private val db = FirebaseFirestore.getInstance()
    // Identificador único del dispositivo
    private val userId: String? = preferenceManager.getUniqueId()


    // Método para agregar un medicamento
    fun addMedication(medicineData: Medicine): Task<Pair<Boolean, String?>> {
        val taskCompletionSource = TaskCompletionSource<Pair<Boolean, String?>>()
        val medicamentosCollectionReference = db.collection("usuarios")
            .document(userId!!)
            .collection("medicamentos")

        medicamentosCollectionReference.add(medicineData)
            .addOnSuccessListener { addedDocumentReference -> // Cambié el nombre aquí
                val medId = addedDocumentReference.id // Captura el ID del documento generado
                Toast.makeText(context, "Medicamento registrado con éxito!", Toast.LENGTH_SHORT).show()
                taskCompletionSource.setResult(Pair(true, medId)) // Indica éxito y devuelve el ID
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al guardar!", Toast.LENGTH_SHORT).show()
                taskCompletionSource.setResult(Pair(false, null)) // Indica fallo y no devuelve ID
            }

        return taskCompletionSource.task
    }

    fun getMedication(medId: String): Task<Medicine?> { // -- READ
        val taskCompletionSource = TaskCompletionSource<Medicine?>()
        val documentReference = db.collection("usuarios")
            .document(userId!!)
            .collection("medicamentos")
            .document(medId)

        documentReference.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Convierte los datos del documento en un objeto Consulta
                    val medication = documentSnapshot.toObject(Medicine::class.java)
                    taskCompletionSource.setResult(medication) // Devuelve el objeto Consulta
                } else {
                    taskCompletionSource.setResult(null) // Documento no encontrado
                }
            }
            .addOnFailureListener { e ->
                Log.e("MedicationRepository", "Error al leer el registro: ${e.message}")
                taskCompletionSource.setResult(null) // En caso de error
            }

        return taskCompletionSource.task
    }

}
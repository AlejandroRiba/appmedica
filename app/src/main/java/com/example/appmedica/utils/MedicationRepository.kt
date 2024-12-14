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
import com.google.firebase.firestore.Query

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

    fun obtenerMedicamentos(): Task<List<Pair<Medicine, String>>> { //medicamentos ordenados por la duración
        val taskCompletionSource = TaskCompletionSource<List<Pair<Medicine, String>>>()
        val consulta = db.collection("usuarios")
            .document(userId!!)
            .collection("medicamentos")
            .orderBy("duracion", Query.Direction.ASCENDING)

        consulta.get()
            .addOnSuccessListener { result ->
                val medicamentos = mutableListOf<Pair<Medicine, String>>()
                for (document in result) {
                    val nombre = document.getString("nombre") ?: ""
                    val dosis = document.getString("dosis") ?: ""
                    val frecuencia = document.getString("frecuencia") ?: ""
                    val duracion = document.getString("duracion") ?: ""
                    val primertoma = document.getString("primertoma") ?: ""
                    val tipo = document.getString("tipo") ?: ""
                    val medida = document.getString("medida") ?: ""
                    val zonaApl = document.getString("zonaAplicacion") ?: ""
                    val color = document.getString("color") ?: ""

                    // Obtén el ID del documento
                    val documentId = document.id

                    // Crea el objeto Medicine y añádelo a la lista
                    val medicamento = Medicine(
                        nombre,
                        tipo,
                        dosis,
                        frecuencia,
                        primertoma,
                        duracion,
                        color,
                        zonaApl,
                        medida
                    )
                    // Añade el par a la lista
                    medicamentos.add(Pair(medicamento, documentId))
                }
                taskCompletionSource.setResult(medicamentos) // Devuelve la lista de citas
            }
            .addOnFailureListener {
                taskCompletionSource.setException(it) // Devuelve la excepción
            }

        return taskCompletionSource.task
    }

    fun eliminarMedicamento(medId: String, context: Context) { // -- DELETE
        // Referencia al documento del medicamento
        val documentReference = db.collection("usuarios")
            .document(userId!!)
            .collection("medicamentos")
            .document(medId)

        // Eliminar el medicamento
        documentReference.delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Medicamento eliminado con éxito!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al eliminar el medicamento: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}
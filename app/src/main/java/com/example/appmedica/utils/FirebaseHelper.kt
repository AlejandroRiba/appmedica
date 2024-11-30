package com.example.appmedica.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.appmedica.com.example.appmedica.Consulta
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Query



class FirebaseHelper(private val context: Context) {
    private val preferenceManager: PreferenceManager = PreferenceManager(context)
    private val db = FirebaseFirestore.getInstance()

    // Identificador único del dispositivo
    private val userId: String? = preferenceManager.getUniqueId()
    // Método para agregar un documento a una colección
    fun agregarUsuario(data: Map<String, Any>) { // ++ CREATE
        db.collection("usuarios")
            .document(userId!!)
            .set(data)
            .addOnSuccessListener {
                Log.d(
                    "FirebaseHelper",
                    "Usuario agregado correctamente"
                )
            }
            .addOnFailureListener { e: Exception? ->
                Log.e(
                    "FirebaseHelper",
                    "Error al agregar usuario",
                    e
                )
            }
    }

    fun editarUsuario(data: Map<String, Any>) { // ++ UPDATE
        db.collection("usuarios")
            .document(userId!!)
            .update(data)
            .addOnSuccessListener {
                Log.d("FirebaseHelper", "Usuario editado correctamente")
            }
            .addOnFailureListener { e: Exception? ->
                Log.e("FirebaseHelper", "Error al editar usuario", e)
            }
    }

    fun eliminarUsuario() { //++ DELETE
        val docRef = db.collection("usuarios").document(userId!!) // Asegúrate de que userId no sea nulo

        // Primero eliminamos las subcolecciones
        eliminarSubcolecciones(docRef)

        docRef.delete()
            .addOnSuccessListener {
                Log.d("FirebaseHelper", "Usuario eliminado correctamente")
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseHelper", "Error al eliminar usuario", e)
            }
    }

    fun eliminarSubcolecciones(docRef: DocumentReference) {
        docRef.collection("citas").get() // Cambia "subcoleccion1" por el nombre de tu subcolección
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete() // Elimina cada documento en la subcolección
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseHelper", "Error al eliminar documentos en subcolección", e)
            }
    }
    // Método para actualizar una cita
    fun actualizarCita( //-- UPDATE
        identificador: String,
        nuevosDatos: Map<String, Any>
    ): Task<Consulta?> {
        val taskCompletionSource = TaskCompletionSource<Consulta?>()
        val documentReference = db.collection("usuarios")
            .document(userId!!)
            .collection("citas")
            .document(identificador)

        documentReference.set(nuevosDatos, SetOptions.merge())
            .addOnSuccessListener {
                documentReference.get()
                    .addOnSuccessListener { documentSnapshot ->
                        if(documentSnapshot.exists()){
                            val consulta = documentSnapshot.toObject(Consulta::class.java)
                            taskCompletionSource.setResult(consulta)
                        }else{
                            taskCompletionSource.setResult(null)
                        }
                    }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al guardar!", Toast.LENGTH_SHORT).show()
                taskCompletionSource.setResult(null) // Indica fallo
            }

        return taskCompletionSource.task
    }
    // Método para agregar una cita
    fun agregarCita(datosCita: Map<String, Any>): Task<Pair<Boolean, String?>> {
        val taskCompletionSource = TaskCompletionSource<Pair<Boolean, String?>>()
        val citasCollectionReference = db.collection("usuarios")
            .document(userId!!)
            .collection("citas")

        citasCollectionReference.add(datosCita)
            .addOnSuccessListener { addedDocumentReference -> // Cambié el nombre aquí
                val citaId = addedDocumentReference.id // Captura el ID del documento generado
                Toast.makeText(context, "Cita creada con éxito!", Toast.LENGTH_SHORT).show()
                taskCompletionSource.setResult(Pair(true, citaId)) // Indica éxito y devuelve el ID
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al guardar!", Toast.LENGTH_SHORT).show()
                taskCompletionSource.setResult(Pair(false, null)) // Indica fallo y no devuelve ID
            }

        return taskCompletionSource.task
    }

    fun leerCita(citaId: String): Task<Consulta?> { // -- READ
        val taskCompletionSource = TaskCompletionSource<Consulta?>()
        val documentReference = db.collection("usuarios")
            .document(userId!!)
            .collection("citas")
            .document(citaId)

        documentReference.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Convierte los datos del documento en un objeto Consulta
                    val consulta = documentSnapshot.toObject(Consulta::class.java)
                    taskCompletionSource.setResult(consulta) // Devuelve el objeto Consulta
                } else {
                    taskCompletionSource.setResult(null) // Documento no encontrado
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseHelper", "Error al leer la cita: ${e.message}")
                taskCompletionSource.setResult(null) // En caso de error
            }

        return taskCompletionSource.task
    }


    fun eliminarCita(citaId: String, context: Context) { // -- DELETE
        // Referencia al documento de la cita
        val documentReference = db.collection("usuarios")
            .document(userId!!)
            .collection("citas")
            .document(citaId)

        // Eliminar la cita
        documentReference.delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Cita eliminada con éxito!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al eliminar la cita: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun obtenerCitas(): Task<List<Pair<Consulta, String>>> { //citas ordenadas por fecha
        val taskCompletionSource = TaskCompletionSource<List<Pair<Consulta, String>>>()
        val consulta = db.collection("usuarios")
            .document(userId!!)
            .collection("citas")
            .orderBy("date", Query.Direction.ASCENDING)

        consulta.get()
            .addOnSuccessListener { result ->
                val citas = mutableListOf<Pair<Consulta, String>>()
                for (document in result) {
                    val estado = document.getString("estado")
                    if (estado == "pendiente") {
                        val idcons = document.getString("idcons") ?: ""
                        val fecha = document.getString("date")
                        val hora = document.getString("time")
                        val clinica = document.getString("clinic")
                        val doc = document.getString("doctor")
                        val num = document.getString("contactdoc")

                        // Obtén el ID del documento
                        val documentId = document.id

                        // Crea el objeto Consulta y añádelo a la lista
                        val cita = Consulta(idcons, fecha, hora, clinica, doc, num,estado)
                        // Añade el par a la lista
                        citas.add(Pair(cita, documentId))
                    }
                }
                taskCompletionSource.setResult(citas) // Devuelve la lista de citas
            }
            .addOnFailureListener {
                taskCompletionSource.setException(it) // Devuelve la excepción
            }

        return taskCompletionSource.task
    }

    fun marcarAsistido(idcons: String) {
        val consultasRef = db.collection("usuarios").document(userId!!).collection("citas").document(idcons)

        // Realizar una consulta para buscar el documento con el campo "idcons" igual a idcons
        consultasRef.update("estado", "asistido")
            .addOnSuccessListener {
                // La actualización fue exitosa
                Toast.makeText(context, "Estado actualizado con éxito", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                // Manejar errores al actualizar el documento
                Toast.makeText(context, "No se pudo ejecutar la acción", Toast.LENGTH_SHORT).show()
            }
    }

}


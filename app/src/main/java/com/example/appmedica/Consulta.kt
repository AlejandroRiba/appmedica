package com.example.appmedica.com.example.appmedica

import java.io.Serializable

class Consulta : Serializable{
    var idcons: String? = null
    var date: String? = null
    var time: String? = null
    var clinic: String? = null
    var doctor: String? = null
    var contactdoc: String? = null
    var selectedcolor: String? = null
    var estado: String? = null

    // Constructor vacío
    constructor()

    // Constructor con parámetros
    constructor(
        idcons: String?,
        date: String?,
        time: String?,
        clinic: String?,
        doctor: String?,
        contactdoc: String?,
        selectedcolor: String?,
        estado: String?
    ) {
        this.idcons = idcons
        this.date = date
        this.time = time
        this.clinic = clinic
        this.doctor = doctor
        this.contactdoc = contactdoc
        this.selectedcolor = selectedcolor
        this.estado = estado
    }

    // Métodos getter y setter según sea necesario para otras propiedades
}
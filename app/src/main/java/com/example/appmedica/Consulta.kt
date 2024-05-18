package com.example.appmedica.com.example.appmedica

class Consulta {
    var id_cons: String? = null
    var date: String? = null
    var time: String? = null
    var clinic: String? = null
    var doctor: String? = null
    var contact_doct: String? = null

    // Constructor vacío
    constructor()

    // Constructor con parámetros
    constructor(
        id_cons: String?,
        date: String?,
        time: String?,
        clinic: String?,
        doctor: String?,
        contact_doct: String?
    ) {
        this.id_cons = id_cons
        this.date = date
        this.time = time
        this.clinic = clinic
        this.doctor = doctor
        this.contact_doct = contact_doct
    }

    // Métodos getter y setter según sea necesario para otras propiedades
}
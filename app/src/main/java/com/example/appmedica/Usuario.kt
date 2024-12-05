package com.example.appmedica

class Usuario {
    var name: String? = null
    var age: Int = 0
    var blood: String? = null
    var contact: String? = null
    var h1: String? = null //despertar
    var h2: String? = null //dormid
    var h3: String? = null //desayunar
    var h4: String? = null //comer
    var h5: String? = null //cenar

    // Constructor vacío
    constructor()

    // Constructor con parámetros
    constructor(
        name: String?,
        age: Int,
        blood: String?,
        contact: String?,
        h1: String?,
        h2: String?,
        h3: String?,
        h4: String?,
        h5: String?
    ) {
        this.name = name
        this.age = age
        this.blood = blood
        this.contact = contact
        this.h1 = h1
        this.h2 = h2
        this.h3 = h3
        this.h4 = h4
        this.h5 = h5
    }

    // Métodos getter y setter según sea necesario para otras propiedades
}
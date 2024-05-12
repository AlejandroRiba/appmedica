package com.example.appmedica

class Usuario {
    var name: String? = null
    var age: Int = 0
    var blood: String? = null
    var contact: Int = 0
    var h1: String? = null
    var h2: String? = null
    var h3: String? = null
    var h4: String? = null
    var h5: String? = null

    // Constructor vacío
    constructor()

    // Constructor con parámetros
    constructor(
        name: String?,
        age: Int,
        blood: String?,
        contact: Int,
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
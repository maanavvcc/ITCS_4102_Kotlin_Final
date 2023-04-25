package com.example.itcs_4102_kotlin_final

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot

class SpinnerItems {
    var name = ""
    var link = ""
    var ref : DocumentReference? = null
    var price = 0.0

    constructor(doc: DocumentSnapshot) {
        this.name = doc.getString("name").toString()
        this.ref = doc.reference
        this.link = doc.id
        this.price = doc.get("price").toString().toDouble()
    }
}
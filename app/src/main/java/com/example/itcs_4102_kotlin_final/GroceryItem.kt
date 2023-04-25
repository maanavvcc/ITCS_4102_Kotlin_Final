package com.example.itcs_4102_kotlin_final

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot

class GroceryItem {
    var name = ""
        get() = field
        set(value) {field = value }
    var amount = 0
        get() = field
        set(value) {field = value }
    var price = 0.0
        get() = field
        set(value) {field = value }
    var id = ""
        get() = field
        set(value) {field = value }

    constructor(item: DocumentSnapshot) {
        this.name = item.get("name") as String
        this.amount = item.get("amount") as Int
        this.price = item.get("price") as Double
        this.id = item.id
    }
}
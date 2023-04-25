package com.example.itcs_4102_kotlin_final

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot

class Coupon {
    var type = 0 //1 for bogo, 2 for perOff, 3 for amtOff
        get() = field
        set(value) {field = value}
    var forItemRef: DocumentReference
        get() = field
        set(value) {field = value}
    var forItem: DocumentSnapshot
        get() = field
        set(value) {field = value}
    var amtOff = 0.0
        get() = field
        set(value) {field = value}
    var perOff = 0
        get() = field
        set(value) {field = value}
    var bogo = intArrayOf(0,0)
        get() = field
        set(value) {field = value}
    var id = ""
        get() = field
        set(value) {field = value}

    constructor(coupon: DocumentSnapshot) {
        this.type = coupon.get("type") as Int
        this.forItemRef = coupon.get("for") as DocumentReference
        this.forItem = this.forItemRef!!.get().result
        this.id = coupon.id
        when (this.type) {
            1 -> {
                this.bogo[0] = coupon.get("buyAmt") as Int
                this.bogo[1] = coupon.get("freeAmt") as Int
            }
            2 -> {
                this.perOff = coupon.get("perOff") as Int
            }
            3 -> {
                this.amtOff = coupon.get("amtOff") as Double
            }
        }
    }
}
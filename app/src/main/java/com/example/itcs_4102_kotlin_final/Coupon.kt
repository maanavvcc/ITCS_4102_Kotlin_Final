package com.example.itcs_4102_kotlin_final

import com.google.android.gms.tasks.Task
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
        this.type = coupon.get("type").toString().toInt()
        this.forItemRef = coupon.get("for") as DocumentReference
        this.id = coupon.id
        when (this.type) {
            1 -> {
                this.bogo[0] = coupon.get("buyAmt").toString().toInt()
                this.bogo[1] = coupon.get("freeAmt").toString().toInt()
            }
            2 -> {
                this.perOff = coupon.get("perOff").toString().toInt()
            }
            3 -> {
                this.amtOff = coupon.get("amtOff").toString().toDouble()
            }
        }
    }
}
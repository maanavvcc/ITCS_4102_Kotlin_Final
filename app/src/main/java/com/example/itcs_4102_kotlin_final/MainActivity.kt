package com.example.itcs_4102_kotlin_final

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), LoginFragment.LoginListener,CreateFragment.CreateListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mAuth = FirebaseAuth.getInstance()
        if(mAuth.currentUser == null){
            supportFragmentManager.beginTransaction()
                .add(R.id.containerView, LoginFragment())
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                //.add(R.id.containerView, LoginFragment()) TODO
                .commit()
        }
    }

    override fun goBack() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerView, LoginFragment())
            .commit()
    }

    override fun signUp() {
        supportFragmentManager.beginTransaction()
            //.replace(R.id.containerView, CreateFragment()) TODO
            .commit()
    }

    override fun login() {
        supportFragmentManager.beginTransaction()
            //.replace(R.id.containerView, CreateFragment()) TODO
            .commit()
    }

    override fun newAccount() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerView, CreateFragment())
            .commit()
    }
}
package com.example.itcs_4102_kotlin_final

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), LoginFragment.LoginListener,CreateFragment.CreateListener, MainFragment.MainListener, CouponFragment.CouponListener, GroceryFragment.GroceryListener , ProcessFragment.ProcessListener, ItemWithCouponListener{
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
                .add(R.id.containerView, MainFragment())
                .commit()
        }
    }

    override fun goBack() {supportFragmentManager.popBackStack()}

    override fun clickOnItem(itemID: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerView, ItemWithCouponsFragment.newInstance(itemID) )
            .addToBackStack(null)
            .commit()}

    override fun clickOnGeneral() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerView, ItemWithCouponsFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun signUp() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerView, MainFragment())
            .commit()
    }

    override fun login() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerView, MainFragment())
            .commit()
    }

    override fun newAccount() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerView, CreateFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun logOut() {
        FirebaseAuth.getInstance().signOut()
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerView, LoginFragment())
            .commit()
    }

    override fun grocery() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerView, GroceryFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun coupons() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerView, CouponFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun process() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerView, ProcessFragment())
            .addToBackStack(null)
            .commit()
    }
}
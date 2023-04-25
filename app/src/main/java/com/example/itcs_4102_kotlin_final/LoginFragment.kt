package com.example.itcs_4102_kotlin_final

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.itcs_4102_kotlin_final.databinding.FragmentLoginBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private var mBinding: FragmentLoginBinding? = null
    private var mListener: LoginListener? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentLoginBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as LoginListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        mBinding!!.buttonLogin.setOnClickListener{
            val email = mBinding!!.editTextEmail.text.toString()
            val password = mBinding!!.editTextPassword.text.toString()
            if (email.isEmpty()) {
                Toast.makeText(context, "Please enter an email", Toast.LENGTH_SHORT).show()
            } else if (password.isEmpty()) {
                Toast.makeText(context, "Password is required", Toast.LENGTH_SHORT).show()
            } else {
                mAuth!!.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task: Task<AuthResult?> ->
                        if(task.isSuccessful){mListener!!.login()}
                        else{Toast.makeText(context,"Login Failed",Toast.LENGTH_SHORT).show()}
                    }
            }
        }
        mBinding!!.buttonCreateNew.setOnClickListener { mListener!!.newAccount() }
        requireActivity().title = "Login"
    }

    interface LoginListener{
        fun login()
        fun newAccount()
    }
}
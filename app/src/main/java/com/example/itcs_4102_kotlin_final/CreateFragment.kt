package com.example.itcs_4102_kotlin_final

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.itcs_4102_kotlin_final.databinding.FragmentCreateBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreateFragment : Fragment() {

    private var mBinding: FragmentCreateBinding? = null
    private var mListener: CreateListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentCreateBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding!!.buttonCancel.setOnClickListener { mListener!!.goBack() }
        mBinding!!.buttonSubmit.setOnClickListener {
            val email = mBinding!!.editTextEmail.text.toString()
            val password = mBinding!!.editTextPassword.text.toString()
            val name = mBinding!!.editTextName.text.toString()
            if(email.isEmpty()){
                Toast.makeText(context,"Email is required",Toast.LENGTH_SHORT).show()
            }else if(password.isEmpty()){
                Toast.makeText(context,"Password is required",Toast.LENGTH_SHORT).show()
            }else if(name.isEmpty()){
                Toast.makeText(context,"Name is required",Toast.LENGTH_SHORT).show()
            }else{
                val mAuth = FirebaseAuth.getInstance()
                val db = FirebaseFirestore.getInstance()
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task: Task<AuthResult> ->
                        if(task.isSuccessful){
                            db.collection("People").document(mAuth.uid!!).update("name", name).addOnCompleteListener { task1: Task<Void> ->
                                if(task1.isSuccessful){mListener!!.signUp()}
                                else{Toast.makeText(context, task1.exception!!.message,Toast.LENGTH_SHORT).show()}
                            }
                        }
                        else{Toast.makeText(context,task.exception!!.message,Toast.LENGTH_SHORT).show()}
                    }
            }
        }
        requireActivity().title="Sign Up"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as CreateListener
    }

    interface CreateListener{
        fun goBack()
        fun signUp()
    }
}
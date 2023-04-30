package com.example.itcs_4102_kotlin_final

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.itcs_4102_kotlin_final.databinding.FragmentMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainFragment : Fragment() {

    private var mBinding: FragmentMainBinding? = null
    private var mListener: MainListener? = null
    private var db = FirebaseFirestore.getInstance()
    private var mAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentMainBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        db.collection("People").document(mAuth.uid!!).collection("Coupons").addSnapshotListener { value, error ->
            var counter: Int = 0
            try {
                for (doc in value!!) {
                    counter += 1
                }
                mBinding!!.textViewCoupons.setText(counter.toString())
            }catch(e: NullPointerException){mBinding!!.textViewCoupons.setText("Empty")}
        }
        db.collection("People").document(mAuth.uid!!).collection("Items").addSnapshotListener { value, error ->
            var counter: Int = 0
            try {
                for (doc in value!!) {
                    counter += 1
                }
                mBinding!!.textViewItemsCart.setText(counter.toString())
            }catch(e:NullPointerException){mBinding!!.textViewItemsCart.setText("Empty")}

        }
        mBinding!!.buttonCoupons.setOnClickListener{mListener!!.coupons()}
        mBinding!!.buttonGroceryList.setOnClickListener{mListener!!.grocery()}
        mBinding!!.buttonProcess.setOnClickListener{mListener!!.process()}
        requireActivity().title = "Home"
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.logOutMenuItem){
            mListener!!.logOut()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as MainListener
    }

    interface MainListener{
        fun logOut()
        fun grocery()
        fun coupons()
        fun process()
    }
}
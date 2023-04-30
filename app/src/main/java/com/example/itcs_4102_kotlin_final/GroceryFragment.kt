package com.example.itcs_4102_kotlin_final

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.itcs_4102_kotlin_final.databinding.FragmentGroceryBinding
import com.example.itcs_4102_kotlin_final.databinding.ListItemGroceryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DecimalFormat

class GroceryFragment : Fragment() {

    private var mBinding: FragmentGroceryBinding? = null
    private var mListener: GroceryListener? = null
    private var db = FirebaseFirestore.getInstance()
    private var mAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentGroceryBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db.collection("People").document(mAuth.uid!!).collection("Items").addSnapshotListener { value, _ ->
            mBinding!!.rvItemList.layoutManager = LinearLayoutManager(context)
            val groceryItems = mutableListOf<GroceryItem>()
            //try {
                for (item in value!!) {
                    groceryItems.add(GroceryItem(item))
                }
            //}catch(e:NullPointerException){}
            val adapter = ItemsAdapter(groceryItems)
            mBinding!!.rvItemList.adapter = adapter
        }
        mBinding!!.buttonBackFromItems.setOnClickListener { mListener!!.goBack() }
        mBinding!!.buttonAddItem.setOnClickListener {
            var name = mBinding!!.editTextItemName.text.toString()
            val amtString = mBinding!!.editTextAmount.text.toString()
            val priceString = mBinding!!.editTextPrice.text.toString()
            try {
                val amt = amtString.toInt()
                val price = priceString.toDouble()
                if(name.isEmpty() or name.isBlank()){Toast.makeText(context,"Please enter a name",Toast.LENGTH_SHORT).show()}
                else if(amt <= 0){Toast.makeText(context,"Please enter an amount greater than 0",Toast.LENGTH_SHORT).show()}
                else if(price <= 0){Toast.makeText(context,"Please enter a price greater than 0",Toast.LENGTH_SHORT).show()}
                else{
                    name.lowercase()
                    val itemToAdd = hashMapOf(
                        "name" to name,
                        "amount" to amt,
                        "price" to price
                    )
                    name = name.replace(" ","")
                    db.collection("People").document(mAuth.uid!!).collection("Items").document(name).set(itemToAdd)
                        .addOnFailureListener { e-> Toast.makeText(context,e.message,Toast.LENGTH_SHORT).show() }
                }
            }catch(e:NumberFormatException){
                Toast.makeText(context,"Please fill in all of the blanks",Toast.LENGTH_SHORT).show()
            }
        }
        requireActivity().title = "Items"
    }

    inner class ItemsAdapter(private var mGrocery: MutableList<GroceryItem>): RecyclerView.Adapter<ItemsAdapter.GViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GViewHolder {
            val binding = ListItemGroceryBinding.inflate(
                layoutInflater, parent, false
            )
            return GViewHolder(binding)
        }
        override fun onBindViewHolder(holder: GViewHolder, position: Int) {
            holder.setup(mGrocery[position])
        }
        override fun getItemCount(): Int {
            return mGrocery.size
        }
        inner class GViewHolder(private var mBinding: ListItemGroceryBinding):
            RecyclerView.ViewHolder(
                mBinding.root
            ) {
            fun setup(g:GroceryItem){
                val dec = DecimalFormat("#,###.00")
                val total = "$"+ dec.format(g.price * g.amount)
                val priceSet = "$"+dec.format(g.price)
                mBinding.textViewItemAmt.text = "x"+g.amount
                mBinding.textViewItemName.text = g.name
                mBinding.textViewItemPrice.text = priceSet
                mBinding.textViewSubtotal.text = total
                mBinding.imageViewDeleteItem.setOnClickListener{
                    db.collection("People").document(mAuth.uid!!).collection("Items").document(g.id).delete()
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as GroceryListener
    }

    interface GroceryListener{
        fun goBack()
    }
}
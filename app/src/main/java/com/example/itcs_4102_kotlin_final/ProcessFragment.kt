package com.example.itcs_4102_kotlin_final

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.itcs_4102_kotlin_final.databinding.FragmentProcessBinding
import com.example.itcs_4102_kotlin_final.databinding.ListItemSubtotalsBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import java.text.DecimalFormat

class ProcessFragment : Fragment() {

    private var mBinding:FragmentProcessBinding?=null
    private var mListener: ProcessListener? = null
    private var db = FirebaseFirestore.getInstance()
    private var mAuth = FirebaseAuth.getInstance()
    var subtotal = 0.0
    var allDiscounts = 0.0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentProcessBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding!!.rvSubTotals.layoutManager = LinearLayoutManager(context)
        val mItems = mutableListOf<GroceryItem>()
        val mDiscounts = mutableListOf<Double>()
        db.collection("People").document(mAuth.uid!!).collection("Items").addSnapshotListener { value, _ ->
            for(x in value!!){
                mItems.add(GroceryItem(x))
                subtotal += (x.get("price").toString().toDouble() * x.get("amount").toString().toInt())
                mDiscounts.add(0.0)
            }
            mDiscounts.add(0.0)
            db.collection("People").document(mAuth.uid!!).collection("Coupons").get().addOnCompleteListener { task:Task<QuerySnapshot> ->
                for(i in task.result){
                    Log.d("tt", i.toString())
                    val doc:DocumentReference = i.get("for") as DocumentReference
                    var item: GroceryItem? = null
                    doc.get().addOnCompleteListener{task1:Task<DocumentSnapshot>->
                        var pos = 0
                        try{
                            item = GroceryItem(task1.result)
                            for(j in mItems){
                                if(j.id == item!!.id){
                                    pos = mItems.indexOf(j)
                                }
                            }
                        }catch(_:NumberFormatException){
                            pos = mDiscounts.size-1
                        }
                        var discountAmt = 0.0
                        when(i.get("type").toString().toInt()){
                            1->{
                                val mAmt = i.get("buyAmt").toString().toInt() + i.get("freeAmt").toString().toInt()
                                if(item!!.amount >= mAmt){
                                    discountAmt = (item!!.amount / mAmt) * (item!!.price * i.get("freeAmt").toString().toInt())

                                }
                            }
                            2->{
                                try{
                                    discountAmt = ((item!!.price*item!!.amount)* i.get("perOff").toString().toInt())/100
                                }catch(e:NullPointerException){
                                    discountAmt = (subtotal * i.get("perOff").toString().toInt())/100
                                }
                            }
                            3->{
                                discountAmt = i.get("amtOff").toString().toDouble()
                            }
                        }
                        allDiscounts += discountAmt
                        try{
                            mDiscounts[pos] += discountAmt
                        }catch(e:ArrayIndexOutOfBoundsException){
                            mDiscounts[mDiscounts.size-1] += discountAmt
                        }
                        Log.d("tt", mDiscounts.toString())
                        if (i  ==  task.result.elementAt(task.result.size()-1)){
                            Log.d("tt", mDiscounts.toString())
                            val adapter = ProcessAdapter(mItems, mDiscounts)
                            mBinding!!.rvSubTotals.adapter = adapter
                            val dec = DecimalFormat("#,###.00")
                            val s = "$" + dec.format(subtotal - allDiscounts)
                            val t = "$" + dec.format((subtotal - allDiscounts) * 0.07)
                            val tp =
                                "$" + dec.format((subtotal - allDiscounts) + ((subtotal - allDiscounts) * 0.07))
                            mBinding!!.textViewSubtotal.text = s
                            mBinding!!.textViewTax.text = t
                            mBinding!!.textViewTotalPrice.text = tp
                        }
                    }
                }
            }
        }
        mBinding!!.buttonGoBackFromProcess.setOnClickListener { mListener!!.goBack()}
    }

    inner class ProcessAdapter(private var mGrocery: MutableList<GroceryItem>, private var mDiscounts: MutableList<Double>): RecyclerView.Adapter<ProcessAdapter.PViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PViewHolder {
            val binding = ListItemSubtotalsBinding.inflate(
                layoutInflater, parent, false
            )
            return PViewHolder(binding)
        }
        override fun onBindViewHolder(holder: PViewHolder, position: Int) {
            if(position == 0){
                holder.setupGeneral(mDiscounts[mDiscounts.size-1])
            }else {
                holder.setup(mGrocery[position-1], mDiscounts[position-1])
            }
        }
        override fun getItemCount(): Int {
            return mGrocery.size+1
        }
        inner class PViewHolder(private var mBinding: ListItemSubtotalsBinding):
            RecyclerView.ViewHolder(
                mBinding.root
            ) {
            fun setup(g:GroceryItem, d:Double){
                val dec = DecimalFormat("#,###.00")
                val total = "$"+ dec.format(g.price * g.amount)
                val priceSet = "$"+dec.format(g.price)
                val amtSet = "x"+g.amount
                val discount = "$"+ dec.format(d)
                val newSub = "$" + dec.format((g.price * g.amount) - d)
                mBinding.textViewItemAmt.text = amtSet
                mBinding.textViewItemName.text = g.name
                mBinding.textViewItemPrice.text = priceSet
                mBinding.textViewSubtotal.text = total
                mBinding.textViewDiscount.text = discount
                mBinding.textViewNewSubtotal.text = newSub
            }
            fun setupGeneral(d:Double){
                mBinding.tvNewSub.visibility = View.INVISIBLE
                mBinding.tvTotalPrice.visibility = View.INVISIBLE
                val dec = DecimalFormat("#,###.00")
                val name = "General"
                val discount = "$"+ dec.format(d)
                mBinding.textViewItemName.text = name
                mBinding.textViewDiscount.text = discount
            }
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as ProcessListener
    }
    public interface ProcessListener{
        fun goBack()
        fun clickOnItem(ref:DocumentReference)
    }
}
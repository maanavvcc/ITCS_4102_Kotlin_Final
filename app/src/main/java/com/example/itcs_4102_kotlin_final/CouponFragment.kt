package com.example.itcs_4102_kotlin_final

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.itcs_4102_kotlin_final.databinding.FragmentCouponBinding
import com.example.itcs_4102_kotlin_final.databinding.ListItemCouponBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

class CouponFragment : Fragment() {

    private var mBinding: FragmentCouponBinding? = null
    private var mListener: CouponListener? = null
    private var db = FirebaseFirestore.getInstance()
    private var mAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentCouponBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding!!.buttonBackFromCoupons.setOnClickListener { mListener!!.goBack() }
        var couponType = 0
        val spinsList = mutableListOf<SpinnerItems>()
        val spinsNames = mutableListOf<String>()
        db.collection("People").document(mAuth.uid!!).collection("Items").addSnapshotListener { value, _ ->
            spinsNames.add("General")
            try {
                for (doc in value!!) {
                    spinsList.add(SpinnerItems(doc))
                    spinsNames.add(doc.get("name").toString())
                }
            }catch(e:NullPointerException){}
            val spinnerAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item, spinsNames)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            mBinding!!.spinnerItems.adapter=spinnerAdapter
        }
        mBinding!!.rdCoupons.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId){
                R.id.radBOGO -> {
                    couponType = 1
                    mBinding!!.editTextBuyAmt.visibility = View.VISIBLE
                    mBinding!!.editTextFreeAmt.visibility = View.VISIBLE
                    mBinding!!.textViewperSign.visibility = View.INVISIBLE
                    mBinding!!.editTextPerOff.visibility = View.INVISIBLE
                    mBinding!!.textViewdollarSign.visibility = View.INVISIBLE
                    mBinding!!.editTextAmtOff.visibility = View.INVISIBLE
                }
                R.id.radPerOff -> {
                    couponType = 2
                    mBinding!!.textViewperSign.visibility = View.VISIBLE
                    mBinding!!.editTextPerOff.visibility = View.VISIBLE
                    mBinding!!.editTextBuyAmt.visibility = View.INVISIBLE
                    mBinding!!.editTextFreeAmt.visibility = View.INVISIBLE
                    mBinding!!.textViewdollarSign.visibility = View.INVISIBLE
                    mBinding!!.editTextAmtOff.visibility = View.INVISIBLE
                }
                R.id.radAmountOff -> {
                    couponType = 3
                    mBinding!!.textViewdollarSign.visibility = View.VISIBLE
                    mBinding!!.editTextAmtOff.visibility = View.VISIBLE
                    mBinding!!.editTextBuyAmt.visibility = View.INVISIBLE
                    mBinding!!.editTextFreeAmt.visibility = View.INVISIBLE
                    mBinding!!.textViewperSign.visibility = View.INVISIBLE
                    mBinding!!.editTextPerOff.visibility = View.INVISIBLE
                }
            }
        }
        var general: DocumentReference? = null
        db.collection("People").document("general").get().addOnCompleteListener { task: Task<DocumentSnapshot> ->
            general = task.result.reference
        }
        mBinding!!.buttonAddCoupon.setOnClickListener {
            val spinNum = mBinding!!.spinnerItems.selectedItemPosition
            when(couponType){
                0->{Toast.makeText(context,"Please select a coupon type",Toast.LENGTH_SHORT).show()}
                1->{
                    try{
                        val buyAmt = (mBinding!!.editTextBuyAmt.text.toString()).toInt()
                        val freeAmt = (mBinding!!.editTextFreeAmt.text.toString()).toInt()
                        if((buyAmt < 1) or (freeAmt < 1)){
                            Toast.makeText(context,"Please enter a value greater than 0 into both entries",Toast.LENGTH_SHORT).show()}
                        else if (spinNum == 0){Toast.makeText(context,"Please select an item for this coupon type",Toast.LENGTH_SHORT).show()}
                        else{
                            val couponToAdd = hashMapOf(
                                "type" to 1,
                                "for" to spinsList[spinNum-1].ref,
                                "buyAmt" to buyAmt,
                                "freeAmt" to freeAmt
                            )
                            val docName = spinsList[spinNum-1].name + "1"
                            db.collection("People").document(mAuth.uid!!).collection("Coupons").document(docName).set(couponToAdd)
                                .addOnFailureListener { e-> Toast.makeText(context,e.message,Toast.LENGTH_SHORT).show() }
                        }
                    }catch(e:NumberFormatException){Toast.makeText(context,"Please enter a value greater than 0 into both entries",Toast.LENGTH_SHORT).show()}
                }
                2->{
                    try{
                        val perOff = mBinding!!.editTextPerOff.text.toString().toInt()
                        if((perOff>100) or (perOff < 1)){
                                Toast.makeText(context,"Please enter a value greater than 0 and less 100 into the entry",Toast.LENGTH_SHORT).show()}
                        else{
                            val docName:String
                            val couponToAdd: HashMap<String,Any?>
                            if(spinNum>0){
                                couponToAdd = hashMapOf(
                                    "type" to 2,
                                    "perOff" to perOff,
                                    "for" to (spinsList[spinNum-1].ref)
                                )
                                docName = spinsList[spinNum-1].name + "2"
                            }else{
                                couponToAdd = hashMapOf(
                                    "type" to 2,
                                    "perOff" to perOff,
                                    "for" to general
                                )
                                docName = "General2"
                            }
                            db.collection("People").document(mAuth.uid!!).collection("Coupons").document(docName).set(couponToAdd)
                                .addOnFailureListener { e-> Toast.makeText(context,e.message,Toast.LENGTH_SHORT).show() }
                        }
                    }catch(e:NumberFormatException){Toast.makeText(context,"Please enter a value greater than 0 and less 100 into the entry",Toast.LENGTH_SHORT).show()}
                }
                3->{
                    try{
                        val amtOff = mBinding!!.editTextAmtOff.text.toString().toDouble()
                        if((amtOff < 0)){
                            Toast.makeText(context,"Please enter a value greater than 0",Toast.LENGTH_SHORT).show()
                        }
                        else if(spinNum != 0){
                                if(amtOff > (spinsList[spinNum-1]).price){
                                    Toast.makeText(context,"Please enter a value less than the item price",Toast.LENGTH_SHORT).show()
                                }else{
                                    val couponToAdd = hashMapOf(
                                        "type" to 3,
                                        "amtOff" to amtOff,
                                        "for" to (spinsList[spinNum-1].ref)
                                    )
                                    val docName:String = spinsList[spinNum-1].name + "3"
                                    db.collection("People").document(mAuth.uid!!).collection("Coupons").document(docName).set(couponToAdd)
                                        .addOnFailureListener { e-> Toast.makeText(context,e.message,Toast.LENGTH_SHORT).show() }
                                }
                        }else{
                            var couponToAdd = hashMapOf(
                                "type" to 3,
                                "amtOff" to amtOff,
                                "for" to general
                            )
                            val docName:String = "General3"
                            db.collection("People").document(mAuth.uid!!).collection("Coupons").document(docName).set(couponToAdd)
                                .addOnFailureListener { e-> Toast.makeText(context,e.message,Toast.LENGTH_SHORT).show() }
                        }
                    }catch (e:NumberFormatException){Toast.makeText(context,"Please enter a value greater than 0 and less than the item price",Toast.LENGTH_SHORT).show()}
                }
            }
        }
        db.collection("People").document(mAuth.uid!!).collection("Coupons").addSnapshotListener { value, _ ->
            mBinding!!.rvCoupons.layoutManager = LinearLayoutManager(context)
            val coupons = mutableListOf<Coupon>()
            for(coupon in value!!){
                coupons.add(Coupon(coupon))
            }
            val adapter = CouponsAdapter(coupons)
            mBinding!!.rvCoupons.adapter = adapter
        }
        requireActivity().title = "Coupons"
    }

    inner class CouponsAdapter(private var mCoupons: MutableList<Coupon>): RecyclerView.Adapter<CouponsAdapter.CViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CViewHolder {
            val binding = ListItemCouponBinding.inflate(
                layoutInflater, parent, false
            )
            return CViewHolder(binding)
        }
        override fun onBindViewHolder(holder: CViewHolder, position: Int) {
            holder.setup(mCoupons[position])
        }
        override fun getItemCount(): Int {
            return mCoupons.size
        }
        inner class CViewHolder(private var mBinding: ListItemCouponBinding):
            RecyclerView.ViewHolder(
                mBinding.root
            ) {
            fun setup(c: Coupon){
                c.forItemRef.get().addOnCompleteListener { task:Task<DocumentSnapshot> ->
                    mBinding.textViewForName.text = task.result.get("name").toString()
                }
                when(c.type){
                    1->{
                        mBinding.tvBuy.visibility = View.VISIBLE
                        mBinding.tvFree.visibility = View.VISIBLE
                        mBinding.tvGet.visibility = View.VISIBLE
                        mBinding.textViewBuyAmt.visibility = View.VISIBLE
                        mBinding.textViewFreeAmt.visibility = View.VISIBLE
                        mBinding.textViewBuyAmt.text = c.bogo[0].toString()
                        mBinding.textViewFreeAmt.text = c.bogo[1].toString()
                    }
                    2->{
                        mBinding.tvPerOff.visibility = View.VISIBLE
                        mBinding.textViewPerOff.visibility = View.VISIBLE
                        val perText = c.perOff.toString()+"%"
                        mBinding.textViewPerOff.text = perText
                    }
                    3->{
                        mBinding.tvPriceOff.visibility = View.VISIBLE
                        mBinding.textViewPriceOff.visibility = View.VISIBLE
                        val amtText = "$" + c.amtOff.toString()
                        mBinding.textViewPriceOff.text = amtText
                    }
                }
                mBinding.imageViewDeleteCoupon.setOnClickListener{
                    db.collection("People").document(mAuth.uid!!).collection("Coupons").document(c.id).delete()
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as CouponListener
    }

    interface CouponListener{
        fun goBack()
    }
}
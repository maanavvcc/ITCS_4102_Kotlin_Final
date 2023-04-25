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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
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
        db.collection("People").document(mAuth.uid!!).collection("Items").addSnapshotListener { value, _ ->
            val spinsNames = mutableListOf<String>()
            spinsNames.add("General")
            for(doc in value!!){
                spinsList.add(SpinnerItems(doc))
                spinsNames.add(doc.get("name").toString())
            }
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
                }
                R.id.radPerOff -> {
                    couponType = 2
                    mBinding!!.textViewperSign.visibility = View.VISIBLE
                    mBinding!!.editTextPerOff.visibility = View.VISIBLE
                }
                R.id.radAmountOff -> {
                    couponType = 3
                    mBinding!!.textViewdollarSign.visibility = View.VISIBLE
                    mBinding!!.editTextAmtOff.visibility = View.VISIBLE
                }
            }
        }
        mBinding!!.buttonAddCoupon.setOnClickListener {
            val spinNum = mBinding!!.spinnerItems.selectedItemPosition
            val general:DocumentReference = db.collection("People").document("general").get().result.reference
            when(couponType){
                0->{Toast.makeText(context,"Please select a coupon type",Toast.LENGTH_SHORT).show()}
                1->{
                    val buyAmtString = mBinding!!.editTextBuyAmt.toString()
                    val buyAmt = buyAmtString.toInt()
                    val freeAmtString = mBinding!!.editTextFreeAmt.toString()
                    val freeAmt = freeAmtString.toInt()
                    if(buyAmtString.isEmpty() or freeAmtString.isEmpty() or (buyAmt < 1) or (freeAmt < 1)){
                        Toast.makeText(context,"Please enter a value greater than 0 into both entries",Toast.LENGTH_SHORT).show()}
                    else if (spinNum < 1){Toast.makeText(context,"Please select an item for this coupon type",Toast.LENGTH_SHORT).show()}
                    else{
                        val couponToAdd = hashMapOf(
                            "type" to 1,
                            "for" to spinsList[1+spinNum].ref,
                            "buyAmt" to buyAmt,
                            "freeAmt" to freeAmt
                        )
                        val docName = spinsList[spinNum-1].name + "1"
                        db.collection("People").document(mAuth.uid!!).collection("Items").document(docName).set(couponToAdd)
                            .addOnFailureListener { e-> Toast.makeText(context,e.message,Toast.LENGTH_SHORT).show() }
                    }
                }
                2->{
                    val perOffString = mBinding!!.editTextPerOff.toString()
                    val perOff = perOffString.toInt()
                    if(perOffString.isEmpty() or (perOff>100) or (perOff < 1)){
                            Toast.makeText(context,"Please enter a value greater than 0 and less 100 into the entry",Toast.LENGTH_SHORT).show()}
                    else if (spinNum<0){Toast.makeText(context,"Please select an item for this coupon type",Toast.LENGTH_SHORT).show()}
                    else{
                        val docName:String
                        val couponToAdd: HashMap<String,Any?>
                        if(spinNum>0){
                            couponToAdd = hashMapOf(
                                "type" to 2,
                                "perOff" to perOff,
                                "for" to (spinsList[spinNum-1].ref)
                            )
                            docName = spinsList[spinNum-1].name + "1"
                        }else{
                            couponToAdd = hashMapOf(
                                "type" to 2,
                                "perOff" to perOff,
                                "for" to general
                            )
                            docName = "General" + "1" + Random(100000).nextInt().toString()
                        }
                        db.collection("People").document(mAuth.uid!!).collection("Items").document(docName).set(couponToAdd)
                            .addOnFailureListener { e-> Toast.makeText(context,e.message,Toast.LENGTH_SHORT).show() }
                    }
                }
                3->{
                    val amtOffString = mBinding!!.editTextAmtOff.toString()
                    val amtOff = amtOffString.toDouble()
                    if(amtOffString.isEmpty() or (amtOff < 0) or (amtOff > (spinsList[spinNum-1]).price)){
                        Toast.makeText(context,"Please enter a value greater than 0 and less than the item price",Toast.LENGTH_SHORT).show()
                    }else{
                        val docName:String
                        val couponToAdd: HashMap<String,Any?>
                        if(spinNum>0){
                            couponToAdd = hashMapOf(
                                "type" to 2,
                                "amtOff" to amtOff,
                                "for" to (spinsList[spinNum-1].ref)
                            )
                            docName = spinsList[spinNum-1].name + "1"
                        }else{
                            couponToAdd = hashMapOf(
                                "type" to 2,
                                "amtOff" to amtOff,
                                "for" to general
                            )
                            docName = "General" + "1" + Random(100000).nextInt().toString()
                        }
                        db.collection("People").document(mAuth.uid!!).collection("Items").document(docName).set(couponToAdd)
                            .addOnFailureListener { e-> Toast.makeText(context,e.message,Toast.LENGTH_SHORT).show() }
                    }
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
                mBinding.textViewForName.text = c.forItem.getString("name")
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
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
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.itcs_4102_kotlin_final.databinding.FragmentItemWithCouponsBinding
import com.example.itcs_4102_kotlin_final.databinding.FragmentProcessBinding
import com.example.itcs_4102_kotlin_final.databinding.ListItemCouponBinding
import com.example.itcs_4102_kotlin_final.databinding.ListItemProcessCouponsBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.text.DecimalFormat

private const val ARG_PARAM1 = "param1"

class ItemWithCouponsFragment : Fragment() {
    private var mBinding: FragmentItemWithCouponsBinding?=null
    private var mListener: ItemWithCouponListener? = null
    private var db = FirebaseFirestore.getInstance()
    private var mAuth = FirebaseAuth.getInstance()
    private var itemID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemID = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentItemWithCouponsBinding.inflate(inflater,container,false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db.collection("People").document(mAuth.uid!!).collection("Items").get().addOnCompleteListener { task: Task<QuerySnapshot> ->
            val dec = DecimalFormat("#,###.00")
            for(item in task.result){
                if ((item.id).equals(itemID)){
                    val price = "$" + dec.format(item.get("price"))
                    val amt = "x"+ item.get("amount")
                    val sub = "$"+dec.format ((item.get("price").toString().toDouble()) * (item.get("amount").toString().toInt()))
                    mBinding!!.textViewItemName.text = item.getString("name")
                    mBinding!!.textViewItemPrice.text = price
                    mBinding!!.textViewItemAmt.text = amt
                    mBinding!!.textViewSubtotal.text = sub
                    mBinding!!.imageViewDeleteItem.setOnClickListener{
                        //TODO
                    }
                }
            }
        }
        val mCoupons = mutableListOf<Coupon>()
        mBinding!!.rvCouponsForItem.layoutManager = LinearLayoutManager(context)
        db.collection("People").document(mAuth.uid!!).collection("Coupons").get().addOnCompleteListener{ task: Task<QuerySnapshot> ->
            for(coupon in task.result){
                val doc: DocumentReference = coupon.get("for") as DocumentReference
                doc.get().addOnCompleteListener { doc1: Task<DocumentSnapshot> ->
                    if(doc1.result.id.equals(itemID)){
                        mCoupons.add(Coupon(coupon))
                        Log.d("tt",mCoupons.toString())
                        val adapter = CouponsAdapter(mCoupons)
                        mBinding!!.rvCouponsForItem.adapter = adapter
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            ItemWithCouponsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as ItemWithCouponListener
    }
    private inner class CouponsAdapter(private var mCoupons: MutableList<Coupon>): RecyclerView.Adapter<CouponsAdapter.CViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CViewHolder {
            val binding = ListItemProcessCouponsBinding.inflate(
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
        inner class CViewHolder(private var mBinding: ListItemProcessCouponsBinding):
            RecyclerView.ViewHolder(
                mBinding.root
            ) {
            fun setup(c: Coupon){
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

}
interface ItemWithCouponListener{
    fun goBack()
}
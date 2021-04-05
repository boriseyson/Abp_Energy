package com.misit.abpenergy.Adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.misit.abpenergy.R
import com.misit.abpenergy.Rkb.Response.DataItem
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*


class UserRkbListAdapter(
    private val context: Context?,
    private val username: String?,
    private val rkbList:MutableList<DataItem>,section:String):
    RecyclerView.Adapter<UserRkbListAdapter.MyViewHolder>(){


    private val layoutInflater: LayoutInflater
    private var simpleDateFormat: SimpleDateFormat? = null
    private var section=section
    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {

        val view = layoutInflater.inflate(R.layout.list_item_rkb,parent,false)
        return MyViewHolder(view)
        
    }

    override fun getItemCount(): Int {
        return rkbList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val today = Date()
        var rkbList =rkbList[position]
        val date = LocalDate.parse(rkbList.tglOrder)
        val fmt: DateTimeFormatter = DateTimeFormat.forPattern("d MMMM, yyyy")

        val kabagAppr = rkbList.tglDisetujui?.split(" ")
        val KttAppr = rkbList.tglDiketahui?.split(" ")

        var tglOrder = date.toString(fmt);
        holder.btnApproveKabag.visibility=View.GONE
        holder.btnApproveKTT.visibility=View.GONE
        holder.v_appr.visibility=View.GONE
        holder.cancelKabag.visibility=View.GONE
        holder.cancelKTT.visibility=View.GONE

        holder.tvNorkb?.text = rkbList.noRkb
        holder.tvTgl?.text =tglOrder.toString()
        holder.tvUserCreate?.text = rkbList.namaLengkap
        holder.tvDeptSect?.text = "${rkbList.dept} - ${rkbList.section}"
        holder.tvKabag.text = if(rkbList.disetujui=="1") {
            kabagAppr!![1]+", "+LocalDate.parse(kabagAppr!![0]).toString(fmt)
                                }else{"Waiting"}
        if(rkbList.disetujui=="1"){
            holder.tvKabag.setTextSize(TypedValue.COMPLEX_UNIT_SP,8F)
            holder.tvKabag.setBackgroundResource(R.drawable.btn_success)
            holder.tvCancelUser.text = ""
        }else{
            if(rkbList.cancelSection==null && section=="KABAG") {
                holder.cancelKabag.visibility = View.VISIBLE
                holder.btnApproveKabag.visibility = View.VISIBLE
                holder.v_appr.visibility=View.VISIBLE
                holder.btnApproveKTT.visibility=View.GONE
                holder.cancelKTT.visibility=View.GONE
                holder.tvCancelUser.text = ""

            }
            if(rkbList.userEntry==username && rkbList.cancelSection==null && (section!="KABAG" && section!="KTT")){
                holder.v_appr.visibility=View.VISIBLE
                holder.lnCancelUser.visibility=View.VISIBLE
                holder.tvCancelUser.text = ""
            }else{
                holder.lnCancelUser.visibility=View.GONE
                holder.tvCancelUser.text = ""
            }
            holder.tvKabag.setBackgroundResource(R.drawable.btn_waiting)
        }
        holder.tvKtt.text = if(rkbList.diketahui=="1") {
                             KttAppr!![1]+", "+LocalDate.parse(KttAppr!![0]).toString(fmt)
                             }else{"Waiting"}
        if(rkbList.diketahui=="1"){
            holder.tvCancelUser.text = ""
            holder.tvKtt.setBackgroundResource(R.drawable.btn_success)
        }else{
            holder.tvCancelUser.text = ""
            holder.tvKtt.setBackgroundResource(R.drawable.btn_waiting)

        }
        if(rkbList.disetujui=="1" && rkbList.diketahui=="1"){
            holder.tvNorkb.setBackgroundResource(R.color.bgApprove)
        }else{
            if(rkbList.cancelUser!=null){
                if(rkbList.cancelSection=="KTT"){
                    holder.tvKtt.setBackgroundResource(R.drawable.btn_cancel)
                    holder.tvKtt.text=rkbList.cancelUser
                    holder.tvCancelUser.text = ""
                }else if(rkbList.cancelSection=="KABAG"){
                    holder.tvKabag.setBackgroundResource(R.drawable.btn_cancel)
                    holder.tvKabag.text=rkbList.cancelUser
                    holder.tvCancelUser.text = ""
                }else{
                    holder.tvKabag.setBackgroundResource(R.drawable.btn_cancel)
                    holder.tvKtt.setBackgroundResource(R.drawable.btn_cancel)
                    holder.tvKabag.text="X"
                    holder.tvKtt.text="X"
                    if(rkbList.cancelUser==rkbList.userEntry){
                        holder.tvCancelUser.text = rkbList.cancelUser
                    }else{
                        holder.tvCancelUser.text = ""
                    }
                }
                holder.tvNorkb.setBackgroundResource(R.color.bgCancel)
            }else{
                holder.tvNorkb.setBackgroundResource(R.color.bgWaiting)
                if(rkbList.cancelSection==null && section == "KTT" && rkbList.disetujui=="1"){
                    holder.cancelKTT.visibility = View.VISIBLE
                    holder.btnApproveKTT.visibility = View.VISIBLE
                    holder.v_appr.visibility = View.VISIBLE
                    holder.btnApproveKabag.visibility=View.GONE
                    holder.cancelKabag.visibility=View.GONE
                }
                holder.tvCancelUser.text = ""
            }
//            if(rkbList.cancelUser !=null){
//                if(rkbList.cancelSection!="KTT" && rkbList.cancelSection!="KABAG"){
//                    holder.tvCancelUser.visibility= View.VISIBLE
//                    holder.tvCancelUser.text = rkbList.cancelUser
//                }else{
//                    holder.tvCancelUser.visibility= View.GONE
//                }
//
//            }else{
//                holder.tvCancelUser.visibility= View.GONE
//            }

        }
        if(onItemClickListener != null){
            holder.cvRkb?.setOnClickListener{onItemClickListener?.onItemClick(rkbList.noRkb)}
            holder.btnApproveKabag?.setOnClickListener{onItemClickListener?.onApproveKabag(rkbList.noRkb)}
            holder.cancelKabag?.setOnClickListener{onItemClickListener?.onCancelKabag(rkbList.noRkb)}
            holder.btnApproveKTT?.setOnClickListener{onItemClickListener?.onApproveKTT(rkbList.noRkb)}
            holder.cancelKTT?.setOnClickListener{onItemClickListener?.onCancelKTT(rkbList.noRkb)}
            holder.cancelUser?.setOnClickListener{onItemClickListener?.onCancelUser(rkbList.noRkb)}

        }

    }
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var cvRkb = itemView.findViewById<View>(R.id.cv_Rkb) as CardView
        var tvNorkb = itemView.findViewById<View>(R.id.tv_norkb) as TextView
        var tvTgl= itemView.findViewById<View>(R.id.tv_tgl) as TextView
        var tvUserCreate = itemView.findViewById<View>(R.id.tv_userCreate) as TextView
        var tvDeptSect = itemView.findViewById<View>(R.id.tv_dept_sect) as TextView
        var tvKabag = itemView.findViewById<View>(R.id.tv_kabag) as TextView
        var tvKtt = itemView.findViewById<View>(R.id.tv_ktt) as TextView
        var tvCancelUser = itemView.findViewById<View>(R.id.tv_userCancel) as TextView
        var btnApproveKabag = itemView.findViewById<View>(R.id.btnApproveKabag) as Button
        var btnApproveKTT = itemView.findViewById<View>(R.id.btnApproveKTT) as Button
        var cancelKTT = itemView.findViewById<View>(R.id.cancelKTT) as Button
        var cancelKabag = itemView.findViewById<View>(R.id.cancelKabag) as Button
        var cancelUser = itemView.findViewById<View>(R.id.CancelUser) as Button
        var v_appr = itemView.findViewById<View>(R.id.v_appr) as View
        var lnCancelUser = itemView.findViewById<View>(R.id.lnCancelUser) as LinearLayout


    }
    interface OnItemClickListener{
        fun onItemClick(noRkb:String?)
        fun onApproveKabag(noRkb:String?)
        fun onCancelKabag(noRkb:String?)
        fun onApproveKTT(noRkb:String?)
        fun onCancelKTT(noRkb:String?)
        fun onCancelUser(noRkb:String?)
    }
    fun setListener (listener:OnItemClickListener){
        onItemClickListener = listener
    }

    init {
        layoutInflater = LayoutInflater.from(context)
        simpleDateFormat= SimpleDateFormat("yyyy-MM-dd")
    }

}
package com.misit.abpenergy.HazardReport.Adapter

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.misit.abpenergy.HazardReport.Response.HazardItem
import com.misit.abpenergy.R
import com.misit.abpenergy.Utils.PrefsUtil
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*

class ListHazardReportAdapter (private val context: Context,
                               private val rule:String,
                               private val activityName :String,
                               private val hazardList:MutableList<HazardItem>):
    RecyclerView.Adapter<ListHazardReportAdapter.MyViewHolder>(){
    private var userRule:Array<String>?=null

    private var onItemClickListener: OnItemClickListener? = null

    private val layoutInflater: LayoutInflater
    private var simpleDateFormat: SimpleDateFormat? = null
    lateinit var view:View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        view = layoutInflater.inflate(R.layout.hazard_list,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        Log.d("HazarList",hazardList.size.toString())
        return hazardList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var hazardList =hazardList[position]
        val fmt: DateTimeFormatter = DateTimeFormat.forPattern("d MMMM, yyyy")
        holder.tvJamHazard.text = hazardList.jamHazard
        holder.tvTglHazard.text = LocalDate.parse(hazardList.tglHazard).toString(fmt)
        holder.tvPerusahaan.text = hazardList.perusahaan
        holder.tvLokasi.text = hazardList.lokasiHazard
        holder.tvDeskripsi.text = hazardList.deskripsi
        holder.tvStatus.text = hazardList.statusPerbaikan
        holder.tvUSER.text = hazardList.namaLengkap
        holder.tvPJ.text = hazardList.namaPJ

        if(hazardList.statusPerbaikan=="BELUM SELESAI"){
            holder.lnHeader.setBackgroundResource(R.color.bgCancel)
            holder.tvDeskripsiBahaya.setBackgroundResource(R.color.bgCancel)
            holder.tvStatus.setBackgroundResource(R.color.bgCancel)
        }else if(hazardList.statusPerbaikan=="SELESAI"){
            holder.lnHeader.setBackgroundResource(R.color.bgApprove)
            holder.tvDeskripsiBahaya.setBackgroundResource(R.color.bgApprove)
            holder.tvStatus.setBackgroundResource(R.color.bgApprove)
        }else if(hazardList.statusPerbaikan=="DIKERJAKAN"){
            holder.lnHeader.setBackgroundResource(R.color.bgWaiting)
            holder.tvDeskripsiBahaya.setBackgroundResource(R.color.bgWaiting)
            holder.tvStatus.setBackgroundResource(R.color.bgWaiting)
        }else if(hazardList.statusPerbaikan=="BERLANJUT"){
            holder.lnHeader.setBackgroundResource(R.color.bgTotal)
            holder.tvDeskripsiBahaya.setBackgroundResource(R.color.bgTotal)
            holder.tvStatus.setBackgroundResource(R.color.bgTotal)
        }
        if(hazardList.uservalid!=null){
            holder.tvVerfikasi.text = "Di Setujui Oleh Safety"
            holder.tvVerfikasi.setBackgroundResource(R.color.bgApprove)
        }else{
            holder.tvVerfikasi.setBackgroundResource(R.color.bgCancel)
            holder.tvVerfikasi.text = "Belum Disetujui Oleh Safety"
        }
        holder.cvHazard.setOnClickListener{
            onItemClickListener?.onItemClick(hazardList.uid.toString())
        }
        holder.btnUpdateStatus.setOnClickListener {
            onItemClickListener?.onUpdateClick(hazardList.uid.toString())
        }
        holder.bntHSEappr.setOnClickListener {
            onItemClickListener?.onVerify(hazardList.uid.toString(),1)
        }
        holder.btnHSEdeny.setOnClickListener {
            onItemClickListener?.onVerify(hazardList.uid.toString(),0)
        }

        if(activityName=="ALL") {
            if (rule != null) {
                userRule = rule.split(",").toTypedArray()
                var hseAdmin = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Arrays.stream(userRule).anyMatch { t -> t == "admin_hse" }
                } else {
                    userRule?.contains("admin_hse")
                }
                if (hseAdmin!!) {
//                    Log.d("UserValid",hazardList!!.uservalid)
                    if(hazardList?.uservalid==null || hazardList?.uservalid==""){
                        holder.lnHSEAdmin.visibility = View.VISIBLE
                    }else{
                        holder.lnHSEAdmin.visibility = View.GONE
                    }
                } else {
                    holder.lnHSEAdmin.visibility = View.GONE
                }
            }else{
                holder.lnHSEAdmin.visibility = View.GONE
            }
        }else{
            holder.lnHSEAdmin.visibility = View.GONE
        }
    }
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cvHazard = itemView.findViewById<View>(R.id.cvHazard) as CardView
        var tvJamHazard = itemView.findViewById<View>(R.id.tvJamHazard) as TextView
        var tvTglHazard = itemView.findViewById<View>(R.id.tvTglHazard) as TextView
        var tvPerusahaan = itemView.findViewById<View>(R.id.tvPerusahaan) as TextView
        var tvLokasi = itemView.findViewById<View>(R.id.tvLokasi) as TextView
        var tvDeskripsi = itemView.findViewById<View>(R.id.tvDeskripsi) as TextView
        var tvStatus = itemView.findViewById<View>(R.id.tvStatus) as TextView
        var lnHeader = itemView.findViewById<View>(R.id.lnHeader) as LinearLayout
        var tvDeskripsiBahaya = itemView.findViewById<View>(R.id.tvDeskripsiBahaya) as TextView
        var btnUpdateStatus = itemView.findViewById<View>(R.id.btnUpdateStatus) as Button
        var tvUSER = itemView.findViewById<View>(R.id.tvUSER) as TextView
        var tvPJ = itemView.findViewById<View>(R.id.tvPJ) as TextView
        var tvVerfikasi = itemView.findViewById<View>(R.id.tvVerfikasi) as TextView
        var lnHSEAdmin = itemView.findViewById<View>(R.id.lnHSEAdmin) as LinearLayout
        var bntHSEappr = itemView.findViewById<View>(R.id.bntHSEappr) as Button
        var btnHSEdeny = itemView.findViewById<View>(R.id.btnHSEdeny) as Button
    }
    interface OnItemClickListener{
        fun onItemClick(uid:String?)
        fun onUpdateClick(uid:String?)
        fun onVerify(uid: String?,option:Int?)
    }
    fun setListener (listener: OnItemClickListener){
        onItemClickListener = listener
    }
    init {
        PrefsUtil.initInstance(context)

        layoutInflater = LayoutInflater.from(context)
        simpleDateFormat= SimpleDateFormat("yyyy-MM-dd")
    }
}
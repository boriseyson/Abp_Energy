package com.misit.abpenergy.HazardReport.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.misit.abpenergy.HazardReport.Response.DataItem
import com.misit.abpenergy.R
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.text.SimpleDateFormat

class ListHazardReportAdapter (private val context: Context?,
                               private val hazardList:MutableList<DataItem>):
    RecyclerView.Adapter<ListHazardReportAdapter.MyViewHolder>(){

    private var onItemClickListener: OnItemClickListener? = null

    private val layoutInflater: LayoutInflater
    private var simpleDateFormat: SimpleDateFormat? = null
    lateinit var view:View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            view = layoutInflater.inflate(R.layout.hazard_list,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return hazardList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var hazardList =hazardList[position]
        val fmt: DateTimeFormatter = DateTimeFormat.forPattern("d MMMM, yyyy")

        holder.tvJamHazard.text = hazardList.jamHazard
        holder.tvTglHazard.text = LocalDate.parse(hazardList.tglHazard).toString(fmt)
        holder.tvPerusahaan.text = hazardList.perusahaan
        holder.tvLokasi.text = hazardList.lokasi
        holder.tvDeskripsi.text = hazardList.deskripsi
        holder.tvStatus.text = hazardList.statusPerbaikan

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
        holder.cvHazard.setOnClickListener{
            onItemClickListener?.onItemClick(hazardList.uid.toString())
        }
        holder.btnUpdateStatus.setOnClickListener {
            onItemClickListener?.onUpdateClick(hazardList.uid.toString())
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

    }
    interface OnItemClickListener{
        fun onItemClick(uid:String?)
        fun onUpdateClick(uid:String?)
    }
    fun setListener (listener: OnItemClickListener){
        onItemClickListener = listener
    }
    init {
        layoutInflater = LayoutInflater.from(context)
        simpleDateFormat= SimpleDateFormat("yyyy-MM-dd")
    }
}
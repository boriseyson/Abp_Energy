package com.misit.abpenergy.HazardReport.Adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.misit.abpenergy.HazardReport.Response.DataItem
import com.misit.abpenergy.HazardReport.Response.SumberItem
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

        if(hazardList.statusPerbaikan=="BELUM SELESAI"){
            holder.lnHeader.setBackgroundResource(R.color.bgCancel)
            holder.tvDeskripsiBahaya.setBackgroundResource(R.color.bgCancel)
            holder.tvOpen.visibility=View.VISIBLE
            holder.tvClose.visibility=View.GONE
            holder.tvProgress.visibility=View.GONE
            holder.tvContinue.visibility=View.GONE
        }else if(hazardList.statusPerbaikan=="SELESAI"){
            holder.lnHeader.setBackgroundResource(R.color.bgApprove)
            holder.tvDeskripsiBahaya.setBackgroundResource(R.color.bgApprove)
            holder.tvOpen.visibility=View.GONE
            holder.tvClose.visibility=View.VISIBLE
            holder.tvProgress.visibility=View.GONE
            holder.tvContinue.visibility=View.GONE
        }else if(hazardList.statusPerbaikan=="DIKERJAKAN"){
            holder.lnHeader.setBackgroundResource(R.color.bgWaiting)
            holder.tvDeskripsiBahaya.setBackgroundResource(R.color.bgWaiting)
            holder.tvOpen.visibility=View.GONE
            holder.tvClose.visibility=View.GONE
            holder.tvProgress.visibility=View.VISIBLE
            holder.tvContinue.visibility=View.GONE
        }else if(hazardList.statusPerbaikan=="BERLANJUT"){
            holder.lnHeader.setBackgroundResource(R.color.bgTotal)
            holder.tvDeskripsiBahaya.setBackgroundResource(R.color.bgTotal)
            holder.tvOpen.visibility=View.GONE
            holder.tvClose.visibility=View.GONE
            holder.tvProgress.visibility=View.GONE
            holder.tvContinue.visibility=View.VISIBLE
        }
        holder.cvHazard.setOnClickListener{
            onItemClickListener?.onItemClick(hazardList.uid.toString())
        }
    }
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cvHazard = itemView.findViewById<View>(R.id.cvHazard) as CardView
        var tvJamHazard = itemView.findViewById<View>(R.id.tvJamHazard) as TextView
        var tvTglHazard = itemView.findViewById<View>(R.id.tvTglHazard) as TextView
        var tvPerusahaan = itemView.findViewById<View>(R.id.tvPerusahaan) as TextView
        var tvLokasi = itemView.findViewById<View>(R.id.tvLokasi) as TextView
        var tvDeskripsi = itemView.findViewById<View>(R.id.tvDeskripsi) as TextView
        var tvOpen = itemView.findViewById<View>(R.id.tvOpen) as TextView
        var tvClose = itemView.findViewById<View>(R.id.tvClose) as TextView
        var tvProgress = itemView.findViewById<View>(R.id.tvProgress) as TextView
        var tvContinue = itemView.findViewById<View>(R.id.tvContinue) as TextView
        var lnHeader = itemView.findViewById<View>(R.id.lnHeader) as LinearLayout
        var tvDeskripsiBahaya = itemView.findViewById<View>(R.id.tvDeskripsiBahaya) as TextView

    }
    interface OnItemClickListener{
        fun onItemClick(uid:String?)
    }
    fun setListener (listener: OnItemClickListener){
        onItemClickListener = listener
    }
    init {
        layoutInflater = LayoutInflater.from(context)
        simpleDateFormat= SimpleDateFormat("yyyy-MM-dd")
    }
}
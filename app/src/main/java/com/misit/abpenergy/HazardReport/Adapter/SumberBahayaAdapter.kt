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
import androidx.recyclerview.widget.RecyclerView
import com.misit.abpenergy.HazardReport.Response.SumberItem
import com.misit.abpenergy.R
import java.text.SimpleDateFormat

class SumberBahayaAdapter (private val context: Context?,
                           private var sumberDipilih: String?,
                           private val sumberList:MutableList<SumberItem>):
    RecyclerView.Adapter<SumberBahayaAdapter.MyViewHolder>(){

    private var onItemClickListener: OnItemClickListener? = null

    private val layoutInflater: LayoutInflater
    private var simpleDateFormat: SimpleDateFormat? = null
    lateinit var view:View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            view = layoutInflater.inflate(R.layout.sumber_bahaya_list,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return sumberList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var sumberList =sumberList[position]

        holder.rbSumberBahaya.text = sumberList.bahaya
        if(sumberDipilih==sumberList.bahaya){
            holder.rbSumberBahaya.setTextColor(Color.parseColor("#FFFFFF"))
            holder.rbSumberBahaya.setBackgroundColor(Color.parseColor("#4F8E50"))
            holder.lnPilih.setBackgroundColor(Color.parseColor("#4F8E50"))
        }else{
            holder.rbSumberBahaya.setTextColor(Color.parseColor("#4F8E50"))
            holder.rbSumberBahaya.setBackgroundColor(Color.parseColor("#FFFFFF"))
            holder.lnPilih.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }
        holder.rbSumberBahaya.setOnClickListener{
            onItemClickListener?.onItemClick(sumberList.idBahaya.toString(),sumberList.bahaya)
        }
    }
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rbSumberBahaya = itemView.findViewById<View>(R.id.rbSumberBahaya) as Button
        var lnPilih = itemView.findViewById<View>(R.id.lnPilih) as LinearLayout
    }
    interface OnItemClickListener{
        fun onItemClick(idBahaya:String?,bahaya:String?)
    }
    fun setListener (listener: OnItemClickListener){
        onItemClickListener = listener
    }
    init {
        layoutInflater = LayoutInflater.from(context)
        simpleDateFormat= SimpleDateFormat("yyyy-MM-dd")
    }
}
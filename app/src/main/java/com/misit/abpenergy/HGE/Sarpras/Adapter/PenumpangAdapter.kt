package com.misit.abpenergy.HGE.Sarpras.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.misit.abpenergy.R
import com.misit.abpenergy.HGE.Sarpras.SaranaResponse.PenumpangListModel
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

class PenumpangAdapter (
    private val context: Context?,
    private val penumpangList:MutableList<PenumpangListModel>,
    private var list: ArrayList<String>):
    RecyclerView.Adapter<PenumpangAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater
    private var simpleDateFormat: SimpleDateFormat? = null
    private var onItemClickListener: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = layoutInflater.inflate(R.layout.custom_karyawan_list,
            parent,
            false)
        return MyViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return penumpangList.size
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var listPenumpang =penumpangList[position]
        holder.tvNik.text= listPenumpang.nik
        holder.tvNama.text= listPenumpang.nama
        holder.tvJabatan.text= listPenumpang.jabatan
        if(list.contains(listPenumpang.nik)) {
                holder.cbNik.isChecked=true
        }else{
                holder.cbNik.isChecked=false
        }
        if(onItemClickListener!=null){
            holder.lnPenumpang.setOnClickListener {
                holder.cbNik.performClick()
            }
            holder.cbNik.setOnClickListener{
                if (holder.cbNik.isChecked){
                    onItemClickListener?.onCheckTrue(listPenumpang.nik)
                }else{
                    onItemClickListener?.onCheckFalse(listPenumpang.nik)
                }
            }
        }
    }
    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvNik = itemView.findViewById<View>(R.id.tvNik) as TextView
        var tvNama = itemView.findViewById<View>(R.id.tvNama) as TextView
        var tvJabatan = itemView.findViewById<View>(R.id.tvJabatan) as TextView
        var cbNik = itemView.findViewById<View>(R.id.cbNik) as CheckBox
        var lnPenumpang = itemView.findViewById<View>(R.id.lnPenumpang) as LinearLayout

    }
    interface OnItemClickListener{
        fun onItemClick(nik:String?)
        fun onCheckTrue(nik:String?)
        fun onCheckFalse(nik:String?)
    }



    fun setListener (listener: OnItemClickListener){
        onItemClickListener = listener
    }
    init {
        layoutInflater = LayoutInflater.from(context)
        simpleDateFormat= SimpleDateFormat("yyyy-MM-dd")
    }

}
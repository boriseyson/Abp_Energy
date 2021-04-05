package com.misit.abpenergy.Sarpras.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.misit.abpenergy.R
import com.misit.abpenergy.Sarpras.Realm.PenumpangModel
import java.text.SimpleDateFormat

class ListPenumpangDetailAdapter (
    private val context: Context?,
    private val penumpangList:MutableList<PenumpangModel>):
    RecyclerView.Adapter<ListPenumpangDetailAdapter.MyViewHolder>(){

    private val layoutInflater: LayoutInflater
    private var simpleDateFormat: SimpleDateFormat? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {

        val view = layoutInflater.inflate(
            R.layout.list_penumpang_detail,
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

    }
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvNik = itemView.findViewById<View>(R.id.tvNik) as TextView
        var tvNama = itemView.findViewById<View>(R.id.tvNama) as TextView
        var tvJabatan = itemView.findViewById<View>(R.id.tvJabatan) as TextView
    }
    init {
        layoutInflater = LayoutInflater.from(context)
        simpleDateFormat= SimpleDateFormat("yyyy-MM-dd")
    }
}
package com.misit.abpenergy.Adapter.Karyawan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.misit.abpenergy.Model.KaryawanModel
import com.misit.abpenergy.R

class KaryawanSpinnerAdapter (val mContex: Context, val mData:ArrayList<KaryawanModel>) : BaseAdapter(){
    private val mInflater: LayoutInflater
    init {
        mInflater = LayoutInflater.from(mContex)
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var viewHolder: DropDownViewHolder
        var view = convertView
        if (view==null){
            view = mInflater.inflate(R.layout.custom_karyawan_list,parent,false)
            viewHolder= DropDownViewHolder(view)
        }else{
            viewHolder = view.tag as DropDownViewHolder
        }
        view?.tag = viewHolder
        viewHolder.tvNik.text = mData[position].nik
        viewHolder.tvNama.text = mData[position].nama
        viewHolder.tvJabatan.text = mData[position].jabatan
        return view!!
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return super.getDropDownView(position, convertView, parent)
    }

    override fun getItem(position: Int): Any = mData[position]

    override fun getItemId(position: Int): Long =position.toLong()

    override fun getCount(): Int =mData.size


    class DropDownViewHolder(view: View){
        var tvNik: TextView
        var tvNama: TextView
        var tvJabatan: TextView
        init {
            tvNik= view.findViewById(R.id.tvNik)
            tvNama= view.findViewById(R.id.tvNama)
            tvJabatan= view.findViewById(R.id.tvJabatan)
        }
    }
}
package com.misit.abpenergy.Sarpras.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.misit.abpenergy.R
import com.misit.abpenergy.Sarpras.SaranaModel

class SaranaAdapter(val mContex:Context,val mData:ArrayList<SaranaModel>) :BaseAdapter(){
    private val mInflater:LayoutInflater
    init {
        mInflater = LayoutInflater.from(mContex)
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var viewHolder: DropDownViewHolder
        var view = convertView
        if (view==null){
            view = mInflater.inflate(R.layout.custom_lv_list,parent,false)
            viewHolder=
                DropDownViewHolder(
                    view
                )
        }else{
            viewHolder = view.tag as DropDownViewHolder
        }
        view?.tag = viewHolder
        viewHolder.tvNoLV.text = mData[position].noLV
        viewHolder.tvNoPol.text = "( ${mData[position].noPol} )"
        return view!!
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return super.getDropDownView(position, convertView, parent)
    }

    override fun getItem(position: Int): Any = mData[position]

    override fun getItemId(position: Int): Long =position.toLong()

    override fun getCount(): Int =mData.size


    class DropDownViewHolder(view: View){
        var tvNoLV: TextView
        var tvNoPol: TextView
        init {
            tvNoLV= view.findViewById(R.id.tvNoLV)
            tvNoPol= view.findViewById(R.id.tvNoPol)
        }
    }
//    class ViewHolder(view: View){
//        var tvNoLV: TextView
//        var tvNoPol: TextView
//        init {
//            tvNoLV= view.findViewById(R.id.tvNoLV)
//            tvNoPol= view.findViewById(R.id.tvNoPol)
//        }
//    }
}
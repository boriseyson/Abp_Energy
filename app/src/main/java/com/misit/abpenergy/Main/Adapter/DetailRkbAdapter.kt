package com.misit.abpenergy.Main.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.misit.abpenergy.R
import com.misit.abpenergy.Rkb.Response.DetailRkbItem
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*

class DetailRkbAdapter(
private val context: Context?,
private val detailRkbList:MutableList<DetailRkbItem>):
RecyclerView.Adapter<DetailRkbAdapter.MyViewHolder>(){
    private val layoutInflater: LayoutInflater
    private var simpleDateFormat: SimpleDateFormat? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailRkbAdapter.MyViewHolder {
        val view = layoutInflater.inflate(R.layout.list_rkb_detail,parent,false)
        return DetailRkbAdapter.MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return detailRkbList.size
    }

    override fun onBindViewHolder(holder: DetailRkbAdapter.MyViewHolder, position: Int) {
        val today = Date()
        var detailRkbList =detailRkbList[position]

        val fmt: DateTimeFormatter = DateTimeFormat.forPattern("d MMMM, yyyy")

        var tglDueDate = detailRkbList.dueDate?.split(" ")

        holder.tvDueDate?.text = LocalDate.parse(tglDueDate!![0]).toString(fmt)
        holder.tvPartName.text= detailRkbList.partName
        holder.tvPartNumber.text= detailRkbList.partNumber
        holder.tvQuantity.text= "${detailRkbList.quantity.toString()} ${detailRkbList.satuan}"
        holder.tvRemarks.text= detailRkbList.remarks
    }
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvPartName = itemView.findViewById<View>(R.id.tvPartName) as TextView
        var tvPartNumber = itemView.findViewById<View>(R.id.tvPartNumber) as TextView
        var tvQuantity = itemView.findViewById<View>(R.id.tvQuantity) as TextView
        var tvRemarks = itemView.findViewById<View>(R.id.tvRemarks) as TextView
        var tvDueDate = itemView.findViewById<View>(R.id.tvDueDate) as TextView

    }
    init {
        layoutInflater = LayoutInflater.from(context)
        simpleDateFormat= SimpleDateFormat("yyyy-MM-dd")
    }
}
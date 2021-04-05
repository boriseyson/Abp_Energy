package com.misit.abpenergy.Monitoring_Produksi.Adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.misit.abpenergy.Monitoring_Produksi.Response.CoalItem
import com.misit.abpenergy.Monitoring_Produksi.Response.ProduksiDailyItem
import com.misit.abpenergy.R
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class StockListAdapter (
    private val context: Context?,
    private val stock: String?,
    private val obList:MutableList<CoalItem>):
    RecyclerView.Adapter<StockListAdapter.MyViewHolder>(){
    private val layoutInflater: LayoutInflater
    private var simpleDateFormat: SimpleDateFormat? = null
    lateinit var view:View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        if(stock=="ROOM"){
            view = layoutInflater.inflate(R.layout.stock_room_view,parent,false)
        }else if(stock=="PRODUCT"){
            view = layoutInflater.inflate(R.layout.stock_product_view,parent,false)
        }
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return obList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var red = "#B8281D"
        var green = "#027368"
        var blue = "#230A59"
        val fmt: DateTimeFormatter = DateTimeFormat.forPattern("d MMMM, yyyy")

        var decFmt = DecimalFormat("#,###.000")
        var rkbList =obList[position]

        val date = LocalDate.parse(rkbList.tgl)
        holder.tvTanggal.text = date.toString(fmt)
        if(stock=="ROOM"){
            holder.tv_Stock.text = decFmt.format(rkbList.stockRom!!.toDouble())+" MT"
        }else if(stock=="PRODUCT"){
            holder.tv_Stock.text = decFmt.format(rkbList.stockProduct!!.toDouble())+" MT"
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTanggal = itemView.findViewById<View>(R.id.tvTanggal) as TextView
        var tv_Stock = itemView.findViewById<View>(R.id.tv_Stock) as TextView
    }
    init {
        layoutInflater = LayoutInflater.from(context)
        simpleDateFormat= SimpleDateFormat("yyyy-MM-dd")
    }
}
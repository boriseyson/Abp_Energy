package com.misit.abpenergy.ENP.Monitoring_Produksi.Adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.misit.abpenergy.ENP.Monitoring_Produksi.Response.ProduksiDailyItem
import com.misit.abpenergy.R
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class ProduksiListAdapter (
    private val context: Context?,
    private val obList:MutableList<ProduksiDailyItem>):
    RecyclerView.Adapter<ProduksiListAdapter.MyViewHolder>(){
    private val layoutInflater: LayoutInflater
    private var simpleDateFormat: SimpleDateFormat? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = layoutInflater.inflate(R.layout.list_ob_view,parent,false)
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
        holder.tvPlDaily.text = decFmt.format(rkbList.planDaily!!.toDouble())+" MT"
        holder.tvActDaily.text = decFmt.format(rkbList.actualDaily!!.toDouble())+" MT"
        holder.tvPlMTD.text = decFmt.format(rkbList.mtdPlan!!.toDouble())+" MT"
        holder.tvActMTD.text = decFmt.format(rkbList.mtdActual!!.toDouble())+" MT"

        if(rkbList.actualDaily!! < rkbList.planDaily!!){
            holder.tvActDaily.setTextColor(Color.parseColor(red))
        }else if(rkbList.actualDaily!! > rkbList.planDaily!!){
            holder.tvActDaily.setTextColor(Color.parseColor(green))
        }else{
            holder.tvActDaily.setTextColor(Color.parseColor(blue))
        }

        if(rkbList.mtdActual!! < rkbList.mtdPlan!!){
            holder.tvActMTD.setTextColor(Color.parseColor(red))
        }else if(rkbList.mtdActual!! > rkbList.mtdPlan!!){
            holder.tvActMTD.setTextColor(Color.parseColor(green))
        }else{
            holder.tvActMTD.setTextColor(Color.parseColor(blue))
        }

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTanggal = itemView.findViewById<View>(R.id.tvTanggal) as TextView
        var tvPlDaily = itemView.findViewById<View>(R.id.tv_plDaily) as TextView
        var tvActDaily = itemView.findViewById<View>(R.id.tv_actDaily) as TextView
        var tvPlMTD = itemView.findViewById<View>(R.id.tv_plMTD) as TextView
        var tvActMTD = itemView.findViewById<View>(R.id.tv_actMTD) as TextView
    }
    init {
        layoutInflater = LayoutInflater.from(context)
        simpleDateFormat= SimpleDateFormat("yyyy-MM-dd")
    }
}
package com.misit.abpenergy.HGE.Sarpras.Adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.misit.abpenergy.R
import com.misit.abpenergy.HGE.Sarpras.SQLite.DataSource.PenumpangDataSource
import com.misit.abpenergy.HGE.Sarpras.SarprasResponse.DataItem
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*

class SarprasAdapter(
private val context: Context?,
private val sarprasList:MutableList<DataItem>,
private val ruleUser:String):
RecyclerView.Adapter<SarprasAdapter.MyViewHolder>(){


    private val layoutInflater: LayoutInflater
    private var simpleDateFormat: SimpleDateFormat? = null
    private var onItemClickListener: OnItemClickListener? = null
    private var userRule:Array<String>?=null
    lateinit var d : PenumpangDataSource
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        d = PenumpangDataSource(context!!)
        userRule =ruleUser.split(",").toTypedArray()
        val view = layoutInflater.inflate(R.layout.list_sarpras,parent,false)
        return MyViewHolder(
            view
        )

    }
    private fun getPenumpang(niknya:String,holder: MyViewHolder) {
           val driver = d.getItem(niknya)
            holder.driver.text = driver?.nama
    }
    override fun getItemCount(): Int {
        return sarprasList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //val today = Date()

        var sarprasList =sarprasList[position]
        val fmt: DateTimeFormatter = DateTimeFormat.forPattern("d MMMM, yyyy")
        holder.tvpemohon.text = sarprasList.userPemohon!!.capitalize()

        if(sarprasList.flagAppr==2){
            holder.lnCancelRemark.visibility = View.VISIBLE
            holder.cancelRemark.text = sarprasList.flagNote
        }else{
            holder.lnCancelRemark.visibility = View.GONE
            holder.cancelRemark.text = null
        }
        if(sarprasList.noPol!=null){
            holder.txtNoLV.text="Jenis Kendaraan :"
            holder.txtDriver.text="Merk Kendaraan :"
            holder.noLV.text = "${sarprasList.noLv!!.capitalize()} ( ${sarprasList.noPol} )"
            holder.driver.text = sarprasList.driver
        }else{
            holder.txtDriver.text="Driver :"
            getPenumpang(sarprasList.driver!!,holder)
            holder.txtNoLV.text="NO LV : "
            holder.noLV.text = "${sarprasList.noLv}"
        }

        var foundRule = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Arrays.stream(userRule).anyMatch{t -> t== "approve sarpras"}
        } else {
            userRule?.contains("approve sarpras")
        }
//        Toasty.info(context!!,foundRule.toString(),Toasty.LENGTH_SHORT).show()

//        holder.driver.text = sarprasList.driver

        if(foundRule!!){
            checkStatus(holder,sarprasList)
        }else{
            checkStatus(holder,sarprasList)
        }

        holder.keperluan.text = sarprasList.keperluan

        val date = LocalDate.parse(sarprasList.tglOut)
        var tglOut = date.toString(fmt);
        holder.tv_tgl.text = "${tglOut} ${sarprasList.jamOut}"
        if(onItemClickListener != null){
            holder.btnDoc?.setOnClickListener{
                onItemClickListener?.onItemClick(sarprasList.noidOut)
            }
            holder.lihatDetail?.setOnClickListener{
                onItemClickListener?.onDetailClick(sarprasList.noidOut)
            }
            holder.btnQRCode.setOnClickListener{
                onItemClickListener?.onQRCodeClick(sarprasList.noidOut)
            }
        }
        holder.txtNoAntri.text = sarprasList.nomor
    }
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var v_appr = itemView.findViewById<View>(R.id.v_appr) as View
        var lnAppr = itemView.findViewById<View>(R.id.lnAppr) as LinearLayout
        var tvpemohon = itemView.findViewById<View>(R.id.tvpemohon) as TextView
        var status = itemView.findViewById<View>(R.id.status) as TextView
        var tv_tgl = itemView.findViewById<View>(R.id.tv_tgl) as TextView
        var noLV = itemView.findViewById<View>(R.id.noLV) as TextView
        var driver = itemView.findViewById<View>(R.id.driver) as TextView
        var keperluan = itemView.findViewById<View>(R.id.keperluan) as TextView
        var btnDoc = itemView.findViewById<View>(R.id.btnDoc) as Button
        var txtNoLV = itemView.findViewById<View>(R.id.txtNoLV) as TextView
        var txtDriver = itemView.findViewById<View>(R.id.txtDriver) as TextView
        var lnSaranaList = itemView.findViewById<View>(R.id.lnSaranaList) as LinearLayout
        var lnCancelUser = itemView.findViewById<View>(R.id.lnCancelUser) as LinearLayout
        var vUser = itemView.findViewById<View>(R.id.vUser) as View
        var vDownload = itemView.findViewById<View>(R.id.vDownload) as View
        var lihatDetail = itemView.findViewById<View>(R.id.lihatDetail) as Button
        var cancelRemark = itemView.findViewById<View>(R.id.cancelRemark) as TextView
        var lnCancelRemark= itemView.findViewById<View>(R.id.lnCancelRemark) as LinearLayout
        var txtNoAntri = itemView.findViewById<View>(R.id.txtNoAntri) as TextView
        var btnQRCode = itemView.findViewById<View>(R.id.btnQRCode) as Button
    }
    interface OnItemClickListener{
        fun onItemClick(noIdOut:String?)
        fun onDetailClick(noIdOut:String?)
        fun onQRCodeClick(noIdOut:String?)
    }
    fun setListener (listener: OnItemClickListener){
        onItemClickListener = listener
    }
    fun checkStatus(holder: MyViewHolder,sarprasList:DataItem){
        if(sarprasList.flag==0){
            if(sarprasList.flagAppr==1){
                holder.v_appr.visibility=View.GONE
                holder.lnAppr.visibility=View.GONE
                holder.status.text="Approved"
                holder.btnDoc.visibility=View.VISIBLE
                holder.lnSaranaList.setBackgroundResource(R.color.bgApprove)
                holder.status.setTextColor(Color.parseColor("#4CAF50"))
                holder.lnCancelUser.visibility=View.GONE
                holder.vUser.visibility=View.GONE
                holder.vDownload.visibility=View.VISIBLE
//            holder.lihatDetail.visibility=View.VISIBLE
            }else if(sarprasList.flagAppr==2){
                holder.btnDoc.visibility=View.GONE
                holder.vDownload.visibility=View.GONE
//            holder.lihatDetail.visibility=View.GONE
                holder.lnAppr.visibility=View.GONE
                holder.v_appr.visibility=View.GONE
                holder.status.setTextColor(Color.parseColor("#FF9800"))
                holder.status.text="Waiting"
                holder.lnSaranaList.setBackgroundResource(R.color.bgWaiting)
                holder.lnCancelUser.visibility=View.GONE
                holder.vUser.visibility=View.GONE
            }
        }
        else if(sarprasList.flag==1){
            sarprasList.tglOut
            holder.btnDoc.visibility=View.GONE
            holder.vDownload.visibility=View.GONE
//            holder.lihatDetail.visibility=View.GONE
            holder.v_appr.visibility=View.GONE
            holder.lnAppr.visibility=View.GONE
            holder.status.setTextColor(Color.parseColor("#F44336"))
            holder.status.text="Cancel"
            holder.lnSaranaList.setBackgroundResource(R.color.bgCancel)
            holder.lnCancelUser.visibility=View.GONE
            holder.vUser.visibility=View.GONE

        }
    }
    init {
        layoutInflater = LayoutInflater.from(context)
        simpleDateFormat= SimpleDateFormat("yyyy-MM-dd")
    }
}
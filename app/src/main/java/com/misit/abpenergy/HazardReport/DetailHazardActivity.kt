package com.misit.abpenergy.HazardReport

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.misit.abpenergy.Api.ApiClient
import com.misit.abpenergy.Api.ApiEndPoint
import com.misit.abpenergy.HazardReport.Response.DataItem
import com.misit.abpenergy.R
import com.misit.abpenergy.Utils.PopupUtil
import com.misit.abpenergy.Utils.PrefsUtil
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_detail_hazard.*
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailHazardActivity : AppCompatActivity(),View.OnClickListener {

    private var uid:String?=null
    private var bukti:String?=null
    private var updateBukti:String?=null
    private var adminHazard:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_hazard)
        title="Detail Hazard Report"
        var actionBar = supportActionBar
        PrefsUtil.initInstance(this)
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        uid = intent.getStringExtra(UID)
        adminHazard = intent.getStringExtra("ALLHazard")

        loadDetail(uid.toString())
        floatUpdateDenganGambar.setOnClickListener(this)
        floatUpdateStatus.setOnClickListener(this)
        cvImageDetail.setOnClickListener(this)
        cvStatusPerbaikan.setOnClickListener(this)
    }
    override fun onResume() {
        if(adminHazard!=null){
            btnFLMenu.visibility= View.GONE
        }else{
            btnFLMenu.visibility= View.VISIBLE
        }
        super.onResume()
    }
    override fun onClick(v: View?) {
        if(v?.id==R.id.floatUpdateDenganGambar){
            var intent = Intent(this@DetailHazardActivity,UpdateHazardActivity::class.java)
            intent.putExtra("UID",uid)
            intent.putExtra("FORM_UPLOAD",true)
            startActivityForResult(intent,13)
        }
        if(v?.id==R.id.floatUpdateStatus){
            var intent = Intent(this@DetailHazardActivity,UpdateHazardActivity::class.java)
            intent.putExtra("UID",uid)
            intent.putExtra("FORM_UPLOAD",false)
            startActivityForResult(intent,14)
        }
        if(v?.id==R.id.cvImageDetail){
            var intent = Intent(this@DetailHazardActivity,ImageHazardActivity::class.java)
            intent.putExtra("ImageHazard",bukti)
            intent.putExtra("Direktori","https://abpjobsite.com/bukti_hazard/")
            startActivity(intent)
        }
        if(v?.id==R.id.cvStatusPerbaikan){
            var intent = Intent(this@DetailHazardActivity,ImageHazardActivity::class.java)
            intent.putExtra("ImageHazard",updateBukti)
            intent.putExtra("Direktori","https://abpjobsite.com/bukti_hazard/update/")
            startActivity(intent)
        }
        btnFLMenu.collapse()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode==Activity.RESULT_OK){
            loadDetail(uid!!)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
    fun loadDetail(uid:String){
        PopupUtil.showProgress(this@DetailHazardActivity,"Loading...","Membuat Hazard Report!")
        val apiEndPoint = ApiClient.getClient(this)!!.create(ApiEndPoint::class.java)
        val call = apiEndPoint.getItemHazard(uid)
        call?.enqueue(object : Callback<DataItem> {
            override fun onFailure(call: Call<DataItem>, t: Throwable) {
                Toasty.error(this@DetailHazardActivity,"Error : $t", Toasty.LENGTH_SHORT).show()
                PopupUtil.dismissDialog()
            }

            override fun onResponse(call: Call<DataItem>, response: Response<DataItem>) {
                var itemHazard = response.body()
                val fmt: DateTimeFormatter = DateTimeFormat.forPattern("d MMMM, yyyy")

                if(itemHazard!=null){
                    tvPerusahaanD.text = itemHazard.perusahaan
                    tvTanggalD.text = LocalDate.parse(itemHazard.tglHazard).toString(fmt)
                    tvJamD.text = itemHazard.jamHazard
                    tvLokasiD.text = itemHazard.lokasiHazard
                    tvLokasiDetails.text= itemHazard.lokasiDetail
                    tvBahayaD.text = itemHazard.deskripsi
                    tvSumberBahayaD.text = itemHazard.sumberBahaya
                    tvKatBahayaD.text = itemHazard.katBahaya
                    tvPerbaikanD.text = itemHazard.tindakan
                    tvStatusPerbaikanD.text = itemHazard.statusPerbaikan
                    tvDibuat.text = itemHazard.namaLengkap
                    if(itemHazard.risk!=null){
                        tvRisk.text = itemHazard.risk
                        tvRisk.setBackgroundColor(Color.parseColor(itemHazard.bgColor))
                        cvRisk.setBackgroundColor(Color.parseColor(itemHazard.bgColor))
                        tvRisk.setTextColor(Color.parseColor(itemHazard.txtColor))
                        cvRisk.visibility= View.VISIBLE
                    }else{
                        cvRisk.visibility= View.GONE
                    }
                    if(itemHazard.tglSelesai!=null){
                        btnFLMenu.visibility=View.GONE
                        tvTGLSelesaiD.text = LocalDate.parse(itemHazard.tglSelesai).toString(fmt)
                    }else{
                        imgStatus.visibility=View.GONE
                        btnFLMenu.visibility=View.VISIBLE
                        tvTGLSelesaiD.text = "-"
                    }
                    if(itemHazard.jamSelesai!=null){
                    tvJamSelesaiD.text = itemHazard.jamSelesai
                    }else{
                        tvJamSelesaiD.text = "-"
                    }
                    tvPenanggungJawabD.text = itemHazard.penanggungJawab
                    Glide.with(this@DetailHazardActivity)
                        .load("https://abpjobsite.com/bukti_hazard/"+itemHazard?.bukti)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(imgView)
                    bukti = itemHazard?.bukti
                    if(itemHazard.updateBukti!=null){
                        Glide.with(this@DetailHazardActivity)
                            .load("https://abpjobsite.com/bukti_hazard/update/"+itemHazard?.updateBukti)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(imgStatus)
                        imgStatus.visibility=View.VISIBLE
                        tvStatusPerbaikan.visibility=View.VISIBLE
                        cvStatusPerbaikan.visibility=View.VISIBLE
                        updateBukti = itemHazard?.updateBukti
                    }else{
                        cvStatusPerbaikan.visibility=View.GONE
                        tvStatusPerbaikan.visibility=View.GONE
                        imgStatus.visibility=View.GONE
                    }

                }
                PopupUtil.dismissDialog()
            }

        })

    }
    companion object{
        var UID = "UID"
    }

}

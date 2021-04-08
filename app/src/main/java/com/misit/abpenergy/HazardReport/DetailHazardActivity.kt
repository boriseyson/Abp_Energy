package com.misit.abpenergy.HazardReport

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.misit.abpenergy.Api.ApiClient
import com.misit.abpenergy.Api.ApiEndPoint
import com.misit.abpenergy.HazardReport.Response.DataItem
import com.misit.abpenergy.R
import com.misit.abpenergy.Sarpras.SarprasActivity
import com.misit.abpenergy.Utils.PopupUtil
import com.misit.abpenergy.Utils.PrefsUtil
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_detail_hazard.*
import kotlinx.android.synthetic.main.activity_sarpras.*
import kotlinx.android.synthetic.main.detail_hazard.*
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailHazardActivity : AppCompatActivity(),View.OnClickListener {

    private var uid:String?=null

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
        loadDetail(uid.toString())
        floatUpdateDenganGambar.setOnClickListener(this)
        floatUpdateStatus.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if(v?.id==R.id.floatUpdateDenganGambar){
            var intent = Intent(this@DetailHazardActivity,UpdateHazardActivity::class.java)
            intent.putExtra(UpdateHazardActivity.UID,uid)
            startActivityForResult(intent,13)
        }
        if(v?.id==R.id.floatUpdateStatus){
            var intent = Intent(this@DetailHazardActivity,UpdateHazardActivity::class.java)
            intent.putExtra(UpdateHazardActivity.UID,uid)
            startActivityForResult(intent,14)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

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
                    tvLokasiD.text = itemHazard.lokasi
                    tvBahayaD.text = itemHazard.deskripsi
                    tvSumberBahayaD.text = itemHazard.sumberBahaya
                    tvKatBahayaD.text = itemHazard.katBahaya
                    tvPerbaikanD.text = itemHazard.tindakan
                    tvStatusPerbaikanD.text = itemHazard.statusPerbaikan
                    if(itemHazard.tglSelesai!=null){
                        tvTGLSelesaiD.text = LocalDate.parse(itemHazard.tglSelesai).toString(fmt)
                    }else{
                        tvStatusPerbaikan.visibility=View.GONE
                        cvStatusPerbaikan.visibility=View.GONE
                        imgStatus.visibility=View.GONE

                        tvTGLSelesaiD.text = ""
                    }
                    if(itemHazard.jamSelesai!=null){
                    tvJamSelesaiD.text = itemHazard.jamSelesai
                    }else{
                        tvJamSelesaiD.text = ""
                    }
                    tvPenanggungJawabD.text = itemHazard.penanggungJawab
                    Glide.with(this@DetailHazardActivity)
                        .load("https://abpjobsite.com/bukti_hazard/"+itemHazard?.bukti)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(imgView)
                }
                PopupUtil.dismissDialog()
            }

        })

    }
    companion object{
        var UID = "UID"
    }

}

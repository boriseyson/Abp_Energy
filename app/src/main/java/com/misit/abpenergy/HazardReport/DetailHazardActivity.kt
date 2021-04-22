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
import com.misit.abpenergy.HazardReport.Response.DetailHazardResponse
import com.misit.abpenergy.HazardReport.Response.HazardItem
import com.misit.abpenergy.R
import com.misit.abpenergy.Service.MatrikResikoWebViewActivity
import com.misit.abpenergy.Utils.PopupUtil
import com.misit.abpenergy.Utils.PrefsUtil
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_detail_hazard.*
import kotlinx.android.synthetic.main.detail_hazard.*
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
    private var fotoPJ:String?=null
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
        pjFOTO.setOnClickListener(this)
        matrikResiko.setOnClickListener(this)
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
        if(v?.id==R.id.pjFOTO){
            var intent = Intent(this@DetailHazardActivity,ImageHazardActivity::class.java)
            intent.putExtra("ImageHazard",fotoPJ)
            intent.putExtra("Direktori","https://abpjobsite.com/bukti_hazard/penanggung_jawab/")
            startActivity(intent)
        }
        if(v?.id==R.id.matrikResiko){
            var intent = Intent(this@DetailHazardActivity,MatrikResikoWebViewActivity::class.java)
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
        call?.enqueue(object : Callback<DetailHazardResponse> {
            override fun onFailure(call: Call<DetailHazardResponse>, t: Throwable) {
                Toasty.error(this@DetailHazardActivity,"Error : $t", Toasty.LENGTH_SHORT).show()
                PopupUtil.dismissDialog()
            }

            override fun onResponse(call: Call<DetailHazardResponse>, response: Response<DetailHazardResponse>) {
                var dataHazard = response.body()
                val fmt: DateTimeFormatter = DateTimeFormat.forPattern("d MMMM, yyyy")

                if(dataHazard!=null){
                    if(dataHazard.itemHazardList!=null){
                        var itemHazard =dataHazard.itemHazardList!!
                        tvPerusahaanD.text = itemHazard.perusahaan
                        tvTanggalD.text = LocalDate.parse(itemHazard.tglHazard).toString(fmt)
                        tvJamD.text = itemHazard.jamHazard
                        tvLokasiD.text = itemHazard.lokasiHazard
                        tvLokasiDetails.text= itemHazard.lokasiDetail
                        tvBahayaD.text = itemHazard.deskripsi
                        tvKemungkinan.text = itemHazard.kemungkinan
                        tvKeparahan.text = itemHazard.keparahan
                        tvPengendalian.text = itemHazard.namaPengendalian
                        tvKatBahayaD.text = itemHazard.katBahaya
                        tvPerbaikanD.text = itemHazard.tindakan
                        tvStatusPerbaikanD.text = itemHazard.statusPerbaikan
                        tvDibuat.text = itemHazard.namaLengkap
                        tvNilaiKeparahan.text = itemHazard.nilaiKeparahan.toString()
                        tvNilaiKemungkinan.text = itemHazard.nilaiKemungkinan.toString()

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
                        namaPJ.text = itemHazard.namaPJ
                        nikPJ.text = itemHazard.nikPJ
                        Glide.with(this@DetailHazardActivity)
                            .load("https://abpjobsite.com/bukti_hazard/"+itemHazard?.bukti)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(imgView)

                        Glide.with(this@DetailHazardActivity)
                            .load("https://abpjobsite.com/bukti_hazard/penanggung_jawab/"+itemHazard?.fotoPJ)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(pjFOTO)
                        bukti = itemHazard?.bukti
                        fotoPJ = itemHazard.fotoPJ
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
                        if(itemHazard.keteranganUpdate!=null){
                            lnKetPerbaikan.visibility = View.VISIBLE
                            tvKetPerbaikan.text = itemHazard.keteranganUpdate
                        }else{
                            lnKetPerbaikan.visibility = View.GONE
                            tvKetPerbaikan.text = ""
                        }
                    }
                    if(dataHazard.nilairRisk!=null){
                        var itemHazard =  dataHazard.itemHazardList
                        tvTotalResiko.text = "${itemHazard!!.nilaiKemungkinan} x ${itemHazard!!.nilaiKeparahan} = ${dataHazard.nilairRisk}"
                    }
                    if(dataHazard.risk!=null){
                        tvKDresiko.text ="${dataHazard.risk!!.kodeBahaya}"
                        tvRisk.text = "${dataHazard.risk!!.kategori} "
                        tvNilaiResiko.text = "${dataHazard.risk!!.min} - ${dataHazard.risk!!.max}"
                        cvResiko.setCardBackgroundColor(Color.parseColor(dataHazard.risk!!.bgColor))
                        tvRisk.setBackgroundColor(Color.parseColor(dataHazard.risk!!.bgColor))
                        tvRisk.setTextColor(Color.parseColor(dataHazard.risk!!.txtColor))
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

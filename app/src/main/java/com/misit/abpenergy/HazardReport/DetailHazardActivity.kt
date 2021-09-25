package com.misit.abpenergy.HazardReport

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.bumptech.glide.Glide
import com.misit.abpenergy.Api.ApiClient
import com.misit.abpenergy.Api.ApiEndPoint
import com.misit.abpenergy.HazardReport.Response.DetailHazardResponse
import com.misit.abpenergy.HazardReport.ViewModel.HazardDetailViewModel
import com.misit.abpenergy.HazardReport.ViewModel.HeaderViewModel
import com.misit.abpenergy.R
import com.misit.abpenergy.Service.MatrikResikoWebViewActivity
import com.misit.abpenergy.Utils.PopupUtil
import com.misit.abpenergy.Utils.PrefsUtil
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_detail_hazard.*
import kotlinx.android.synthetic.main.detail_hazard.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
    lateinit var viewModel: HazardDetailViewModel
    var method:String? = null
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
        method = intent.getStringExtra("Method")
//        loadDetail(uid.toString())
        floatUpdateDenganGambar.setOnClickListener(this)
        floatUpdateStatus.setOnClickListener(this)
        cvImageDetail.setOnClickListener(this)
        cvStatusPerbaikan.setOnClickListener(this)
        pjFOTO.setOnClickListener(this)
        matrikResiko.setOnClickListener(this)
        matrikResikoSesudah.setOnClickListener(this)
        viewModel = ViewModelProvider(this@DetailHazardActivity).get(HazardDetailViewModel::class.java)
        if(method!=null){
            if(method=="Online"){
                GlobalScope.launch(Dispatchers.IO) {
                    PopupUtil.showLoading(this@DetailHazardActivity,"Loading...","Memuat Hazard Report!")
                    viewModel?.loadDetailOnline("${uid}",this@DetailHazardActivity)
                }
            }else if (method =="Offline"){
                GlobalScope.launch(Dispatchers.IO) {
                    PopupUtil.showLoading(this@DetailHazardActivity,"Loading...","Memuat Hazard Report!")
                    viewModel?.loadDetailOffline("${uid}",this@DetailHazardActivity)
                }
            }
        }
        initViewModel()
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
        if(v?.id==R.id.matrikResiko || v?.id==R.id.matrikResikoSesudah){
            var intent = Intent(this@DetailHazardActivity,MatrikResikoWebViewActivity::class.java)
            startActivity(intent)
        }
        btnFLMenu.collapse()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode==Activity.RESULT_OK){
            PopupUtil.showLoading(this@DetailHazardActivity,"Loading...","Memuat Hazard Report!")
//            loadDetail(uid!!)
            GlobalScope.launch(Dispatchers.IO) {
                viewModel?.loadDetailOnline("${uid}",this@DetailHazardActivity)
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
    fun loadDetail(uid:String){
        PopupUtil.showLoading(this@DetailHazardActivity,"Loading...","Memuat Hazard Report!")
        val apiEndPoint = ApiClient.getClient(this)!!.create(ApiEndPoint::class.java)
        val call = apiEndPoint.getItemHazard(uid)
        call?.enqueue(object : Callback<DetailHazardResponse> {
            override fun onFailure(call: Call<DetailHazardResponse>, t: Throwable) {
                Toasty.error(this@DetailHazardActivity,"Error : $t", Toasty.LENGTH_SHORT).show()
                PopupUtil.dismissDialog()
            }

            override fun onResponse(call: Call<DetailHazardResponse>, response: Response<DetailHazardResponse>) {
                var dataHazard = response.body()

                if(dataHazard!=null){
                    if(dataHazard.itemHazardList!=null){
                        itemHazardList(dataHazard!!)
                    }
                }
                PopupUtil.dismissDialog()
            }
        })

    }

    private fun itemHazardList(dataHazard: DetailHazardResponse){
        val fmt: DateTimeFormatter = DateTimeFormat.forPattern("d MMMM, yyyy")
        var itemHazard =dataHazard.itemHazardList
        if(itemHazard!=null){
            tvPerusahaanD.text = itemHazard.perusahaan
            tvTanggalD.text = LocalDate.parse(itemHazard.tglHazard).toString(fmt)
            tvJamD.text = itemHazard.jamHazard
            tvLokasiD.text = itemHazard.lokasiHazard
            tvLokasiDetails.text= itemHazard.lokasiDetail
            tvBahayaD.text = itemHazard.deskripsi
            tvKemungkinan.text = itemHazard.kemungkinanSebelum
            tvKeparahan.text = itemHazard.keparahanSebelum
            tvKemungkinanSesudah.text = itemHazard.kemungkinanSesudah
            tvKeparahanSesudah.text = itemHazard.keparahanSesudah
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
                .into(imgView)

            Glide.with(this@DetailHazardActivity)
                .load("https://abpjobsite.com/bukti_hazard/penanggung_jawab/"+itemHazard?.fotoPJ)
                .into(pjFOTO)
            bukti = itemHazard?.bukti
            fotoPJ = itemHazard.fotoPJ
            if(itemHazard.updateBukti!=null){
                Glide.with(this@DetailHazardActivity)
                    .load("https://abpjobsite.com/bukti_hazard/update/"+itemHazard?.updateBukti)
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
        if(dataHazard.nilaiRiskSebelum!=null){
            var itemHazard =  dataHazard.itemHazardList
            Log.d("riskSebelum","${dataHazard}")
            tvTotalResiko.text = "${itemHazard!!.nilaiKemungkinan} x ${itemHazard!!.nilaiKeparahan} = ${dataHazard.nilaiRiskSebelum}"
            tvKDresiko.text ="${dataHazard.riskSebelum!!.kodeBahaya}"
            tvRisk.text = "${dataHazard.riskSebelum!!.kategori} "
            tvNilaiResiko.text = "${dataHazard.riskSebelum!!.min} - ${dataHazard.riskSebelum!!.max}"
            cvResiko.setCardBackgroundColor(Color.parseColor(dataHazard.riskSebelum!!.bgColor))
            tvRisk.setBackgroundColor(Color.parseColor(dataHazard.riskSebelum!!.bgColor))
            tvRisk.setTextColor(Color.parseColor(dataHazard.riskSebelum!!.txtColor))
        }
        Log.d("RiskSesudah","${dataHazard.riskSesudah}")
        if(dataHazard.nilaiRiskSesudah!! > 0){
            lnDetMatrikResiko.visibility = View.VISIBLE
            var itemHazard =  dataHazard.itemHazardList
            tvTotalResikoSesudah.text = "${itemHazard!!.nilaiKemungkinanSesudah} x ${itemHazard!!.nilaiKeparahanSesudah} = ${dataHazard.nilaiRiskSesudah}"
            tvNilaiKemungkinanSesudah.text ="${itemHazard.nilaiKemungkinanSesudah}"
            tvNilaiKeparahanSesudah.text = "${itemHazard.nilaiKeparahanSesudah} "
            tvRiskSesudah.text = "${dataHazard.riskSesudah!!.kategori} "
            tvNilaiResikoSesudah.text = "${dataHazard.riskSesudah!!.min} - ${dataHazard.riskSesudah!!.max}"
            tvKDresikoSesudah.text = "${dataHazard.riskSesudah!!.kodeBahaya} "
            cvResikoSesudah.setCardBackgroundColor(Color.parseColor(dataHazard.riskSesudah!!.bgColor))
            tvRiskSesudah.setBackgroundColor(Color.parseColor(dataHazard.riskSesudah!!.bgColor))
            tvRiskSesudah.setTextColor(Color.parseColor(dataHazard.riskSesudah!!.txtColor))
        }else{
            lnDetMatrikResiko.visibility = View.GONE
        }
    }
    private fun initViewModel(){
        viewModel.hazardDetailObserver()?.observe(this@DetailHazardActivity,{
            if(it!=null){
                itemHazardList(it)
                PopupUtil.dismissDialog()
            }
        })
        viewModel.progressObserver()?.observe(this@DetailHazardActivity,{
            if(it){
                PopupUtil.dismissDialog()
                crlDetail.visibility = View.VISIBLE
            }else{
                crlDetail.visibility = View.GONE
                PopupUtil.showProgress(this@DetailHazardActivity,"Loading...","Memuat Hazard Report!")
            }
        })
    }
    companion object{
        var UID = "UID"
    }

}

package com.misit.abpenergy.ENP.Monitoring_Produksi

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.textfield.TextInputEditText
import com.misit.abpenergy.Api.ApiClient
import com.misit.abpenergy.Api.ApiEndPoint
import com.misit.abpenergy.ENP.Monitoring_Produksi.Adapter.ProduksiListAdapter
import com.misit.abpenergy.ENP.Monitoring_Produksi.Response.ProduksiDailyItem
import com.misit.abpenergy.ENP.Monitoring_Produksi.Response.ProduksiResponse
import com.misit.abpenergy.R
import com.misit.abpenergy.Utils.PopupUtil
import com.misit.abpenergy.Utils.PrefsUtil
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_ob.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ProductionActivity : AppCompatActivity(),View.OnClickListener {

    var produksiList : MutableList<ProduksiDailyItem>? = null
    private var adapter: ProduksiListAdapter? = null
    var monitoring:String?=null
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ob)
        monitoring = intent.getStringExtra(MONITORING)
        title="MONITORING "+monitoring
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        PrefsUtil.initInstance(this@ProductionActivity)
        DARI =PrefsUtil.getInstance().getStringState(PrefsUtil.AWAL_BULAN,"")
        SAMPAI = PrefsUtil.getInstance().getStringState(PrefsUtil.AKHIR_BULAN,"")
        produksiList = ArrayList()
        adapter = ProduksiListAdapter(
            this@ProductionActivity,
            produksiList!!
        )
        val linearLayoutManager = LinearLayoutManager(this@ProductionActivity)
        rvOB?.layoutManager = linearLayoutManager
        rvOB.adapter =adapter
        txtTglDari.setText(DARI)
        txtTglSampai.setText(SAMPAI)
        txtTglDari.setOnClickListener(this)
        txtTglSampai.setOnClickListener(this)
        btnLoad.setOnClickListener(this)
        swipeRefreshLayout = findViewById(R.id.pullRefresh)
        swipeRefreshLayout.setOnRefreshListener(object :SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                rvOB.adapter = adapter
                produksiList?.clear()
                loadOb(monitoring!!,DARI,SAMPAI)
            }
        })
    }

    override fun onResume() {
        produksiList?.clear()
        loadOb(monitoring!!,DARI,SAMPAI)
        super.onResume()
    }
    private fun loadOb(mtr: String,dari: String, sampai: String) {
        PopupUtil.showLoading(this@ProductionActivity,"Loading","Mengambil Data!")
        val apiEndPoint = ApiClient.getClient(this@ProductionActivity)!!.create(ApiEndPoint::class.java)
        val call = apiEndPoint.getOBList(mtr,dari,sampai)
        call?.enqueue(object : Callback<ProduksiResponse?> {
            override fun onFailure(call: Call<ProduksiResponse?>, t: Throwable) {
                Toasty.error(this@ProductionActivity,"Gagal Mengambil Data! "+t.message).show()
            }

            override fun onResponse(call: Call<ProduksiResponse?>, response: Response<ProduksiResponse?>) {
                var obRespon = response.body()
                if (obRespon != null) {
                    if(obRespon.produksiDaily!=null){
                        produksiList?.addAll(obRespon.produksiDaily!!)
                            adapter?.notifyDataSetChanged()
                            PopupUtil.dismissDialog()
                        swipeRefreshLayout.isRefreshing=false
                    }

                }
            }

        })
    }

    override fun onClick(v: View?) {
        if(v!!.id==R.id.txtTglDari){
            showDialogTgl(txtTglDari)
        }
        if(v!!.id==R.id.txtTglSampai){
            showDialogTgl(txtTglSampai)
        }
        if(v!!.id==R.id.btnLoad){
            produksiList!!.clear()
            var dari = txtTglDari.text.toString()
            var sampai = txtTglSampai.text.toString()
            loadOb(monitoring!!,dari!!,sampai!!)
            this@ProductionActivity?.runOnUiThread {
                adapter?.notifyDataSetChanged()
            }
        }
    }

    fun showDialogTgl(inTgl: TextInputEditText){
        val now = Calendar.getInstance()
        val datePicker  = DatePickerDialog.OnDateSetListener{
                view: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
            now.set(Calendar.YEAR,year)
            now.set(Calendar.MONTH,month)
            now.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            inTgl.setText(SimpleDateFormat("dd MMMM yyyy").format(now.time))
        }

        DatePickerDialog(this,
            datePicker,
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    override fun onSupportNavigateUp(): Boolean {
       onBackPressed()
        return super.onSupportNavigateUp()
    }
    companion object{
        private  var DARI="01 January 2021"
        private  var SAMPAI="31 January 2021"
        var MONITORING = "OB"
    }

}

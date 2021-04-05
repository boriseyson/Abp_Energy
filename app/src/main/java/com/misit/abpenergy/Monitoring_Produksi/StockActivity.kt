package com.misit.abpenergy.Monitoring_Produksi

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.DatePicker
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.textfield.TextInputEditText
import com.misit.abpenergy.Api.ApiClient
import com.misit.abpenergy.Api.ApiEndPoint
import com.misit.abpenergy.Monitoring_Produksi.Adapter.StockListAdapter
import com.misit.abpenergy.Monitoring_Produksi.Response.CoalItem
import com.misit.abpenergy.Monitoring_Produksi.Response.StockResponse
import com.misit.abpenergy.R
import com.misit.abpenergy.Utils.PopupUtil
import com.misit.abpenergy.Utils.PrefsUtil
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_stock.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class StockActivity : AppCompatActivity(),View.OnClickListener {

    var stockList : MutableList<CoalItem>? = null
    private var adapter: StockListAdapter? = null
    var monitoring:String?=null
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock)
        monitoring = intent.getStringExtra(MONITORING)
        title="MONITORING STOCK "+monitoring
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        PrefsUtil.initInstance(this@StockActivity)
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        DARI =PrefsUtil.getInstance().getStringState(PrefsUtil.AWAL_BULAN,"")
        SAMPAI = PrefsUtil.getInstance().getStringState(PrefsUtil.AKHIR_BULAN,"")
        stockList = ArrayList()
        adapter = StockListAdapter(
            this@StockActivity,
            monitoring,
            stockList!!
        )
        val gridLayoutManager = GridLayoutManager(this@StockActivity,2)
        rvStock?.layoutManager = gridLayoutManager
        rvStock.adapter =adapter
        txtTglDari.setText(DARI)
        txtTglSampai.setText(SAMPAI)
        txtTglDari.setOnClickListener(this)
        txtTglSampai.setOnClickListener(this)
        btnLoad.setOnClickListener(this)
        swipeRefreshLayout = findViewById(R.id.pullRefresh)
        swipeRefreshLayout.setOnRefreshListener(object :SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                rvStock.adapter = adapter
                stockList?.clear()
                loadStock(monitoring!!,DARI,SAMPAI)
            }
        })
    }

    override fun onResume() {
        stockList?.clear()
        loadStock(monitoring!!,DARI,SAMPAI)
        super.onResume()
    }
    private fun loadStock(mtr: String,dari: String, sampai: String) {
        PopupUtil.showLoading(this@StockActivity,"Loading","Mengambil Data!")
        val apiEndPoint = ApiClient.getClient(this@StockActivity)!!.create(ApiEndPoint::class.java)
        val call = apiEndPoint.getStockList(dari,sampai)
        call?.enqueue(object : Callback<StockResponse?> {
            override fun onFailure(call: Call<StockResponse?>, t: Throwable) {
                Toasty.error(this@StockActivity,"Gagal Mengambil Data! "+t.message).show()
            }

            override fun onResponse(call: Call<StockResponse?>, response: Response<StockResponse?>) {
                var obRespon = response.body()
                if (obRespon != null) {
                    if(obRespon.coal!=null){
                        stockList?.addAll(obRespon.coal!!)
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
            stockList!!.clear()
            var dari = txtTglDari.text.toString()
            var sampai = txtTglSampai.text.toString()
            loadStock(monitoring!!,dari!!,sampai!!)
            this@StockActivity?.runOnUiThread {
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

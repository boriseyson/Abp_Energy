package com.misit.abpenergy.HazardReport

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.misit.abpenergy.Api.ApiClient
import com.misit.abpenergy.Api.ApiEndPoint
import com.misit.abpenergy.HazardReport.Adapter.ListHazardReportAdapter
import com.misit.abpenergy.HazardReport.Response.HazardItem
import com.misit.abpenergy.HazardReport.Response.ListHazard
import com.misit.abpenergy.Login.LoginActivity
import com.misit.abpenergy.R
import com.misit.abpenergy.Utils.ConfigUtil
import com.misit.abpenergy.Utils.ConnectionLiveData
import com.misit.abpenergy.Utils.PopupUtil
import com.misit.abpenergy.Utils.PrefsUtil
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_hazard_report.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HazardReportActivity : AppCompatActivity(), ListHazardReportAdapter.OnItemClickListener,View.OnClickListener {

    private var adapter: ListHazardReportAdapter? = null
    private var hazardList:MutableList<HazardItem>?=null
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var page : Int=1
    private var visibleItem : Int=0
    private var total : Int=0
    private var pastVisibleItem : Int=0
    private var loading : Boolean=false
    var curentPosition: Int=0
    private lateinit var cld: ConnectionLiveData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hazard_report)
        title="HAZARD REPORT"
        PrefsUtil.initInstance(this)
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        if(PrefsUtil.getInstance().getBooleanState("IS_LOGGED_IN",false)){
            USERNAME = PrefsUtil.getInstance().getStringState(PrefsUtil.USER_NAME,"")
            RULE = PrefsUtil.getInstance().getStringState(PrefsUtil.RULE,"")
        }else{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        hazardList= ArrayList()
        adapter = ListHazardReportAdapter(this@HazardReportActivity,RULE,"",hazardList!!)
        val linearLayoutManager = LinearLayoutManager(this@HazardReportActivity)
        rvHazardList?.layoutManager = linearLayoutManager
        rvHazardList.adapter =adapter
        adapter?.setListener(this)
        DARI =PrefsUtil.getInstance().getStringState(PrefsUtil.AWAL_BULAN,"")
        SAMPAI = PrefsUtil.getInstance().getStringState(PrefsUtil.AKHIR_BULAN,"")
        TOTAL_HAZARD_USER = PrefsUtil.getInstance().getStringState(PrefsUtil.TOTAL_HAZARD_USER!!,"0")
        hazardVerify.text= TOTAL_HAZARD_USER
        txtTglDari.setText(DARI)
        txtTglSampai.setText(SAMPAI)
        swipeRefreshLayout = findViewById(R.id.pullRefreshHazard)
        swipeRefreshLayout.setOnRefreshListener(object :SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                rvHazardList.adapter = adapter
                page=1
                hazardList?.clear()
                load(page.toString(), DARI, SAMPAI)
                pullRefreshHazard.visibility=View.VISIBLE
                shimmerHazard.visibility = View.GONE
            }
        })
        floatingNewHazard.setOnClickListener {
            var intent = Intent(this@HazardReportActivity,NewHazardActivity::class.java)
            startActivity(intent)
        }
        hazardList?.clear()
//        load("1",DARI, SAMPAI)
        txtTglDari.setOnClickListener(this)
        txtTglSampai.setOnClickListener(this)
        btnLoad.setOnClickListener(this)
    }
    private fun checkNetworkConnection() {
        cld = ConnectionLiveData(application)
        cld.observe(this@HazardReportActivity,{ isConnected->
            if (isConnected){
                hazardList?.clear()
                load("1",DARI, SAMPAI)
                internetConnection.visibility = View.GONE
            }else{
                internetConnection.visibility= View.VISIBLE
            }
        })
    }

    override fun onResume() {
        checkNetworkConnection()
        super.onResume()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.newForm){
            var intent = Intent(this@HazardReportActivity,NewHazardActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
    fun load(hal:String,dari:String,sampai:String){
        swipeRefreshLayout.isRefreshing=true
        val apiEndPoint = ApiClient.getClient(this)!!.create(ApiEndPoint::class.java)
        val call = apiEndPoint.getListHazard(USERNAME,dari,sampai,hal)
        call?.enqueue(object : Callback<ListHazard> {
            override fun onFailure(call: Call<ListHazard>, t: Throwable) {
                swipeRefreshLayout.isRefreshing=false
                Toasty.error(this@HazardReportActivity,"Error : No Internet Connection", Toasty.LENGTH_SHORT).show()
                PopupUtil.dismissDialog()
            }

            override fun onResponse(call: Call<ListHazard>, response: Response<ListHazard>) {
                var listHazard = response.body()
                if(listHazard!=null){
                    totalHazard.text = listHazard.total.toString()
                    if (listHazard.data!=null){
                        PopupUtil.showProgress(this@HazardReportActivity,"Loading...","Membuat Hazard Report!")
                        loading=true
                        hazardList!!.addAll(listHazard.data!!)
                        adapter?.notifyDataSetChanged()
                        pullRefreshHazard.visibility=View.VISIBLE
                        shimmerHazard.visibility = View.GONE
                    }else{
                        curentPosition = (rvHazardList.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                        hazardList!!.addAll(listHazard.data!!)
                        adapter?.notifyDataSetChanged()
                        pullRefreshHazard.visibility=View.VISIBLE
                        shimmerHazard.visibility = View.GONE
                    }
                }
                rvHazardList.addOnScrollListener(object : RecyclerView.OnScrollListener(){
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                        if (dy > 0) {
                            visibleItem = recyclerView.layoutManager!!.childCount
                            total = recyclerView.layoutManager!!.itemCount
                            pastVisibleItem =
                                (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                            if (loading) {
                                if ((visibleItem + pastVisibleItem) >= total) {
                                    loading = false
                                    page++
                                    load(page.toString(), dari,sampai)
                                    pullRefreshHazard.visibility=View.GONE
                                    shimmerHazard.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                    override fun onScrollStateChanged(
                        recyclerView: RecyclerView,
                        newState: Int
                    ) {
                        super.onScrollStateChanged(recyclerView, newState)
                    }
                })
                    PopupUtil.dismissDialog()
                    swipeRefreshLayout.isRefreshing=false

            }
        })
    }
    companion object{
        var USERNAME="USERNAME"
        private  var DARI="01 January 2021"
        private  var SAMPAI="31 January 2021"
        private  var TOTAL_HAZARD_USER = "TOTAL_HAZARD_USER"
        private var RULE ="RULE"
    }

    override fun onItemClick(uid: String?) {
        var intent = Intent(this@HazardReportActivity,DetailHazardActivity::class.java)
        intent.putExtra(DetailHazardActivity.UID,uid.toString())
        startActivity(intent)
    }

    override fun onUpdateClick(uid: String?) {
        Toasty.info(this@HazardReportActivity,uid!!).show()
    }

    override fun onVerify(uid: String?, option: Int?) {

    }

    override fun onClick(v: View?) {
        if(v?.id==R.id.txtTglDari){
            ConfigUtil.showDialogTgl(txtTglDari,this@HazardReportActivity)
        }
        if(v?.id==R.id.txtTglSampai){
            ConfigUtil.showDialogTgl(txtTglSampai,this@HazardReportActivity)
        }
        if(v?.id==R.id.btnLoad){
            hazardList!!.clear()
            var dari = txtTglDari.text.toString()
            var sampai = txtTglSampai.text.toString()
            load("1",dari!!,sampai!!)
            this@HazardReportActivity?.runOnUiThread {
                adapter?.notifyDataSetChanged()
            }
        }
    }
}

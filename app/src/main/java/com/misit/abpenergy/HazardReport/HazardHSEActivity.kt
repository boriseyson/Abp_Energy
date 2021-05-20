package com.misit.abpenergy.HazardReportimport android.content.Intentimport androidx.appcompat.app.AppCompatActivityimport android.os.Bundleimport android.util.Logimport android.view.Viewimport android.view.Windowimport android.view.WindowManagerimport androidx.core.content.ContextCompatimport androidx.recyclerview.widget.LinearLayoutManagerimport androidx.recyclerview.widget.RecyclerViewimport androidx.swiperefreshlayout.widget.SwipeRefreshLayoutimport com.misit.abpenergy.Api.ApiClientimport com.misit.abpenergy.Api.ApiEndPointTwoimport com.misit.abpenergy.HazardReport.Adapter.ListHazardReportAdapterimport com.misit.abpenergy.HazardReport.Response.HazardItemimport com.misit.abpenergy.HazardReport.Response.ListHazardimport com.misit.abpenergy.Login.LoginActivityimport com.misit.abpenergy.Login.Response.DaftarAkunResponseimport com.misit.abpenergy.Rimport com.misit.abpenergy.Utils.ConfigUtilimport com.misit.abpenergy.Utils.PopupUtilimport com.misit.abpenergy.Utils.PrefsUtilimport es.dmoral.toasty.Toastyimport kotlinx.android.synthetic.main.activity_hazard_h_s_e.*import kotlinx.android.synthetic.main.activity_hazard_h_s_e.btnLoadimport kotlinx.android.synthetic.main.activity_hazard_h_s_e.floatingNewHazardimport kotlinx.android.synthetic.main.activity_hazard_h_s_e.txtTglDariimport kotlinx.android.synthetic.main.activity_hazard_h_s_e.txtTglSampaiimport kotlinx.android.synthetic.main.activity_hazard_saya.*import retrofit2.Callimport retrofit2.Callbackimport retrofit2.Responseclass HazardHSEActivity : AppCompatActivity(), ListHazardReportAdapter.OnItemClickListener, View.OnClickListener {    private var adapter: ListHazardReportAdapter? = null    private var hazardList:MutableList<HazardItem>?=null    lateinit var swipeRefreshLayout: SwipeRefreshLayout    private var page : Int=1    private var visibleItem : Int=0    private var total : Int=0    private var pastVisibleItem : Int=0    private var loading : Boolean=false    var curentPosition: Int=0    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        setContentView(R.layout.activity_hazard_h_s_e)        title= "All Hazard Report"        val window: Window = this.window        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)        window.statusBarColor = ContextCompat.getColor(this@HazardHSEActivity, R.color.colorPrimary)        var actionBar = supportActionBar        actionBar?.setDisplayHomeAsUpEnabled(true)        if(PrefsUtil.getInstance().getBooleanState("IS_LOGGED_IN",false)){            USERNAME = PrefsUtil.getInstance().getStringState(PrefsUtil.USER_NAME,"")            RULE = PrefsUtil.getInstance().getStringState(PrefsUtil.RULE,"")        }else{            val intent = Intent(this, LoginActivity::class.java)            startActivity(intent)            finish()        }        hazardList= ArrayList()        adapter = ListHazardReportAdapter(this@HazardHSEActivity,RULE,"ALL",hazardList!!)        val linearLayoutManager = LinearLayoutManager(this@HazardHSEActivity)        rvHazardListHSE?.layoutManager = linearLayoutManager        rvHazardListHSE.adapter =adapter        adapter?.setListener(this)        DARI =PrefsUtil.getInstance().getStringState(PrefsUtil.AWAL_BULAN,"")        SAMPAI = PrefsUtil.getInstance().getStringState(PrefsUtil.AKHIR_BULAN,"")        TOTAL_HAZARD_USER = PrefsUtil.getInstance().getStringState(PrefsUtil.TOTAL_HAZARD_USER!!,"0")        txtTglDari.setText(DARI)        txtTglSampai.setText(SAMPAI)        swipeRefreshLayout = findViewById(R.id.pullRefreshHazard)        swipeRefreshLayout.setOnRefreshListener(object :SwipeRefreshLayout.OnRefreshListener{            override fun onRefresh() {                rvHazardListHSE.adapter = adapter                page=1                hazardList?.clear()                load(page.toString(), DARI, SAMPAI)//                swipeRefreshLayout.isRefreshing=false                //PopupUtil.dismissDialog()            }        })        floatingNewHazard.setOnClickListener {            var intent = Intent(this@HazardHSEActivity,NewHazardActivity::class.java)            startActivity(intent)        }        hazardList?.clear()        load("1", DARI, SAMPAI)        txtTglDari.setOnClickListener(this)        txtTglSampai.setOnClickListener(this)        btnLoad.setOnClickListener(this)    }    override fun onSupportNavigateUp(): Boolean {        onBackPressed()        return super.onSupportNavigateUp()    }    override fun onItemClick(uid: String?) {        var intent = Intent(this@HazardHSEActivity,DetailHazardActivity::class.java)        intent.putExtra(DetailHazardActivity.UID,uid.toString())        startActivity(intent)    }    override fun onUpdateClick(uid: String?) {    }    override fun onVerify(uid: String?, option: Int?) {            doVerify(option!!,uid!!)    }    private fun doVerify(option: Int, uid: String) {        val c = this@HazardHSEActivity        var apiEndPoint=ApiClient.getClient(this@HazardHSEActivity)!!.create(ApiEndPointTwo::class.java)        var call= apiEndPoint.doVerifyHazard(option,uid)        call?.enqueue(object :Callback<DaftarAkunResponse>{            override fun onResponse(                call: Call<DaftarAkunResponse>,                response: Response<DaftarAkunResponse>            ) {                val r = response.body()                if (r!=null){                    if (r.success){                        Toasty.success(c,r.resultLog!!).show()                    }else{                        Toasty.error(c,r.resultLog!!).show()                    }                }else{                    Toasty.error(c,"Gagal, Silahkan Coba Lagi!").show()                }            }            override fun onFailure(call: Call<DaftarAkunResponse>, t: Throwable) {                Log.d("VerifyError",t.toString())            }        })    }    override fun onClick(v: View?) {        if(v?.id==R.id.txtTglDari){            ConfigUtil.showDialogTgl(txtTglDari,this@HazardHSEActivity)        }        if(v?.id==R.id.txtTglSampai){            ConfigUtil.showDialogTgl(txtTglSampai,this@HazardHSEActivity)        }        if(v?.id==R.id.btnLoad){            hazardList!!.clear()            var dari = txtTglDari.text.toString()            var sampai = txtTglSampai.text.toString()            load("1",dari!!,sampai!!)            this@HazardHSEActivity?.runOnUiThread {                adapter?.notifyDataSetChanged()            }        }    }    companion object{        private var NIK = "NIK"        private var RULE = "RULE"        private var USERNAME="USERNAME"        private  var DARI="01 January 2021"        private  var SAMPAI="31 January 2021"        private  var TOTAL_HAZARD_USER = "TOTAL_HAZARD_USER"    }    fun load(hal:String,dari:String,sampai:String){        swipeRefreshLayout.isRefreshing=true        val apiEndPoint = ApiClient.getClient(this)!!.create(ApiEndPointTwo::class.java)        val call = apiEndPoint.getListHazardHSE(dari,sampai,hal)        call?.enqueue(object : Callback<ListHazard> {            override fun onFailure(call: Call<ListHazard>, t: Throwable) {                swipeRefreshLayout.isRefreshing=false                Toasty.error(this@HazardHSEActivity,"Error : $t", Toasty.LENGTH_SHORT).show()                PopupUtil.dismissDialog()            }            override fun onResponse(call: Call<ListHazard>, response: Response<ListHazard>) {                var listHazard = response.body()                if(listHazard!=null){                    if (listHazard.data!=null){                        PopupUtil.showProgress(this@HazardHSEActivity,"Loading...","Membuat Hazard Report!")                        loading=true                        hazardList!!.addAll(listHazard.data!!)                        adapter?.notifyDataSetChanged()                    }else{                        curentPosition = (rvHazardListHSE.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()                        hazardList!!.addAll(listHazard.data!!)                        adapter?.notifyDataSetChanged()                    }                }                rvHazardListHSE.addOnScrollListener(object : RecyclerView.OnScrollListener(){                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {                        if (dy > 0) {                            visibleItem = recyclerView.layoutManager!!.childCount                            total = recyclerView.layoutManager!!.itemCount                            pastVisibleItem =                                (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()                            if (loading) {                                if ((visibleItem + pastVisibleItem) >= total) {                                    loading = false                                    page++                                    load(page.toString(), dari,sampai)                                }                            }                        }                    }                    override fun onScrollStateChanged(                        recyclerView: RecyclerView,                        newState: Int                    ) {                        super.onScrollStateChanged(recyclerView, newState)                    }                })                PopupUtil.dismissDialog()                swipeRefreshLayout.isRefreshing=false            }        })    }}
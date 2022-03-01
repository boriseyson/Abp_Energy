package com.misit.abpenergy.HSE.Inspeksiimport android.content.Intentimport androidx.appcompat.app.AppCompatActivityimport android.os.Bundleimport android.util.Logimport android.view.Viewimport android.view.Windowimport android.view.WindowManagerimport androidx.core.content.ContextCompatimport androidx.recyclerview.widget.LinearLayoutManagerimport com.misit.abpenergy.Api.ApiClientimport com.misit.abpenergy.Api.ApiEndPointTwoimport com.misit.abpenergy.HSE.Inspeksi.Adapter.InspeksiDataAdapaterimport com.misit.abpenergy.HSE.Inspeksi.Response.DataInspeksiResponseimport com.misit.abpenergy.HSE.Inspeksi.Response.DataItemInspeksiimport com.misit.abpenergy.Login.LoginActivityimport com.misit.abpenergy.Rimport com.misit.abpenergy.Utils.PopupUtilimport com.misit.abpenergy.Utils.PrefsUtilimport kotlinx.android.synthetic.main.activity_list_inspeksi.*import retrofit2.Callimport retrofit2.Callbackimport retrofit2.Responseclass ListInspeksiActivity : AppCompatActivity(),View.OnClickListener, InspeksiDataAdapater.OnItemsClickListener {    private var idForm:String? = null    private var nameForm:String? = null    private var adapter: InspeksiDataAdapater?=null    private var listHeader:MutableList<DataItemInspeksi>?=null    private var page=0    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        setContentView(R.layout.activity_list_inspeksi)        idForm = intent.getStringExtra(IDFORM)        nameForm = intent.getStringExtra(NAMEFORM)        title = nameForm        PrefsUtil.initInstance(this)        val window: Window = this.window        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)        var actionBar = supportActionBar        actionBar?.setDisplayHomeAsUpEnabled(true)        if(PrefsUtil.getInstance().getBooleanState("IS_LOGGED_IN",false)){            USERNAME = PrefsUtil.getInstance().getStringState(PrefsUtil.USER_NAME,"")        }else{            val intent = Intent(this, LoginActivity::class.java)            startActivity(intent)            finish()        }        listHeader=ArrayList()        adapter = InspeksiDataAdapater(this@ListInspeksiActivity,listHeader!!)        val linearLayoutManager = LinearLayoutManager(this@ListInspeksiActivity)        rvFormInspeksiList?.layoutManager = linearLayoutManager        rvFormInspeksiList.adapter =adapter        adapter?.setListener(this)        loadInspeksi(page.toString(),idForm!!)        floatingNewInspeksi.setOnClickListener(this)    }    private fun loadInspeksi(hal: String,idForm:String) {        PopupUtil.showProgress(this@ListInspeksiActivity,"Loading...","Memuat Form Inspeksi!")        val apiEndPoint = ApiClient.getClient(this)!!.create(ApiEndPointTwo::class.java)        val call = apiEndPoint.getInspeksiUser(USERNAME,idForm)        call?.enqueue(object : Callback<DataInspeksiResponse>{            override fun onResponse(                call: Call<DataInspeksiResponse>,                response: Response<DataInspeksiResponse>            ) {                val r = response.body()                if(r!=null){                    if(r.inspeksi!=null){                        listHeader!!.addAll(r!!.inspeksi!!.data!!)                        adapter?.notifyDataSetChanged()                        PopupUtil.dismissDialog()                    }else{                    }                }            }            override fun onFailure(call: Call<DataInspeksiResponse>, t: Throwable) {                Log.d("ErrorListUserInspeksi",t.toString())                PopupUtil.dismissDialog()            }        })    }    override fun onClick(v: View?) {        if(v?.id==R.id.floatingNewInspeksi){            var intent = Intent(this@ListInspeksiActivity, NewInspeksiActivity::class.java)            intent.putExtra(NewInspeksiActivity.IDFORM,idForm)            intent.putExtra(NAMEFORM,nameForm)            startActivityForResult(intent,100)        }    }    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {        super.onActivityResult(requestCode, resultCode, data)    }    override fun onSupportNavigateUp(): Boolean {        onBackPressed()        return super.onSupportNavigateUp()    }    companion object{        var USERNAME = "USERNAME"        var IDFORM = "IDFORM"        var NAMEFORM = "NAMEFORM"    }    override fun onItemClick(idItem: String,tglInspeksi: String,ptInspeksi: String,lokInspeksi: String,saranInspeksi: String,namaInspeksi:String) {        var intent = Intent(this@ListInspeksiActivity, DetailInspeksiActivity::class.java)        intent.putExtra("idInspeksi",idItem)        intent.putExtra("tglInspeksi",tglInspeksi)        intent.putExtra("perusahaanInspeksi",ptInspeksi)        intent.putExtra("lokasiInspeksi",lokInspeksi)        intent.putExtra("saranInspeksi",saranInspeksi)        intent.putExtra("namaInspeksi",namaInspeksi)        intent.putExtra("formId",idForm)        startActivity(intent)    }}
package com.misit.abpenergy.Masterimport android.content.Intentimport androidx.appcompat.app.AppCompatActivityimport android.os.Bundleimport android.util.Logimport android.view.Menuimport android.view.MenuItemimport android.view.Windowimport android.view.WindowManagerimport androidx.core.content.ContextCompatimport androidx.recyclerview.widget.LinearLayoutManagerimport com.misit.abpenergy.Api.ApiClientimport com.misit.abpenergy.Api.ApiEndPointTwoimport com.misit.abpenergy.Login.Adapter.CompanyAdapaterimport com.misit.abpenergy.Login.Response.CompanyItemimport com.misit.abpenergy.Login.Response.CompanyResponseimport com.misit.abpenergy.Rimport com.misit.abpenergy.Sarpras.NewSarprasActivityimport com.misit.abpenergy.Utils.PopupUtilimport es.dmoral.toasty.Toastyimport kotlinx.android.synthetic.main.activity_perusahaan.*import retrofit2.Callimport retrofit2.Callbackimport retrofit2.Responseclass PerusahaanActivity : AppCompatActivity(),CompanyAdapater.OnItemClickListener {    private var adapter: CompanyAdapater? = null    private var companyList:MutableList<CompanyItem>?=null    private var idCompany:String?=null    private var companyDipilih:String?=null    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        setContentView(R.layout.activity_perusahaan)        title= "Perusahaan"        val window: Window = this.window        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)        window.statusBarColor = ContextCompat.getColor(this@PerusahaanActivity, R.color.colorPrimary)        var actionBar = supportActionBar        actionBar?.setDisplayHomeAsUpEnabled(true)        companyDipilih = ""        companyList= ArrayList()        adapter = CompanyAdapater(this@PerusahaanActivity,companyDipilih!!,companyList!!)        val linearLayoutManager = LinearLayoutManager(this@PerusahaanActivity)        rvPerusahaan?.layoutManager = linearLayoutManager        rvPerusahaan.adapter =adapter        adapter?.setListener(this@PerusahaanActivity)//        loadData()    }    override fun onResume() {        loadData()        super.onResume()    }    private fun loadData() {        PopupUtil.showProgress(this@PerusahaanActivity,"Loading...","Memuat Data Perusahaan!")        val apiEndPoint = ApiClient.getClient(this)!!.create(ApiEndPointTwo::class.java)        val call = apiEndPoint.getCompany()        call?.enqueue(object : Callback<CompanyResponse> {            override fun onResponse(                call: Call<CompanyResponse>,                response: Response<CompanyResponse>            ) {                var r = response.body()                if(r!=null){                    companyList?.addAll(r.company!!)                    adapter?.notifyDataSetChanged()                    PopupUtil.dismissDialog()                }else{                    Toasty.error(this@PerusahaanActivity,"Perusahaan Tidak Ditemukan!").show()                    PopupUtil.dismissDialog()                }            }            override fun onFailure(call: Call<CompanyResponse>, t: Throwable) {                Log.d("ErrorCompany",t.toString())            }        })    }    override fun onCreateOptionsMenu(menu: Menu?): Boolean {        menuInflater.inflate(R.menu.menu_sarpras,menu)        return super.onCreateOptionsMenu(menu)    }    override fun onOptionsItemSelected(item: MenuItem): Boolean {        if (item.itemId==R.id.newFormSarpras) {            var intent = Intent(this@PerusahaanActivity, NewPerusahaanActivity::class.java)            startActivity(intent)        }        return super.onOptionsItemSelected(item)    }    override fun onItemClick(idCompany: String?, namaPerusahaan: String?) {    }    override fun onSupportNavigateUp(): Boolean {        onBackPressed()        return super.onSupportNavigateUp()    }}
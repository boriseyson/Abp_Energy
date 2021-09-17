package com.misit.abpenergy.HazardReportimport android.app.Activityimport android.content.Contextimport android.content.Intentimport androidx.appcompat.app.AppCompatActivityimport android.os.Bundleimport android.util.Logimport android.view.Windowimport android.view.WindowManagerimport androidx.core.content.ContextCompatimport androidx.recyclerview.widget.LinearLayoutManagerimport com.misit.abpenergy.Api.ApiClientimport com.misit.abpenergy.Api.ApiEndPointimport com.misit.abpenergy.HazardReport.Adapter.KemungkinanAdapterimport com.misit.abpenergy.HazardReport.Response.KemungkinanItemimport com.misit.abpenergy.HazardReport.Response.KemungkinanResponseimport com.misit.abpenergy.DataSource.KemungkinanDataSourceimport com.misit.abpenergy.Rimport es.dmoral.toasty.Toastyimport kotlinx.android.synthetic.main.activity_kemungkinan.*import retrofit2.Callimport retrofit2.Callbackimport retrofit2.Responseimport java.sql.SQLExceptionclass   KemungkinanActivity : AppCompatActivity(),    KemungkinanAdapter.OnItemClickListener {    private var adapter: KemungkinanAdapter? = null    private var kemungkinanList:MutableList<KemungkinanItem>?=null    private var kemungkinanDipilih:String?=null    private var call: Call<KemungkinanResponse>?=null    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        setContentView(R.layout.activity_kemungkinan)        title="Pilih Resiko Kemungkinan"        val window: Window = this.window        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)        kemungkinanList = ArrayList()        kemungkinanDipilih = intent.getStringExtra("kemungkinanDipilih")        var actionBar = supportActionBar        actionBar?.setDisplayHomeAsUpEnabled(true)        adapter = KemungkinanAdapter(            this,            kemungkinanDipilih,            kemungkinanList!!        )        val linearLayoutManager = LinearLayoutManager(this@KemungkinanActivity)        rvKemungkinan?.layoutManager = linearLayoutManager        rvKemungkinan.adapter =adapter        adapter?.setListener(this)//        loadData()        kemungkinanSQL(this@KemungkinanActivity)    }    private fun loadData() {        val apiEndPoint = ApiClient.getClient(this@KemungkinanActivity)!!.create(ApiEndPoint::class.java)        call = apiEndPoint.resikoKemungkinan()        call?.enqueue(object : Callback<KemungkinanResponse?> {            override fun onFailure(call: Call<KemungkinanResponse?>, t: Throwable) {                Toasty.error(this@KemungkinanActivity,"Error : "+ t).show()            }            override fun onResponse(                call: Call<KemungkinanResponse?>,                response: Response<KemungkinanResponse?>            ) {                var kemungkinanRes= response.body()                if(kemungkinanRes!=null){                    Log.d("Kemungkinan",kemungkinanRes.toString())                    if(kemungkinanRes.kemungkinan!=null){                        kemungkinanList?.addAll(kemungkinanRes.kemungkinan!!)                        adapter?.notifyDataSetChanged()                    }                }            }        })    }    private fun kemungkinanSQL(c: Context){        val kemungkinanDataSource =            KemungkinanDataSource(c)        try {            val kemungkinanRow=kemungkinanDataSource.getAll()            kemungkinanRow.forEach{                Log.d("KemungkinanSQL",kemungkinanRow.toString())                kemungkinanList?.add(KemungkinanItem(it.kemungkinan,it.flag,it.nilai,it.idKemungkinan))                adapter?.notifyDataSetChanged()            }        }catch (e: SQLException){            Log.d("KemungkinanSQL",e.toString())        }    }    override fun onSupportNavigateUp(): Boolean {        onBackPressed()        return super.onSupportNavigateUp()    }    override fun onItemClick(uid: String, kemungkinan: String) {        val intent: Intent = Intent()        intent.putExtra("kemungkinanID",uid)        intent.putExtra("kemungkinanDipilih",kemungkinan)        setResult(Activity.RESULT_OK,intent)        finish()    }}
package com.misit.abpenergy.Sarpras

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.misit.abpenergy.Api.ApiClient
import com.misit.abpenergy.Api.ApiEndPoint
import com.misit.abpenergy.R
import com.misit.abpenergy.Sarpras.Adapter.ApproveSarprasAdapter
import com.misit.abpenergy.Sarpras.Adapter.SarprasAdapter
import com.misit.abpenergy.Sarpras.Realm.PenumpangModel
import com.misit.abpenergy.Sarpras.SarprasResponse.DataItem
import com.misit.abpenergy.Sarpras.SarprasResponse.UserSarprasResponse
import com.misit.abpenergy.Utils.PopupUtil
import com.misit.abpenergy.Utils.PrefsUtil
import es.dmoral.toasty.Toasty
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_kabag_appr_sarpras.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KabagApprSarprasActivity : AppCompatActivity(), ApproveSarprasAdapter.OnItemClickListener {

    private var adapter: ApproveSarprasAdapter? = null
    private var noidOut:String?=null
    private var username:String?=null
    private var myList:MutableList<PenumpangModel>?=null
    var curentPosition: Int=0
    private var page : Int=1
    private var visibleItem : Int=0
    private var total : Int=0
    private var pastVisibleItem : Int=0
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    var sarprasList : MutableList<DataItem>? = null
    private var call: Call<UserSarprasResponse>?=null
    private var loading : Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kabag_appr_sarpras)
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        title="Keluar Masuk Sarana Approve"
        PrefsUtil.initInstance(this)
        Realm.init(this@KabagApprSarprasActivity)

        if(PrefsUtil.getInstance().getBooleanState("IS_LOGGED_IN",true)){
            USERNAME = PrefsUtil.getInstance().getStringState(PrefsUtil.USER_NAME,"")
            NIK = PrefsUtil.getInstance().getStringState(PrefsUtil.NIK,"")
            NAMA_LENGKAP = PrefsUtil.getInstance().getStringState(PrefsUtil.NAMA_LENGKAP,"")
            DEPARTMENT = PrefsUtil.getInstance().getStringState(PrefsUtil.DEPT,"")
            SECTON = PrefsUtil.getInstance().getStringState(PrefsUtil.SECTION,"")
            LEVEL = PrefsUtil.getInstance().getStringState(PrefsUtil.LEVEL,"")
            RULE = PrefsUtil.getInstance().getStringState(PrefsUtil.RULE,"")
        }

        permit()

        username = intent.getStringExtra(USERNAME)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        sarprasList = ArrayList()
        myList = ArrayList()
        getPenumpang()
        adapter = ApproveSarprasAdapter(
            this,
            sarprasList!!,
            RULE
        )
        val linearLayoutManager = LinearLayoutManager(this@KabagApprSarprasActivity)
        rvSarpras?.layoutManager = linearLayoutManager
        rvSarpras.adapter =adapter
        adapter?.setListener(this)
        loadDetailRkb(page)
        swipeRefreshLayout = findViewById(R.id.pullRefresh)
        swipeRefreshLayout.setOnRefreshListener(object :SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                rvSarpras.adapter = adapter
                page=1
                sarprasList?.clear()
                loadDetailRkb(page)
            }
        })

    }

    fun getPenumpang(){
        myList?.clear()
        var realm = Realm.getDefaultInstance()
        var listPenumpang =
            realm.
                where(PenumpangModel::class.java)
                .findAll()
        listPenumpang?.forEach {
            myList?.add(it)
        }
        realm.close()
    }
    override fun onItemClick(noIdOut: String?) {
        var url:String = "https://abpjobsite.com/sarpras/sarana/keluar-masuk-print-out-"+noIdOut.toString()
        val request = DownloadManager.Request(Uri.parse(url))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
        request.setTitle("Surat Keluar Masuk Sarana.pdf")

        request.setDescription("The File Is Downloading...")
        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"${System.currentTimeMillis()}")

        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        manager.enqueue(request)
    }

    override fun onDetailClick(noIdOut: String?) {
        val intent = Intent(this@KabagApprSarprasActivity,DetailSarprasActivity::class.java)
        intent.putExtra(DetailSarprasActivity.noidOut,noIdOut)
        startActivity(intent)
    }
    private fun permit(){
        if (ContextCompat.checkSelfPermission(this@KabagApprSarprasActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@KabagApprSarprasActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this@KabagApprSarprasActivity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    SarprasActivity.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                )

            } else {
                Toasty.info(this@KabagApprSarprasActivity,"c", Toasty.LENGTH_SHORT).show()

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this@KabagApprSarprasActivity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    KabagApprSarprasActivity.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                )
            }
        }
    }
    private fun loadDetailRkb(currPage:Int) {
        PopupUtil.showLoading(this@KabagApprSarprasActivity,"Loading","Mengambil Data!")
        val apiEndPoint = ApiClient.getClient(this@KabagApprSarprasActivity)!!.create(ApiEndPoint::class.java)
        call = apiEndPoint.getSarprasKabag(DEPARTMENT, SECTON,currPage)
        call?.enqueue(object : Callback<UserSarprasResponse?> {
            override fun onFailure(call: Call<UserSarprasResponse?>, t: Throwable) {
                Toast.makeText(this@KabagApprSarprasActivity, "Failed to Fetch Data\n" +
                        "e: $t", Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(
                call: Call<UserSarprasResponse?>,
                response: Response<UserSarprasResponse?>
            ) {
                val listSarpras = response.body()
                if (listSarpras != null) {
                    if (listSarpras.data != null) {
                        loading=true
                        if(sarprasList?.size==0){
                            sarprasList?.addAll(listSarpras.data!!)
                            this@KabagApprSarprasActivity?.runOnUiThread {
                                adapter?.notifyDataSetChanged()
                                PopupUtil.dismissDialog()
                                swipeRefreshLayout.isRefreshing=false
                            }
                        }else{
                            curentPosition = (rvSarpras.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                            sarprasList?.addAll(listSarpras.data!!)
                            this@KabagApprSarprasActivity?.runOnUiThread {
                                adapter?.notifyDataSetChanged()
                                PopupUtil.dismissDialog()
                                swipeRefreshLayout.isRefreshing=false
                            }
                        }

                        rvSarpras.addOnScrollListener(object : RecyclerView.OnScrollListener(){
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
                                            loadDetailRkb(page)
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
                    }
                }
            }

        })
    }
    companion object{
        var NO_RKB = "no_rkb"
        var USERNAME = "16060052"
        var DEPARTMENT="department"
        var SECTON="section"
        var LEVEL="level"
        var NAMA_LENGKAP = "nama_lengkap"
        var NIK = "nama_lengkap"
        var RULE = "rule"
        var NOTIF = false
        var MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE=0
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toasty.info(this@KabagApprSarprasActivity,"PERMISSION_GRANTED",Toasty.LENGTH_SHORT).show()

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toasty.info(this@KabagApprSarprasActivity,"PERMISSION_DENIED",Toasty.LENGTH_SHORT).show()

                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
                Toasty.info(this@KabagApprSarprasActivity,"PERMISSION_Ignore",Toasty.LENGTH_SHORT).show()

            }
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}

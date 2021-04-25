package com.misit.abpenergy.Sarpras

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.misit.abpenergy.Api.ApiClient
import com.misit.abpenergy.Api.ApiEndPoint
import com.misit.abpenergy.QRCodeActivity
import com.misit.abpenergy.R
import com.misit.abpenergy.Sarpras.Adapter.SarprasAdapter
import com.misit.abpenergy.Sarpras.SarprasResponse.DataItem
import com.misit.abpenergy.Sarpras.SarprasResponse.UserSarprasResponse
import com.misit.abpenergy.Utils.PopupUtil
import com.misit.abpenergy.Utils.PrefsUtil
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_sarpras.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SarprasActivity : AppCompatActivity(), SarprasAdapter.OnItemClickListener {
    private var adapter: SarprasAdapter? = null
    private var noidOut:String?=null
    private var username:String?=null
    private var page : Int=1
    private var visibleItem : Int=0
    private var total : Int=0
    private var pastVisibleItem : Int=0
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    var sarprasList : MutableList<DataItem>? = null
    private var call: Call<UserSarprasResponse>?=null
    private var loading : Boolean=false
    var curentPosition: Int=0
    lateinit var container: ShimmerFrameLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sarpras)
        title="Keluar Masuk Sarana"
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        if(PrefsUtil.getInstance().getBooleanState("IS_LOGGED_IN",true)){
            USERNAME = PrefsUtil.getInstance().getStringState(PrefsUtil.USER_NAME,"")
            NIK = PrefsUtil.getInstance().getStringState(PrefsUtil.NIK,"")
            NAMA_LENGKAP = PrefsUtil.getInstance().getStringState(PrefsUtil.NAMA_LENGKAP,"")
            DEPARTMENT = PrefsUtil.getInstance().getStringState(PrefsUtil.DEPT,"")
            SECTON = PrefsUtil.getInstance().getStringState(PrefsUtil.SECTION,"")
            LEVEL = PrefsUtil.getInstance().getStringState(PrefsUtil.LEVEL,"")
            RULE = PrefsUtil.getInstance().getStringState(PrefsUtil.RULE,"")
        }
//        Toasty.info(this,RULE,Toasty.LENGTH_SHORT).show()
        permit()
        username = intent.getStringExtra(USERNAME)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        sarprasList = ArrayList()
        adapter = SarprasAdapter(
            this,
            sarprasList!!,
            RULE
        )
        val linearLayoutManager = LinearLayoutManager(this@SarprasActivity)
        rvSarpras?.layoutManager = linearLayoutManager
        rvSarpras.adapter =adapter
        adapter?.setListener(this)
        loadDetailRkb(NIK,page)
        swipeRefreshLayout = findViewById(R.id.pullRefresh)
        swipeRefreshLayout.setOnRefreshListener(object :SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                rvSarpras.adapter = adapter
                page=1
                sarprasList?.clear()
                loadDetailRkb(NIK,page)
//                swipeRefreshLayout.isRefreshing=false
                //PopupUtil.dismissDialog()

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_sarpras,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.newFormSarpras) {
            var intent = Intent(this@SarprasActivity,NewSarprasActivity::class.java)
            startActivity(intent)
        }
            return super.onOptionsItemSelected(item)
    }
    private fun permit(){
        if (ContextCompat.checkSelfPermission(this@SarprasActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@SarprasActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this@SarprasActivity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                )

            } else {
                Toasty.info(this@SarprasActivity,"c",Toasty.LENGTH_SHORT).show()

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this@SarprasActivity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                )
            }
        }
    }

    private fun loadDetailRkb(username: String?,curPage:Int) {
        PopupUtil.showLoading(this@SarprasActivity,"Loading","Mengambil Data!")
        val apiEndPoint = ApiClient.getClient(this@SarprasActivity)!!.create(ApiEndPoint::class.java)
        call = apiEndPoint.getSarprasUser(username,curPage)
        call?.enqueue(object : Callback<UserSarprasResponse?> {
            override fun onFailure(call: Call<UserSarprasResponse?>, t: Throwable) {
                Toast.makeText(this@SarprasActivity, "Failed to Fetch Data\n" +
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
                            this@SarprasActivity?.runOnUiThread {
                                adapter?.notifyDataSetChanged()
                                PopupUtil.dismissDialog()
                                swipeRefreshLayout.isRefreshing=false
                            }
                        }else{
                            curentPosition = (rvSarpras.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                            sarprasList?.addAll(listSarpras.data!!)
                            this@SarprasActivity?.runOnUiThread {
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
                                            loadDetailRkb(NIK,page)
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
        val intent = Intent(this@SarprasActivity,DetailSarprasActivity::class.java)
        intent.putExtra(DetailSarprasActivity.noidOut,noIdOut)
        startActivity(intent)
    }
    override fun onQRCodeClick(noIdOut: String?) {
        val intent = Intent(this@SarprasActivity,QRCodeActivity::class.java)
        intent.putExtra("itemCodes",noIdOut)
        intent.putExtra("judul","Tunjukkan QRCode Kepada Security Untuk Di Scan")
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toasty.info(this@SarprasActivity,"PERMISSION_GRANTED",Toasty.LENGTH_SHORT).show()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toasty.info(this@SarprasActivity,"PERMISSION_DENIED",Toasty.LENGTH_SHORT).show()
                }
                return
            }
            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
                Toasty.info(this@SarprasActivity,"PERMISSION_Ignore",Toasty.LENGTH_SHORT).show()
            }
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
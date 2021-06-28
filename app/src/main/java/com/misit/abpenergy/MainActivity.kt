package com.misit.abpenergy

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.storage.StorageReference
import com.misit.abpenergy.Api.ApiClient
import com.misit.abpenergy.Api.ApiEndPoint
import com.misit.abpenergy.HazardReport.Service.HazardService
import com.misit.abpenergy.Login.LoginActivity
import com.misit.abpenergy.Model.KaryawanModel
import com.misit.abpenergy.Sarpras.Realm.PenumpangModel
import com.misit.abpenergy.Sarpras.SaranaResponse.ListSaranaResponse
import com.misit.abpenergy.Sarpras.Service.LoadSarana
import com.misit.abpenergy.Sarpras.Service.SaranaService
import com.misit.abpenergy.Service.LoadingServices
import com.misit.abpenergy.Utils.ConfigUtil
import com.misit.abpenergy.Utils.Constants
import com.misit.abpenergy.Utils.PrefsUtil
import es.dmoral.toasty.Toasty
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.index_new.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response




class MainActivity : AppCompatActivity() {
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private var app_version : String?=""
    private var cekVersion:String?=null
    var karyawan : ArrayList<KaryawanModel>? = null
    private val tokenPassingReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val bundle = intent.extras
            if (bundle != null) {
                if (bundle.containsKey("fgSarana")) {
                    val tokenData = bundle.getString("fgSarana")
                    Log.d("ServiceName","${tokenData} Main")
                    if(tokenData=="fgDone"){
                        updateProgress()
                        startStopService(LoadingServices::class.java)
                    }else{
                        Toasty.info(this@MainActivity,"Failed To Load Data").show()
                    }
                }
                if (bundle.containsKey("bgSarana")) {
                    val tokenData = bundle.getString("bgSarana")
                    Log.d("ServiceName","${tokenData} Main")
                    if(tokenData=="bgDone"){
                        updateProgress()
                        bgStopService(saranaService)
                    }else{
                        Toasty.info(this@MainActivity,"Failed To Load Data").show()
                    }
                }
            }
        }

    }
    lateinit var saranaService:Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        realmConfig(this)
        ConfigUtil.changeColor(this)
        versionApp()
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this@MainActivity)
        Glide.with(this).load(R.drawable.abp).into(imageView)
        tvVersionCode.text=" V.${app_version}"
        PrefsUtil.initInstance(this)
        karyawan = ArrayList()
        ConfigUtil.deleteInABPIMAGES(this@MainActivity)
        ConfigUtil.createFolder(this@MainActivity,"ABP_IMAGES")
        ConfigUtil.createFolder(this@MainActivity,"HAZARD_TEMP")
        LocalBroadcastManager.getInstance(this).registerReceiver(tokenPassingReceiver, IntentFilter("com.misit.abpenergy"))
        saranaService = Intent(this@MainActivity,SaranaService::class.java)
    }
    private fun startStopService(jvClass:Class<*>) {
        if(isMyServiceRunning(jvClass)){
            var intent = Intent(this@MainActivity, jvClass).apply {
                this.action = Constants.SERVICE_STOP
                LocalBroadcastManager.getInstance(this@MainActivity).unregisterReceiver(tokenPassingReceiver!!)
            }
            stopService(intent)
        }else{
            var intent = Intent(this@MainActivity, jvClass).apply {
                this.action = Constants.SERVICE_START
            }
            intent.putExtra("username", NewIndexActivity.USERNAME)
            startService(intent)

        }
    }
    private fun isMyServiceRunning(mClass: Class<*>): Boolean {
        val manager: ActivityManager =getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service: ActivityManager.RunningServiceInfo in manager.getRunningServices(Integer.MAX_VALUE)){
            if(mClass.name.equals(service.service.className)){
                return true
            }
        }
        return false
    }
    override fun onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(tokenPassingReceiver)
        super.onStop()
    }
    override fun onResume() {

        if(PrefsUtil.getInstance().getBooleanState("INTRO_APP",false)){
            if (ConfigUtil.cekKoneksi(this)) {
                updateProgress()
            }else{
                updateProgress()
            }
        }else{
            startActivity(Intent(this@MainActivity,IntroActivity::class.java))
            finish()
        }
        super.onResume()
    }

    fun updateProgress(){
        val runnable= {
            var besar = progressHorizontal.progress

            progressHorizontal.progress = besar + 100
            if(besar<100){
                if (ConfigUtil.cekKoneksi(this)) {
                    startService()
                }else{
                    updateProgress()
                }
            } else {
                if(PrefsUtil.getInstance().getBooleanState("IS_LOGGED_IN", false))
                {
                    val intent = Intent(this, NewIndexActivity::class.java)
                    startActivity(intent)
                }
                else
                {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
               finish()
            }
        }
        Handler().postDelayed(runnable, 100)
    }
    private fun bgStopService(intent: Intent){
        stopService(intent)
        LocalBroadcastManager.getInstance(this@MainActivity).unregisterReceiver(tokenPassingReceiver!!)
    }
    fun startService(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            var intent = Intent(this@MainActivity, LoadingServices::class.java).apply {
                this.action = Constants.SERVICE_START
            }
            startService(
                intent
                )
        }else{
            startService(saranaService)
        }
    }
    private fun loadSarana(){
        val apiEndPoint = ApiClient.getClient(this@MainActivity)!!.create(ApiEndPoint::class.java)
        val call = apiEndPoint.getAllSarana()
        call?.enqueue(object : Callback<ListSaranaResponse?> {
            override fun onFailure(call: Call<ListSaranaResponse?>, t: Throwable) {
                if (cekKoneksi(this@MainActivity)) {
                    updateProgress()
                } else {
                    koneksiInActive()
                }
            }

            override fun onResponse(
                call: Call<ListSaranaResponse?>,
                response: Response<ListSaranaResponse?>
            ) {
                tvLoadingText.text = "Mengambil Data!!!"
                var i = 1
                val listSarana = response.body()
                if (listSarana != null) {

                    listSarana.karyawan?.let {
                        //                        karyawan?.addAll(it)
                        listSarana.karyawan?.forEach {
                            karyawan?.add(
                                KaryawanModel(
                                    i.toLong(),
                                    it.nik!!,
                                    it.nama!!,
                                    it.jabatan!!
                                )
                            )

                            i++
                        }
                        PrefsUtil.getInstance()
                            .setStringState(
                                PrefsUtil.AWAL_BULAN,
                                listSarana.awalBulan
                            )
                        PrefsUtil.getInstance()
                            .setStringState(
                                PrefsUtil.AKHIR_BULAN,
                                listSarana.akhirBulan
                            )
                        tvLoadingText.text = "Mengumpulkan Data!!!"
//                        listPenumpang()
                    }
                }
            }
        })
    }

    fun listPenumpang(){
        var realm = Realm.getDefaultInstance()
        realm?.executeTransaction {
            var listPenumpang =
                realm?.where(PenumpangModel::class.java)
                    ?.findAllSorted("id", Sort.DESCENDING)
            listPenumpang?.deleteAllFromRealm()

            val penumpang = PenumpangModel()
            karyawan?.forEach {
                penumpang.id = it.id
                penumpang.nik = it.nik
                penumpang.nama = it.nama
                penumpang.jabatan = it.jabatan
                try {
                    realm?.copyToRealm(penumpang)
                    tvLoadingText.text = "Menyiapkan Data!!!"
                } catch (e: Exception) {
                    Toast.makeText(
                        this@MainActivity,
                        "Load Data Karyawan Gagal : $e",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
//            getPenumpang()
            if(PrefsUtil.getInstance().getBooleanState("IS_LOGGED_IN", false))
            {
                val intent = Intent(this, NewIndexActivity::class.java)
                startActivity(intent)
            }
            else
            {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            finish()
        }
        realm?.close()
    }
    fun versionApp(){
        Use@ try {
            val pInfo: PackageInfo = this.getPackageManager().getPackageInfo(packageName, 0)
            app_version = pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }
    fun cekKoneksi(context: Context):Boolean{
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }

        return result
    }
    fun koneksiInActive(){
        AlertDialog.Builder(this)
            .setTitle("Maaf Koneksi Internet Tidak Ada!")
            .setPositiveButton("OK, Keluar", { dialog,
                                               which ->
                finish()
            }).show()
    }

    companion object {
        private  var TAG="TAG"
    }
}

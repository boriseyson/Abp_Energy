package com.misit.abpenergy

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.storage.StorageReference
import com.misit.abpenergy.Api.ApiClient
import com.misit.abpenergy.Api.ApiEndPoint
import com.misit.abpenergy.Login.LoginActivity
import com.misit.abpenergy.Model.KaryawanModel
import com.misit.abpenergy.Sarpras.Realm.PenumpangModel
import com.misit.abpenergy.Sarpras.SaranaResponse.ListSaranaResponse
import com.misit.abpenergy.Utils.ConfigUtil
import com.misit.abpenergy.Utils.PrefsUtil
import io.realm.Realm
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response




class MainActivity : AppCompatActivity() {
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private var app_version : String?=""
    private var cekVersion:String?=null
    var karyawan : ArrayList<KaryawanModel>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ConfigUtil.changeColor(this)
        Realm.init(this)
        versionApp()
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this@MainActivity)

        Glide.with(this).load(R.drawable.abp).into(imageView)

        tvVersionCode.text=" V.${app_version}"


        PrefsUtil.initInstance(this)
        karyawan = ArrayList()


//        updateProgress()

    }

    override fun onResume() {
        if(verifyStoragePermissions(this@MainActivity,this@MainActivity)){
            loadSarana()
        }
        super.onResume()
    }
    fun deleteRealm(){
        var realm = Realm.getDefaultInstance()
        realm?.executeTransaction {
            //it.deleteAll()
        }
        realm.close()
    }
    fun updateProgress(){

        val runnable= {
            var besar = progressHorizontal.progress

            progressHorizontal.progress = besar + 100
            if (besar == 50) {
                if (cekKoneksi(this)) {
                    updateProgress()
                } else {
                    koneksiInActive()
                }
            }else if(besar<100){
                updateProgress()
//                deleteRealm()
//                loadSarana()
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
    //    verifyStoragePermissions
    private fun verifyStoragePermissions(context: Context,activity: Activity):Boolean {
        val permission = ContextCompat.checkSelfPermission(context,
            Manifest.permission.READ_EXTERNAL_STORAGE)
        val permission1 = ContextCompat.checkSelfPermission(context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permission2 = ContextCompat.checkSelfPermission(context,
            Manifest.permission.READ_PHONE_STATE)
        val permission3 = ContextCompat.checkSelfPermission(context,
            Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("FaceId", "READ_EXTERNAL_STORAGE Permission to record denied")
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),11)
//            finish()
            return false
        }
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            Log.i("FaceId", "WRITE_EXTERNAL_STORAGE Permission to record denied")
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),12)
//            finish()
            return false
        }
        if (permission2 != PackageManager.PERMISSION_GRANTED) {
            Log.i("FaceId", "READ_PHONE_STATE Permission to record denied")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE),13)
//            finish()
        }
        if (permission3 != PackageManager.PERMISSION_GRANTED) {
            Log.i("FaceId", "READ_PHONE_STATE Permission to record denied")
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA),13)
//            finish()
            return false
        }
        return true
    }
    //    verifyStoragePermissions
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
                        listPenumpang()
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
    fun cekVersion(context: Context){
        val appUpdateManager = AppUpdateManagerFactory.create(context)

    // Returns an intent object that you use to check for an update.
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo

    // Checks that the platform will allow the specified type of update.
            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // For a flexible update, use AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                ) {
                    // Request the update.
                }
            }

    }
    fun cekKoneksi(context: Context):Boolean{
        val connectivityManager= context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        return networkInfo != null && networkInfo.isConnected
    }
    fun koneksiInActive(){
        AlertDialog.Builder(this)
            .setTitle("Maaf Koneksi Internet Tidak Ada!")
            .setPositiveButton("OK, Keluar", { dialog,
                                               which ->
                finish()
            }).show()
    }
//    fun alertVerson(){
//        AlertDialog.Builder(this)
//            .setTitle("Silahkan Update Versi Aplikasi Anda!")
//            .setPositiveButton("OK",{
//                    dialog,
//                    which ->
//                val url = "https://abpjobsite.com/api/android/app/download?app=abp_energy"
//                val i = Intent(Intent.ACTION_VIEW)
//                i.data = Uri.parse(url)
//                startActivity(i)
//            }).show()
//    }
    companion object {
        private  var TAG="TAG"
    }
}

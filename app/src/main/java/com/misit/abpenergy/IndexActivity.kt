package com.misit.abpenergy

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.navigation.NavigationView
import com.misit.abpenergy.Api.*
import com.misit.abpenergy.HazardReport.*
import com.misit.abpenergy.Inspeksi.*
import com.misit.abpenergy.Login.*
import com.misit.abpenergy.Monitoring_Produksi.*
import com.misit.abpenergy.Response.*
import com.misit.abpenergy.Rkb.*
import com.misit.abpenergy.Sarpras.*
import com.misit.abpenergy.Service.BarcodeScannerActivity
import com.misit.abpenergy.Utils.PrefsUtil
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_index.*
import java.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IndexActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener ,
    View.OnClickListener {

    //Variable
    lateinit var toolbar: Toolbar
    private var tipe:String? = null
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    var tabindex:String?=null
    var rule_user:String?=null
    private var userRule:Array<String>?=null
//    Variable
private val requestCodeCameraPermission = 1999
    lateinit var container: ShimmerFrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)
        title="PT Alamjaya Bara Pratama"

        val window: Window = this@IndexActivity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this@IndexActivity, R.color.colorPrimary)
//        Session
        if(ContextCompat.checkSelfPermission(this@IndexActivity,
                Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            askForCameraPermission()
        }
        PrefsUtil.initInstance(this)
        if(PrefsUtil.getInstance().getBooleanState("IS_LOGGED_IN",false)){
            USERNAME = PrefsUtil.getInstance().getStringState(PrefsUtil.USER_NAME,"")
            NAMA_LENGKAP = PrefsUtil.getInstance().getStringState(PrefsUtil.NAMA_LENGKAP,"")
            NIK = PrefsUtil.getInstance().getStringState(PrefsUtil.NIK,"")
            DEPARTMENT = PrefsUtil.getInstance().getStringState(PrefsUtil.DEPT,"")
            SECTON = PrefsUtil.getInstance().getStringState(PrefsUtil.SECTION,"")
            LEVEL = PrefsUtil.getInstance().getStringState(PrefsUtil.LEVEL,"")
            RULE = PrefsUtil.getInstance().getStringState(PrefsUtil.RULE,"")
            tvUserName.text = NAMA_LENGKAP
            if(PrefsUtil.getInstance().getBooleanState("PHOTO_PROFILE",false)){
                uploadProfile()
            }
        }else{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
//        Rule User
        userRule =RULE.split(",").toTypedArray()
        var apprSarpras = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Arrays.stream(userRule).anyMatch{ t -> t== "approve sarpras"}
        } else {
            userRule?.contains("approve sarpras")
        }
        if(apprSarpras!!){
            btnSarprasApproveKabag.visibility=View.VISIBLE
        }else{
            btnSarprasApproveKabag.visibility=View.GONE
        }
//        Rule User
        tabindex= intent.getStringExtra(Tab_INDEX)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)

        navView = findViewById(R.id.navigationView)
        var headerView = navView.getHeaderView(0)
        var tvUserText = headerView.findViewById(R.id.tvUser) as TextView
        tvUserText.text= NAMA_LENGKAP
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()
        supportActionBar?.setHomeButtonEnabled(true)
//        supportActionBar?.setHomeAsUpIndicator(R.drawable.abp_menu)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        LogOut Listener
        logOut.setOnClickListener {
            drawer_layout.closeDrawer(GravityCompat.START)
            logOut()
        }
//        LogOut Listener

        navView.setNavigationItemSelectedListener(this)

        btnTotal.setOnClickListener(this@IndexActivity)
        btnApprove.setOnClickListener(this@IndexActivity)
        btnWaiting.setOnClickListener(this@IndexActivity)
        btnCancel.setOnClickListener(this@IndexActivity)
        btnSarpras.setOnClickListener(this@IndexActivity)
        btnNewSarpras.setOnClickListener(this@IndexActivity)
        btnClose.setOnClickListener(this)
        btnSarprasApproveKabag.setOnClickListener(this)
        btnOB.setOnClickListener(this)
        btnHAULING.setOnClickListener(this)
        btnCRUSHING.setOnClickListener(this)
        btnBARGING.setOnClickListener(this)
        btnSTOCKROOM.setOnClickListener(this)
        btnSTOCKPRODUCT.setOnClickListener(this)
        btnHazard.setOnClickListener(this)
        btnNewHazard.setOnClickListener(this)
        btnNewSarana.setOnClickListener(this)
        content_frame.setOnClickListener(this)
        btnHazardALL.setOnClickListener(this)
        btnSarprasAll.setOnClickListener(this)
        btnInspection.setOnClickListener(this)
        btnQRCODES.setOnClickListener(this)
        cvBarcodeProfile.setOnClickListener(this)
    }

    private fun uploadProfile() {
        Toasty.info(this@IndexActivity,"Upload Photo").show()
    }

    private fun askForCameraPermission(){
        ActivityCompat.requestPermissions(this@IndexActivity,
            arrayOf(Manifest.permission.CAMERA),requestCodeCameraPermission)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
//        if(requestCode == requestCodeCameraPermission && grantResults.isNotEmpty()  && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//
//        }else{
//            Toasty.error(this@BarcodeScannerActivity,"Permission Denied!").show()
//        }
        when (requestCode) {
            requestCodeCameraPermission -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toasty.info(this@IndexActivity,"PERMISSION_GRANTED",Toasty.LENGTH_SHORT).show()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toasty.info(this@IndexActivity,"PERMISSION_DENIED",Toasty.LENGTH_SHORT).show()
                }
                return
            }
            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
                Toasty.info(this@IndexActivity,"PERMISSION_Ignore",Toasty.LENGTH_SHORT).show()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
//Get Token
    private fun getToken() {
        val apiEndPoint = ApiClient.getClient(this)!!.create(ApiEndPoint::class.java)
        val call = apiEndPoint.getDataUser(USERNAME)
        call?.enqueue(object : Callback<GetUserResponse> {
            override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                Toast.makeText(this@IndexActivity,"Error : $t", Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(
                call: Call<GetUserResponse>,
                response: Response<GetUserResponse>
            ) {
                var res = response.body()
                if(res!=null){
                     var r= res.dataUser!!
                    if(r.photoProfile!=null){
                        PrefsUtil.getInstance()
                            .setBooleanState("PHOTO_PROFILE",
                                true)
                    }else{
                        PrefsUtil.getInstance()
                            .setBooleanState("PHOTO_PROFILE",
                                false)
                    }
                    if(r.rule!=null){
                        RULE =r.rule!!
                        PrefsUtil.getInstance()
                            .setStringState(PrefsUtil.TOTAL_HAZARD_USER,
                                res!!.dataHazard!!.toString())
                        tvHazardUser.text = res!!.dataHazard!!.toString()
                        tvNIK.text = res!!.dataUser!!.nik.toString()
                        tvDept.text = res!!.dataUser!!.dept
                        tvSect.text = res!!.dataUser!!.sect
                        rvCompany.text = res!!.dataUser!!.namaPerusahaan
                        if(res!!.dataUser!!.perusahaan==0){
                            lnSaranaPrasarana.visibility = View.VISIBLE
                            lnRKBsystem.visibility = View.VISIBLE
                            btnNewSarana.visibility = View.VISIBLE
                        }else{
                            lnSaranaPrasarana.visibility = View.GONE
                            lnRKBsystem.visibility = View.GONE
                            btnNewSarana.visibility = View.GONE
                        }
                        tvInspeksiUser.text = res!!.datInspeksi!!.toString()
                            userRule =RULE.split(",").toTypedArray()
                        var apprSarpras = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Arrays.stream(userRule).anyMatch{ t -> t== "approve sarpras"}
                        } else {
                            userRule?.contains("approve sarpras")
                        }
                        if(apprSarpras!!){
                            btnSarprasApproveKabag.visibility=View.VISIBLE
                        }else{
                            btnSarprasApproveKabag.visibility=View.GONE
                        }
                        var security = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Arrays.stream(userRule).anyMatch{ t -> t== "security"}
                        } else {
                            userRule?.contains("security")
                        }
                        if(security!!){
                            btnSarprasAll.visibility=View.VISIBLE
                        }else{
                            btnSarprasAll.visibility=View.GONE
                        }
                        var allHazard = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Arrays.stream(userRule).anyMatch{ t -> t== "allHazard"}
                        } else {
                            userRule?.contains("allHazard")
                        }
                        if(allHazard!!){
                            btnHazardALL.visibility=View.VISIBLE
                        }else{
                            btnHazardALL.visibility=View.GONE
                        }
                        var allInspeksi = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Arrays.stream(userRule).anyMatch{ t -> t== "allInspeksi"}
                        } else {
                            userRule?.contains("allInspeksi")
                        }
                        if(allInspeksi!!){
                            btnInspectionALL.visibility=View.VISIBLE
                        }else{
                            btnInspectionALL.visibility=View.GONE
                        }
                        lnLoading.visibility=View.GONE
                    }
                }
            }
        })
    }
//    Get Token
    //Menu Item
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawer_layout.closeDrawer(GravityCompat.START)
        if(item.itemId==R.id.settingAdmin){
            Toasty.info(this@IndexActivity,"OKE").show()
        }
        return true
    }
//Menu Item
//    On Resume
    override fun onResume() {
    lnLoading.visibility=View.VISIBLE
//        Session
    getToken()
        tipe =  intent.getStringExtra(TIPE)
        if(tipe=="rkb"){
            rkbNotif("0")
            intent.putExtra(TIPE,"")
        }else if(tipe=="sarpras"){
            sarprasNotif()
            intent.putExtra(TIPE,"")
        }
        super.onResume()
    }
//On Resume
//    Sarpras Notif
    private fun sarprasNotif(){
        var intent = Intent(this@IndexActivity,
            SarprasActivity::class.java)
        intent.putExtra(RkbActivity.TIPE,"notif")
        startActivity(intent)
    }
//    Sarpras Notif
//    NotifRkb
    private fun rkbNotif(tabindex:String?){
        var intent = Intent(this@IndexActivity,RkbActivity::class.java)
        intent.putExtra(RkbActivity.USERNAME,USERNAME)
        intent.putExtra(RkbActivity.DEPARTMENT,DEPARTMENT)
        intent.putExtra(RkbActivity.SECTON,SECTON)
        intent.putExtra(RkbActivity.LEVEL,LEVEL)
        intent.putExtra(RkbActivity.Tab_INDEX,tabindex)
        intent.putExtra(RkbActivity.TIPE,"notif")
        startActivity(intent)
    }
//    NotifRkb
//    LogOut
    private fun logOut() {
        AlertDialog.Builder(this)
            .setTitle("Confirmation")
            .setPositiveButton("OK , Sign Out",{
                    dialog,
                    which ->
                if(PrefsUtil.getInstance().getBooleanState(
                        "IS_LOGGED_IN",true)){
                    PrefsUtil.getInstance().setBooleanState(
                        "IS_LOGGED_IN",false)
                    PrefsUtil.getInstance().setStringState(
                        PrefsUtil.USER_NAME,null)
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            })
            .setNegativeButton("Cancel",
                {
                        dialog,
                        which ->
                    dialog.dismiss()
                })
            .show()
    }
//LogOut
//    Companion
    companion object{
        var USERNAME = "username"
        var DEPARTMENT="department"
        var SECTON="section"
        var LEVEL="level"
        var Tab_INDEX ="tab_index"
        var NAMA_LENGKAP = "nama_lengkap"
        var NO_RKB = "NO_RKB"
        var TIPE = null
        var RULE = "RULE"
        var NIK = "NIK"
    }
//    Companion
//Click View
    override fun onClick(v: View?) {
        btnFLMenuIndex.collapse()
        if(v?.id==R.id.btnTotal){
                var intent = Intent(this@IndexActivity,RkbActivity::class.java)
                intent.putExtra(RkbActivity.USERNAME,USERNAME)
                intent.putExtra(RkbActivity.DEPARTMENT,DEPARTMENT)
                intent.putExtra(RkbActivity.SECTON,SECTON)
                intent.putExtra(RkbActivity.LEVEL,LEVEL)
                var tbindex = 0 as Int
                intent.putExtra(RkbActivity.Tab_INDEX,tbindex)
                startActivity(intent)
            }
        if(v?.id==R.id.btnApprove){
            var intent = Intent(this@IndexActivity,RkbActivity::class.java)
            intent.putExtra(RkbActivity.USERNAME,USERNAME)
            intent.putExtra(RkbActivity.DEPARTMENT,DEPARTMENT)
            intent.putExtra(RkbActivity.SECTON,SECTON)
            intent.putExtra(RkbActivity.LEVEL,LEVEL)
            var tbindex = 1 as Int
            intent.putExtra(RkbActivity.Tab_INDEX,tbindex)
            startActivity(intent)
        }
        if(v?.id==R.id.btnWaiting){
            var intent = Intent(this@IndexActivity,RkbActivity::class.java)
            intent.putExtra(RkbActivity.USERNAME,USERNAME)
            intent.putExtra(RkbActivity.DEPARTMENT,DEPARTMENT)
            intent.putExtra(RkbActivity.SECTON,SECTON)
            intent.putExtra(RkbActivity.LEVEL,LEVEL)
            var tbindex = 2 as Int
            intent.putExtra(RkbActivity.Tab_INDEX,tbindex)
            startActivity(intent)
        }
        if(v?.id==R.id.btnCancel){
            var intent = Intent(this@IndexActivity,RkbActivity::class.java)
            intent.putExtra(RkbActivity.USERNAME,USERNAME)
            intent.putExtra(RkbActivity.DEPARTMENT,DEPARTMENT)
            intent.putExtra(RkbActivity.SECTON,SECTON)
            intent.putExtra(RkbActivity.LEVEL,LEVEL)
            var tbindex = 3 as Int
            intent.putExtra(RkbActivity.Tab_INDEX,tbindex)
            startActivity(intent)
        }
        if(v?.id==R.id.btnClose){
            var intent = Intent(this@IndexActivity,RkbActivity::class.java)
            intent.putExtra(RkbActivity.USERNAME,USERNAME)
            intent.putExtra(RkbActivity.DEPARTMENT,DEPARTMENT)
            intent.putExtra(RkbActivity.SECTON,SECTON)
            intent.putExtra(RkbActivity.LEVEL,LEVEL)
            var tbindex = 4 as Int
            intent.putExtra(RkbActivity.Tab_INDEX,tbindex)
            startActivity(intent)
        }
        if(v?.id==R.id.btnSarpras){
           var intent = Intent(this@IndexActivity,
                SarprasActivity::class.java)
            startActivity(intent)
        }
        if(v?.id==R.id.btnNewSarpras){
            var intent = Intent(this@IndexActivity,NewSarprasActivity::class.java)
            startActivity(intent)
        }
        if(v?.id==R.id.btnSarprasApproveKabag){
            var intent = Intent(this@IndexActivity,KabagApprSarprasActivity::class.java)
            startActivity(intent)
        }
        if(v?.id==R.id.btnOB){
            var intent = Intent(this@IndexActivity,ProductionActivity::class.java)
            intent.putExtra(ProductionActivity.MONITORING,"OB")
            startActivity(intent)
        }
        if(v?.id==R.id.btnHAULING){
            var  intent = Intent(this@IndexActivity,ProductionActivity::class.java)
            intent.putExtra(ProductionActivity.MONITORING,"HAULING")
            startActivity(intent)
        }
        if(v?.id==R.id.btnCRUSHING){
            var  intent = Intent(this@IndexActivity,ProductionActivity::class.java)
            intent.putExtra(ProductionActivity.MONITORING,"CRUSHING")
            startActivity(intent)
        }
        if(v?.id==R.id.btnBARGING){
            var intent = Intent(this@IndexActivity,ProductionActivity::class.java)
            intent.putExtra(ProductionActivity.MONITORING,"BARGING")
            startActivity(intent)
        }
        if(v?.id==R.id.btnSTOCKROOM){
            var intent = Intent(this@IndexActivity,StockActivity::class.java)
            intent.putExtra(ProductionActivity.MONITORING,"ROOM")
            startActivity(intent)
        }
        if(v?.id==R.id.btnSTOCKPRODUCT){
        var intent = Intent(this@IndexActivity,StockActivity::class.java)
        intent.putExtra(ProductionActivity.MONITORING,"PRODUCT")
        startActivity(intent)
        }
        if(v?.id==R.id.btnHazard){
            hazardReport()
        }
        if(v!!.id==R.id.btnNewSarana){
            newSarana()
        }
        if(v!!.id==R.id.btnNewHazard){
            newHazardReport()
        }
        if(v?.id==R.id.content_frame){
            btnFLMenuIndex.collapse()
        }
        if(v?.id==R.id.btnHazardALL){
            var intent = Intent(this@IndexActivity, ALLHazardReportActivity::class.java)
            startActivity(intent)
        }
        if(v?.id==R.id.btnSarprasAll){
            var intent = Intent(this@IndexActivity, AllSarprasActivity::class.java)
            startActivity(intent)
        }
        if(v?.id==R.id.btnInspection){
            var intent = Intent(this@IndexActivity, InspeksiActivity::class.java)
            startActivity(intent)
        }
        if(v?.id==R.id.btnInspectionALL){
            var intent = Intent(this@IndexActivity, AllInspeksiActivity::class.java)
            startActivity(intent)
        }
        if(v?.id==R.id.btnQRCODES){
            var intent = Intent(this@IndexActivity, BarcodeScannerActivity::class.java)
            intent.putExtra("aktivitas","addTeam")
            startActivity(intent)
        }
        if(v?.id==R.id.cvBarcodeProfile){
            val intent = Intent(this@IndexActivity,QRCodeActivity::class.java)
            intent.putExtra("itemCodes",NIK)
            intent.putExtra("judul","QR-Code Anda")
            startActivity(intent)
        }
    }
//onClick
//    onActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode== Activity.RESULT_OK && requestCode==101){
            hazardReport()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
//    onActivityResult
//    HazardRepord
    fun hazardReport() {
        var intent = Intent(this@IndexActivity,HazardReportActivity::class.java)
        startActivity(intent)
    }
    fun newSarana(){
        btnFLMenuIndex.collapse()
        var intent = Intent(this@IndexActivity, NewSarprasActivity::class.java)
        startActivityForResult(intent,102)
    }
    fun newHazardReport() {
        btnFLMenuIndex.collapse()
        var intent = Intent(this@IndexActivity, NewHazardActivity::class.java)
        startActivityForResult(intent,101)
    }
}
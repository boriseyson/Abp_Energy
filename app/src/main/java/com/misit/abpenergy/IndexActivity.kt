package com.misit.abpenergy

import android.app.Activity
import android.content.Intent
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
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.misit.abpenergy.Api.ApiClient
import com.misit.abpenergy.Api.ApiEndPoint
import com.misit.abpenergy.HazardReport.HazardReportActivity
import com.misit.abpenergy.HazardReport.NewHazardActivity
import com.misit.abpenergy.Login.LoginActivity
import com.misit.abpenergy.Monitoring_Produksi.ProductionActivity
import com.misit.abpenergy.Monitoring_Produksi.StockActivity
import com.misit.abpenergy.Response.GetUserResponse
import com.misit.abpenergy.Rkb.RkbActivity
import com.misit.abpenergy.Sarpras.KabagApprSarprasActivity
import com.misit.abpenergy.Sarpras.NewSarprasActivity
import com.misit.abpenergy.Sarpras.SarprasActivity
import com.misit.abpenergy.Utils.PrefsUtil
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_index.*
import kotlinx.android.synthetic.main.activity_new_hazard.*
import java.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IndexActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener ,
    View.OnClickListener {

    //Variable
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    lateinit var toolbar: Toolbar
    private var tipe:String? = null
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    var tabindex:String?=null
    var rule_user:String?=null
    private var userRule:Array<String>?=null
//    Variable
    lateinit var container: ShimmerFrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        title="PT Alamjaya Bara Pratama"

        val window: Window = this@IndexActivity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this@IndexActivity, R.color.colorPrimary)
//        Session
        PrefsUtil.initInstance(this)
        if(PrefsUtil.getInstance().getBooleanState("IS_LOGGED_IN",false)){
            USERNAME = PrefsUtil.getInstance().getStringState(PrefsUtil.USER_NAME,"")
            NAMA_LENGKAP = PrefsUtil.getInstance().getStringState(PrefsUtil.NAMA_LENGKAP,"")
            DEPARTMENT = PrefsUtil.getInstance().getStringState(PrefsUtil.DEPT,"")
            SECTON = PrefsUtil.getInstance().getStringState(PrefsUtil.SECTION,"")
            LEVEL = PrefsUtil.getInstance().getStringState(PrefsUtil.LEVEL,"")
            RULE = PrefsUtil.getInstance().getStringState(PrefsUtil.RULE,"")
        }else{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
//        Session
        getToken()
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
                var r = response.body()
                if(r!=null){
                    if(r.rule!=null){
                        RULE =r.rule!!
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

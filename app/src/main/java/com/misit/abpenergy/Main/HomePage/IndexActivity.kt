package com.misit.abpenergy.Main.HomePageimport android.app.Activityimport android.app.job.JobSchedulerimport android.content.*import android.graphics.Colorimport android.graphics.drawable.ColorDrawableimport android.os.Buildimport androidx.appcompat.app.AppCompatActivityimport android.os.Bundleimport android.util.Logimport android.view.LayoutInflaterimport android.view.Viewimport android.view.ViewGroupimport android.widget.ImageViewimport android.widget.LinearLayoutimport android.widget.TextViewimport androidx.appcompat.app.AlertDialogimport androidx.core.content.ContextCompatimport androidx.lifecycle.Observerimport androidx.lifecycle.ViewModelProviderimport androidx.localbroadcastmanager.content.LocalBroadcastManagerimport androidx.work.*import com.bumptech.glide.Glideimport com.google.android.material.bottomsheet.BottomSheetDialogimport com.google.android.play.core.appupdate.AppUpdateManagerimport com.google.android.play.core.appupdate.AppUpdateManagerFactoryimport com.google.android.play.core.install.model.AppUpdateTypeimport com.google.android.play.core.install.model.UpdateAvailabilityimport com.misit.abpenergy.Barcode.QRCodeActivityimport com.misit.abpenergy.HazardReport.Activity.HazardHSEActivityimport com.misit.abpenergy.HazardReport.Activity.HazardReportActivityimport com.misit.abpenergy.HazardReport.Activity.HazardSayaActivityimport com.misit.abpenergy.HazardReport.Activity.NewHazardActivityimport com.misit.abpenergy.Inspeksi.AllInspeksiActivityimport com.misit.abpenergy.Inspeksi.InspeksiUserActivityimport com.misit.abpenergy.Login.FotoProfileActivityimport com.misit.abpenergy.Login.LoginActivityimport com.misit.abpenergy.Main.Master.ListUserActivityimport com.misit.abpenergy.Main.Model.DataUsersModelimport com.misit.abpenergy.Monitoring_Produksi.ProductionActivityimport com.misit.abpenergy.Monitoring_Produksi.StockActivityimport com.misit.abpenergy.Rimport com.misit.abpenergy.Rkb.RkbActivityimport com.misit.abpenergy.Sarpras.Activity.AllSarprasActivityimport com.misit.abpenergy.Sarpras.Activity.KabagApprSarprasActivityimport com.misit.abpenergy.Sarpras.Activity.NewSarprasActivityimport com.misit.abpenergy.Sarpras.Activity.SarprasActivityimport com.misit.abpenergy.Service.ConnectionServiceimport com.misit.abpenergy.Service.HazardWorkingimport com.misit.abpenergy.Service.InitServiceimport com.misit.abpenergy.Utils.ConfigUtilimport com.misit.abpenergy.Utils.ConnectionLiveDataimport com.misit.abpenergy.Utils.Constantsimport com.misit.abpenergy.Utils.PrefsUtilimport com.misit.abpenergy.Main.ViewModel.IndexViewModelimport es.dmoral.toasty.Toastyimport kotlinx.android.synthetic.main.index_new.*import kotlinx.android.synthetic.main.menu_option.view.*import java.io.Fileimport java.util.*import java.util.concurrent.ExecutionExceptionimport java.util.concurrent.TimeUnitclass IndexActivity : AppCompatActivity() ,View.OnClickListener{    private var shimmerLayout: View?=null    private var shimmerSarana:View?=null    private var userRule:Array<String>?=null    var fotoURL:String?=null    var bottomSheetView:View?=null    lateinit var connectionService:Intent    var tokenPassingReceiver : BroadcastReceiver?=null    private lateinit var cld:ConnectionLiveData    private var scheduler: JobScheduler?=null    lateinit var viewModel:IndexViewModel    private var tipe:String? = null    private var uid:String?=null    var workManager:WorkManager?=null    private var updateApp : AppUpdateManager?=null    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        setContentView(R.layout.index_new)        supportActionBar?.hide()        title= "PT ALAMJAYA BARA PRATAMA"        connectionService = Intent(this@IndexActivity, ConnectionService::class.java)        scheduler = this@IndexActivity.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler        reciever()        viewModel = ViewModelProvider(this@IndexActivity).get(IndexViewModel::class.java)        initIndex(this,this)//        myWork()        updateApp = AppUpdateManagerFactory.create(this)        checkUpdate()    }    fun checkUpdate(){        if(updateApp!=null){            updateApp!!.appUpdateInfo!!.addOnSuccessListener { updateInfo ->                Log.d("checkUpdate","Run")                if(updateInfo.updateAvailability()== UpdateAvailability.UPDATE_AVAILABLE                    && updateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){                    updateApp!!.startUpdateFlowForResult(updateInfo,                        AppUpdateType.IMMEDIATE,this,Constants.APP_IN_UPDATE)                }else{                }            }        }else{            Log.d("checkUpdate","$updateApp")        }    }    private fun simpleWork() {        workManager = WorkManager.getInstance(this@IndexActivity)        val mRequest :WorkRequest = OneTimeWorkRequestBuilder<HazardWorking>()                                    .build()        workManager?.enqueue(mRequest)    }    private fun myWork(){        Log.d("Createing","WorkManager")        workManager = WorkManager.getInstance(this@IndexActivity)        val constraint = Constraints.Builder()                        .setRequiredNetworkType(NetworkType.UNMETERED)                        .setRequiresCharging(false)                        .setRequiresBatteryNotLow(false)                        .build()        val myRequest = PeriodicWorkRequest.Builder(                            HazardWorking::class.java,                            15,TimeUnit.MINUTES                        ).setConstraints(constraint)            .build()        workManager?.enqueueUniquePeriodicWork(Constants.HazardOffline,                                                ExistingPeriodicWorkPolicy.KEEP,                                                myRequest)    }    private fun cancelWorker(workName:String){        workManager?.cancelUniqueWork(workName)    }    private fun checkWork(workName:String):Boolean{            val isWork =   workManager?.getWorkInfosForUniqueWork(workName)        try {            var running = false            var workInfoList = isWork?.get()            if (workInfoList != null) {                for (workInfo in workInfoList){                    var state = workInfo.state                    running = state == WorkInfo.State.RUNNING || state == WorkInfo.State.ENQUEUED                }            }            return running        }catch (e:ExecutionException){            return false        }catch (e:InterruptedException){            return false        }    }    override fun onResume() {        startService(connectionService)        Log.d("ConnectionCheck","Activity Resume")        tipe =  intent.getStringExtra(TIPE)        uid =  intent.getStringExtra("UID")        if(tipe=="rkb"){            ConfigUtil.rkbNotif("0",this@IndexActivity,uid)            intent.putExtra(TIPE, "")        }else if(tipe=="sarpras"){            ConfigUtil.sarprasNotif(this@IndexActivity)            intent.putExtra(TIPE, "")        }        LocalBroadcastManager.getInstance(this@IndexActivity).registerReceiver(tokenPassingReceiver!!, IntentFilter("com.misit.abpenergy"))        super.onResume()    }    override fun onPause() {        Log.d("ConnectionCheck","Activity Pause")        super.onPause()    }    override fun onStop() {        Log.d("ConnectionCheck","Activity Stop")        super.onStop()    }    override fun onDestroy() {        Log.d("ConnectionCheck","Activity Destroy")        stopService(connectionService)        super.onDestroy()    }    private fun btnListener(click:View.OnClickListener){        fourth_menu_item.setOnClickListener(click)        first_menu_item.setOnClickListener(click)        refreshConnection.setOnClickListener(click)        btnTotal.setOnClickListener(click)        btnApprove.setOnClickListener(click)        btnWaiting.setOnClickListener(click)        btnCancel.setOnClickListener(click)        btnSarpras.setOnClickListener(click)        btnOB.setOnClickListener(click)        btnHAULING.setOnClickListener(click)        btnCRUSHING.setOnClickListener(click)        btnBARGING.setOnClickListener(click)        btnSTOCKROOM.setOnClickListener(click)        btnSTOCKPRODUCT.setOnClickListener(click)        btnHazard.setOnClickListener(click)        btnInspection.setOnClickListener(click)        btnQRCODES.setOnClickListener(click)        btnMenuTop.setOnClickListener(click)        first_menu_item.setOnClickListener(click)        second_menu_item.setOnClickListener(click)        third_menu_item.setOnClickListener(click)        fourth_menu_item.setOnClickListener(click)        fabIC.setOnClickListener(click)        btnSarprasApproveKabag.setOnClickListener(click)        btnNewSarpras.setOnClickListener(click)    }    override fun onClick(v: View?) {        val c = this@IndexActivity        btnFLMenuIndex.collapse()        if(v?.id==R.id.fabIC){            bottomDialog(c)        }        if(v?.id==R.id.fourth_menu_item){            profile(this@IndexActivity)        }        if(v?.id==R.id.refreshConnection){            checkNetworkConnection()        }        if(v?.id==R.id.cvBarcodeProfile || v!!.id==R.id.third_menu_item){            val intent = Intent(c, QRCodeActivity::class.java)            intent.putExtra("itemCodes", NIK)            intent.putExtra("judul", "QR-Code Anda")            startActivity(intent)        }        menuCorrectiveAcction(v,c)        menuSarpras(v,c)        menuRKB(v,c)        menuMonitoring(v,c)    }    fun showDialogOptionInspeksi(c:Context, title: Array<String>){        val alertDialog = AlertDialog.Builder(c)        alertDialog.setTitle("Silahkan Pilih")        alertDialog!!.setItems(title, { dialog, which ->            when (which) {                0 ->inspeksiReport(c,"inspeksi")                1 ->inspeksiReport(c,"inspeksi_hse")            }        })        alertDialog.setOnDismissListener {            first_menu_item.setColorFilter(ContextCompat.getColor(c, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN)        }        alertDialog.create()        alertDialog.show()    }    //Inpeksi    fun inspeksiReport(c:Context,pilih:String) {    var intent:Intent?=null    when(pilih){        "inspeksi" -> {            intent = Intent(c, InspeksiUserActivity::class.java)        }        "inspeksi_hse" -> {            intent = Intent(c, AllInspeksiActivity::class.java)        }    }    startActivity(intent)}//    Inspeksi    fun showDialogOption(c:Context, title: Array<String>){        val alertDialog = AlertDialog.Builder(c)        alertDialog.setTitle("Silahkan Pilih")        alertDialog!!.setItems(title, DialogInterface.OnClickListener{ dialog, which ->            when (which) {                0 ->hazardReport(c,"hazard")                1 ->hazardReport(c,"hazard_saya")                2 ->hazardReport(c,"hazard_hse")            }        })        alertDialog.setOnDismissListener {            first_menu_item.setColorFilter(ContextCompat.getColor(c, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN)        }        alertDialog.create()        alertDialog.show()    }    //    HazardRepord    fun hazardReport(c:Context,pilih:String) {        var intent:Intent?=null        when(pilih){            "hazard" -> {                intent = Intent(c, HazardReportActivity::class.java)                first_menu_item.setColorFilter(ContextCompat.getColor(c, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN)            }            "hazard_saya" -> {                intent = Intent(c, HazardSayaActivity::class.java)                first_menu_item.setColorFilter(ContextCompat.getColor(c, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN)            }            "hazard_hse" -> {                intent = Intent(c, HazardHSEActivity::class.java)                first_menu_item.setColorFilter(ContextCompat.getColor(c, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN)            }        }        startActivity(intent)    }    fun newSarana(){        btnFLMenuIndex.collapse()        var intent = Intent(this@IndexActivity, NewSarprasActivity::class.java)        startActivityForResult(intent, 102)    }    fun newHazardReport() {        btnFLMenuIndex.collapse()        var intent = Intent(this@IndexActivity, NewHazardActivity::class.java)        startActivityForResult(intent, 101)    }    //    Companion    companion object{        var USERNAME = "username"        var DEPARTMENT="department"        var SECTON="section"        var LEVEL="level"        var Tab_INDEX ="tab_index"        var NAMA_LENGKAP = "nama_lengkap"        var NO_RKB = "NO_RKB"        var TIPE = null        var RULE = "RULE"        var NIK = "NIK"        var COMPANY = "COMPANY"        var PHOTO_PROFILE=false        var INTRO_APP=false    }    //    Companion    private fun profile(c:Context){        var  mDialogView = LayoutInflater.from(c).inflate(R.layout.menu_option,null)        val nama = mDialogView?.findViewById<View>(R.id.mnNama) as TextView        val nik = mDialogView?.findViewById<View>(R.id.mnNik) as TextView        nama.text = NAMA_LENGKAP        nik.text = NIK        val mnFoto = mDialogView?.findViewById<View>(R.id.mnFoto) as ImageView        Glide.with(c).load(fotoURL).into(mnFoto)        val mBuilder = AlertDialog.Builder(c)        mBuilder.setView(mDialogView)        val dialog =mBuilder.show()        userRule = RULE.split(",").toTypedArray()        var apprSarpras = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {            Arrays.stream(userRule).anyMatch { t -> t == "master perusahaan" }        } else {            userRule?.contains("master perusahaan")        }        if (apprSarpras!!) {            mDialogView?.btnMasterPerusahaan!!.visibility = View.VISIBLE        }else{            mDialogView?.btnMasterPerusahaan!!.visibility = View.GONE        }        val administrator= if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {            Arrays.stream(userRule).anyMatch { t -> t == "administrator" }        } else {            userRule?.contains("administrator")        }        if (administrator!!) {            mDialogView?.btnListUser!!.visibility = View.VISIBLE        }else{            mDialogView?.btnListUser!!.visibility = View.GONE        }        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))        mDialogView?.btnListUser!!.setOnClickListener {            val intent = Intent(c, ListUserActivity::class.java)            intent.putExtra(ListUserActivity.DataExtra,"Index")            startActivity(intent)            dialog.dismiss()        }        mDialogView?.dialogDismis!!.setOnClickListener {            dialog.dismiss()            if(mDialogView!=null){                var parent = mDialogView?.parent as ViewGroup                if (parent!=null){                    parent.removeView(mDialogView)                }            }        }        mDialogView?.btnChangePWD!!.setOnClickListener {            ConfigUtil.changePassword(c, USERNAME)            dialog.dismiss()        }        mDialogView?.btnKeluarApp!!.setOnClickListener{            ConfigUtil.logOut(this@IndexActivity)            dialog.dismiss()        }        mDialogView?.btnUpdateData!!.setOnClickListener {            dialog.dismiss()        }        mDialogView?.btnUploadFoto!!.setOnClickListener {            uploadProfile()            dialog.dismiss()        }        mDialogView?.btnMasterPerusahaan!!.setOnClickListener {            ConfigUtil.masterPerusahaan(c)            dialog.dismiss()        }    }    private fun uploadProfile(){        val intent = Intent(this@IndexActivity, FotoProfileActivity::class.java)        intent.putExtra("fotoURL",fotoURL)        startActivityForResult(intent,1234)    }    private fun initIndex(c:Context,a:Activity) {        PrefsUtil.initInstance(c)        if(PrefsUtil.getInstance().getBooleanState("IS_LOGGED_IN", false)){            USERNAME = PrefsUtil.getInstance().getStringState(PrefsUtil.USER_NAME, "")            NAMA_LENGKAP = PrefsUtil.getInstance().getStringState(PrefsUtil.NAMA_LENGKAP, "")            NIK = PrefsUtil.getInstance().getStringState(PrefsUtil.NIK, "")            DEPARTMENT = PrefsUtil.getInstance().getStringState(PrefsUtil.DEPT, "")            SECTON = PrefsUtil.getInstance().getStringState(PrefsUtil.SECTION, "")            LEVEL = PrefsUtil.getInstance().getStringState(PrefsUtil.LEVEL, "")            RULE = PrefsUtil.getInstance().getStringState(PrefsUtil.RULE, "")            PHOTO_PROFILE = PrefsUtil.getInstance().getBooleanState(PrefsUtil.PHOTO_PROFILE,false)            INTRO_APP = PrefsUtil.getInstance().getBooleanState("INTRO_APP",false)            COMPANY = PrefsUtil.getInstance().getStringState("COMPANY_NAME","")            tvUserName.text = NAMA_LENGKAP            if(!PrefsUtil.getInstance().getBooleanState(PrefsUtil.PHOTO_PROFILE,false)){                uploadProfile()            }            indexViewModel()            if(!PrefsUtil.getInstance().getBooleanState("StartScheduler",false)){                startScheduler()            }else{                if(!ConfigUtil.isJobServiceOn(c,Constants.JOB_SERVICE_ID)){                    ConfigUtil.jobScheduler(c,scheduler)                }            }        }else{            val intent = Intent(this, LoginActivity::class.java)            ConfigUtil.stopJobScheduler(scheduler)            startActivity(intent)            finish()        }        checkNetworkConnection()        shimmerLayout = findViewById(R.id.shimmerLayout)        shimmerSarana = findViewById(R.id.shimmerSarana)        bottomSheetView = LayoutInflater.from(c).inflate(R.layout.bottom_sheet_dialog,findViewById<LinearLayout>(R.id.bottomSheet))        btnListener(this@IndexActivity)    }    private fun indexViewModel() {        viewModel.getUserObserver().observe(this@IndexActivity,Observer{            if(it!=null){                shimmerToggle(false)//                viewVisible(true)                Log.d("HazardUser","${it.dataHazard}")                setHeader("${it.nama_lengkap}","${it.nik}","${it.sect}","${it.dept}","${it.nama_perusahaan}","${it.offline_profile}",it.perusahaan!!)            }else{                shimmerToggle(true)//                viewVisible(false)                viewModel.loadUser(this@IndexActivity, NIK)            }            validate(it)            Log.d("ConnectionCheck","${it}")        })        viewModel.hazardObserver().observe(this@IndexActivity, Observer {            setHazard(it)        })        viewModel.inspeksiObserver().observe(this@IndexActivity, Observer {            setInspeksi(it)        })        viewModel.loadUser(this@IndexActivity,NIK)        var hazardUser = PrefsUtil.getInstance().getNumberState("dataHazard",0)        var inspeksi = PrefsUtil.getInstance().getNumberState("dataInspeksi",0)        viewModel.CARuser(hazardUser,inspeksi)    }    private fun validate(it:DataUsersModel?){        userRule = it?.rule?.split(",")?.toTypedArray()        if(userRule!=null){            var apprSarpras = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {                Arrays.stream(userRule).anyMatch { t -> t == "approve sarpras" }            } else {                userRule?.contains("approve sarpras")            } ?:false            if (apprSarpras) {                btnSarprasApproveKabag.visibility = View.VISIBLE            } else {                btnSarprasApproveKabag.visibility = View.GONE            }            var security = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {                Arrays.stream(userRule).anyMatch { t -> t == "security" }            } else {                userRule?.contains("security")            } ?:false            if (security) {                btnSarprasAll.visibility = View.VISIBLE            } else {                btnSarprasAll.visibility = View.GONE            }            var allHazard = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {                Arrays.stream(userRule).anyMatch { t -> t == "allHazard" }            } else {                userRule?.contains("allHazard")            } ?:false            if (allHazard) {                btnHazardALL.visibility = View.VISIBLE            } else {                btnHazardALL.visibility = View.GONE            }            var allInspeksi = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {                Arrays.stream(userRule).anyMatch { t -> t == "allInspeksi" }            } else {                userRule?.contains("allInspeksi")            } ?:false            if (allInspeksi) {                btnInspectionALL.visibility = View.VISIBLE            } else {                btnInspectionALL.visibility = View.GONE            }        }else{            btnSarprasApproveKabag.visibility = View.GONE            btnSarprasAll.visibility = View.GONE            btnHazardALL.visibility = View.GONE            btnInspectionALL.visibility = View.GONE        }    }    private fun shimmerToggle(state:Boolean){        if(state){            shimmerLayout?.visibility = View.VISIBLE            shimmerSarana?.visibility = View.VISIBLE        }else{            shimmerLayout?.visibility = View.GONE            shimmerSarana?.visibility = View.GONE        }    }    private fun setHazard(hazard: Int){        tvHazardUser.text = "${hazard}"    }    private fun setInspeksi(inspeksi: Int){        tvInspeksiUser.text = "${inspeksi}"    }    private fun setHeader(namaLengkap:String, nik:String, section:String, dept:String, company:String, foto:String,perusahaan:Int) {        tvUserName.text = namaLengkap        tvNIK.text = nik        tvSect.text = section        tvDept.text = dept        rvCompany.text = company        PrefsUtil.getInstance().setStringState("COMPANY_NAME",company)        if (perusahaan == 0) {            lnSaranaPrasarana.visibility = View.VISIBLE            lnRKBsystem.visibility = View.VISIBLE            lnMonitoringProduksi.visibility =View.VISIBLE            btnNewSarana.visibility = View.VISIBLE        } else {            lnSaranaPrasarana.visibility = View.GONE            lnRKBsystem.visibility = View.GONE            btnNewSarana.visibility = View.GONE            lnMonitoringProduksi.visibility =View.GONE        }            try {                val dir = getExternalFilesDir("PROFILE_IMAGE")                val file = File(dir,foto)                Log.d("bitmapImage", "${file}")                Glide.with(this@IndexActivity).load(file).into(fotoProfileA)                fotoURL = file.absolutePath            }catch (e:Exception){                Log.d("fotoURL","${e.message}")            }            Log.d("fotoURL", "${fotoURL}")    }    private fun reciever() {        tokenPassingReceiver = object : BroadcastReceiver() {            override fun onReceive(context: Context, intent: Intent) {                val bundle = intent.extras                if (bundle != null) {                    if (bundle.containsKey("fgUser")) {                        val tokenData = bundle.getString("fgUser")                        if(tokenData=="fgDone"){                            ConfigUtil.startStopService(InitService::class.java,context, USERNAME,tokenPassingReceiver!!)                            stopService(connectionService)                            LocalBroadcastManager.getInstance(this@IndexActivity).unregisterReceiver(tokenPassingReceiver!!)                            Log.d("ServiceName", tokenData)                            unreachableNetwork(false)                        }                    }                    if (bundle.containsKey("bsConnection")) {                        val tokenData = bundle.getString("bsConnection")                        Log.d("ServiceName","${tokenData} Index")                        if(tokenData=="Online"){                            internetConnection.visibility= View.GONE                            ConfigUtil.startStopService(InitService::class.java,context, USERNAME,tokenPassingReceiver!!)                            Log.d("ConnectionCheck",tokenData)                            unreachableNetwork(false)                        }else if(tokenData=="Offline"){                            Log.d("ConnectionCheck",tokenData)                            Toasty.error(this@IndexActivity,"No Internet Connection").show()                            stopService(connectionService)                            unreachableNetwork(true)//                            viewVisible(false)                        }else if(tokenData=="Disabled"){                                  Log.d("ConnectionCheck",tokenData)                                      stopService(connectionService)                            viewModel.loadUser(context,NIK)                            unreachableNetwork(true)//                            viewVisible(false)                            Toasty.error(this@IndexActivity,"Network Disabled").show()                        }                    }                }            }        }    }    private fun unreachableNetwork(status:Boolean){        if(status){            rvTop.visibility = View.VISIBLE            bottomSheetView?.findViewById<View>(R.id.btnNewSarprasMenu)?.visibility = View.GONE            lnRKBsystem.visibility = View.GONE            lnHSEsystem.visibility = View.VISIBLE            lnMonitoringProduksi.visibility = View.GONE            lnSaranaPrasarana.visibility = View.GONE            internetConnection.visibility= View.VISIBLE            second_menu_item.isEnabled = false            btnSarprasApproveKabag.isEnabled = false            btnSarpras.isEnabled = false            btnNewSarana.isEnabled = false            btnTotal.isEnabled = false            btnApprove.isEnabled = false            btnWaiting.isEnabled = false            btnClose.isEnabled = false            btnCancel.isEnabled = false            btnNewSarpras.isEnabled = false            btnOB.isEnabled = false            btnHAULING.isEnabled = false            btnCRUSHING.isEnabled = false            btnBARGING.isEnabled = false            btnSTOCKPRODUCT.isEnabled = false            btnSTOCKROOM.isEnabled = false            fourth_menu_item.isEnabled = false        }else{            bottomSheetView?.findViewById<View>(R.id.btnNewSarprasMenu)?.visibility = View.VISIBLE            rvTop.visibility = View.VISIBLE            lnHSEsystem.visibility = View.VISIBLE            lnRKBsystem.visibility = View.VISIBLE            lnMonitoringProduksi.visibility = View.VISIBLE            lnSaranaPrasarana.visibility = View.VISIBLE            internetConnection.visibility= View.GONE            second_menu_item.isEnabled = true            second_menu_item.isEnabled = true            btnSarprasApproveKabag.isEnabled = true            btnSarpras.isEnabled = true            btnNewSarana.isEnabled = true            btnTotal.isEnabled = true            btnApprove.isEnabled = true            btnWaiting.isEnabled = true            btnClose.isEnabled = true            btnCancel.isEnabled = true            btnNewSarpras.isEnabled = true            btnOB.isEnabled = true            btnHAULING.isEnabled = true            btnCRUSHING.isEnabled = true            btnBARGING.isEnabled = true            btnSTOCKPRODUCT.isEnabled = true            btnSTOCKROOM.isEnabled = true            fourth_menu_item.isEnabled = true        }    }    private fun checkNetworkConnection() {        cld = ConnectionLiveData(application)        cld.observe(this@IndexActivity, Observer{ isConnected->            if (isConnected){                startService(connectionService)                Log.d("ConnectionCheck",isConnected.toString())                internetConnection.visibility = View.GONE            }else{                if(ConfigUtil.isMyServiceRunning(InitService::class.java, this@IndexActivity)){                    LocalBroadcastManager.getInstance(this@IndexActivity).unregisterReceiver(tokenPassingReceiver!!)                    var intent = Intent(this@IndexActivity, InitService::class.java).apply {                        this.action = Constants.SERVICE_STOP                    }                    stopService(intent)                    Log.d("ServiceName", "${InitService::class.java} Stop")                }else{                    startService(connectionService)                }                unreachableNetwork(true)                internetConnection.visibility= View.VISIBLE                Log.d("ConnectionCheck",isConnected.toString())            }        })    }    private fun startScheduler(){        val builder = AlertDialog.Builder(this)        builder.setTitle("Selamat Datang")        builder.setMessage("Terima Kasih Sudah Menggunakan Aplikasi Ini!")        builder.setPositiveButton(android.R.string.yes) { dialog, which ->            PrefsUtil.getInstance().setBooleanState("StartScheduler",true)            ConfigUtil.jobScheduler(this@IndexActivity,scheduler)        }        builder.setCancelable(false)        builder.show()    }    private fun menuMonitoring(v:View?,c:Context){        if(v?.id==R.id.btnOB){            var intent = Intent(c, ProductionActivity::class.java)            intent.putExtra(ProductionActivity.MONITORING, "OB")            startActivity(intent)        }        if(v?.id==R.id.btnHAULING){            var  intent = Intent(c, ProductionActivity::class.java)            intent.putExtra(ProductionActivity.MONITORING, "HAULING")            startActivity(intent)        }        if(v ?.id==R.id.btnCRUSHING){            var  intent = Intent(c, ProductionActivity::class.java)            intent.putExtra(ProductionActivity.MONITORING, "CRUSHING")            startActivity(intent)        }        if(v?.id==R.id.btnBARGING){            var intent = Intent(c, ProductionActivity::class.java)            intent.putExtra(ProductionActivity.MONITORING, "BARGING")            startActivity(intent)        }        if(v?.id==R.id.btnSTOCKROOM){            var intent = Intent(c, StockActivity::class.java)            intent.putExtra(ProductionActivity.MONITORING, "ROOM")            startActivity(intent)        }        if(v?.id==R.id.btnSTOCKPRODUCT){            var intent = Intent(c, StockActivity::class.java)            intent.putExtra(ProductionActivity.MONITORING, "PRODUCT")            startActivity(intent)        }    }    private fun menuRKB(v:View?,c: Context){        if(v?.id==R.id.btnTotal){            var intent = Intent(c, RkbActivity::class.java)            intent.putExtra(RkbActivity.USERNAME, USERNAME)            intent.putExtra(RkbActivity.DEPARTMENT, DEPARTMENT)            intent.putExtra(RkbActivity.SECTON, SECTON)            intent.putExtra(RkbActivity.LEVEL, LEVEL)            var tbindex = 0 as Int            intent.putExtra(RkbActivity.Tab_INDEX, tbindex)            startActivity(intent)        }        if(v?.id==R.id.btnApprove){            var intent = Intent(c, RkbActivity::class.java)            intent.putExtra(RkbActivity.USERNAME, USERNAME)            intent.putExtra(RkbActivity.DEPARTMENT, DEPARTMENT)            intent.putExtra(RkbActivity.SECTON, SECTON)            intent.putExtra(RkbActivity.LEVEL, LEVEL)            var tbindex = 1 as Int            intent.putExtra(RkbActivity.Tab_INDEX, tbindex)            startActivity(intent)        }        if(v?.id==R.id.btnWaiting){            var intent = Intent(c, RkbActivity::class.java)            intent.putExtra(RkbActivity.USERNAME, USERNAME)            intent.putExtra(RkbActivity.DEPARTMENT, DEPARTMENT)            intent.putExtra(RkbActivity.SECTON, SECTON)            intent.putExtra(RkbActivity.LEVEL, LEVEL)            var tbindex = 2 as Int            intent.putExtra(RkbActivity.Tab_INDEX, tbindex)            startActivity(intent)        }        if(v?.id==R.id.btnCancel){            var intent = Intent(c, RkbActivity::class.java)            intent.putExtra(RkbActivity.USERNAME, USERNAME)            intent.putExtra(RkbActivity.DEPARTMENT, DEPARTMENT)            intent.putExtra(RkbActivity.SECTON, SECTON)            intent.putExtra(RkbActivity.LEVEL, LEVEL)            var tbindex = 3 as Int            intent.putExtra(RkbActivity.Tab_INDEX, tbindex)            startActivity(intent)        }        if(v?.id==R.id.btnClose){            var intent = Intent(c, RkbActivity::class.java)            intent.putExtra(RkbActivity.USERNAME, USERNAME)            intent.putExtra(RkbActivity.DEPARTMENT, DEPARTMENT)            intent.putExtra(RkbActivity.SECTON, SECTON)            intent.putExtra(RkbActivity.LEVEL, LEVEL)            var tbindex = 4 as Int            intent.putExtra(RkbActivity.Tab_INDEX, tbindex)            startActivity(intent)        }    }    private fun bottomDialog(c:Context) {        fabIC.setColorFilter(ContextCompat.getColor(c, R.color.red_smooth), android.graphics.PorterDuff.Mode.SRC_IN)        if(bottomSheetView==null){            bottomSheetView = LayoutInflater.from(c).inflate(R.layout.bottom_sheet_dialog,findViewById<LinearLayout>(R.id.bottomSheet))        }        val bottomSheetDialog = BottomSheetDialog(c,R.style.BottomSheetDialogTheme)        bottomSheetView?.findViewById<View>(R.id.btnNewHazardMenu)?.setOnClickListener {            newHazardReport()            bottomSheetDialog.dismiss()        }        bottomSheetView?.findViewById<View>(R.id.btnNewSarprasMenu)?.setOnClickListener {            newSarana()            bottomSheetDialog.dismiss()        }        bottomSheetView?.findViewById<View>(R.id.btnSaving)?.setOnClickListener {            var status = checkWork(Constants.HazardOffline)            Toasty.info(this,"$status").show()            ConfigUtil.checkJobPending(scheduler)            bottomSheetDialog.dismiss()        }        bottomSheetView?.findViewById<View>(R.id.btnRunJob)?.setOnClickListener {            myWork()            ConfigUtil.jobScheduler(c,scheduler)            bottomSheetDialog.dismiss()        }        bottomSheetView?.findViewById<View>(R.id.btnStopSchedule)?.setOnClickListener {            cancelWorker(Constants.HazardOffline)            if(ConfigUtil.isJobServiceOn(this@IndexActivity,Constants.JOB_SERVICE_ID)) {//                    scheduler?.cancelAll()                Log.d("JobScheduler","Job Canceled")                Toasty.info(this@IndexActivity,"Job Canceled").show()                ConfigUtil.stopJobScheduler(scheduler)            }            bottomSheetDialog.dismiss()        }        bottomSheetDialog.setOnDismissListener {            if(bottomSheetView!=null){                var parent = bottomSheetView?.parent as ViewGroup                if(parent!=null){                    parent.removeView(bottomSheetView)                }            }            fabIC.setColorFilter(ContextCompat.getColor(c, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN)        }        if(ConfigUtil.cekKoneksi(this@IndexActivity)){            bottomSheetView?.findViewById<View>(R.id.btnNewSarprasMenu)?.visibility = View.VISIBLE            bottomSheetView?.findViewById<View>(R.id.btnNewCuti)?.visibility = View.VISIBLE        }else{            bottomSheetView?.findViewById<View>(R.id.btnNewSarprasMenu)?.visibility = View.GONE            bottomSheetView?.findViewById<View>(R.id.btnNewCuti)?.visibility = View.GONE        }        bottomSheetDialog.setContentView(bottomSheetView!!)        bottomSheetDialog.show()    }    private fun menuSarpras(v: View?, c: Context) {        if(v?.id==R.id.btnSarprasAll){            var intent = Intent(c, AllSarprasActivity::class.java)            startActivity(intent)        }        if(v!!.id==R.id.btnNewSarana){            newSarana()        }        if(v?.id==R.id.btnSarpras  || v!!.id==R.id.second_menu_item){            var intent = Intent(                c,                SarprasActivity::class.java            )            startActivity(intent)        }        if(v?.id== R.id.btnSarprasApproveKabag){            var intent = Intent(c, KabagApprSarprasActivity::class.java)            startActivity(intent)        }        if(v?.id== R.id.btnNewSarpras){            var intent = Intent(c, NewSarprasActivity::class.java)            startActivity(intent)        }    }    private fun menuCorrectiveAcction(v: View?, c: Context) {        if(v?.id==R.id.btnHazard|| v!!.id==R.id.first_menu_item){            first_menu_item.setColorFilter(ContextCompat.getColor(c, R.color.red_smooth), android.graphics.PorterDuff.Mode.SRC_IN)            userRule = RULE.split(",").toTypedArray()            var hseAdmin = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {                Arrays.stream(userRule).anyMatch{ t -> t== "admin_hse"}            } else {                userRule?.contains("admin_hse")            }            if(hseAdmin!!) {                val list = arrayOf("Hazard Report","Hazard Report Ke Saya","Seluruh Hazard Report")                showDialogOption(c,list)            }else{                val list = arrayOf<String>("Hazard Report","Hazard Report Ke Saya")                showDialogOption(c,list)            }        }        if(v?.id==R.id.btnInspection){            first_menu_item.setColorFilter(ContextCompat.getColor(c, R.color.red_smooth), android.graphics.PorterDuff.Mode.SRC_IN)            userRule = RULE.split(",").toTypedArray()            var hseAdmin = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {                Arrays.stream(userRule).anyMatch{ t -> t== "admin_hse"}            } else {                userRule?.contains("admin_hse")            }            if(hseAdmin!!) {                val list = arrayOf<String>("Inspection Report","All Inspection Report")                showDialogOptionInspeksi(c,list)            }else{                val list = arrayOf<String>("Inspection Report")                showDialogOptionInspeksi(c,list)            }        }    }    //    onActivityResult    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {        if(resultCode== Activity.RESULT_OK && requestCode==101){                Log.d("HazardReport","Success")                var intent = Intent(this@IndexActivity, HazardReportActivity::class.java)                startActivity(intent)        }        super.onActivityResult(requestCode, resultCode, data)    }    //    onActivityResult}
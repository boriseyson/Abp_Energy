package com.misit.abpenergy.Serviceimport android.app.*import android.app.job.JobParametersimport android.app.job.JobServiceimport android.content.Contextimport android.content.Intentimport android.os.Buildimport android.os.CountDownTimerimport android.util.Logimport androidx.localbroadcastmanager.content.LocalBroadcastManagerimport com.misit.abpenergy.HazardReport.SQLite.DataSource.HazardDetailDataSourceimport com.misit.abpenergy.HazardReport.SQLite.DataSource.HazardHeaderDataSourceimport com.misit.abpenergy.NewIndexActivityimport com.misit.abpenergy.Rimport com.misit.abpenergy.Utils.Constantsimport kotlinx.coroutines.Dispatchersimport kotlinx.coroutines.GlobalScopeimport kotlinx.coroutines.launchimport okhttp3.internal.notifyimport java.sql.SQLExceptionimport java.util.*class JobServices :JobService(){    private var TAG="JobScheduler"    private var jobCanceled = false    lateinit var timer: CountDownTimer    lateinit var manager: NotificationManager    lateinit var bgTokenService:Intent    var randomID:Int?=0    override fun onStartJob(params: JobParameters?): Boolean {        randomID = (0..10).random()        bgTokenService = Intent(this@JobServices,BgTokenService::class.java)        Log.d(TAG,"Jobs Started")        showNotification("Job Started","RunningJob",randomID!!)        doBackgroundWork(params)        return true    }    override fun onStopJob(params: JobParameters?): Boolean {        Log.d(TAG,"Job Cancelation Before Completion")        timer.cancel()        jobCanceled=true        return false    }    private fun doBackgroundWork(params: JobParameters?) {        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {            startStopService(FgTokenService::class.java)        }else{            bgTokenService.putExtra("username", NewIndexActivity.USERNAME)            startService(bgTokenService)        }        loadHazardSQL()        var i=1        timer = object: CountDownTimer(10000, 1000) {            override fun onTick(millisUntilFinished: Long) {                Log.d(TAG,"run: ${i}")                loadHazardSQL()                i++                if(jobCanceled) {                    return                }            }            override fun onFinish() {                jobFinished(params,false)                Log.d(TAG,"Job Finised")                stopService(bgTokenService)                startStopService(FgTokenService::class.java)                showNotification("Job Finished","RunningJob",randomID!!)            }        }        timer.start()    }    private fun loadHazardSQL(){        GlobalScope.launch(Dispatchers.IO) {            val hazardHeader = HazardHeaderDataSource(this@JobServices)            try {                val hazardRow = hazardHeader.getAll()                hazardRow.forEach {                    val detail = HazardDetailDataSource(this@JobServices)                    val detailFirst = detail.getItem(it.idHazard.toString())                    if(detailFirst!=null){                        Log.d("HazardReport",it.deskripsi.toString())                        Log.d("HazardReport",detailFirst.tindakan.toString())                        Log.d("HazardReport",detailFirst.namaPJ.toString())                        Log.d("HazardReport",detailFirst.keterangan_update.toString())                    }                }            }catch (e: SQLException){                Log.d("HazardReport",e.toString())            }        }    }    private fun createNotificationChannel(channel:String){        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){            val serviceChannel = NotificationChannel(                channel,                "Saving Service Channel",                NotificationManager.IMPORTANCE_DEFAULT            )            manager = getSystemService(                NotificationManager::class.java            )            manager.createNotificationChannel(serviceChannel)        }    }    private fun showNotification(jobString: String,channel:String,idNotification:Int) {        createNotificationChannel(channel)        val notificationIntent = Intent(this, NewIndexActivity::class.java)        val pendingIntent = PendingIntent.getActivity(this, 1, notificationIntent, 0)        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {            val notification =                Notification.Builder(this, channel).setContentText(jobString).setSmallIcon(                    R.drawable.abp_white                ).setContentIntent(pendingIntent).build()//            startForeground(idNotification, notification)            manager.notify(idNotification,notification)            Log.d("LoadingServices", "Service Started!!")        }    }    private fun startStopService(jvClass:Class<*>) {        if(isMyServiceRunning(jvClass)){            var intent = Intent(this@JobServices, jvClass).apply {                this.action = Constants.SERVICE_STOP//                LocalBroadcastManager.getInstance(this@JobServices).unregisterReceiver(tokenPassingReceiver!!)            }            stopService(intent)        }else{            var intent = Intent(this@JobServices, jvClass).apply {                this.action = Constants.SERVICE_START            }            intent.putExtra("username", NewIndexActivity.USERNAME)            startService(intent)        }    }    private fun isMyServiceRunning(mClass: Class<*>): Boolean {        val manager: ActivityManager =getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager        for (service: ActivityManager.RunningServiceInfo in manager.getRunningServices(Integer.MAX_VALUE)){            if(mClass.name.equals(service.service.className)){                return true            }        }        return false    }}
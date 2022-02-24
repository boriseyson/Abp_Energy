package com.misit.abpenergy.HazardReport.Serviceimport android.app.*import android.content.Contextimport android.content.Intentimport android.os.Buildimport android.os.CountDownTimerimport android.os.IBinderimport android.util.Logimport androidx.localbroadcastmanager.content.LocalBroadcastManagerimport com.misit.abpenergy.Main.HomePage.IndexActivityimport com.misit.abpenergy.Rimport com.misit.abpenergy.Utils.Constantsimport es.dmoral.toasty.Toastyimport kotlinx.coroutines.Dispatchersimport kotlinx.coroutines.GlobalScopeimport kotlinx.coroutines.launchclass HazardService: Service() {    var tvTimer:String?=null    lateinit var manager:NotificationManager    lateinit var hazardSave :HazardSaveOffline    override fun onBind(intent: Intent?): IBinder? {        return null    }    override fun onCreate() {        createNotificationChannel()        hazardSave = HazardSaveOffline()        super.onCreate()    }    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {        intent.let {            when(it!!.action){                Constants.SERVICE_START -> showNotification()                Constants.SERVICE_STOP -> stopService()            }        }        return super.onStartCommand(intent, flags, startId)    }    private fun showNotification() {        sendMessageToActivity(this,"HazardLoading","Loading")        val notificationIntent = Intent(this, IndexActivity::class.java)        val pendingIntent = PendingIntent.getActivity(this, 1, notificationIntent, 0)        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {            sendMessageToActivity("FgHazardSaving")            val notification =                Notification.Builder(this, Constants.CHANNEL_ID).setContentText("Sinkronisasi Data....!!!").setSmallIcon(                    R.drawable.abp_white                ).setContentIntent(pendingIntent).build()            startForeground(Constants.NOTIFICATION_ID, notification)//            Toasty.info(this@HazardService, "Service Started!!").show()            savingHazard()        }    }    private fun savingHazard() {        GlobalScope.launch(Dispatchers.IO) {            hazardSave.getToken(this@HazardService,"FgHazard","FgHazardDone")        }//        object : CountDownTimer(10000, 10) {////            override fun onTick(millisUntilFinished: Long) {//                Log.d("HAZARDSERVICE", "seconds remaining: " + millisUntilFinished / 1000)////                sendMessageToActivity("${millisUntilFinished / 1000}")//            }//            override fun onFinish() {//                GlobalScope.launch(Dispatchers.IO) {//                    hazardSave.getToken(this@HazardService,"FgHazard","FgHazardDone")//                }////                stopService(Intent(this@HazardService, HazardService::class.java).apply {////                    this.action = Constants.SERVICE_STOP////                })////                sendMessageToActivity("FgHazardDone")////                Log.d("HAZARDSERVICE", "done")//            }//        }.start()    }    private fun createNotificationChannel(){        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){            val serviceChannel = NotificationChannel(                Constants.CHANNEL_ID,                "Saving Service Channel",                NotificationManager.IMPORTANCE_DEFAULT            )            manager = getSystemService(                NotificationManager::class.java            )            manager.createNotificationChannel(serviceChannel)        }    }    private fun stopService(){        manager.cancel(Constants.NOTIFICATION_ID)        stopForeground(true)        stopSelf()        Toasty.info(this@HazardService, "Service Stopped!!").show()    }    private fun sendMessageToActivity(msg: String) {        val intent = Intent()        intent.action = "com.misit.abpenergy"        intent.putExtra("FgHazard", msg)        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)    }    private fun sendMessageToActivity(c: Context, name:String, msg: String) {        Log.d("BroadcastMessage","sendMessageToActivity")        val intent = Intent()        intent.action = "com.misit.abpenergy"        intent.putExtra(name, msg)        LocalBroadcastManager.getInstance(c).sendBroadcast(intent)    }}
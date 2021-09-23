package com.misit.abpenergy.HazardReport.Serviceimport android.app.*import android.content.BroadcastReceiverimport android.content.Contextimport android.content.Intentimport android.os.Buildimport android.os.IBinderimport android.util.Logimport androidx.localbroadcastmanager.content.LocalBroadcastManagerimport com.misit.abpenergy.NewIndexActivityimport com.misit.abpenergy.Rimport com.misit.abpenergy.Utils.ConfigUtilimport com.misit.abpenergy.Utils.Constantsimport kotlinx.coroutines.GlobalScopeimport kotlinx.coroutines.launchclass FgHazardService:Service() {    var TAG ="FgHazardService"    lateinit var manager:NotificationManager    lateinit var hazardSave :HazardSaveOffline    lateinit var bgHazardService:Intent    override fun onCreate() {        Log.d(TAG,"onCreate")        hazardSave = HazardSaveOffline()        createNotificationChannel()        bgHazardService = Intent(this@FgHazardService,BgHazardService::class.java)        super.onCreate()    }    override fun onBind(intent: Intent?): IBinder? {        return null    }    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {        Log.d(TAG,"onStartCommand")        intent.let {            when(it!!.action){                Constants.SERVICE_START -> showNotification()                Constants.SERVICE_STOP -> stopService()            }        }        return super.onStartCommand(intent, flags, startId)    }    override fun onDestroy() {        super.onDestroy()    }    private fun showNotification() {        Log.d(TAG,"showNotification")        val notificationIntent = Intent(this, NewIndexActivity::class.java)        val pendingIntent = PendingIntent.getActivity(this, 1, notificationIntent, 0)        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {            val notification =                Notification.Builder(this, Constants.CHANNEL_ID).setContentText("Synchronize . . .!!!").setSmallIcon(                    R.drawable.abp_white                ).setContentIntent(pendingIntent).build()            GlobalScope.launch {                hazardSave.getToken(this@FgHazardService,"SavingHazard","FgHazardDone")                startForeground(Constants.NOTIFICATION_ID, notification)            }        }    }    private fun createNotificationChannel(){        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){            val serviceChannel = NotificationChannel(                Constants.CHANNEL_ID,                "Saving Service Channel",                NotificationManager.IMPORTANCE_DEFAULT            )            manager = getSystemService(                NotificationManager::class.java            )            manager.createNotificationChannel(serviceChannel)        }    }    private fun stopService(){        manager.cancel(Constants.NOTIFICATION_ID)        stopForeground(true)        stopSelf()        Log.d(TAG, "Service Stopped!!")    }}
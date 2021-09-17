package com.misit.abpenergy.Serviceimport android.app.*import android.content.Intentimport android.nfc.Tagimport android.os.Buildimport android.os.IBinderimport android.util.Logimport androidx.localbroadcastmanager.content.LocalBroadcastManagerimport com.misit.abpenergy.NewIndexActivityimport com.misit.abpenergy.Rimport com.misit.abpenergy.Utils.Constantsimport com.misit.abpenergy.Utils.PrefsUtilimport kotlinx.coroutines.Dispatchersimport kotlinx.coroutines.GlobalScopeimport kotlinx.coroutines.launchclass FgTokenService :Service(){    var TAG ="FgToken"    lateinit var manager: NotificationManager    lateinit var getToken :GetToken    private var username:String?=null    override fun onBind(intent: Intent?): IBinder? {        return null    }    override fun onCreate() {        PrefsUtil.initInstance(this@FgTokenService)        if(PrefsUtil.getInstance().getBooleanState("IS_LOGGED_IN", false)) {            username =                PrefsUtil.getInstance().getStringState(PrefsUtil.USER_NAME, "")        }        Log.d(TAG,"onCreate")        getToken = GetToken()        createNotificationChannel()        super.onCreate()    }    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {        Log.d(TAG,"onStartCommand")        intent.let {//            username = it?.getStringExtra("username")            when(it!!.action){                Constants.SERVICE_START -> showNotification()                Constants.SERVICE_STOP -> stopService()            }        }        return super.onStartCommand(intent, flags, startId)    }    private fun showNotification() {        val notificationIntent = Intent(this, NewIndexActivity::class.java)        val pendingIntent = PendingIntent.getActivity(this, 1, notificationIntent, 0)        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {            val notification =                Notification.Builder(this, Constants.CHANNEL_ID).setContentText("Mengambil data....!!!").setSmallIcon(                    R.drawable.abp_white                ).setContentIntent(pendingIntent).build()            GlobalScope.launch(Dispatchers.IO) {                getToken.getToken(this@FgTokenService,username!!,"LoadData","fgTokenService")                startForeground(Constants.NOTIFICATION_ID, notification)                Log.d("LoadingServices", "Service Started!!")            }        }    }    private fun createNotificationChannel(){        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){            val serviceChannel = NotificationChannel(                Constants.CHANNEL_ID,                "Saving Service Channel",                NotificationManager.IMPORTANCE_DEFAULT            )            manager = getSystemService(                NotificationManager::class.java            )            manager.createNotificationChannel(serviceChannel)        }    }    private fun stopService(){//        sendMessageToActivity("fgTokenService")        manager.cancel(Constants.NOTIFICATION_ID)        stopForeground(true)        stopSelf()        Log.d(TAG, "Service Stopped!!")    }    private fun sendMessageToActivity(msg: String) {        val intent = Intent()        intent.action = "com.misit.abpenergy"        intent.putExtra("LoadData", msg)        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)    }}
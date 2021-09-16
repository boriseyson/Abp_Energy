package com.misit.abpenergy.Serviceimport android.app.IntentServiceimport android.content.Contextimport android.content.Intentimport android.os.IBinderimport android.util.Logimport androidx.localbroadcastmanager.content.LocalBroadcastManagerimport com.misit.abpenergy.Utils.ConfigUtilimport kotlinx.coroutines.*class ConnectionService:IntentService("ConnectionService"){    val TAG = "SaranaService"    override fun onBind(intent: Intent): IBinder? {        return null    }    override fun onHandleIntent(intent: Intent?) {        Log.d(TAG,"On Intent")        GlobalScope.launch(Dispatchers.IO) {            coroutineScope {                val deffered = async {                    ConfigUtil.isOnline()                }                val result = deffered.await()                if(result){                    sendMessageToActivity("bsConnection","Online",this@ConnectionService)                }else{                    sendMessageToActivity("bsConnection","Offline",this@ConnectionService)                }                Log.d(TAG,"${result}")            }        }        if(ConfigUtil.cekKoneksi(this@ConnectionService)){            Log.d(TAG,"Connected!")        }else{            Log.d(TAG,"Not Connected!")        }    }    override fun onCreate() {        showLog("onCreate")        super.onCreate()    }    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {        showLog("onStartCommand")        return super.onStartCommand(intent, flags, startId)    }    override fun onDestroy() {        showLog("onDestroy")        super.onDestroy()    }    private fun showLog(s: String) {        Log.d(TAG,s)    }    private fun sendMessageToActivity(name: String,msg: String,c: Context) {        val intent = Intent()        intent.action = "com.misit.abpenergy"        intent.putExtra(name, msg)        LocalBroadcastManager.getInstance(c).sendBroadcast(intent)    }}
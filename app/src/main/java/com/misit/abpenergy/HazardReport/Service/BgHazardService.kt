package com.misit.abpenergy.HazardReport.Serviceimport android.app.*import android.content.Intentimport android.content.res.Resourcesimport com.misit.abpenergy.Rimport kotlinx.coroutines.Dispatchersimport kotlinx.coroutines.GlobalScopeimport kotlinx.coroutines.launchclass BgHazardService :IntentService("com.misit.abpenergy"){    lateinit var manager:NotificationManager    lateinit var hazardSave :HazardSaveOffline    override fun onCreate() {        hazardSave = HazardSaveOffline()        super.onCreate()    }    override fun onHandleIntent(intent: Intent?) {        GlobalScope.launch(Dispatchers.IO) {            hazardSave.getToken(this@BgHazardService,"SavingHazard","BgHazardDone")        }    }}
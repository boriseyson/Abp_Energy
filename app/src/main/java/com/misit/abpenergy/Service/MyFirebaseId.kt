package com.misit.abpenergy.Service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.misit.abpenergy.Api.ApiClient
import com.misit.abpenergy.Api.ApiEndPointTwo
import com.misit.abpenergy.HomePage.IndexActivity
import com.misit.abpenergy.R
import com.misit.abpenergy.Utils.PrefsUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

class MyFirebaseId : FirebaseMessagingService() {
    override fun onCreate() {
        PrefsUtil.initInstance(this)
        if(PrefsUtil.getInstance().getBooleanState("IS_LOGGED_IN",true)){
            USERNAME = PrefsUtil.getInstance().getStringState(PrefsUtil.USER_NAME,"")
            NAMA_LENGKAP = PrefsUtil.getInstance().getStringState(PrefsUtil.NAMA_LENGKAP,"")
            DEPARTMENT = PrefsUtil.getInstance().getStringState(PrefsUtil.DEPT,"")
            SECTON = PrefsUtil.getInstance().getStringState(PrefsUtil.SECTION,"")
            LEVEL = PrefsUtil.getInstance().getStringState(PrefsUtil.LEVEL,"")
        }
        super.onCreate()
    }
    override fun onMessageReceived(p0: RemoteMessage) {
        if (p0.data.isNotEmpty()) {
            Log.d(TAG, "Message data : " + p0.data)
        }
        val data: Map<String, String> = p0.data
        val teks = data["text"]
        val title = data["title"]
        val tipe = data["tipe"]
        val uid = data["uid"]
        val id_notif = data["id_notif"]
        if(PrefsUtil.getInstance().getBooleanState("IS_LOGGED_IN",true)) {
            if(tipe=="tenggat_hazard"){
                firebaseMessage(tipe,uid!!)
            }else{
                notif(title, teks, tipe, uid)
            }
        }
    }
    private fun firebaseMessage(tipe: String,uid:String){
        getMessage(tipe,uid)
    }
    private fun notifSender(title: String?, body: String?,tipe:String?,uid:String?,id_notif:Int?){
        Log.d("UID", "${uid}")
        var intent = Intent(this, IndexActivity::class.java)
        intent.putExtra(IndexActivity.TIPE,tipe)
        intent.putExtra("UID",uid)
//        sendMessage(title,body,intent,tipe,id_notif)
    }
    private fun notif(title: String?, body: String?,tipe:String?,uid:String?){
        Log.d("UID", "${uid}")
        var intent = Intent(this, IndexActivity::class.java)
        intent.putExtra(IndexActivity.TIPE,tipe)
        intent.putExtra("UID",uid)
        showNotification(title,body,intent,tipe)
    }
    private fun showNotification(title: String?, body: String?,intent: Intent,tipe: String?) {
        var pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)
        var cId = "fcm_default_channel"
        var dSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var nBuilder = NotificationCompat
                                            .Builder(this,cId)
                                            .setSmallIcon(R.drawable.abp_white)
                                            .setColor(R.drawable.abp_blue)
                                            .setContentTitle(title)
                                            .setContentText(null)
                                            .setAutoCancel(true)
                                            .setSound(dSoundUri)
                                            .setContentIntent(pendingIntent)
                                            .setPriority(Notification.PRIORITY_HIGH)
                                            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
        var nManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            var oChannel = NotificationChannel(cId,"Costumer",NotificationManager.IMPORTANCE_HIGH)
            nManager.createNotificationChannel(oChannel)
        }
        var id =(1..9999).random()
        nManager.notify(id,nBuilder.build())
        playNotificationSound(this@MyFirebaseId)
    }
    private fun sendMessage(title: String?,intent: Intent,id_notif:Int?,pesan:List<String>?) {
        val GROUP_KEY_WORK_EMAIL = "com.misit.abpenergy.WORK_EMAIL"
        var iStyle = NotificationCompat.InboxStyle()
        pesan?.forEach {
            iStyle.addLine(it)
        }
        var pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)
        var cId = "fcm_default_channel"
        var dSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var nBuilder = NotificationCompat
            .Builder(this,cId)
            .setSmallIcon(R.drawable.abp_white)
            .setColor(R.drawable.abp_blue)
            .setContentTitle(title)
            .setContentText(null)
            .setAutoCancel(true)
            .setSound(dSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(Notification.PRIORITY_HIGH)
            .setStyle(iStyle)
            .setGroup(GROUP_KEY_WORK_EMAIL)
        var nManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            var oChannel = NotificationChannel(cId,"Costumer",NotificationManager.IMPORTANCE_HIGH)
            nManager.createNotificationChannel(oChannel)
        }
        var id = 1
        if(id_notif!=null){
            id=id_notif
        }else {
            id = (1..9999).random()
        }
            nManager.notify(id,nBuilder.build())
        playNotificationSound(this@MyFirebaseId)
    }
    private fun playNotificationSound(c:Context) {
        try {
            val defSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(c,defSoundURI)
            r.play()
        }catch (e:Exception){
            Log.d("ER_Ringtone",e.toString())
        }
    }
    private fun getMessage(tipe:String,uid: String?){
        var intent = Intent(this, IndexActivity::class.java)
        intent.putExtra(IndexActivity.TIPE,tipe)
        intent.putExtra("UID",uid)
        var pesanku = listOf<String>()
        GlobalScope.launch(Dispatchers.IO){
            val apiEndPoint =
                ApiClient.getClient(this@MyFirebaseId)?.create(ApiEndPointTwo::class.java)
            val response = apiEndPoint?.pesanNotifikasi()
            if (response != null) {
                if (response.isSuccessful) {
                    val pesan = response.body()
                    if (pesan != null) {

                        pesanku=pesan!!.message!!
                        sendMessage("Testing",intent,1,pesanku!!)
                        Log.d("PesanMasuk","${pesan.message}")
                    }
                }
            }
        }
    }
    companion object{
        var USERNAME = "username"
        var DEPARTMENT="department"
        var SECTON="section"
        var LEVEL="level"
        var NAMA_LENGKAP = "nama_lengkap"
        private  var TAG="MyFirebaseMessagingService"
    }
}
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
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.misit.abpenergy.Api.ApiClient
import com.misit.abpenergy.Api.ApiEndPointTwo
import com.misit.abpenergy.HomePage.IndexActivity
import com.misit.abpenergy.Model.NotifGroupResponse
import com.misit.abpenergy.R
import com.misit.abpenergy.Utils.Constants
import com.misit.abpenergy.Utils.PrefsUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import kotlin.math.log

class MyFirebaseId : FirebaseMessagingService() {
    var nManager:NotificationManager?=null
    var notificationIntent : Intent?=null
    var android_token:String?=null
    lateinit var nBuilder:NotificationCompat.Builder
    override fun onCreate() {
        androidToken(this@MyFirebaseId)
        notificationIntent = Intent(this, IndexActivity::class.java)
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
        Log.d("android_token","${android_token}");
        val data: Map<String, String> = p0.data
        val teks = data["text"]
        val title = data["title"]
        val tipe = data["tipe"]
        val uid = data["uid"]
        val id_notif = data["id_notif"]
        if(PrefsUtil.getInstance().getBooleanState("IS_LOGGED_IN",true)) {
            if(tipe=="tenggat_hazard"){
                createNotificationChannel("${tipe}")
                getMessage(tipe)
            }else if(tipe=="hazard_verify"){
                createNotificationChannel("${tipe}")
                getMessage(tipe)
            }
            else{
                notif(title, teks, tipe, uid)
            }
        }
    }
    private fun notifSender(title: String?, body: String?,tipe:String?,uid:String?,id_notif:Int?){
        Log.d("UID", "${uid}")
        notificationIntent?.putExtra(IndexActivity.TIPE,tipe)
        notificationIntent?.putExtra("UID",uid)
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
        nManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            var oChannel = NotificationChannel(cId,"Costumer",NotificationManager.IMPORTANCE_HIGH)
            nManager?.createNotificationChannel(oChannel)
        }
        var id =(1..9999).random()
        nManager?.notify(id,nBuilder.build())
        playNotificationSound(this@MyFirebaseId)
    }
    private fun sendMessage(namaChannel:String) {
        val GROUP_KEY_WORK_EMAIL = "${namaChannel}"
        var pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,PendingIntent.FLAG_ONE_SHOT)
        nBuilder = NotificationCompat
            .Builder(this,namaChannel)
            .setSmallIcon(R.drawable.abp_white)
            .setColor(R.drawable.abp_blue)
            .setGroup(GROUP_KEY_WORK_EMAIL)
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
            .setGroupSummary(true)
            .setContentText(null)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
    }
    private fun getMessage(tipe: String?){
        sendMessage("${tipe}")
        var response :Response<NotifGroupResponse>?=null
        GlobalScope.launch(Dispatchers.IO){
            val apiEndPoint =
                ApiClient.getClient(this@MyFirebaseId)?.create(ApiEndPointTwo::class.java)
            if (tipe=="tenggat_hazard"){
                response = apiEndPoint?.notifGroup()
            }else if(tipe=="hazard_verify"){
                response = apiEndPoint?.notifVerify()
            }
            if (response != null) {
                if (response!!.isSuccessful) {
                    val response = response!!.body()
                    if (response != null) {
                        var res = response.hazardNotClose
                        if(res!=null){
                            res.forEach {
                                var id = (1..1000000).random()
                                var iStyle = NotificationCompat.InboxStyle()
                                Log.d("tipeHazard","${tipe}")
                                if (tipe=="tenggat_hazard") {
                                    if (it!!.phoneToken == android_token) {
                                        nBuilder.setStyle(iStyle)
                                            .setContentTitle("${it.judul}")
                                        it.pesan?.forEach { message ->
                                            iStyle.addLine(message)
                                            Log.d("PesanMasuk", "${message}")
                                        }
                                        nManager?.notify(id, nBuilder.build())
                                    }
                                }
                                if(tipe=="hazard_verify"){
                                    nBuilder.setStyle(iStyle)
                                            .setContentTitle("${it!!.judul}")
                                    it!!.pesan!!.forEach { message ->
                                        iStyle.addLine(message)
                                        Log.d("PesanMasuk", "${message}")
                                    }
                                    nManager?.notify(id, nBuilder.build())
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private fun createNotificationChannel(namaChannel:String){
        Log.d("FirebaseService","CreateChannel")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val serviceChannel = NotificationChannel(
                "${namaChannel}",
                "${namaChannel}",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            nManager = getSystemService(
                NotificationManager::class.java
            )
            nManager?.createNotificationChannel(serviceChannel)
        }
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
    fun androidToken(c: Context){
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Toast.makeText(c,"Error : $task.exception", Toast.LENGTH_SHORT).show()

                    return@OnCompleteListener
                }
                // Get new Instance ID token
                if(task.result!=null){
                    android_token = task.result
                }else{
                    androidToken(c)
                }
            })
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
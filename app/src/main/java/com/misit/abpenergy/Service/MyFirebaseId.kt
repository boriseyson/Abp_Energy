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
import com.misit.abpenergy.HSE.HazardReport.Activity.DetailHazardActivity
import com.misit.abpenergy.Main.HomePage.IndexActivity
import com.misit.abpenergy.Main.Model.NotifGroupResponse
import com.misit.abpenergy.R
import com.misit.abpenergy.Utils.Constants
import com.misit.abpenergy.Utils.PrefsUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception

class MyFirebaseId : FirebaseMessagingService() {
    var nManager:NotificationManager?=null
    var notificationIntent : Intent?=null
    var android_token : String? = null

    lateinit var nBuilder:NotificationCompat.Builder
    override fun onCreate() {
        androidToken()
        createNotificationChannel()
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
        val data: Map<String, String> = p0.data
        val teks = data["text"]
        val title = data["title"]
        val tipe = data["tipe"]
        val uid = data["uid"]
        val id_notif = data["id_notif"]
        if(PrefsUtil.getInstance().getBooleanState("IS_LOGGED_IN",true)) {
            if(tipe=="tenggat_hazard") {
                getMessage()
            }else if(tipe=="tenggat_user"||tipe=="tenggat_pj"){
                userMessage("tenggat_user")
            }else if(tipe=="penanggung_jawab"){
                userMessage("penanggung_jawab")
            }else{
                notif(title, teks, tipe, uid)
            }
        }
    }
    private fun notifSender(title: String?, body: String?,tipe:String?,uid:String?,id_notif:Int?){

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
    private fun sendMessage(pIntent: Intent) {
        val GROUP_KEY_WORK_EMAIL = "com.misit.abpenergy.WORK_EMAIL"
        var pendingIntent = PendingIntent.getActivity(this,0,pIntent,PendingIntent.FLAG_UPDATE_CURRENT)
        nBuilder = NotificationCompat
            .Builder(this,Constants.CHANNEL_ID)
            .setSmallIcon(R.drawable.abp_white)
            .setColor(R.drawable.abp_blue)
            .setGroup(GROUP_KEY_WORK_EMAIL)
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
            .setGroupSummary(true)
            .setContentText(null)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
    }
    private fun inSendMessage(pIntent: Intent): NotificationCompat.Builder {
        val GROUP_KEY_WORK_EMAIL = "com.misit.abpenergy.WORK_EMAIL"
        var pendingIntent = PendingIntent.getActivity(this,0,pIntent,PendingIntent.FLAG_UPDATE_CURRENT)
        var nBuilder = NotificationCompat
            .Builder(this,Constants.CHANNEL_ID)
            .setSmallIcon(R.drawable.abp_white)
            .setColor(R.drawable.abp_blue)
            .setGroup(GROUP_KEY_WORK_EMAIL)
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
            .setGroupSummary(true)
            .setContentText(null)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        return nBuilder
    }
    private fun getMessage(){
        GlobalScope.launch(Dispatchers.IO){
            val apiEndPoint =
                ApiClient.getClient(this@MyFirebaseId)?.create(ApiEndPointTwo::class.java)
            val response = apiEndPoint?.notifGroup()
            if (response != null) {
                if (response.isSuccessful) {
                    val pesan = response.body()
                    if (pesan != null) {
                        if(pesan.hazardNotClose!=null){
                            pesan.hazardNotClose.forEach { itempesan->
                                var nManager = notifChannel()
                                var pIntent = Intent(this@MyFirebaseId, DetailHazardActivity::class.java)
                                pIntent.putExtra("UID","${itempesan?.uid}")
                                pIntent.putExtra("Method","Online")
                                pIntent.putExtra("actionFrom","Notif")
                                pIntent.setAction("${itempesan?.uid}")
                                var nBuilder = inSendMessage(pIntent)
                                var iStyle = NotificationCompat.InboxStyle()
                                if(itempesan?.pesan!=null){
                                    itempesan.pesan?.forEach {
                                        iStyle.addLine(it)
                                    }
                                    iStyle.addLine("UID1 ${itempesan!!.uid}")
                                    nBuilder.setStyle(iStyle)
                                        .setContentTitle("UID1 ${itempesan.judul}")
                                    var id = (1..9999).random()
                                    nManager?.notify(id,nBuilder.build())
                                    Log.d("PesanMasuk","${itempesan.uid}")

                                }
                            }

                        }
                    }
                }
            }
        }
    }
    private fun userMessage(tipe: String?){
        var response: Response<NotifGroupResponse>? = null
        GlobalScope.launch(Dispatchers.IO){
            val apiEndPoint =
                ApiClient.getClient(this@MyFirebaseId)?.create(ApiEndPointTwo::class.java)
            if(tipe=="tenggat_user"){
                response = apiEndPoint?.tenggatUsers(android_token)
            }else if(tipe=="penanggung_jawab"){
                response = apiEndPoint?.notifUser(android_token)
                Log.d("PESAN","${tipe}")

            }
            if (response != null) {
                if (response!!.isSuccessful) {
                    val pesan = response!!.body()
                    if (pesan != null) {
                        if(pesan.hazardNotClose!=null){
                            pesan.hazardNotClose.forEach { itempesan->
                                var nManager = notifChannel()
                                var pIntent = Intent(this@MyFirebaseId, DetailHazardActivity::class.java)
                                pIntent.putExtra("UID","${itempesan?.uid}")
                                pIntent.putExtra("Method","Online")
                                pIntent.putExtra("actionFrom","Notif")
                                pIntent.setAction("${itempesan?.uid}")
                                var nBuilder = inSendMessage(pIntent)
                                var iStyle = NotificationCompat.InboxStyle()
                                if(itempesan?.pesan!=null){
                                    itempesan.pesan?.forEach {
                                        iStyle.addLine(it)
                                    }
                                    iStyle.addLine("UID ${itempesan!!.uid}")
                                    if(itempesan.phoneToken==android_token){
                                        nBuilder.setStyle(iStyle)
                                            .setContentTitle("UID ${itempesan.judul}")
                                        var id = (1..9999).random()
                                        nManager?.notify(id,nBuilder.build())

                                    }
                                    Log.d("android_token","${android_token} | ${itempesan.phoneToken}")

                                    Log.d("PesanMasuk","${itempesan.uid}")
                                }
                            }

                        }
                    }
                }
            }
        }
    }
    private fun createNotificationChannel(){
        Log.d("FirebaseService","CreateChannel")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val serviceChannel = NotificationChannel(
                Constants.CHANNEL_ID,
                "com.misit.abpenergy",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            nManager = getSystemService(
                NotificationManager::class.java
            )
            nManager?.createNotificationChannel(serviceChannel)
        }
    }
    private fun notifChannel(): NotificationManager? {
        Log.d("FirebaseService","CreateChannel")
        var nManager:NotificationManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            nManager = getSystemService(
                NotificationManager::class.java
            )
        }else{
            nManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val serviceChannel = NotificationChannel(
                Constants.CHANNEL_ID,
                "com.misit.abpenergy",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            nManager?.createNotificationChannel(serviceChannel)
            return nManager
        }else{
            return nManager
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
    companion object{
        var USERNAME = "username"
        var DEPARTMENT="department"
        var SECTON="section"
        var LEVEL="level"
        var NAMA_LENGKAP = "nama_lengkap"
        private  var TAG="MyFirebaseMessagingService"
    }

    fun androidToken(){
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Toast.makeText(this@MyFirebaseId,"Error : $task.exception", Toast.LENGTH_SHORT).show()

                    return@OnCompleteListener
                }
                // Get new Instance ID token
                android_token = task.result
            })
    }
}
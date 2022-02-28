package com.misit.abpenergy.Login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.misit.abpenergy.Api.ApiClient
import com.misit.abpenergy.Api.ApiClientTwo
import com.misit.abpenergy.Api.ApiEndPoint
import com.misit.abpenergy.Login.Response.Auth.AuthAppResponse
import com.misit.abpenergy.Login.Response.Auth.UserLogin
import com.misit.abpenergy.MainPageActivity
import com.misit.abpenergy.R
import com.misit.abpenergy.Utils.ConfigUtil
import com.misit.abpenergy.Utils.PopupUtil
import com.misit.abpenergy.Utils.PrefsUtil
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.register_layout.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity(),View.OnClickListener
 {
     private var csrf_token : String?=""
     private var android_token : String?=""
     private var app_version : String?=""
     private var companyDipilih:String?=null
     private var idCompany:String?=null
     private var dialog:AlertDialog?=null
     private var u: UserLogin?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        companyDipilih=""
        idCompany=""
        u = UserLogin()
        PrefsUtil.initInstance(this)
        ConfigUtil.changeColor(this)

        loginBtn.setOnClickListener(this)
        InPassword.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                //Perform Code


                if(isValidatedAll()) {
//                    loginNew(
//                        InUsername.text.toString(),
//                        InPassword.text.toString()
//                    )
                    GlobalScope.launch(Dispatchers.Main) {
                        async { loadingDialog(this@LoginActivity) }.await()

                        userLogin(InUsername.text.toString(),InPassword.text.toString(),csrf_token!!,android_token!!,app_version!!,"abpSystem")
                    }
                }
                return@OnKeyListener true
            }
            false
        })
        tvLpSandi.setOnClickListener(this)
        btnNewUser.setOnClickListener(this)
        btnNewUserMitra.setOnClickListener(this)
        regBtn.setOnClickListener(this)
    }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         if(resultCode==Activity.RESULT_OK && requestCode==10){
             registerUser()
         }
         if(resultCode==Activity.RESULT_OK && requestCode==11){
             if(data!=null){
                 var user = data.getStringExtra("RegUser")
                 InUsername.setText(user)
             }
         }
         super.onActivityResult(requestCode, resultCode, data)
     }

     private fun registerUser(){
         var intent = Intent(this@LoginActivity,RegisterActivity::class.java)
         startActivityForResult(intent,11)
     }
    private fun registerUserMitra(){
        var intent = Intent(this@LoginActivity,RegisterMitraKerjaActivity::class.java)
        startActivityForResult(intent,12)
    }
     private fun forgotPasswd(){
         var intent = Intent(this@LoginActivity,ForgotPasswordActivity::class.java)
         if(InUsername.text!=null){
             intent.putExtra(ForgotPasswordActivity.USERNAME,InUsername.text)
         }
         startActivityForResult(intent,10)
     }

    override fun onClick(v: View?) {
        if(v?.id== R.id.loginBtn){
            if(isValidatedAll()){


//                loginNew(InUsername.text.toString(),InPassword.text.toString())
                GlobalScope.launch(Dispatchers.Main) {
                    async { loadingDialog(this@LoginActivity) }.await()
                    userLogin(InUsername.text.toString(),InPassword.text.toString(),csrf_token!!,android_token!!,app_version!!,"abpSystem")
                }
            }
        }
        if(v?.id==R.id.tvLpSandi){
            forgotPasswd()
        }
        if(v?.id==R.id.btnNewUser){
            registerUser()
        }
        if(v?.id==R.id.btnNewUserMitra){
            registerUserMitra()
        }
        if(v?.id==R.id.regBtn){
            daftarDialog(this@LoginActivity)
        }
    }
     private fun daftarDialog(c:Context){
         val mDialogView = LayoutInflater.from(c).inflate(R.layout.register_layout,null)
         val mBuilder = AlertDialog.Builder(c)
         mBuilder.setView(mDialogView)
         val dialog1 =mBuilder.show()
         dialog1?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
         mDialogView.btnAbp?.setOnClickListener { registerUser() }
         mDialogView.btnMitra?.setOnClickListener { registerUserMitra() }
         mDialogView.btnDismis?.setOnClickListener { dialog1.dismiss() }

     }
    override fun onResume() {
        if(ConfigUtil.cekKoneksi(this)){
            GlobalScope.launch(Dispatchers.Main) {
                async { loadingDialog(this@LoginActivity) }.await()
                async{ corotineToken(this@LoginActivity) }.await()
            }
//            getToken()
            androidToken()
            versionApp()
//            tvVersionCode.text="V$app_version"
        }else{
            ConfigUtil.koneksiInActive(this@LoginActivity)
            Toasty.error(this, "KONEKSI TIDAK ADA", Toasty.LENGTH_SHORT).show()
        }

        super.onResume()
    }

    fun versionApp(){
        try {
            val pInfo: PackageInfo = this.getPackageManager().getPackageInfo(packageName, 0)
            app_version = pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
           Log.d("AppVersion","${e.message}")
        }
    }

     suspend fun corotineToken(c: Context){
         try {
             val apiEndPoint = ApiClient.getClient(c)!!.create(ApiEndPoint::class.java)
             CoroutineScope(Dispatchers.Main).launch {
                 val call = async { apiEndPoint.getTokenCorutine("csrf_token") }
                 val result = call.await()

                 if (result != null) {
                     if (result.isSuccessful) {
                         val tokenRes = async { result.body() }.await()
                         if (tokenRes != null) {
                             csrf_token = tokenRes.csrfToken
                             dialog?.dismiss()
                         } else {
                             corotineToken(c)
                         }
                     } else {
                         corotineToken(c)
                     }
                 }
             }
         }catch (e:Exception){
             Log.d("Error","${e.message}")
             corotineToken(c)
         }
     }
     fun androidToken(){
         FirebaseMessaging.getInstance().isAutoInitEnabled = true
         FirebaseMessaging.getInstance().token
             .addOnCompleteListener(OnCompleteListener { task ->
                 if (!task.isSuccessful) {
                     Toast.makeText(this@LoginActivity,"Error : $task.exception", Toast.LENGTH_SHORT).show()

                     return@OnCompleteListener
                 }
                 // Get new Instance ID token
                 android_token = task.result
             })
     }
    private suspend fun userLogin(username:String,password:String,token:String,phToken:String,appVersion:String,appName:String){
        var mainPage= Intent(this@LoginActivity,MainPageActivity::class.java)
        GlobalScope.launch(IO) {
            var apiEndPoint = ApiClientTwo.getClient(this@LoginActivity)!!.create(ApiEndPoint::class.java)
                var def = async { apiEndPoint.lpLogin(username,password,token,phToken,appVersion,appName)}
            withContext(Main){
                var res =def.await().body()
                if(res!=null){
                    var l= res.login
                    if(l!=null){
                        u = l.userLogin
                        if(l.success!!){
                            PrefsUtil.getInstance()
                                .setBooleanState("IS_LOGGED_IN",true)
                            PrefsUtil.getInstance()
                                .setStringState(PrefsUtil.USER_NAME,
                                    u?.username)
                            PrefsUtil.getInstance()
                                .setStringState(PrefsUtil.NAMA_LENGKAP,
                                    u?.namaLengkap)
                            PrefsUtil.getInstance()
                                .setStringState(PrefsUtil.DEPT,
                                    u?.department)
                            PrefsUtil.getInstance()
                                .setStringState(PrefsUtil.SECTION,
                                    u?.section)
                            PrefsUtil.getInstance()
                                .setStringState(PrefsUtil.RULE,
                                    u?.rule)
                            PrefsUtil.getInstance()
                                .setStringState(PrefsUtil.LEVEL,
                                    u?.level)
                            PrefsUtil.getInstance()
                                .setStringState(PrefsUtil.NIK,
                                    u?.nik)
                            if(u?.photoProfile!=null){
                                PrefsUtil.getInstance().setBooleanState(PrefsUtil.PHOTO_PROFILE, true)
                            }else{
                                PrefsUtil.getInstance().setBooleanState(PrefsUtil.PHOTO_PROFILE, false)
                            }
                            PrefsUtil.getInstance()
                                .setStringState(PrefsUtil.PHOTO_URL, u?.photoProfile)
                            Toasty.success(this@LoginActivity,"Login Success ",Toasty.LENGTH_LONG).show()
                            PopupUtil.dismissDialog()
                            dialog?.dismiss()
                            startActivity(mainPage)
                            finish()
//                            tvErrorLog.text = "$username | $password | $csrf_token | $app_version | abpSystem | ${u} | ${this} "
                            dialog!!.dismiss()
                        }else{
                            dialog!!.dismiss()
                            clearForm()
                            Toasty.error(this@LoginActivity,"Username atau Password Salah ",Toasty.LENGTH_LONG).show()
                        }
                    }else{
                        dialog!!.dismiss()
                        clearForm()
                        Toasty.error(this@LoginActivity,"Username atau Password Salah ",Toasty.LENGTH_LONG).show()
                    }
                }else{
                    dialog!!.dismiss()
                    clearForm()
                    Toasty.error(this@LoginActivity,"Username atau Password Salah ",Toasty.LENGTH_LONG).show()
                }
            }

                }
    }
     private fun loginNew(username:String,password:String,token:String,phToken:String,appVersion:String,appName:String){
             var apiEndPoint = ApiClientTwo.getClient(this@LoginActivity)!!.create(ApiEndPoint::class.java)
             var def = apiEndPoint.loginNew(username,password,token,phToken,appVersion,appName)
            def.enqueue(object:Callback<AuthAppResponse>{
                override fun onResponse(
                    call: Call<AuthAppResponse>,
                    response: Response<AuthAppResponse>
                ) {
                    tvErrorLog.text = "$username | $password | $csrf_token | $app_version | abpSystem | ${response.body()} | ${this} "
                }

                override fun onFailure(call: Call<AuthAppResponse>, t: Throwable) {
                    Toasty.error(this@LoginActivity,"${t.message}",1000).show()

                }

            })
     }
    private suspend fun getUserLogin(username: String){
        GlobalScope.launch(Dispatchers.Main) {
            var api = ApiClientTwo.getClient(this@LoginActivity)!!.create(ApiEndPoint::class.java)
            try {
                var def = async { api.lpUserLogin(username) }
                def.await().let {
                    Toasty.info(this@LoginActivity,"$username | $csrf_token | $app_version | abpSystem | ${it.body()} | ${this} ",1000).show()
                }
            }catch (e:Exception){
                Toasty.error(this@LoginActivity,"${e.message}",1000).show()

            }
        }
    }
    private fun clearForm(){
        InUsername.text=null;
        InPassword.text=null;
    }
    private fun isValidatedAll()  :Boolean{
        clearError()
        if(InUsername.text!!.isEmpty()){
            tilUsername.error="Please Input Someting"
            InUsername.requestFocus()
            return false
        }
        if(InPassword.text!!.isEmpty()){
            tilPassword.error="Please Input Someting"
            InPassword.requestFocus()
            return false
        }
        PopupUtil.dismissDialog()
        return true
    }

    private fun clearError() {
        tilUsername.error=null
        tilPassword.error=null
    }
     private suspend fun loadingDialog(c:Context){
         var  mDialogView = LayoutInflater.from(c).inflate(R.layout.loading_abp,null)
         val mBuilder = AlertDialog.Builder(c)
         var loadingAbp = mDialogView?.findViewById<View>(R.id.loadingAbp) as ImageView
         Glide.with(c).load(R.drawable.abp).into(loadingAbp)
         mBuilder.setView(mDialogView)
         dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
         dialog?.setCanceledOnTouchOutside(false)
         dialog?.setCancelable(false)
         dialog = mBuilder.show()
     }
}

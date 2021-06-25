package com.misit.abpenergy.Login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.misit.abpenergy.Api.ApiClient
import com.misit.abpenergy.Api.ApiEndPoint
import com.misit.abpenergy.Master.ListUserActivity
import com.misit.abpenergy.NewIndexActivity
import com.misit.abpenergy.R
import com.misit.abpenergy.Rkb.Response.CsrfTokenResponse
import com.misit.abpenergy.Rkb.Response.UserResponse
import com.misit.abpenergy.Utils.ConfigUtil
import com.misit.abpenergy.Utils.PopupUtil
import com.misit.abpenergy.Utils.PrefsUtil
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.register_layout.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class LoginActivity : AppCompatActivity(),View.OnClickListener
 {
     private var csrf_token : String?=""
     private var android_token : String?=""
     private var app_version : String?=""
     private var companyDipilih:String?=null
     private var idCompany:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        companyDipilih=""
        idCompany=""
        PrefsUtil.initInstance(this)
        ConfigUtil.changeColor(this)

        loginBtn.setOnClickListener(this)
        InPassword.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                //Perform Code
                if(isValidatedAll()) {
                    loginSubmit(
                        InUsername.text.toString().trim(),
                        InPassword.text.toString().trim()
                    )
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
                loginSubmit(InUsername.text.toString().trim(),InPassword.text.toString().trim())
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
         val dialog =mBuilder.show()
         dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
         mDialogView.btnAbp?.setOnClickListener { registerUser() }
         mDialogView.btnMitra?.setOnClickListener { registerUserMitra() }
         mDialogView.btnDismis?.setOnClickListener { dialog.dismiss() }

     }
    override fun onResume() {
        if(ConfigUtil.cekKoneksi(this)){
            getToken()
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
        Use@ try {
            val pInfo: PackageInfo = this.getPackageManager().getPackageInfo(packageName, 0)
            app_version = pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    fun cekKoneksi(context: Context):Boolean{
        val connectivityManager= context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        return networkInfo != null && networkInfo.isConnected
    }

    private fun getToken() {
        val apiEndPoint = ApiClient.getClient(this)!!.create(ApiEndPoint::class.java)
        val call = apiEndPoint.getToken("csrf_token")
        call?.enqueue(object : Callback<CsrfTokenResponse> {
            override fun onFailure(call: Call<CsrfTokenResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity,"Error : $t", Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(
                call: Call<CsrfTokenResponse>,
                response: Response<CsrfTokenResponse>
            ) {
                csrf_token = response.body()?.csrfToken
            }
        })
    }
     fun androidToken(){
         FirebaseMessaging.getInstance().isAutoInitEnabled = true
         FirebaseInstanceId.getInstance().instanceId
             .addOnCompleteListener(OnCompleteListener { task ->
                 if (!task.isSuccessful) {
                     Toast.makeText(this@LoginActivity,"Error : $task.exception", Toast.LENGTH_SHORT).show()

                     return@OnCompleteListener
                 }
                 // Get new Instance ID token
                 android_token = task.result?.token
             })
     }
    fun loginSubmit(userIn:String,passIn:String){
        PopupUtil.showLoading(this@LoginActivity,"Logging In","Please Wait")
        var intent = Intent(this, NewIndexActivity::class.java)
        val apiEndPoint = ApiClient.getClient(this)!!.create(ApiEndPoint::class.java)
        val call = apiEndPoint.loginChecklogin(
                userIn,
                passIn,
                csrf_token,
            android_token,
            app_version,
            "abpSystem")
        call?.enqueue(object : Callback<UserResponse>{
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                PopupUtil.dismissDialog()
                Toasty.error(this@LoginActivity,"Login Error ",Toasty.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                var userResponse = response.body()
                if(userResponse!=null){
                    PopupUtil.dismissDialog()
                    if(userResponse.success!!){
                        if(userResponse.user!=null){
                            PrefsUtil.getInstance()
                                .setBooleanState("IS_LOGGED_IN",
                                    true)
                            PrefsUtil.getInstance()
                                .setStringState(PrefsUtil.USER_NAME,
                                    userResponse.user?.username)
                            PrefsUtil.getInstance()
                                .setStringState(PrefsUtil.NAMA_LENGKAP,
                                    userResponse.user?.namaLengkap)
                            PrefsUtil.getInstance()
                                .setStringState(PrefsUtil.DEPT,
                                    userResponse.user?.department)
                            PrefsUtil.getInstance()
                                .setStringState(PrefsUtil.SECTION,
                                    userResponse.user?.section)
                            PrefsUtil.getInstance()
                                .setStringState(PrefsUtil.RULE,
                                    userResponse.user?.rule)
                            PrefsUtil.getInstance()
                                .setStringState(PrefsUtil.LEVEL,
                                    userResponse.user?.level)
                            PrefsUtil.getInstance()
                                .setStringState(PrefsUtil.NIK,
                                    userResponse.user?.nik)
                            if(userResponse.user?.photoProfile!=null){
                                PrefsUtil.getInstance().setBooleanState(PrefsUtil.PHOTO_PROFILE, true)
                            }else{
                                PrefsUtil.getInstance().setBooleanState(PrefsUtil.PHOTO_PROFILE, false)
                            }
                            PrefsUtil.getInstance()
                                .setStringState(PrefsUtil.PHOTO_URL, userResponse.user?.photoProfile)
                            Toasty.success(this@LoginActivity,"Login Success ",Toasty.LENGTH_LONG).show()
                            PopupUtil.dismissDialog()
                            startActivity(intent)
                            finish()
                        }else{
                            Toasty.error(this@LoginActivity,"Username Or Password Wrong!",Toasty.LENGTH_SHORT).show()
                            clearForm()
                            InUsername.requestFocus()
                            PopupUtil.dismissDialog()
                        }
                    }else{
                        Toasty.error(this@LoginActivity,"Username Or Password Wrong!",Toasty.LENGTH_SHORT).show()
                        clearForm()
                        InUsername.requestFocus()
                        PopupUtil.dismissDialog()
                    }
                }else{
                    Toasty.error(this@LoginActivity,"Username Or Password Wrong!",Toasty.LENGTH_SHORT).show()
                    clearForm()
                    InUsername.requestFocus()
                    PopupUtil.dismissDialog()
                }
            }
        })
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
}

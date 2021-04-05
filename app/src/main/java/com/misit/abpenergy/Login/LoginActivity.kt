package com.misit.abpenergy.Login

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.misit.abpenergy.Api.ApiClient
import com.misit.abpenergy.Api.ApiEndPoint
import com.misit.abpenergy.IndexActivity
import com.misit.abpenergy.R
import com.misit.abpenergy.Rkb.Response.CsrfTokenResponse
import com.misit.abpenergy.Rkb.Response.UserResponse
import com.misit.abpenergy.Utils.PopupUtil
import com.misit.abpenergy.Utils.PrefsUtil
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity(),View.OnClickListener
 {
     private var csrf_token : String?=""
     private var android_token : String?=""
     private var app_version : String?=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        PrefsUtil.initInstance(this)
        val window: Window = this@LoginActivity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this@LoginActivity, R.color.skyBlue)
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
    }

    override fun onClick(v: View?) {
        if(v?.id== R.id.loginBtn){
            if(isValidatedAll()){
                loginSubmit(InUsername.text.toString().trim(),InPassword.text.toString().trim())
            }
        }
        if(v?.id==R.id.tvLpSandi){
            var intent = Intent(this@LoginActivity,ForgotPasswordActivity::class.java)
            if(InUsername.text!=null){
                intent.putExtra(ForgotPasswordActivity.USERNAME,InUsername.text)
            }
            startActivity(intent)
        }
    }
    override fun onResume() {
        if(cekKoneksi(this)){
            getToken()
            androidToken()
            versionApp()
            tvVersionCode.text="V$app_version"
        }else{
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
        var intent = Intent(this, IndexActivity::class.java)
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

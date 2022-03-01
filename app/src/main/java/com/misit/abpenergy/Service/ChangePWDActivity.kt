package com.misit.abpenergy.Serviceimport android.content.Contextimport android.content.Intentimport androidx.appcompat.app.AppCompatActivityimport android.os.Bundleimport android.view.KeyEventimport android.view.Viewimport android.widget.Toastimport com.misit.abpenergy.Api.ApiClientimport com.misit.abpenergy.Api.ApiEndPointimport com.misit.abpenergy.Api.ApiEndPointTwoimport com.misit.abpenergy.Login.LoginActivityimport com.misit.abpenergy.Login.Response.DaftarAkunResponseimport com.misit.abpenergy.Rimport com.misit.abpenergy.HGE.Rkb.Response.CsrfTokenResponseimport com.misit.abpenergy.Utils.PrefsUtilimport es.dmoral.toasty.Toastyimport kotlinx.android.synthetic.main.activity_change_p_w_d.*import retrofit2.Callimport retrofit2.Callbackimport retrofit2.Responseclass ChangePWDActivity : AppCompatActivity(),View.OnClickListener {    private var csrf_token : String?=""    private var username : String?=null    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        setContentView(R.layout.activity_change_p_w_d)        title ="Ganti Sandi"        username = intent.getStringExtra("USERNAME")        inUser.setText(username)        val actionBar= supportActionBar        actionBar?.setDisplayHomeAsUpEnabled(true)        btnSaveNewPass.setOnClickListener(this)        inRetypePass.setOnKeyListener { v, keyCode, event ->            if (keyCode == KeyEvent.KEYCODE_ENTER) {                //Perform Code                if(isValidatedAll()) {                    val oldPass = inOldPass.text.toString()                    val inNewPass = inNewPass.text.toString()                    val inRetypePass = inRetypePass.text.toString()                    if(inNewPass != inRetypePass) {                        Toasty.error(this@ChangePWDActivity,"Sandi Baru Tidak Sama").show()                    }else{                        if(oldPass==inNewPass){                            Toasty.error(this@ChangePWDActivity,"Sandi Baru Sama Dengan Sandi Lama!").show()                        }else{                            simpanSandi(username!!,oldPass,inNewPass,inRetypePass,this@ChangePWDActivity)                        }                    }                }                return@setOnKeyListener true            }            false        }    }    override fun onClick(v: View?) {        if(v?.id==R.id.btnSaveNewPass){            val oldPass = inOldPass.text.toString()            val inNewPass = inNewPass.text.toString()            val inRetypePass = inRetypePass.text.toString()            if(inNewPass != inRetypePass) {                Toasty.error(this@ChangePWDActivity,"Sandi Baru Tidak Sama").show()            }else{                if(oldPass==inNewPass){                    Toasty.error(this@ChangePWDActivity,"Sandi Baru Sama Dengan Sandi Lama!").show()                }else{                    simpanSandi(username!!,oldPass,inNewPass,inRetypePass,this@ChangePWDActivity)                }            }        }    }    private fun simpanSandi(username:String,oldPass:String,newPass:String,reNewPass:String,c:Context) {        if(!isValidatedAll()) {            return        }        val apiEndPoint = ApiClient.getClient(c)!!.create(ApiEndPointTwo::class.java)        val call = apiEndPoint.saveNewSandi(username,oldPass,newPass,reNewPass,csrf_token)        call?.enqueue(object:Callback<DaftarAkunResponse>{            override fun onResponse(                call: Call<DaftarAkunResponse>,                response: Response<DaftarAkunResponse>            ) {                val r = response.body()                if(r!=null){                    if(r.success){                        Toasty.success(c,"Sandi Anda Telah Dirubah! Silahkan Masuk Lagi").show()                        if (PrefsUtil.getInstance().getBooleanState(                                "IS_LOGGED_IN", true                            )                        ) {                            PrefsUtil.getInstance().setBooleanState(                                "IS_LOGGED_IN", false                            )                            PrefsUtil.getInstance().setStringState(                                PrefsUtil.USER_NAME, null                            )                            val intent = Intent(c, LoginActivity::class.java)                            startActivity(intent)                            finish()                        }                    }else{                        Toasty.error(this@ChangePWDActivity,"Gagal, Coba Lagi!").show()                    }                }else{                    Toasty.error(this@ChangePWDActivity,"Gagal, Coba Lagi!").show()                }            }            override fun onFailure(call: Call<DaftarAkunResponse>, t: Throwable) {            }        })    }    override fun onSupportNavigateUp(): Boolean {        onBackPressed()        return super.onSupportNavigateUp()    }    private fun isValidatedAll()  :Boolean {        clearError()        if (inOldPass.text!!.isEmpty()) {            tilOldPass.error = "Please Input Someting"            inOldPass.requestFocus()            return false        }        if (inNewPass.text!!.isEmpty()) {            tilNewPass.error = "Please Input Someting"            inNewPass.requestFocus()            return false        }        if (inRetypePass.text!!.isEmpty()) {            tilRetypePass.error = "Please Input Someting"            inRetypePass.requestFocus()            return false        }        return true    }    private fun clearError() {        tilOldPass.error=null        tilNewPass.error=null        tilRetypePass.error=null    }    override fun onResume() {        getToken(this@ChangePWDActivity)        super.onResume()    }    fun getToken(context: Context) {        val apiEndPoint = ApiClient.getClient(context)!!.create(ApiEndPoint::class.java)        val call = apiEndPoint.getToken("csrf_token")        call?.enqueue(object : Callback<CsrfTokenResponse> {            override fun onFailure(call: Call<CsrfTokenResponse>, t: Throwable) {                Toast.makeText(context, "Error : $t", Toast.LENGTH_SHORT).show()                csrf_token=null            }            override fun onResponse(                call: Call<CsrfTokenResponse>,                response: Response<CsrfTokenResponse>            ) {                csrf_token= response.body()?.csrfToken            }        })    }}
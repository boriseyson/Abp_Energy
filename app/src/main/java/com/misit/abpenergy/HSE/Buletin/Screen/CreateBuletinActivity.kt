package com.misit.abpenergy.HSE.Buletin.Screen

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.misit.abpenergy.Api.ApiClientTwo
import com.misit.abpenergy.Api.ApiEndPointTwo
import com.misit.abpenergy.Main.Response.MessageInfoItem
import com.misit.abpenergy.R
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_create_buletin.*
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class CreateBuletinActivity : AppCompatActivity(),View.OnClickListener {
    private var dialog:AlertDialog?=null
    private var idInfo:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_buletin)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        var formName = intent.getStringExtra("formName")

        if(formName!=null){
            if(formName=="Ubah"){
                var messageInfoItem = intent.getSerializableExtra("buletin") as MessageInfoItem
                idInfo = messageInfoItem.idInfo.toString()
                if(idInfo!=null){
                    tilIdInfoForm.visibility = View.VISIBLE
                    inIdInfoForm.setText("$idInfo")
                    inJudulForm.setText(messageInfoItem.judul)
                    inContentForm.setText(messageInfoItem.pesan)
                    btnBuletinForm.setText("Update")
                }else{
                    tilIdInfoForm.visibility = View.GONE
                    btnBuletinForm.setText("Simpan")
                }
            }else{
                tilIdInfoForm.visibility = View.GONE
                btnBuletinForm.setText("Simpan")
            }
         title = formName
        }
        btnBuletinForm.setOnClickListener(this@CreateBuletinActivity)
    }
    private fun saveBuletin(){
        dialog=null
        loadingDialog(this@CreateBuletinActivity)
        if(isValidate()){
            var judul = inJudulForm.text.toString()
            var pesan = inContentForm.text.toString()
            var api = ApiClientTwo.getClient(this@CreateBuletinActivity)!!.create(ApiEndPointTwo::class.java)
            GlobalScope.launch(Dispatchers.IO) {
                var r = async { api.saveBuletinApi(judul,pesan) }
                if(r.await()!=null){
                    var re = r.await()
                    if(re!!.isSuccessful){
                        if (re!!.body()!!.success!!){
                            withContext(Dispatchers.Main){
                                dialog?.dismiss()
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        }else{
                            withContext(Dispatchers.Main){
                                dialog?.dismiss()
                                Toasty.error(this@CreateBuletinActivity,"Gagal Menyimpan, Coba Lagi").show()
                            }
                        }
                    }else{
                        withContext(Dispatchers.Main){
                            dialog?.dismiss()
                            Toasty.error(this@CreateBuletinActivity,"Gagal Menyimpan, Coba Lagi").show()
                        }
                    }
                }else{
                    withContext(Dispatchers.Main){
                        dialog?.dismiss()
                        Toasty.error(this@CreateBuletinActivity,"Gagal Menyimpan, Coba Lagi").show()
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        if(v?.id==R.id.btnBuletinForm){
            if(idInfo!=null){
                updateBuletin()
            }else{
                saveBuletin()
            }
        }
    }

    private fun updateBuletin() {
        dialog=null
        loadingDialog(this@CreateBuletinActivity)
        if(isValidate()){
            var judul = inJudulForm.text.toString()
            var pesan = inContentForm.text.toString()
            var data = MessageInfoItem()
            data.idInfo = idInfo?.toInt()
            data.judul = judul
            data.pesan = pesan
            var api = ApiClientTwo.getClient(this@CreateBuletinActivity)!!.create(ApiEndPointTwo::class.java)
            GlobalScope.launch(Dispatchers.IO) {
                var r = async { api.updateBuletinApi(data) }
                if(r.await()!=null){
                    var re = r.await()
                    if(re!!.isSuccessful){
                        if (re!!.body()!!.success!!){
                            withContext(Dispatchers.Main){
                                dialog?.dismiss()
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        }else{
                            withContext(Dispatchers.Main){
                                dialog?.dismiss()
                                Toasty.error(this@CreateBuletinActivity,"Gagal Memperbaharui, Coba Lagi").show()
                            }
                        }
                    }else{
                        withContext(Dispatchers.Main){
                            dialog?.dismiss()
                            Toasty.error(this@CreateBuletinActivity,"Gagal Memperbaharui, Coba Lagi").show()
                        }
                    }
                }else{
                    withContext(Dispatchers.Main){
                        dialog?.dismiss()
                        Toasty.error(this@CreateBuletinActivity,"Gagal Memperbaharui, Coba Lagi").show()
                    }
                }
            }
        }else{
            dialog?.dismiss()
        }
    }

    private fun isValidate():Boolean{
        tilJudulForm.error = null
        tilContentForm.error = null
        if(inJudulForm.text.toString()==null || inJudulForm.text.toString()==""){
            tilJudulForm.error = "Judul Tidak Boleh Kosong!"
            return false
        }else{
            Log.d("Form","${inJudulForm.text}")
        }
        if(inContentForm.text.toString()==null || inContentForm.text.toString()==""){
                tilContentForm.error = "Konten Tidak Boleh Kosong!"
                return false
        }else{
            Log.d("Form","${inContentForm.text}")

        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        finish()
        super.onBackPressed()
    }
    private fun loadingDialog(c: Context){
        var  mDialogView = LayoutInflater.from(c).inflate(R.layout.loading_abp,null)
        val mBuilder = AlertDialog.Builder(c)
        var loadingAbp = mDialogView?.findViewById<View>(R.id.loadingAbp) as ImageView
        Glide.with(c).load(R.drawable.abp).into(loadingAbp)
        mBuilder.setView(mDialogView)
        dialog = mBuilder.show()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)
    }
}
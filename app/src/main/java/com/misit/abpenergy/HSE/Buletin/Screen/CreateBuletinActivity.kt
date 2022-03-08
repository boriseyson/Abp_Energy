package com.misit.abpenergy.HSE.Buletin.Screen

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.misit.abpenergy.Api.ApiClientTwo
import com.misit.abpenergy.Api.ApiEndPointTwo
import com.misit.abpenergy.R
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_create_buletin.*
import kotlinx.coroutines.*

class CreateBuletinActivity : AppCompatActivity(),View.OnClickListener {
    private var dialog:AlertDialog?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_buletin)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        var formName = intent.getStringExtra("formName")
        if(formName!=null){
         title = formName
        }
        btnBuletinForm.setOnClickListener(this@CreateBuletinActivity)
    }
    private fun saveBuletin(){
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
            saveBuletin()
        }
    }
    private fun isValidate():Boolean{
        tilJudulForm.error = null
        tilContentForm.error = null
        if(inJudulForm.text.toString()==null){
            tilJudulForm.error = "Judul Tidak Boleh Kosong!"
            return false
        }else if(inContentForm.text.toString()==null){
                tilContentForm.error = "Konten Tidak Boleh Kosong!"
                return false
            }
        return true
    }
    private fun loadingDialog(c: Context){
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
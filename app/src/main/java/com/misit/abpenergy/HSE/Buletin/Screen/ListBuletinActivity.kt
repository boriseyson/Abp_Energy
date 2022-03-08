package com.misit.abpenergy.HSE.Buletin.Screen

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.misit.abpenergy.Api.ApiClientTwo
import com.misit.abpenergy.Api.ApiEndPointTwo
import com.misit.abpenergy.HSE.Buletin.Adapter.BuletinAdapter
import com.misit.abpenergy.HSE.Buletin.ViewModels.BuletinViewModel
import com.misit.abpenergy.Main.Response.MessageInfoItem
import com.misit.abpenergy.R
import com.misit.abpenergy.Utils.Constants
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_list_buletin.*
import kotlinx.android.synthetic.main.index_new.*
import kotlinx.coroutines.*

class ListBuletinActivity : AppCompatActivity(),View.OnClickListener,BuletinAdapter.OnItemClickListener {
    private var adapter:BuletinAdapter?=null
    private var listInfo:MutableList<MessageInfoItem>?=null
    private var dialog:AlertDialog?=null
    lateinit private var viewmodel:BuletinViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        title="Buletin"
        setContentView(R.layout.activity_list_buletin)
        listInfo= ArrayList()
        adapter = BuletinAdapter(this@ListBuletinActivity, listInfo!!)
        val linearLayoutManager = LinearLayoutManager(this@ListBuletinActivity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rvListInfo?.layoutManager = linearLayoutManager
        rvListInfo?.adapter=adapter
        viewmodel = ViewModelProvider(this@ListBuletinActivity).get(BuletinViewModel::class.java)
        loadViewModel()
        btnAddBuletin.setOnClickListener(this@ListBuletinActivity)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
    private fun loadViewModel(){
        loadingDialog(this@ListBuletinActivity)
        viewmodel?.loadInfo().observe(this@ListBuletinActivity,{ listPesan->
            listInfo?.clear()
            if(listPesan!=null){
                listInfo?.addAll(listPesan)
                adapter?.notifyDataSetChanged()
                dialog?.dismiss()
            }

        })
        viewmodel?.loadMessageInfo(this@ListBuletinActivity)
    }
    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        finish()
        super.onBackPressed()
    }

    override fun onClick(v: View?) {
        if(v?.id==R.id.btnAddBuletin){
            var newBuletin = Intent(this@ListBuletinActivity,CreateBuletinActivity::class.java)
            newBuletin.putExtra("formName","Buat Buletin")
            startActivityForResult(newBuletin,Constants.INFOHARIAN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK && requestCode==Constants.INFOHARIAN){
            loadingDialog(this@ListBuletinActivity)
            viewmodel?.loadMessageInfo(this@ListBuletinActivity)
        }
        super.onActivityResult(requestCode, resultCode, data)
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

    override fun onItemClick(idInfo: String) {
        Log.d("Diclick","$idInfo")
        val list = arrayOf("Ubah","Hapus","Tampilkan / Sembunyikan")
        showDialogOption(this@ListBuletinActivity,list,idInfo)
    }
    private fun showDialogOption(c:Context, title: Array<String>,idInfo: String){
        val alertDialog = AlertDialog.Builder(c)
        alertDialog.setTitle("Silahkan Pilih")
        alertDialog!!.setItems(title, { dialog, which ->
            when (which) {
                0 ->ubahInfo(c,idInfo)
                1 ->hapusInfo(c,idInfo)
                2 ->showHideInfo(c,idInfo)
            }
        })
        alertDialog.setOnDismissListener {
            first_menu_item.setColorFilter(ContextCompat.getColor(c, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN)
        }
        alertDialog.create()
        alertDialog.show()
    }
    private fun ubahInfo(c:Context,idInfo: String){
        var ubahInfo = Intent(c,CreateBuletinActivity::class.java)
        ubahInfo.putExtra("formName","Ubah")
        ubahInfo.putExtra("idInfo",idInfo)
        startActivityForResult(ubahInfo,Constants.INFOHARIAN)
    }
    private fun hapusInfo(c:Context,idInfo: String){
        areYouSure("Hapus","Apakah Anda Yakin Menghapus Ini?",idInfo,c)
    }
    private fun showHideInfo(c:Context,idInfo: String){

    }
    private fun areYouSure(titleDialog: String, msgDialog: String,idInfo: String,c: Context){
        val builder = AlertDialog.Builder(c)
        builder.setTitle(titleDialog)
        builder.setMessage(msgDialog)
        builder.setPositiveButton("Tidak") { dialog, which ->
            dialog.dismiss()
        }
        builder.setNegativeButton("Ya") { dialog, which ->
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    var api = ApiClientTwo.getClient(c)!!.create(ApiEndPointTwo::class.java)
                    var r = async { api.deleteBuletinApi(idInfo) }.await()
                    if(r!=null){
                        if(r.isSuccessful){
                            if(r.body()!!.success!!){
                                withContext(Dispatchers.Main){
                                    Toasty.success(c,"Berhasil di hapus!").show()
                                    dialog.dismiss()
                                    viewmodel?.loadMessageInfo(c)
                                }
                            }else{
                                withContext(Dispatchers.Main){
                                    Toasty.error(c,"Gagal Menghapus, Coba Lagi!").show()
                                    dialog.dismiss()
                                }
                            }
                        }else
                        {
                            withContext(Dispatchers.Main){
                                Toasty.error(c,"Gagal Menghapus, Coba Lagi!").show()
                                dialog.dismiss()
                            }
                        }
                    }
                }catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toasty.error(c,"Gagal Menghapus, Coba Lagi!").show()
                        dialog.dismiss()
                    }
                }
            }
        }
        builder.show()
    }
}
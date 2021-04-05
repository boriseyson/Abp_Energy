package com.misit.abpenergy.Sarpras

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.misit.abpenergy.Api.ApiClient
import com.misit.abpenergy.Api.ApiEndPoint
import com.misit.abpenergy.R
import com.misit.abpenergy.Sarpras.Adapter.ListPenumpangDetailAdapter
import com.misit.abpenergy.Sarpras.Adapter.PenumpangAdapter
import com.misit.abpenergy.Sarpras.Realm.PenumpangModel
import com.misit.abpenergy.Sarpras.SaranaResponse.PenumpangListModel
import com.misit.abpenergy.Sarpras.SarprasResponse.DataPenumpang
import com.misit.abpenergy.Sarpras.SarprasResponse.LihatSarprasResponse
import com.misit.abpenergy.Utils.PopupUtil
import es.dmoral.toasty.Toasty
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_detail_sarpras.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailSarprasActivity : AppCompatActivity() {
    private var adapter: ListPenumpangDetailAdapter? = null
    private var list:MutableList<PenumpangModel>?=null
    private var arrList:ArrayList<String>?=null
    lateinit var recyclerView: RecyclerView
    lateinit var realm : Realm
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_sarpras)

        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        var noidOut = intent.getStringExtra(noidOut)
        list = ArrayList()
        arrList = ArrayList()
        adapter = ListPenumpangDetailAdapter(this,list!!)
        recyclerView = findViewById(R.id.rvPenumpangList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        loadDetailSarpras(noidOut)

    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
    override fun onResume() {
        Realm.init(this)
        realm = Realm.getDefaultInstance()
        super.onResume()
    }

    override fun onStop() {
        realm.close()
        super.onStop()
    }
    fun loadDetailSarpras(noIdOut:String){
        PopupUtil.showLoading(this@DetailSarprasActivity,"Loading","Mengambil Data!")
        val apiEndPoint = ApiClient.getClient(this@DetailSarprasActivity)!!.create(ApiEndPoint::class.java)
        val call = apiEndPoint.getLihatSarpras(noIdOut)
        call?.enqueue(object : Callback<LihatSarprasResponse?> {
            override fun onFailure(call: Call<LihatSarprasResponse?>, t: Throwable) {
                Toast.makeText(this@DetailSarprasActivity, "Failed to Fetch Data\n" +
                        "e: $t", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<LihatSarprasResponse?>,
                response: Response<LihatSarprasResponse?>
            ) {
                val detailSarpras = response.body()
                if(detailSarpras!=null){
                    title = "Pemohon : ${detailSarpras.userPemohon}"
                    tvNoLV.text = detailSarpras.noLv
                    namaDriver(detailSarpras.driver!!)
                    tvKeperluan.text = detailSarpras.keperluan
                    tvTanggalKeluar.text = detailSarpras.tglOut
                    tvJamKeluar.text = detailSarpras.jamOut
                    if(detailSarpras.tglIn!=null) {
                        tvTanggalKembali.text = detailSarpras.tglIn
                    }else{
                        tvTanggalKembali.text = "-"
                    }
                    if(detailSarpras.jamIn!=null){
                        tvJamKembali.text = detailSarpras.jamIn
                    }else{
                        tvJamKembali.text = "-"
                    }
                    var penumpangs = detailSarpras.penumpangOut!!.split(",").toTypedArray()
                    penumpangs.forEach {
                        loadData(it)
                    }

                    PopupUtil.dismissDialog()


                }
            }

        })


        }
    fun namaDriver(niknya:String){
        val realmRes = realm.where(PenumpangModel::class.java)
            .equalTo("nik",niknya)
            .findFirst()
        tvDriver.text = realmRes!!.nama
    }
    fun loadData(niknya:String){

        val realmRes = realm.where(PenumpangModel::class.java)
            .equalTo("nik",niknya)
            .findAll()
        list?.addAll(realmRes)
            adapter?.notifyDataSetChanged()
    }

    companion object{
        var noidOut = "noidOut"
    }
}

package com.misit.abpenergy.HGE.Sarpras.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.misit.abpenergy.Api.ApiClient
import com.misit.abpenergy.Api.ApiEndPoint
import com.misit.abpenergy.R
import com.misit.abpenergy.HGE.Sarpras.Adapter.ListPenumpangDetailAdapter
import com.misit.abpenergy.HGE.Sarpras.SQLite.DataSource.PenumpangDataSource
import com.misit.abpenergy.HGE.Sarpras.SaranaResponse.PenumpangListModel
import com.misit.abpenergy.HGE.Sarpras.SarprasResponse.LihatSarprasResponse
import com.misit.abpenergy.Utils.PopupUtil
import kotlinx.android.synthetic.main.activity_detail_sarpras.*
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailSarprasActivity : AppCompatActivity() {
    private var adapter: ListPenumpangDetailAdapter? = null
    private var list:MutableList<PenumpangListModel>?=null
    private var arrList:ArrayList<String>?=null
    lateinit var recyclerView: RecyclerView
    val fmt: DateTimeFormatter = DateTimeFormat.forPattern("d MMMM, yyyy")
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
        if (noidOut != null) {
            loadDetailSarpras(noidOut)
        }

    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
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
                    if(detailSarpras.noPol==null){
                        lvTV.text="No LV"
                        driverTX.text="Driver"
                        tvNoLV.text = detailSarpras.noLv!!.capitalize()
                        namaDriver(detailSarpras.driver!!)
                    }else{
                        tvNoLV.text = detailSarpras.noLv
                        tvDriver.text = detailSarpras.driver
                        lvTV.text="Jenis Kendaraan"
                        driverTX.text="Merk Kendaraan"
                    }
                    tvKeperluan.text = detailSarpras.keperluan
                    tvTanggalKeluar.text = LocalDate.parse(detailSarpras.tglOut).toString(fmt)

                    tvJamKeluar.text = detailSarpras.jamOut
                    if(detailSarpras.tglIn!=null) {
                        tvTanggalKembali.text = LocalDate.parse(detailSarpras.tglIn).toString(fmt)
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
            var p = PenumpangDataSource(this@DetailSarprasActivity)
            var rowPenumpang = p.getItem(niknya)
            tvDriver.text = rowPenumpang!!.nama
    }
    fun loadData(niknya:String){
            var p = PenumpangDataSource(this@DetailSarprasActivity)
            var rowPenumpang = p.getItem(niknya)
            list?.add(PenumpangListModel(rowPenumpang.id,rowPenumpang.nik,rowPenumpang.nama,rowPenumpang.jabatan))
            adapter?.notifyDataSetChanged()
    }
    companion object{
        var noidOut = "noidOut"
    }
}

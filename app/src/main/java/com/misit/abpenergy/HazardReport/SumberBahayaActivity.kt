package com.misit.abpenergy.HazardReport

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.misit.abpenergy.Api.ApiClient
import com.misit.abpenergy.Api.ApiEndPoint
import com.misit.abpenergy.DataSource.LokasiDataSource
import com.misit.abpenergy.HazardReport.Adapter.HirarkiAdapter
import com.misit.abpenergy.HazardReport.Response.*
import com.misit.abpenergy.HazardReport.SQLite.DataSource.DetHirarkiDataSource
import com.misit.abpenergy.HazardReport.SQLite.DataSource.PengendalianDataSource
import com.misit.abpenergy.R
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_sumber_bahaya.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.SQLException

class SumberBahayaActivity : AppCompatActivity(),
HirarkiAdapter.OnItemClickListener {
    private var adapter: HirarkiAdapter? = null
    private var hirarkiList:MutableList<HirarkiItemFull>?=null
    private var hirarkiDipilih:String?=null
    private var call: Call<HirarkiResponse>?=null
    var detHirarkiList:MutableList<DetHirarkiItem>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sumber_bahaya)
        title="Pilih Sumber Pengendalian"
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        hirarkiList = ArrayList()
        hirarkiDipilih = intent.getStringExtra("hirarkiDipilh")
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        adapter = HirarkiAdapter(
            this,
            hirarkiDipilih,
            hirarkiList!!
        )
        val linearLayoutManager = LinearLayoutManager(this@SumberBahayaActivity)
        rvSumberBahaya?.layoutManager = linearLayoutManager
        rvSumberBahaya.adapter =adapter
        adapter?.setListener(this)
        pengendalianSQL(this)
    }

    private fun loadData() {
        val apiEndPoint = ApiClient.getClient(this@SumberBahayaActivity)!!.create(ApiEndPoint::class.java)
        call = apiEndPoint.getHirarkiPengendalian()
        call?.enqueue(object : Callback<HirarkiResponse?> {
            override fun onFailure(call: Call<HirarkiResponse?>, t: Throwable) {
                Toasty.error(this@SumberBahayaActivity,"Error : "+ t).show()
            }

            override fun onResponse(
                call: Call<HirarkiResponse?>,
                response: Response<HirarkiResponse?>
            ) {
                var hirarkiRes= response.body()
                if(hirarkiRes!=null){
                    Log.d("HIRARKI_LOG",hirarkiRes.toString())
                    if(hirarkiRes.hirarki!=null){
//                        hirarkiList?.addAll(hirarkiRes.hirarki!!)
                            adapter?.notifyDataSetChanged()
                    }
                }
            }

        })
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onItemClick(uid: String, pengendalian: String) {
        val intent: Intent = Intent()
        intent.putExtra("hirarkiID",uid)
        intent.putExtra("hirarkiDipilih",pengendalian)
        setResult(Activity.RESULT_OK,intent)
        finish()
    }
    private fun pengendalianSQL(c: Context){
        val pengendalian =
            PengendalianDataSource(c)
        try {
            val lokasiRow=pengendalian.getAll()
            lokasiRow.forEach{
                detHirarkiList=ArrayList()
                hirarkiList?.add(HirarkiItemFull(it.tglInput,it.flag,it.userInput,it.namaPengendalian,it.idHirarki,loadDetail("${it.idHirarki}",c)))
                adapter?.notifyDataSetChanged()
                Log.d("KemungkinanSQL","${hirarkiList}")
            }

        }catch (e: SQLException){
            Log.d("KemungkinanSQL",e.toString())
        }

    }
    fun  loadDetail(id_hirarki:String,c: Context): MutableList<DetHirarkiItem>? {
        detHirarkiList?.clear()
        var hirarkiDetDB = DetHirarkiDataSource(c)
        try {
            val getItem = hirarkiDetDB.getBy(id_hirarki)
            getItem.forEach {
                detHirarkiList?.add(DetHirarkiItem(it.keterangan,it.id_ket,it.time_input,it.ket_input,it.id_hirarki?.toInt()))
                Log.d("detHirarkiList", "${detHirarkiList}")
            }
        }catch (e: SQLiteException){
            e.message?.let { Log.d("ErrorDetail", it) }
        }

        return detHirarkiList;
    }

}

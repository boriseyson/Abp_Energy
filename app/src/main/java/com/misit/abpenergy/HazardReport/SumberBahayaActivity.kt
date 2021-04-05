package com.misit.abpenergy.HazardReport

import android.app.Activity
import android.content.Intent
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
import com.misit.abpenergy.HazardReport.Adapter.SumberBahayaAdapter
import com.misit.abpenergy.HazardReport.Response.SumberBahayaResponse
import com.misit.abpenergy.HazardReport.Response.SumberItem
import com.misit.abpenergy.R
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_new_hazard.*
import kotlinx.android.synthetic.main.activity_new_hazard.tvLokasi
import kotlinx.android.synthetic.main.activity_sumber_bahaya.*
import kotlinx.android.synthetic.main.hazard_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SumberBahayaActivity : AppCompatActivity(), View.OnClickListener,
SumberBahayaAdapter.OnItemClickListener {
    private var adapter: SumberBahayaAdapter? = null
    private var bahayaList:MutableList<SumberItem>?=null
    private var bahayaDipilih:String?=null
    private var call: Call<SumberBahayaResponse>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sumber_bahaya)
        title="Pilih Sumber Bahaya"
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        bahayaList = ArrayList()
        bahayaDipilih = intent.getStringExtra("bahayaDipilih")
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        adapter = SumberBahayaAdapter(
            this,
            bahayaDipilih,
            bahayaList!!
        )
        val linearLayoutManager = LinearLayoutManager(this@SumberBahayaActivity)
        rvSumberBahaya?.layoutManager = linearLayoutManager
        rvSumberBahaya.adapter =adapter
        adapter?.setListener(this)
        loadData()
    }

    private fun loadData() {
        val apiEndPoint = ApiClient.getClient(this@SumberBahayaActivity)!!.create(ApiEndPoint::class.java)
        call = apiEndPoint.getBahayaList()
        call?.enqueue(object : Callback<SumberBahayaResponse?> {
            override fun onFailure(call: Call<SumberBahayaResponse?>, t: Throwable) {
                Toasty.error(this@SumberBahayaActivity,"Error : "+ t).show()
            }

            override fun onResponse(
                call: Call<SumberBahayaResponse?>,
                response: Response<SumberBahayaResponse?>
            ) {
                var bahayaRes= response.body()
                if(bahayaRes!=null){
                    Log.d("BAHAYA_LOG",bahayaRes.toString())
                    if(bahayaRes.sumber!=null){
                        bahayaList?.addAll(bahayaRes.sumber!!)
                            adapter?.notifyDataSetChanged()
                    }
                }
            }

        })
    }

    override fun onClick(v: View?) {

    }

    override fun onItemClick(idBahaya: String?,bahaya: String?) {
        val intent: Intent = Intent()
        intent.putExtra("bahayaId",idBahaya)
        intent.putExtra("bahayaDipilih",bahaya)
        setResult(Activity.RESULT_OK,intent)
        finish()
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }


}

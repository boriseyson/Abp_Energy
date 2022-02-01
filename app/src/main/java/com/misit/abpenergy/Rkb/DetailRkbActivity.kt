package com.misit.abpenergy.Rkb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.misit.abpenergy.Main.Adapter.DetailRkbAdapter
import com.misit.abpenergy.Api.ApiClient
import com.misit.abpenergy.Api.ApiEndPoint
import com.misit.abpenergy.R
import com.misit.abpenergy.Rkb.Response.DetailRkbItem
import com.misit.abpenergy.Rkb.Response.DetailRkbResponse
import com.misit.abpenergy.Utils.PrefsUtil
import kotlinx.android.synthetic.main.activity_detail_rkb.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailRkbActivity : AppCompatActivity() {
    private var adapter: DetailRkbAdapter? = null
    private var no_rkb:String?=null
    private var notif:Boolean?=null

    var detailRkbItem : MutableList<DetailRkbItem>? = null

    private var call: Call<DetailRkbResponse>?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       setContentView(R.layout.activity_detail_rkb)
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        if(PrefsUtil.getInstance().getBooleanState("IS_LOGGED_IN",true)){
            USERNAME = PrefsUtil.getInstance().getStringState(PrefsUtil.USER_NAME,"")
            NAMA_LENGKAP = PrefsUtil.getInstance().getStringState(PrefsUtil.NAMA_LENGKAP,"")
            DEPARTMENT = PrefsUtil.getInstance().getStringState(PrefsUtil.DEPT,"")
            SECTON = PrefsUtil.getInstance().getStringState(PrefsUtil.SECTION,"")
            LEVEL = PrefsUtil.getInstance().getStringState(PrefsUtil.LEVEL,"")
        }
        var actionBar = supportActionBar
        no_rkb = intent.getStringExtra(NO_RKB)
        notif = intent.getBooleanExtra(NOTIF.toString(),false)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        title=no_rkb
        detailRkbItem = ArrayList()
        adapter = DetailRkbAdapter(this,detailRkbItem!!)
        val linearLayoutManager = LinearLayoutManager(this@DetailRkbActivity)
        rvDetailRkb?.layoutManager = linearLayoutManager
        rvDetailRkb.adapter =adapter
        loadDetailRkb(no_rkb)
    }

    private fun loadDetailRkb(noRkb: String?) {
        val apiEndPoint = ApiClient.getClient(this@DetailRkbActivity)!!.create(ApiEndPoint::class.java)
        call = apiEndPoint.getRkbDetail(no_rkb.toString())
        call?.enqueue(object : Callback<DetailRkbResponse?> {
            override fun onFailure(call: Call<DetailRkbResponse?>, t: Throwable) {
                Toast.makeText(this@DetailRkbActivity, "Failed to Fetch Data\n" +
                        "e: $t", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<DetailRkbResponse?>,
                response: Response<DetailRkbResponse?>
            ) {
                val detailRkbResponse = response.body()
                if (detailRkbResponse != null) {
                    detailRkbItem?.addAll(detailRkbResponse.detailRkb!!)
                    this@DetailRkbActivity?.runOnUiThread {
                        adapter?.notifyDataSetChanged()
                    }
                }
            }

        })
    }

    override fun onBackPressed() {

        var intent = Intent(this,RkbActivity::class.java)
        intent.putExtra(RkbActivity.USERNAME,
            USERNAME
        )
        intent.putExtra(RkbActivity.DEPARTMENT,
            DEPARTMENT
        )
        intent.putExtra(RkbActivity.SECTON,
            SECTON
        )
        intent.putExtra(RkbActivity.LEVEL,
            LEVEL
        )
        intent.putExtra(RkbActivity.Tab_INDEX,0)
        if(notif!!){
            startActivity(intent)
            finish()
        }
        super.onBackPressed()
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
    companion object{
        var NO_RKB = "no_rkb"
        var USERNAME = "username"
        var DEPARTMENT="department"
        var SECTON="section"
        var LEVEL="level"
        var NAMA_LENGKAP = "nama_lengkap"
        var NOTIF = false
    }

}

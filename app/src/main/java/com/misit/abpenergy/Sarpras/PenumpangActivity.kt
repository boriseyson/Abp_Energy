package com.misit.abpenergy.Sarpras

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.misit.abpenergy.Sarpras.Adapter.PenumpangAdapter
import com.misit.abpenergy.R
import com.misit.abpenergy.Sarpras.Realm.PenumpangModel
import com.misit.abpenergy.Sarpras.SaranaResponse.KaryawanItem
import com.misit.abpenergy.Sarpras.SaranaResponse.PenumpangListModel
import es.dmoral.toasty.Toasty
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_penumpang.*
import java.util.*
import kotlin.collections.ArrayList

class PenumpangActivity : AppCompatActivity(),
    View.OnClickListener,
    PenumpangAdapter.OnItemClickListener {
    private var adapter: PenumpangAdapter? = null
    private var penumpangList: ArrayList<String>? = null
    private var list:MutableList<PenumpangListModel>?=null
    private var displayList:MutableList<PenumpangListModel>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_penumpang)
        title="Pilih Penumpang"
        penumpangList = ArrayList()
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        list = ArrayList()
        displayList=ArrayList()
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

//        penumpangPilih= ArrayList()
        var listDipilih = intent.getStringArrayListExtra("listDipilih")
        if(listDipilih!=null){
//            var dipilih = listDipilih.split(",")
//            dipilih.forEach {
//                penumpangList?.add(it)
//            }
            penumpangList?.addAll(listDipilih)
        }
        adapter = PenumpangAdapter(
            this,
            displayList!!,
            penumpangList!!
        )
        val linearLayoutManager = LinearLayoutManager(this@PenumpangActivity)
        rvPenumpang?.layoutManager = linearLayoutManager

        rvPenumpang.adapter =adapter
        adapter?.setListener(this)
        loadData()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_penumpang,menu)
        val menuItem = menu!!.findItem(R.id.searchPenumpang)
        val searchView = menuItem.actionView as SearchView

        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText.let {
                    filter(it!!.toLowerCase())
                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.savePenumpang){
            val intent:Intent = Intent()
//            val listDipilih = penumpangList?.
//                                    joinToString(separator=","){ "${it}" }
            intent.putExtra("listDipilih",penumpangList)
            setResult(Activity.RESULT_OK,intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
    fun filter(search:String){
        if(search!!.isNotEmpty()){
            displayList?.clear()
            list?.forEach {
                if(it!!.nik!!.toLowerCase(Locale.getDefault()).contains(search) ||
                    it!!.nama!!.toLowerCase(Locale.getDefault()).contains(search) ||
                        it!!.jabatan!!.toLowerCase(Locale.getDefault()).contains(search)){
                    displayList?.add(it)
                }
                rvPenumpang.adapter!!.notifyDataSetChanged()
            }
        }else{
            displayList?.clear()
            displayList?.addAll(list!!)
            rvPenumpang.adapter!!.notifyDataSetChanged()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
    fun loadData(){
        Realm.init(this)
        val realm = Realm.getDefaultInstance()
        val realmRes = realm.where(PenumpangModel::class.java)
            .findAll()
        for (i in realmRes.indices) {
            val penumpang = realmRes[i]
            list?.add(PenumpangListModel(i!!.toLong()!!,penumpang?.nik,penumpang?.nama,penumpang?.jabatan))
        }
        displayList!!.addAll(list!!)
        adapter?.notifyDataSetChanged()
    }
    override fun onItemClick(nik: String?) {

    }

    override fun onCheckTrue(nik: String?) {
        penumpangList?.add(nik!!)
    }

    override fun onCheckFalse(nik: String?) {
        penumpangList?.remove(nik!!)
    }

    companion object{
        var PENUMPANG_S=ArrayList<KaryawanItem>()
    }

    override fun onClick(v: View?) {
    }
}

package com.misit.abpenergy.Sarpras

import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.misit.abpenergy.Api.ApiClient
import com.misit.abpenergy.Api.ApiEndPoint
import com.misit.abpenergy.Model.KaryawanModel
import com.misit.abpenergy.R
import com.misit.abpenergy.Rkb.Response.CsrfTokenResponse
import com.misit.abpenergy.Sarpras.Adapter.SaranaAdapter
import com.misit.abpenergy.Sarpras.Realm.PenumpangModel
import com.misit.abpenergy.Sarpras.SaranaResponse.ListSaranaResponse
import com.misit.abpenergy.Sarpras.SaranaResponse.DataItem
import com.misit.abpenergy.Sarpras.SaranaResponse.IzinKeluarSaranaResponse
import com.misit.abpenergy.Utils.PopupUtil
import com.misit.abpenergy.Utils.PrefsUtil
import es.dmoral.toasty.Toasty
import io.realm.Realm
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_new_sarpras.*
import kotlinx.android.synthetic.main.activity_new_sarpras.noLV
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NewSarprasActivity :
    AppCompatActivity(),
    View.OnClickListener,
    DatePickerDialog.OnDateSetListener,
    TimePicker.OnTimeChangedListener{
    var saranaList : MutableList<DataItem>? = null
    var list : ArrayList<SaranaModel>? = null
    var karyawan : ArrayList<KaryawanModel>? = null
    var driver : String?=null
    var listArr:List<String>?=null
    var inPenumpangList:ArrayList<String>?=null
    var penumpangs : ArrayList<PenumpangModel>?=null
    private var myList:MutableList<PenumpangModel>?=null
    var displayPenumpang : ArrayList<String>?=null
    var spinnerDialog:SpinnerDialog?=null
    var listDipilih:ArrayList<String>?=null
    var csrf_token:String?=null
    var progressLoad:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_sarpras)
        getToken()
        Realm.init(this@NewSarprasActivity)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        if(PrefsUtil.getInstance().getBooleanState("IS_LOGGED_IN",true)){
            USERNAME = PrefsUtil.getInstance().getStringState(PrefsUtil.USER_NAME,"")
            NIK = PrefsUtil.getInstance().getStringState(PrefsUtil.NIK,"")
            NAMA_LENGKAP = PrefsUtil.getInstance().getStringState(PrefsUtil.NAMA_LENGKAP,"")
            DEPARTMENT = PrefsUtil.getInstance().getStringState(PrefsUtil.DEPT,"")
            SECTON = PrefsUtil.getInstance().getStringState(PrefsUtil.SECTION,"")
            LEVEL = PrefsUtil.getInstance().getStringState(PrefsUtil.LEVEL,"")
        }
        lnNewSarpras.visibility=View.GONE
        loadSarana()
        csrf_token= String()
        listArr=ArrayList()
        listDipilih= ArrayList()
        saranaList = ArrayList()
        list = ArrayList()
        penumpangs = ArrayList()
        myList = ArrayList()
        displayPenumpang = ArrayList()
        karyawan = ArrayList()
        inPenumpangList=ArrayList()

        title ="Form Izin Sarana"
        inTglKeluar.setOnClickListener(this)
        inJamKeluar.setOnClickListener(this)
        inTglKembali.setOnClickListener(this)
        inJamKembali.setOnClickListener(this)
        inDriver.setOnClickListener(this)
        inPenumpang.setOnClickListener(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
    private fun getToken() {
        val apiEndPoint = ApiClient.getClient(this)!!.create(ApiEndPoint::class.java)
        val call = apiEndPoint.getToken("csrf_token")
        call?.enqueue(object : Callback<CsrfTokenResponse> {
            override fun onFailure(call: Call<CsrfTokenResponse>, t: Throwable) {
                Toast.makeText(this@NewSarprasActivity,"Error : $t", Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(
                call: Call<CsrfTokenResponse>,
                response: Response<CsrfTokenResponse>
            ) {
                csrf_token = response.body()?.csrfToken
            }
        })
    }
    private fun clearError() {
        tilDriver.error=null
        tilPenumpang.error=null
        tilTglKeluar.error=null
        tilJamKeluar.error=null
        tilTglKembali.error=null
        tilJamKembali.error=null
        tilKeterangan.error=null

    }
    fun isValidate():Boolean{
        clearError()

        if(inDriver.text!!.isEmpty()){
            tilDriver.error="Please Input Someting"
            inDriver.requestFocus()
            return false
        }
        if(inPenumpang.text!!.isEmpty()){
            tilPenumpang.error="Please Input Someting"
            inPenumpang.requestFocus()
            return false
        }
        if(inTglKeluar.text!!.isEmpty()){
            tilTglKeluar.error="Please Input Someting"
            inTglKeluar.requestFocus()
            return false
        }
        if(inJamKeluar.text!!.isEmpty()){
            tilJamKeluar.error="Please Input Someting"
            inJamKeluar.requestFocus()
            return false
        }
        if(inTglKembali.text!!.isEmpty()){
            tilTglKembali.error="Please Input Someting"
            inTglKembali.requestFocus()
            return false
        }
        if(inJamKembali.text!!.isEmpty()){
            tilJamKembali.error="Please Input Someting"
            inJamKembali.requestFocus()
            return false
        }
        if(inKeterangan.text!!.isEmpty()){
            tilKeterangan.error="Please Input Someting"
            inKeterangan.requestFocus()
            return false
        }
        return true
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_submit,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.btnSubmit){
            postIzinKeluarSarana()
        }
        return super.onOptionsItemSelected(item)
    }
fun postIzinKeluarSarana(){
    if(!isValidate()){
        return
    }
    PopupUtil.showProgress(this@NewSarprasActivity,"Loading...","Membuat Surat Izin Keluar Masuk Sarana")

    var username = USERNAME
    var pemohon = NIK
    var nolv= saranaList!![noLV.selectedItemId.toInt()].noLv
    var driver = driver
    var noPol= saranaList!![noLV.selectedItemId.toInt()].noPol
    var penumpang:ArrayList<String> = ArrayList()
    penumpang.addAll(listDipilih!!)
    var tglKeluar = inTglKeluar.text.toString()
    var jamKeluar = inJamKeluar.text.toString()
    var tglMasuk = inTglKembali.text.toString()
    var jamMasuk = inJamKembali.text.toString()
    var keperluan = inKeterangan.text.toString()
    val apiEndPoint = ApiClient.getClient(this)!!.create(ApiEndPoint::class.java)
    val call = apiEndPoint.
        keluarSarana(
            username,
            pemohon,
            nolv,
            driver,
            noPol,
            penumpang,
            keperluan,
            tglKeluar,
            jamKeluar,
            true,
            tglMasuk,
            jamMasuk,
            csrf_token)
    call?.enqueue(object : Callback<IzinKeluarSaranaResponse>{
        override fun onFailure(call: Call<IzinKeluarSaranaResponse>, t: Throwable) {
            Toasty.error(this@NewSarprasActivity,"Error Membuat Izin Keluar Masuk Sarana",Toasty.LENGTH_SHORT).show()
            finish()
            PopupUtil.dismissDialog()
        }

        override fun onResponse(
            call: Call<IzinKeluarSaranaResponse>,
            response: Response<IzinKeluarSaranaResponse>
        ) {
            var izinSarana = response.body()
            if (izinSarana!=null){
                if (izinSarana.success!!){
                    PopupUtil.dismissDialog()
                    Toasty.success(this@NewSarprasActivity,"Membuat Izin Keluar Masuk Sarana Berhasil",Toasty.LENGTH_SHORT).show()
                    var intent = Intent(this@NewSarprasActivity,SarprasActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    PopupUtil.dismissDialog()
                    Toasty.error(this@NewSarprasActivity,"Error Membuat Izin Keluar Masuk Sarana",Toasty.LENGTH_SHORT).show()
                    finish()
                }
            }else{
                PopupUtil.dismissDialog()
                Toasty.error(this@NewSarprasActivity,"Error Membuat Izin Keluar Masuk Sarana",Toasty.LENGTH_SHORT).show()
                finish()
            }
        }

    })

}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(resultCode==Activity.RESULT_OK){
            getPenumpang()
            inPenumpangList?.clear()
            listDipilih = data!!.getStringArrayListExtra("listDipilih")
//            listArr = listDipilih!!.split(",")
//            Toasty.info(this@NewSarprasActivity,listDipilih.toString(),Toasty.LENGTH_SHORT).show()
            for(i in listDipilih!!){
//                Toasty.info(this@NewSarprasActivity,i,Toasty.LENGTH_SHORT).show()

                myList?.forEach {
                    if(it.nik.equals(i)){
                        inPenumpangList?.add("(${it.nik}) ${it.nama} | ${it.jabatan}")
                    }
                }
            }
            inPenumpang.setText(inPenumpangList?.joinToString(separator=", \r\n"){ "${it}" })
        }else if(resultCode==Activity.RESULT_CANCELED){
//            Toasty.info(this@NewSarprasActivity,data.toString(),Toasty.LENGTH_SHORT).show()
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
    fun getPenumpang(){
        myList?.clear()
        var realm = Realm.getDefaultInstance()
        var listPenumpang =
            realm.
            where(PenumpangModel::class.java)
                .findAll()
        listPenumpang?.forEach {
            myList?.add(it)
        }

        realm.close()
    }
    companion object{
        var NO_RKB = "no_rkb"
        var USERNAME = "16060052"
        var DEPARTMENT="department"
        var SECTON="section"
        var LEVEL="level"
        var NAMA_LENGKAP = "nama_lengkap"
        var NIK = "nama_lengkap"
        var PENUMPANGDIPILIH=""
        var NOTIF = false
        var MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE=0
    }
    private fun loadSarana(){
        PopupUtil.showLoading(this@NewSarprasActivity,"Loading","Mengambil Data!")
        val apiEndPoint = ApiClient.getClient(this@NewSarprasActivity)!!.create(ApiEndPoint::class.java)
        val call = apiEndPoint.getAllSarana()
        call?.enqueue(object : Callback<ListSaranaResponse?> {
            override fun onFailure(call: Call<ListSaranaResponse?>, t: Throwable) {

            }
            override fun onResponse(
                call: Call<ListSaranaResponse?>,
                response: Response<ListSaranaResponse?>
            ) {
                var i = 1
                val listSarana = response.body()
                if (listSarana != null) {
                    listSarana.data?.let {
                        saranaList?.addAll(it)
                        listSarana.data?.forEach {


                            list?.add(SaranaModel(i.toLong(),it.noPol!!,it.noLv!!))
                            i++
                        }
                    }

                    listSarana.karyawan?.let {
//                        karyawan?.addAll(it)

                            listSarana.karyawan?.forEach {
                                karyawan?.add(
                                    KaryawanModel(
                                        i.toLong(),
                                        it.nik!!,
                                        it.nama!!,
                                        it.jabatan!!
                                    )
                                )
                                i++
                            }



                    }

                    val adapter =
                        SaranaAdapter(
                            this@NewSarprasActivity,
                            list!!
                        )
//                    val karyawanAdapter = KaryawanSpinnerAdapter(this@NewSarprasActivity,karyawan!!)
                    noLV.adapter = adapter
                    lnNewSarpras.visibility=View.VISIBLE
                    PopupUtil.dismissDialog()
                }
            }
        })
        }


    override fun onClick(v: View?) {
        if(v!!.id == R.id.inTglKeluar){
            showDialogTgl(inTglKeluar)
        }
        if(v!!.id == R.id.inJamKeluar){
            showDialogJam(inJamKeluar)
        }
        if(v!!.id == R.id.inTglKembali){
            showDialogTgl(inTglKembali)
        }
        if(v!!.id == R.id.inJamKembali){
            showDialogJam(inJamKembali)
        }
        if(v!!.id == R.id.inDriver){
            showKaryawan(inDriver)
        }
        if(v!!.id == R.id.inPenumpang){
            listPenumpang()
//            showKaryawan(inPenumpang)
            var intent = Intent(this@NewSarprasActivity,PenumpangActivity::class.java)
            intent.putExtra("listDipilih",listDipilih)
            startActivityForResult(intent,1)
        }
    }
    fun listPenumpang(){

        var realm = Realm.getDefaultInstance()
        realm?.executeTransaction {
            var listPenumpang =
                realm?.where(PenumpangModel::class.java)
                    ?.findAllSorted("id", Sort.DESCENDING)
            listPenumpang?.deleteAllFromRealm()
            val penumpang = PenumpangModel()
            karyawan?.forEach {
                penumpang.id = it.id
                penumpang.nik = it.nik
                penumpang.nama = it.nama
                penumpang.jabatan = it.jabatan
                try {
                    realm?.copyToRealm(penumpang)

                } catch (e: Exception) {
                    Toast.makeText(
                        this@NewSarprasActivity,
                        "Load Data Karyawan Gagal : $e",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
        realm?.close()

    }

    fun showKaryawan(inKaryawan:TextInputEditText){
        spinnerDialog = SpinnerDialog(this@NewSarprasActivity,modelList(),"Select Karyawan")
        spinnerDialog!!.bindOnSpinerListener( { s: String, i: Int ->
            if(inKaryawan == inDriver){
                driver = "${karyawan!![i].nik}"
                inKaryawan.setText("(${karyawan!![i].nik}) ${karyawan!![i].nama} : ${karyawan!![i].jabatan}")
            }
        })
        spinnerDialog!!.showSpinerDialog()
    }
    fun showDialogTgl(inTgl:TextInputEditText){
        val now = Calendar.getInstance()
        val datePicker  = DatePickerDialog.OnDateSetListener{
            view: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
            now.set(Calendar.YEAR,year)
            now.set(Calendar.MONTH,month)
            now.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            inTgl.setText(SimpleDateFormat("dd MMMM yyyy").format(now.time))
        }

        DatePickerDialog(this,
            datePicker,
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    fun showDialogJam(inJam :TextInputEditText){
        var now = Calendar.getInstance()
        var timeListener  = TimePickerDialog.OnTimeSetListener{
                timerPiker,hour,minute->
            now.set(Calendar.HOUR_OF_DAY,hour)
            now.set(Calendar.MINUTE,minute)
            inJam.setText(SimpleDateFormat("HH:mm").format(now.time))
        }
        TimePickerDialog(this,
            timeListener,
            now.get(Calendar.HOUR_OF_DAY),
            now.get(Calendar.MINUTE),
            true).show()
    }
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val tglPilih = "$dayOfMonth - ${month+1} - $year"
//        inTglKeluar.setText(tglPilih)
    }
    override fun onTimeChanged(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val jamPilih = "$hourOfDay : ${minute}"
//        inJamKeluar.setText(jamPilih)
    }
        private fun modelListSarana():ArrayList<SaranaModel>{
            var list = ArrayList<SaranaModel>()
            for(i in 0 until saranaList!!.size) {
                list.add(SaranaModel(i.toLong(),saranaList!![i].noPol!!,i.toString()))
            }
            return list
        }
        private fun modelList():ArrayList<String>{
            var list = ArrayList<String>()
            for(i in 0 until karyawan!!.size) {
                list.add(i,"(${karyawan!![i].nik}) ${karyawan!![i].nama} : ${karyawan!![i].jabatan}")
            }
            return list
        }
    }
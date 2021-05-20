package com.misit.abpenergy.HazardReport

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.misit.abpenergy.Api.ApiClient
import com.misit.abpenergy.Api.ApiEndPoint
import com.misit.abpenergy.HazardReport.Response.HazardReportResponse
import com.misit.abpenergy.Login.CompanyActivity
import com.misit.abpenergy.R
import com.misit.abpenergy.Rkb.Response.CsrfTokenResponse
import com.misit.abpenergy.Service.MatrikResikoWebViewActivity
import com.misit.abpenergy.Utils.ConfigUtil
import com.misit.abpenergy.Utils.ConfigUtil.resultIntent
import com.misit.abpenergy.Utils.PopupUtil
import com.misit.abpenergy.Utils.PrefsUtil
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_new_hazard.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class NewHazardActivity : AppCompatActivity(),View.OnClickListener {
    private var bahayaDipilih:String? = null
    private var lokasiDipilih:String? = null
    private var hirarkiDipilih:String? = null
    private var kemungkinanDipilih:String? = null
    private var keparahanDipilih:String? = null
    private var kemungkinanDipilihSesudah:String? = null
    private var keparahanDipilihSesudah:String? = null
    private var lokasiID:String? = null
    private var companyDipilih:String?=null
    private var hirarkiID:String? = null
    private var kemungkinanID:String? = null
    private var keparahanID:String? = null
    private var kemungkinanIDSesudah:String? = null
    private var keparahanIDSesudah:String? = null
    private var csrf_token:String?=null
    private var plKondisi:RequestBody?=null
    var rbStatus:RequestBody?=null
    private var bitmap:Bitmap?=null
    private var bitmapBuktiSelesai:Bitmap?=null
    private var bitmapPJ:Bitmap?=null
    private var fileUpload:Uri?=null
    private var fileUploadSelesai:Uri?=null
    private var fileUploadPJ:Uri?=null
    private var imgIn:Int=0
    private var imgSelesai:Int=0
    private var imgPJ:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_hazard)
        title="Form Hazard Report"
        getToken()
        PrefsUtil.initInstance(this)
        verifyStoragePermissions(this,this)
        if(PrefsUtil.getInstance().getBooleanState("IS_LOGGED_IN",false)){
            USERNAME = PrefsUtil.getInstance().getStringState(PrefsUtil.USER_NAME,"")
        }
        imgIn=0
        bitmap=null
        imgSelesai=0
        bitmapBuktiSelesai=null
        fileUploadPJ=null
        companyDipilih=""
        imgPJ = 0
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
//        tvSumberBahaya.setOnClickListener(this)
        inTanggal.setOnClickListener(this)
        inJam.setOnClickListener(this)
        inTGLSelesai.setOnClickListener(this)
        imagePicker.setOnClickListener(this)
        btnSimpan.setOnClickListener(this)
        inJamSelesai.setOnClickListener(this)
        btnBatalHazard.setOnClickListener(this)
        matrikResiko.setOnClickListener(this)
//        btnOpenCamera.setOnClickListener(this)
//        btnOpenGalery.setOnClickListener(this)
        groupStatus.setOnCheckedChangeListener { group, checkedId ->
            if(checkedId==R.id.rbSelesai){
                lnJamSelesai.visibility=View.VISIBLE
                lnTglSelesai.visibility=View.VISIBLE
                imagePickerBuktiSelesai.visibility=View.VISIBLE
                lnResikoSesudah.visibility = View.VISIBLE
            }else{
                lnJamSelesai.visibility=View.GONE
                lnTglSelesai.visibility=View.GONE
                imagePickerBuktiSelesai.visibility=View.GONE
                lnResikoSesudah.visibility = View.GONE
            }
        }
        imgBuktiSelesai.setOnClickListener(this)
        lnJamSelesai.visibility=View.GONE
        lnTglSelesai.visibility=View.GONE
        imagePickerBuktiSelesai.visibility=View.GONE
        lnResikoSesudah.visibility = View.GONE
        inLokasi.setOnClickListener(this)
        inKemungkinan.setOnClickListener(this)
        inKeparahan.setOnClickListener(this)
        inKemungkinanSesudah.setOnClickListener(this)
        inKeparahanSesudah.setOnClickListener(this)
        inPengendalian.setOnClickListener(this)
        btnGambarHazard.setOnClickListener(this)
        btnFotoPJ.setOnClickListener(this)
        btnPerbaikan.setOnClickListener(this)
        pjFOTO.setOnClickListener(this)
        matrikResikoSesudah.setOnClickListener(this)
        inPerusaan.setOnClickListener(this)
    }
    //    VIEW LISTENER
    override fun onClick(v: View?) {
        if(v!!.id==R.id.inTanggal){
            showDialogTgl(inTanggal)
        }
        if (v!!.id==R.id.inJam){
            showDialogTime(inJam)
        }
        if(v!!.id==R.id.inTGLSelesai){
            showDialogTgl(inTGLSelesai)
        }
        if(v!!.id==R.id.inJamSelesai){
            showDialogTime(inJamSelesai)
        }
        if(v!!.id==R.id.imagePicker){
            showDialogOption(333,222)
        }
        if(v!!.id==R.id.btnSimpan){
            simpanHazard()
        }
        if(v!!.id==R.id.btnBatalHazard){
            finish()
        }
        if(v!!.id==R.id.btnGambarHazard){
            bitmap=null
            imgIn=0
            showDialogOption(333,222)
        }
        if(v!!.id==R.id.btnFotoPJ){
//            PENSNGGUNGJAWAB
            bitmapPJ=null
            imgPJ=0
            showDialogOption(533,522)
        }
        if(v!!.id==R.id.pjFOTO){
//            PENSNGGUNGJAWAB
            bitmapPJ=null
            imgPJ=0
            showDialogOption(533,522)
        }
        if(v?.id==R.id.imgBuktiSelesai){
//            BUKTI PERBAIKAN
            showDialogOption(433,422)
        }
        if (v?.id==R.id.btnPerbaikan){
//            BUKTI PERBAIKAN
            showDialogOption(433,422)
        }
        if(v?.id==R.id.inLokasi){
            var intent = Intent(this@NewHazardActivity,LokasiActivity::class.java)
            intent.putExtra("lokasiDipilih",lokasiDipilih)
            startActivityForResult(intent,123)
        }
        if(v?.id==R.id.inPengendalian){
            var intent = Intent(this@NewHazardActivity,SumberBahayaActivity::class.java)
            intent.putExtra("hirarkiDipilh",hirarkiDipilih)
            startActivityForResult(intent,456)
        }
        if(v?.id==R.id.inKemungkinan){
            var intent = Intent(this@NewHazardActivity,KemungkinanActivity::class.java)
            intent.putExtra("kemungkinanDipilih",kemungkinanDipilih)
            startActivityForResult(intent,457)
        }
        if(v?.id==R.id.inKeparahan){
            var intent = Intent(this@NewHazardActivity,KeparahanActivity::class.java)
            intent.putExtra("keparahanDipilih",keparahanDipilih)
            startActivityForResult(intent,458)
        }

        if(v?.id==R.id.inKemungkinanSesudah){
            var intent = Intent(this@NewHazardActivity,KemungkinanActivity::class.java)
            intent.putExtra("kemungkinanDipilih",kemungkinanDipilihSesudah)
            startActivityForResult(intent,477)
        }
        if(v?.id==R.id.inKeparahanSesudah){
            var intent = Intent(this@NewHazardActivity,KeparahanActivity::class.java)
            intent.putExtra("keparahanDipilih",keparahanDipilihSesudah)
            startActivityForResult(intent,488)
        }
        if(v?.id==R.id.matrikResiko || v?.id==R.id.matrikResikoSesudah){
            var intent = Intent(this@NewHazardActivity,MatrikResikoWebViewActivity::class.java)
            startActivity(intent)
        }
        if(v?.id==R.id.inPerusaan){
            var intent = Intent(this@NewHazardActivity,CompanyActivity::class.java)
            intent.putExtra("companyDipilih",companyDipilih)
            startActivityForResult(intent,987)
        }
    }
    //    VIEW LISTENER
//    onCreateOptionsMenu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_submit,menu)
        return super.onCreateOptionsMenu(menu)
    }
//    onCreateOptionsMenu
//    onSupportNavigateUp
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
//    onSupportNavigateUp
//    onOptionsItemSelected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.btnSubmit){
            simpanHazard()
        }
        return super.onOptionsItemSelected(item)
    }
//    onOptionsItemSelected
//    verifyStoragePermissions
    private fun verifyStoragePermissions(context: Context,activity: Activity) {
        val permission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE)
        val permission1 = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permission2 = ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_PHONE_STATE)
        val permission3 = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("FaceId", "READ_EXTERNAL_STORAGE Permission to record denied")
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),11)
//            finish()
        }
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            Log.i("FaceId", "WRITE_EXTERNAL_STORAGE Permission to record denied")
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),12)
//            finish()
        }
        if (permission2 != PackageManager.PERMISSION_GRANTED) {
            Log.i("FaceId", "READ_PHONE_STATE Permission to record denied")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE),13)
//            finish()
        }
        if (permission3 != PackageManager.PERMISSION_GRANTED) {
            Log.i("FaceId", "READ_PHONE_STATE Permission to record denied")
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA),13)
//            finish()
        }
    }
//    verifyStoragePermissions
    //OPEN GALERY
    private fun openGalleryForImage(codeRequest: Int) {
    btnFLMenu.collapse()
    val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    intent.type = "image/*"
    startActivityForResult(intent, codeRequest)
}
    //OPEN GALERY
//    ATIVITY RESULT
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(resultCode== Activity.RESULT_OK && requestCode==987){
            companyDipilih = data!!.getStringExtra("companyDipilih")
            inPerusaan.setText(companyDipilih)
        }else if(resultCode== Activity.RESULT_OK && requestCode==457){
            kemungkinanDipilih = data!!.getStringExtra("kemungkinanDipilih")
            kemungkinanID = data.getStringExtra("kemungkinanID")
            inKemungkinan.setText(kemungkinanDipilih)
        }else if(resultCode== Activity.RESULT_OK && requestCode==477){
            kemungkinanDipilihSesudah = data!!.getStringExtra("kemungkinanDipilih")
            kemungkinanIDSesudah = data.getStringExtra("kemungkinanID")
            inKemungkinanSesudah.setText(kemungkinanDipilihSesudah)
        }else if(resultCode== Activity.RESULT_OK && requestCode==123){
            lokasiDipilih = data!!.getStringExtra("lokasiDipilih")
            lokasiID = data.getStringExtra("lokasiID")
            inLokasi.setText(lokasiDipilih)
        }else if(resultCode== Activity.RESULT_OK && requestCode==456){
            hirarkiDipilih = data!!.getStringExtra("hirarkiDipilih")
            hirarkiID = data.getStringExtra("hirarkiID")
            inPengendalian.setText(hirarkiDipilih)
        }else if(resultCode== Activity.RESULT_OK && requestCode==458){
            keparahanDipilih = data!!.getStringExtra("keparahanDipilih")
            keparahanID = data.getStringExtra("keparahanID")
            inKeparahan.setText(keparahanDipilih)
        }else if(resultCode== Activity.RESULT_OK && requestCode==488){
            keparahanDipilihSesudah = data!!.getStringExtra("keparahanDipilih")
            keparahanIDSesudah = data.getStringExtra("keparahanID")
            inKeparahanSesudah.setText(keparahanDipilihSesudah)
        }else if(resultCode==Activity.RESULT_OK && requestCode==222) {
            try {
                fileUpload = data!!.data
                try {
                   bitmap = BitmapFactory.decodeStream(
                           contentResolver.openInputStream(fileUpload!!))
                    imgView.setImageBitmap(bitmap);
                    imgIn = 1
                } catch (e: IOException) {
                    imgIn = 0
                }
            } catch (e: IOException) {
                imgIn = 0
            }
        }else if(resultCode==Activity.RESULT_OK && requestCode==333){
            try {
                var dataFoto = data!!
                bitmap = BitmapFactory.decodeByteArray(
                    dataFoto.getByteArrayExtra("gambarDiFoto"), 0, dataFoto
                        .getByteArrayExtra("gambarDiFoto").size
                )
                imgView.setImageBitmap(bitmap);
                imgIn = 1
            } catch (e: IOException) {
                imgIn = 0
                e.printStackTrace();
            }
        }else if(resultCode==Activity.RESULT_OK && requestCode==422) {
            try {
                fileUploadSelesai = data!!.data
                try {
                    bitmapBuktiSelesai = BitmapFactory.decodeStream(
                        contentResolver.openInputStream(fileUploadSelesai!!))
                    imgBuktiSelesai.setImageBitmap(bitmapBuktiSelesai);
                    imgSelesai = 1
                } catch (e: IOException) {
                    imgSelesai = 0
                }
            } catch (e: IOException) {
                imgSelesai = 0
            }
        }else if(resultCode==Activity.RESULT_OK && requestCode==433){
            try {
                var dataSelesai = data!!
                bitmapBuktiSelesai = BitmapFactory.decodeByteArray(
                    dataSelesai.getByteArrayExtra("gambarDiFoto"), 0, dataSelesai
                        .getByteArrayExtra("gambarDiFoto").size
                )
                imgBuktiSelesai.setImageBitmap(bitmapBuktiSelesai);
                imgSelesai = 1
            } catch (e: IOException) {
                imgSelesai = 0
                e.printStackTrace();
            }
        }else if(resultCode==Activity.RESULT_OK && requestCode==522) {
            try {
//                data.clipData
                fileUploadPJ = data!!.data
                try {
                    bitmapPJ = BitmapFactory.decodeStream(
                        contentResolver.openInputStream(fileUploadPJ!!))
                    pjFOTO.setImageBitmap(bitmapPJ);
                    imgPJ = 1
                } catch (e: IOException) {
                    imgPJ = 0
                }
            } catch (e: IOException) {
                imgPJ = 0
            }
        }else if(resultCode==Activity.RESULT_OK && requestCode==533){
            try {
                var dataPJ = data!!
                bitmapPJ = BitmapFactory.decodeByteArray(
                    dataPJ.getByteArrayExtra("gambarDiFoto"), 0, dataPJ
                        .getByteArrayExtra("gambarDiFoto").size
                )
                pjFOTO.setImageBitmap(bitmapPJ);
                imgPJ = 1
            } catch (e: IOException) {
                imgPJ = 0
                e.printStackTrace();
            }
        }else if(resultCode==Activity.RESULT_CANCELED){
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
    //ACTIVITY RESULT
    //    DIALOG TANGGAL
    fun showDialogTgl(inTgl: TextInputEditText){
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
//    DIALOG TANGGAL
//    DIALOG JAM
    fun showDialogTime(inTime: TextInputEditText){
        val now = Calendar.getInstance()
        val timePicker  = TimePickerDialog.OnTimeSetListener {
                timePicker, hour, minute: Int ->
            now.set(Calendar.HOUR_OF_DAY,hour)
            now.set(Calendar.MINUTE,minute)
            inTime.setText(SimpleDateFormat("HH:mm").format(now.time))
        }
        TimePickerDialog(
            this,
            timePicker,
            now.get(Calendar.HOUR_OF_DAY),
            now.get(Calendar.MINUTE),
            true).show()

    }
//    DIALOG JAM
//    Dialog PICK PICTURE
    fun showDialogOption(camera:Int,galery:Int){
    val alertDialog = AlertDialog.Builder(this@NewHazardActivity)
    alertDialog.setTitle("Silahkan Pilih")
    val animals = arrayOf<String>(
        "Ambil Sebuah Gambar",
        "Pilih Gambar dari galery"
    )
    alertDialog!!.setItems(animals, DialogInterface.OnClickListener{ dialog, which ->
        when (which) {
            0 -> openCamera(camera)
            1 -> openGalleryForImage(galery)
        }
    })
    alertDialog.create()
    alertDialog.show()
}
    //    Dialog PICK PICTURE
    fun openCamera(codeRequest:Int){
        btnFLMenu.collapse()
        var intent = Intent(this@NewHazardActivity,PhotoHazardActivity::class.java)
        startActivityForResult(intent,codeRequest)
    }
//       Simpan Hazard
    fun simpanHazard(){
        if(rbSelesai.isChecked) {
            if(!isValidate1()){
                return
            }
        }
        else{
            if (!isValidate()) {
                return
            }
        }

    PopupUtil.showProgress(this@NewHazardActivity,"Loading...","Membuat Hazard Report!")

    var inPerusahan = inPerusaan.text.toString().toRequestBody(MultipartBody.FORM)
    var inTanggal = inTanggal.text.toString().toRequestBody(MultipartBody.FORM)
    var inJam = inJam.text.toString().toRequestBody(MultipartBody.FORM)
    var lokasi = lokasiID.toString().toRequestBody(MultipartBody.FORM)
    var inLokasiDet = inLokasiDet.text.toString().toRequestBody(MultipartBody.FORM)
    var inBahaya = inBahaya.text.toString().toRequestBody(MultipartBody.FORM)
    var kemungkinanID = kemungkinanID.toString().toRequestBody(MultipartBody.FORM)
    var keparahanID = keparahanID.toString().toRequestBody(MultipartBody.FORM)
    var kemungkinanSesudah = kemungkinanIDSesudah.toString().toRequestBody(MultipartBody.FORM)
    var keparahanSesudah =keparahanIDSesudah.toString().toRequestBody(MultipartBody.FORM)
    var hirarkiID = hirarkiID.toString().toRequestBody(MultipartBody.FORM)
    var inPerbaikan = inPerbaikan.text.toString().toRequestBody(MultipartBody.FORM)
    var inPenanggungJawab = inPenanggungJawab.text.toString().toRequestBody(MultipartBody.FORM)
    var inNikPJ = inNikPJ.text.toString().toRequestBody(MultipartBody.FORM)
        if(plKta.isChecked){
            plKondisi = plKta.text.toString().toRequestBody(MultipartBody.FORM)
        }else if(plTta.isChecked){
            plKondisi = plTta.text.toString().toRequestBody(MultipartBody.FORM)
        }
        if(rbSelesai.isChecked){
            rbStatus= rbSelesai.text.toString().toRequestBody(MultipartBody.FORM)
        }else if(rbBelumSelesai.isChecked){
            rbStatus= rbBelumSelesai.text.toString().toRequestBody(MultipartBody.FORM)
        }else if(rbBerlanjut.isChecked){
            rbStatus= rbBerlanjut.text.toString().toRequestBody(MultipartBody.FORM)
        }else if(rbDLMpengerjaan.isChecked){
            rbStatus= rbDLMpengerjaan.text.toString().toRequestBody(MultipartBody.FORM)
        }
    var inTGLSelesai = inTGLSelesai.text.toString().toRequestBody(MultipartBody.FORM)
    var inJamSelesai = inJamSelesai.text.toString().toRequestBody(MultipartBody.FORM)
    var inKeteranganPJ = inKeteranganPJ.text.toString().toRequestBody(MultipartBody.FORM)
    var username = USERNAME.toRequestBody(MultipartBody.FORM)
    var _token:RequestBody = csrf_token!!.toRequestBody(MultipartBody.FORM)

    var waktu = Date()
    val cal = Calendar.getInstance()
    cal.time = waktu
    var jam = "${cal.get(Calendar.HOUR_OF_DAY)}${cal.get(Calendar.MINUTE)}${cal.get(Calendar.SECOND)}"
    val wrapper = ContextWrapper(applicationContext)
        //    var filenya = File(fileUpload!!.path, jam)
        var file = wrapper.getDir("images", Context.MODE_PRIVATE)
        file = File(file, "${jam}_sebelum.jpg")
        ConfigUtil.streamFoto(bitmap!!,file)
        var fileUri = file.asRequestBody("image/*".toMediaTypeOrNull())
        var bukti = MultipartBody.Part.createFormData("fileToUpload",file.name,fileUri)
        //        FOTO PENANGGUNG JAWAB
        var filePJ = wrapper.getDir("images", Context.MODE_PRIVATE)
        filePJ = File(filePJ, "${jam}_penanggung_jawab.jpg")
        //    var reqFile = RequestBody.create("image/*".toMediaTypeOrNull(),file!!);
        ConfigUtil.streamFoto(bitmapPJ!!,filePJ)
        var fileUriPJ = filePJ.asRequestBody("image/*".toMediaTypeOrNull())
        var fotoPJ = MultipartBody.Part.createFormData("fileToUploadPJ",filePJ.name,fileUriPJ)

        var fileUriSelsai:RequestBody?=null
        var buktiSelesai :MultipartBody.Part?=null
        var fileSelesai = wrapper.getDir("images", Context.MODE_PRIVATE)
        if(bitmapBuktiSelesai!=null) {
//        Bukti Selesai
            fileSelesai = File(fileSelesai, "${jam}_selesai.jpg")
            ConfigUtil.streamFoto(bitmapBuktiSelesai!!,fileSelesai)
            fileUriSelsai = fileSelesai.asRequestBody("image/*".toMediaTypeOrNull())
            buktiSelesai = MultipartBody.Part.createFormData(
                "fileToUploadSelesai",
                fileSelesai.name,
                fileUriSelsai!!
            )
//        Bukti Selesai
        }
        if(imgSelesai==0){
            hazardPost(
                bukti,
                fotoPJ,
                buktiSelesai,
                inPerusahan,
                inTanggal,
                inJam,
                inBahaya,
                lokasi,
                inLokasiDet,
                kemungkinanID,
                keparahanID,
                plKondisi!!,
                hirarkiID,
                inPerbaikan,
                inPenanggungJawab,
                inNikPJ,
                rbStatus!!,
                inTGLSelesai,
                inJamSelesai,
                inKeteranganPJ,
                username,
                _token,"Bukti_Progress",
                kemungkinanSesudah,
                keparahanSesudah)
        }else if(imgSelesai==1){
            hazardPost(
                bukti,
                fotoPJ,
                buktiSelesai,
                inPerusahan,
                inTanggal,
                inJam,
                inBahaya,
                lokasi,
                inLokasiDet,
                kemungkinanID,
                keparahanID,
                plKondisi!!,
                hirarkiID,
                inPerbaikan,
                inPenanggungJawab,
                inNikPJ,
                rbStatus!!,
                inTGLSelesai,
                inJamSelesai,
                inKeteranganPJ,
                username,
                _token,"Bukti_Selesai",
                kemungkinanSesudah,
                keparahanSesudah
            )
        }
    }
//        Simpan Hazard
//    Save Hazard
    private fun hazardPost(
                   fileBukti:MultipartBody.Part,
                   filePJ:MultipartBody.Part?,
                   fileSelesai:MultipartBody.Part?,
                   perusahaan:RequestBody,
                   tanggal:RequestBody,
                   jam:RequestBody,
                   bahaya:RequestBody,
                   lokasi:RequestBody,
                   lokasiDet:RequestBody,
                   kemungkinan:RequestBody,
                   keparahan:RequestBody,
                   kondisi:RequestBody,
                   hirarki:RequestBody,
                   perbaikan:RequestBody,
                   namaPJ:RequestBody,
                   nikPJ:RequestBody,
                   status:RequestBody,
                   tglSelesai:RequestBody,
                   jamSelesai:RequestBody,
                   keteranganPJ:RequestBody,
                   user:RequestBody,
                   token:RequestBody,
                   tipe:String,
                   kemungkinanSesudah:RequestBody,
                   keparahanSesudah:RequestBody){
    //    API POST
    val apiEndPoint = ApiClient.getClient(this)!!.create(ApiEndPoint::class.java)
     var call:Call<HazardReportResponse>?=null
        if(tipe=="Bukti_Progress"){
            call = apiEndPoint.postHazardReport(
                fileBukti,filePJ,perusahaan,tanggal,jam,lokasi,
                lokasiDet,bahaya,kemungkinan,keparahan,kondisi,hirarki,perbaikan,
                namaPJ,nikPJ,status,user,token
            )
        }else if(tipe=="Bukti_Selesai"){
             call = apiEndPoint.postHazardReportSelesai(
                 fileBukti,filePJ,fileSelesai,perusahaan,tanggal,jam,lokasi,
                 lokasiDet,bahaya,kemungkinan,keparahan,kemungkinanSesudah,keparahanSesudah,kondisi,hirarki,perbaikan,
                 namaPJ,nikPJ,status,tglSelesai,jamSelesai,keteranganPJ,user,token
            )
        }
    call?.enqueue(object : Callback<HazardReportResponse> {
        override fun onFailure(call: Call<HazardReportResponse>, t: Throwable) {
            Toast.makeText(this@NewHazardActivity,"Error : $t", Toast.LENGTH_SHORT).show()
            PopupUtil.dismissDialog()
        }
        override fun onResponse(
            call: Call<HazardReportResponse>,
            response: Response<HazardReportResponse>
        ) {
            var sResponse = response.body()
            if(sResponse!=null){
                if(sResponse.success!!){
                    Toasty.success(this@NewHazardActivity,"Hazard Report Telah Dibuat! ").show()
                    resultIntent(this@NewHazardActivity)
                    PopupUtil.dismissDialog()
                    finish()
                }else{
                    Toasty.error(this@NewHazardActivity,"Gagal Membuat Hazard Report! ").show()
                    PopupUtil.dismissDialog()
                }
            }else{
                Toasty.error(this@NewHazardActivity,"Gagal Membuat Hazard Report! ").show()
                PopupUtil.dismissDialog()
            }
        }
    })
//    API POST
    }
//    Save Hazard
//    TOKEN
private fun getToken() {
    val apiEndPoint = ApiClient.getClient(this)!!.create(ApiEndPoint::class.java)
    val call = apiEndPoint.getToken("csrf_token")
    call?.enqueue(object : Callback<CsrfTokenResponse> {
        override fun onFailure(call: Call<CsrfTokenResponse>, t: Throwable) {
            Toast.makeText(this@NewHazardActivity,"Error : $t", Toast.LENGTH_SHORT).show()
        }
        override fun onResponse(
            call: Call<CsrfTokenResponse>,
            response: Response<CsrfTokenResponse>
        ) {
            csrf_token = response.body()?.csrfToken
        }
    })
}
//    TOKEN
    //    Validasi
    fun isValidate():Boolean{
    clearError()

    if(inPerusaan.text!!.isEmpty()){
        tilPerusahaan.error="Please Input Someting"
        inPerusaan.requestFocus()
        return false
    }
    if(inTanggal.text!!.isEmpty()){
        tilTanggal.error="Please Input Someting"
        inTanggal.requestFocus()
        return false
    }
    if(inJam.text!!.isEmpty()){
        tilJam.error="Please Input Someting"
        inJam.requestFocus()
        return false
    }
    if(inLokasi.text!!.isEmpty()){
        tilLokasi.error="Please Input Someting"
        inLokasi.requestFocus()
        return false
    }
    if(inLokasiDet.text!!.isEmpty()){
        tilLokasiDet.error="Please Input Someting"
        inLokasiDet.requestFocus()
        return false
    }

    if(inBahaya.text!!.isEmpty()){
        tilBahaya.error="Please Input Someting"
        inBahaya.requestFocus()
        return false
    }
    if(inKemungkinan.text!!.isEmpty()){
        tilKemungkinan.error="Please Input Someting"
        inKemungkinan.requestFocus()
        return false
    }
    if(inKeparahan.text!!.isEmpty()){
        tilKeparahan.error="Please Input Someting"
        inKeparahan.requestFocus()
        return false
    }
    if(inPengendalian.text!!.isEmpty()){
        tilPengendalian.error="Please Input Someting"
        inPengendalian.requestFocus()
        return false
    }
    if(inPerbaikan.text!!.isEmpty()){
        tilPerbaikan.error="Please Input Someting"
        inPerbaikan.requestFocus()
        return false
    }
    if(inPenanggungJawab.text!!.isEmpty()){
        tilPenanggungJawab.error="Please Input Someting"
        inPenanggungJawab.requestFocus()
        return false
    }
    if (grupKategori.getCheckedRadioButtonId() == -1)
    {
        tilKatBahaya.visibility=View.VISIBLE
        plKta.requestFocus()
        return false
    }
    if (groupStatus.getCheckedRadioButtonId() == -1)
    {
        tilStatus.visibility=View.VISIBLE
        rbBelumSelesai.requestFocus()
        return false
    }
    if (imgIn <= 0 )
    {
        imagePicker.performClick()
        return false
    }
    return true
}
    fun isValidate1():Boolean{
        clearError()

        if(inPerusaan.text!!.isEmpty()){
            tilPerusahaan.error="Please Input Someting"
            inPerusaan.requestFocus()
            return false
        }
        if(inTanggal.text!!.isEmpty()){
            tilTanggal.error="Please Input Someting"
            inTanggal.requestFocus()
            return false
        }
        if(inJam.text!!.isEmpty()){
            tilJam.error="Please Input Someting"
            inJam.requestFocus()
            return false
        }
        if(inLokasi.text!!.isEmpty()){
            tilLokasi.error="Please Input Someting"
            inLokasi.requestFocus()
            return false
        }
        if(inLokasiDet.text!!.isEmpty()){
            tilLokasiDet.error="Please Input Someting"
            inLokasiDet.requestFocus()
            return false
        }

        if(inBahaya.text!!.isEmpty()){
            tilBahaya.error="Please Input Someting"
            inBahaya.requestFocus()
            return false
        }
        if(inKemungkinan.text!!.isEmpty()){
            tilKemungkinan.error="Please Input Someting"
            inKemungkinan.requestFocus()
            return false
        }
        if(inKeparahan.text!!.isEmpty()){
            tilKeparahan.error="Please Input Someting"
            inKeparahan.requestFocus()
            return false
        }
        if(inKemungkinanSesudah.text!!.isEmpty()){
            tilKemungkinanSesudah.error="Please Input Someting"
            inKemungkinanSesudah.requestFocus()
            return false
        }
        if(inKeparahanSesudah.text!!.isEmpty()){
            tilKeparahanSesudah.error="Please Input Someting"
            inKeparahanSesudah.requestFocus()
            return false
        }
        if(inPengendalian.text!!.isEmpty()){
            tilPengendalian.error="Please Input Someting"
            inPengendalian.requestFocus()
            return false
        }
        if(inPerbaikan.text!!.isEmpty()){
            tilPerbaikan.error="Please Input Someting"
            inPerbaikan.requestFocus()
            return false
        }
        if(inPenanggungJawab.text!!.isEmpty()){
            tilPenanggungJawab.error="Please Input Someting"
            inPenanggungJawab.requestFocus()
            return false
        }
        if(inNikPJ.text!!.isEmpty()){
            tilNikPJ.error="Please Input Someting"
            inNikPJ.requestFocus()
            return false
        }
        if(inTGLSelesai.text!!.isEmpty()){
            tilTGLSelesai.error="Please Input Someting"
            inTGLSelesai.requestFocus()
            return false
        }
        if(inJamSelesai.text!!.isEmpty()){
            tilJamSelesai.error="Please Input Someting"
            inJamSelesai.requestFocus()
            return false
        }
        if (grupKategori.getCheckedRadioButtonId() == -1)
        {
            tilKatBahaya.visibility=View.VISIBLE
            plKta.requestFocus()
            return false
        }
        if (groupStatus.getCheckedRadioButtonId() == -1)
        {
            tilStatus.visibility=View.VISIBLE
            rbSelesai.requestFocus()
            return false
        }
        if (imgIn <= 0 )
        {
            Toasty.error(this@NewHazardActivity,"Harap Memilih Gambar",Toasty.LENGTH_LONG).show()
            imagePicker.performClick()
            return false
        }
        if (imgSelesai <= 0 )
        {
            Toasty.error(this@NewHazardActivity,"Harap Memilih Gambar Bukti Selesai",Toasty.LENGTH_LONG).show()
            imagePickerBuktiSelesai.performClick()
            return false
        }
        return true
    }
    private fun clearError() {
        tilPerusahaan.error=null
        tilTanggal.error=null
        tilJam.error=null
        tilLokasi.error=null
        tilLokasiDet.error=null
        tilBahaya.error=null
        tilKemungkinan.error=null
        tilKeparahan.error=null
        tilKemungkinanSesudah.error=null
        tilKeparahanSesudah.error=null
        tilPengendalian.error=null
        tilPerbaikan.error=null
        tilKatBahaya.visibility=View.GONE
        tilStatus.visibility=View.GONE
    }
    //    Validasi
//    OBJECT
    companion object{
    var USERNAME = "USERNAME"
    }
//    OBJECT
}
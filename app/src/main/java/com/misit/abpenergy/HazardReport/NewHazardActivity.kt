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
import com.misit.abpenergy.R
import com.misit.abpenergy.Rkb.Response.CsrfTokenResponse
import com.misit.abpenergy.Utils.PopupUtil
import com.misit.abpenergy.Utils.PrefsUtil
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_new_hazard.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class NewHazardActivity : AppCompatActivity(),View.OnClickListener {
    private var bahayaDipilih:String? = null
    private var bahayaID:String? = null
    private var csrf_token:String?=null
    private var plKondisi:RequestBody?=null
    var rbStatus:RequestBody?=null
    private var bitmap:Bitmap?=null
    private var fileUpload:Uri?=null
    private var imgIn:Int=0

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
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        tvSumberBahaya.setOnClickListener(this)
        tvTanggal.setOnClickListener(this)
        tvJam.setOnClickListener(this)
        tvTGLSelesai.setOnClickListener(this)
        imagePicker.setOnClickListener(this)
        btnSimpan.setOnClickListener(this)
        tvJamSelesai.setOnClickListener(this)
        btnBatalHazard.setOnClickListener(this)
        btnOpenCamera.setOnClickListener(this)
        btnOpenGalery.setOnClickListener(this)
    }
    //    VIEW LISTENER
    override fun onClick(v: View?) {
        if(v!!.id==R.id.tvSumberBahaya){
            var intent = Intent(this@NewHazardActivity,SumberBahayaActivity::class.java)
            intent.putExtra("bahayaDipilih",bahayaDipilih)
            startActivityForResult(intent,111)
        }
        if(v!!.id==R.id.tvTanggal){
            showDialogTgl(tvTanggal)
        }
        if (v!!.id==R.id.tvJam){
            showDialogTime(tvJam)
        }
        if(v!!.id==R.id.tvTGLSelesai){
            showDialogTgl(tvTGLSelesai)
        }
        if(v!!.id==R.id.imagePicker){
            showDialogOption()
        }
        if(v!!.id==R.id.btnSimpan){
            simpanHazard()
        }
        if(v!!.id==R.id.tvJamSelesai){
            showDialogTime(tvJamSelesai)
        }
        if(v!!.id==R.id.btnBatalHazard){
            finish()
        }
        if(v!!.id==R.id.btnOpenCamera){
            openCamera()

        }
        if(v!!.id==R.id.btnOpenGalery){
            openGalleryForImage()
        }

    }
    //    VIEW LISTENER
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_submit,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.btnSubmit){
            simpanHazard()
        }
        return super.onOptionsItemSelected(item)
    }

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

    override fun onResume() {

        super.onResume()
    }


    //OPEN GALERY
private fun openGalleryForImage() {
    btnFLMenu.collapse()
    val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    intent.type = "image/*"
    startActivityForResult(intent, 222)
}
//OPEN GALERY


//    ATIVITY RESULT
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        bitmap=null
        imgIn=0
        if(resultCode== Activity.RESULT_OK && requestCode==111){
            bahayaDipilih = data!!.getStringExtra("bahayaDipilih")
            bahayaID = data.getStringExtra("bahayaId")
            tvSumberBahaya.setText(bahayaDipilih)
        }else if(resultCode==Activity.RESULT_OK && requestCode==222) {
            try {
//                data.clipData
                fileUpload = data!!.data
                try {
                   bitmap = BitmapFactory.decodeStream(
                           contentResolver.openInputStream(fileUpload!!))
//                    bitmap = BitmapFactory.decodeStream(stream)
//                    bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, Uri.parse(fileUpload!!.path))
                    imgView.setImageBitmap(bitmap);
//                    imgView.setImageURI(fileUpload)
                    imgIn = 1
                    tvAmbilGambar.visibility=View.VISIBLE
                } catch (e: IOException) {
                    imgIn = 0
                    tvAmbilGambar.visibility=View.GONE
                }
            } catch (e: IOException) {
                imgIn = 0
                tvAmbilGambar.visibility=View.GONE
            }
        }else if(resultCode==Activity.RESULT_OK && requestCode==333){
            try {
                var dataFoto = data!!
                bitmap = BitmapFactory.decodeByteArray(
                    dataFoto.getByteArrayExtra("gambarDiFoto"), 0, dataFoto
                        .getByteArrayExtra("gambarDiFoto").size
                )
//                fileUpload =  createImage(bitmap)
                    imgView.setImageBitmap(bitmap);
                    imgIn = 1
            } catch (e: IOException) {
                tvAmbilGambar.visibility=View.GONE
                imgIn = 0
                e.printStackTrace();
                Toasty.info(this@NewHazardActivity,fileUpload.toString(),Toasty.LENGTH_SHORT).show()
            }
        }else if(resultCode==Activity.RESULT_CANCELED){
            Toasty.info(this@NewHazardActivity,resultCode.toString(),Toasty.LENGTH_SHORT).show()
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
    fun showDialogOption(){
    val alertDialog = AlertDialog.Builder(this@NewHazardActivity)
    alertDialog.setTitle("Silahkan Pilih")
    val animals = arrayOf<String>(
        "Ambil Sebuah Gambar",
        "Pilih Gambar dari galery"
    )
    alertDialog!!.setItems(animals, DialogInterface.OnClickListener{ dialog, which ->
        when (which) {
            0 -> openCamera()
            1 -> openGalleryForImage()
        }
    })
    alertDialog.create()
    alertDialog.show()
}
    //    Dialog PICK PICTURE
    fun openCamera(){
        btnFLMenu.collapse()
        var intent = Intent(this@NewHazardActivity,PhotoHazardActivity::class.java)
        startActivityForResult(intent,333)
    }
    //   Simpan Hazard
    fun simpanHazard(){

        if(!isValidate()){
            return
        }

    PopupUtil.showProgress(this@NewHazardActivity,"Loading...","Membuat Hazard Report!")

    var tvPerusaan = RequestBody.create(MultipartBody.FORM, tvPerusaan.text.toString())
    var tvTanggal = RequestBody.create(MultipartBody.FORM, tvTanggal.text.toString())
    var tvJam = RequestBody.create(MultipartBody.FORM, tvJam.text.toString())
    var tvLokasi = RequestBody.create(MultipartBody.FORM, tvLokasi.text.toString())
    var tvBahaya = RequestBody.create(MultipartBody.FORM, tvBahaya.text.toString())
    var tvSumberBahaya = RequestBody.create(MultipartBody.FORM, tvSumberBahaya.text.toString())
    var tvPerbaikan = RequestBody.create(MultipartBody.FORM, tvPerbaikan.text.toString())
    var tvPenanggungJawab = RequestBody.create(MultipartBody.FORM, tvPenanggungJawab.text.toString())

        if(plKta.isChecked){
            plKondisi = RequestBody.create(MultipartBody.FORM, plKta.text.toString())
        }else if(plTta.isChecked){
            plKondisi = RequestBody.create(MultipartBody.FORM, plTta.text.toString())
        }
        if(rbSelesai.isChecked){
            rbStatus= RequestBody.create(MultipartBody.FORM, rbSelesai.text.toString())
        }else if(rbBelumSelesai.isChecked){
            rbStatus= RequestBody.create(MultipartBody.FORM, rbBelumSelesai.text.toString())
        }else if(rbBerlanjut.isChecked){
            rbStatus= RequestBody.create(MultipartBody.FORM, rbBerlanjut.text.toString())
        }
    var tvTGLselesai = RequestBody.create(MultipartBody.FORM, tvTGLSelesai.text.toString())
    var tvJAMselesai = RequestBody.create(MultipartBody.FORM, tvJamSelesai.text.toString())
    var username = RequestBody.create(MultipartBody.FORM, USERNAME)
    var _token:RequestBody = RequestBody.create(MultipartBody.FORM, csrf_token!!)



    var waktu = Date()
    val cal = Calendar.getInstance()
    cal.time = waktu
    var jam = "${cal.get(Calendar.HOUR_OF_DAY)}${cal.get(Calendar.MINUTE)}${cal.get(Calendar.SECOND)}"
    val wrapper = ContextWrapper(applicationContext)
//    var filenya = File(fileUpload!!.path, jam)
    var file = wrapper.getDir("images", Context.MODE_PRIVATE)
    file = File(file, "${jam}.jpg")
//    var reqFile = RequestBody.create("image/*".toMediaTypeOrNull(),file!!);
    try {
        // Get the file output stream
        val stream: OutputStream = FileOutputStream(file)
        //var uri = Uri.parse(file.absolutePath)
        // Compress bitmap
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 20, stream)
        // Flush the stream
        stream.flush()
        // Close stream
        stream.close()
    } catch (e: IOException){ // Catch the exception
        e.printStackTrace()
    }

    var fileUri = file.asRequestBody("image/*".toMediaTypeOrNull())
    var bukti = MultipartBody.Part.createFormData("fileToUpload",file.name,fileUri)

//    API POST
    val apiEndPoint = ApiClient.getClient(this)!!.create(ApiEndPoint::class.java)
    val call = apiEndPoint.postHazardReport(
        bukti,tvPerusaan,tvTanggal,tvJam,plKondisi!!,tvBahaya,rbStatus!!,
        tvLokasi,tvPerbaikan,tvPenanggungJawab,tvSumberBahaya,
        tvTGLselesai,tvJAMselesai,username,_token
    )
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
                    val intent: Intent = Intent()
                    setResult(Activity.RESULT_OK,intent)
                    finish()
                    PopupUtil.dismissDialog()
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
    //    Simpan Hazard
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

        if(tvPerusaan.text!!.isEmpty()){
            tilPerusahaan.error="Please Input Someting"
            tvPerusaan.requestFocus()
            return false
        }
        if(tvTanggal.text!!.isEmpty()){
            tilTanggal.error="Please Input Someting"
            tvTanggal.requestFocus()
            return false
        }
        if(tvJam.text!!.isEmpty()){
            tilJam.error="Please Input Someting"
            tvJam.requestFocus()
            return false
        }
        if(tvLokasi.text!!.isEmpty()){
            tilLokasi.error="Please Input Someting"
            tvLokasi.requestFocus()
            return false
        }
        if(tvBahaya.text!!.isEmpty()){
            tilBahaya.error="Please Input Someting"
            tvBahaya.requestFocus()
            return false
        }
        if(tvSumberBahaya.text!!.isEmpty()){
            tilSumberBahaya.error="Please Input Someting"
            tvSumberBahaya.requestFocus()
            return false
        }
        if(tvPerbaikan.text!!.isEmpty()){
            tilPerbaikan.error="Please Input Someting"
            tvPerbaikan.requestFocus()
            return false
        }
        if(tvPenanggungJawab.text!!.isEmpty()){
            tilPenanggungJawab.error="Please Input Someting"
            tvPenanggungJawab.requestFocus()
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
        return true
    }
    private fun clearError() {
        tilPerusahaan.error=null
        tilTanggal.error=null
        tilJam.error=null
        tilLokasi.error=null
        tilBahaya.error=null
        tilSumberBahaya.error=null
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

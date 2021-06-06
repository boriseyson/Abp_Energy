package com.misit.abpenergy.HazardReport

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.*
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
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.misit.abpenergy.Api.ApiClient
import com.misit.abpenergy.Api.ApiEndPoint
import com.misit.abpenergy.HazardReport.Response.HazardReportResponse
import com.misit.abpenergy.Login.CompanyActivity
import com.misit.abpenergy.Master.ListUserActivity
import com.misit.abpenergy.R
import com.misit.abpenergy.Rkb.Response.CsrfTokenResponse
import com.misit.abpenergy.Service.MatrikResikoWebViewActivity
import com.misit.abpenergy.Utils.ConfigUtil
import com.misit.abpenergy.Utils.ConfigUtil.resultIntent
import com.misit.abpenergy.Utils.PopupUtil
import com.misit.abpenergy.Utils.PrefsUtil
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_new_hazard.*
import kotlinx.coroutines.*
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
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class NewHazardActivity : AppCompatActivity(),View.OnClickListener {
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
    private var pathFileSebelum:String?=null
    private var pathFileSelesai:String?=null
    private var pathFilePJ:String?=null
    private var storageDir:File? = null
    private var cal = Calendar.getInstance()
    private var userPick:String?=null
    private var pjOption:Int?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_hazard)
        title="Form Hazard Report"
        getToken()
        PrefsUtil.initInstance(this)
        verifyStoragePermissions(this, this)
        if(PrefsUtil.getInstance().getBooleanState("IS_LOGGED_IN", false)){
            USERNAME = PrefsUtil.getInstance().getStringState(PrefsUtil.USER_NAME, "")
        }
        imgIn=0
        bitmap=null
        imgSelesai=0
        bitmapBuktiSelesai=null
        fileUploadPJ=null
        companyDipilih=""
        userPick=""
        imgPJ = 0
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        inTanggal.setOnClickListener(this)
        inJam.setOnClickListener(this)
        inTGLSelesai.setOnClickListener(this)
        imagePicker.setOnClickListener(this)
        btnSimpan.setOnClickListener(this)
        inJamSelesai.setOnClickListener(this)
        btnBatalHazard.setOnClickListener(this)
        matrikResiko.setOnClickListener(this)
        groupStatus.setOnCheckedChangeListener { group, checkedId ->
            if(checkedId==R.id.rbSelesai) {
                lnJamSelesai.visibility = View.VISIBLE
                lnTglSelesai.visibility = View.VISIBLE
                imagePickerBuktiSelesai.visibility = View.VISIBLE
                lnResikoSesudah.visibility = View.VISIBLE
                lnTglTenggat.visibility = View.GONE
            }else{
                lnJamSelesai.visibility=View.GONE
                lnTglSelesai.visibility=View.GONE
                imagePickerBuktiSelesai.visibility=View.GONE
                lnResikoSesudah.visibility = View.GONE
                lnTglTenggat.visibility = View.VISIBLE
            }
        }
        groupPJ.setOnCheckedChangeListener { group, checkedId ->
            if(checkedId==R.id.rbPilih) {
                cvPilihPJ.visibility = View.VISIBLE
                cvManualPJ.visibility=View.GONE
                pjOption=1
            }else{
                cvPilihPJ.visibility = View.GONE
                cvManualPJ.visibility=View.VISIBLE
                pjOption=0
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
        inTGLTenggat.setOnClickListener(this)
        cvPilihPJ.setOnClickListener(this@NewHazardActivity)
    }

    override fun onResume() {
        storageDir = getExternalFilesDir("ABP_IMAGES")
        super.onResume()
    }
    //    VIEW LISTENER
    override fun onClick(v: View?) {
        val c = this@NewHazardActivity
        var waktu = Date()
        cal.time = waktu
        var jam = "${cal.get(Calendar.HOUR_OF_DAY)}${cal.get(Calendar.MINUTE)}${cal.get(Calendar.SECOND)}"
        if(v?.id==R.id.cvPilihPJ){
            val intent = Intent(c, ListUserActivity::class.java)
            intent.putExtra(ListUserActivity.DataExtra,"Hazard")
            intent.putExtra("userPick",userPick)
            startActivityForResult(intent,2626)
        }
        if(v!!.id==R.id.inTanggal){
            ConfigUtil.showDialogTgl(inTanggal,c)
        }
        if (v!!.id==R.id.inJam){
            ConfigUtil.showDialogTime(inJam,c)
        }
        if(v!!.id==R.id.inTGLSelesai){
            ConfigUtil.showDialogTgl(inTGLSelesai,c)
        }
        if(v!!.id==R.id.inTGLTenggat){
            ConfigUtil.showDialogTgl(inTGLTenggat,c)
        }
        if(v!!.id==R.id.inJamSelesai){
            ConfigUtil.showDialogTime(inJamSelesai,c)
        }
        if(v!!.id==R.id.imagePicker){
            showDialogOption(333, 222,"sebelum")
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
            showDialogOption(333, 222,"sebelum")
        }
        if(v!!.id==R.id.btnFotoPJ){
//            PENSNGGUNGJAWAB
            bitmapPJ=null
            imgPJ=0
            showDialogOption(533, 522,"penanggung_jawab")
        }
        if(v!!.id==R.id.pjFOTO){
//            PENSNGGUNGJAWAB
            bitmapPJ=null
            imgPJ=0
            showDialogOption(533,522,"penanggung_jawab")
//            cameraIntent(this@NewHazardActivity, 999, "penanggung_jawab")
        }
        if(v?.id==R.id.imgBuktiSelesai){
//            BUKTI PERBAIKAN
            showDialogOption(433, 422,"selesai")
        }
        if (v?.id==R.id.btnPerbaikan){
//            BUKTI PERBAIKAN
            showDialogOption(433, 422,"selesai")
        }
        if(v?.id==R.id.inLokasi){
            var intent = Intent(this@NewHazardActivity, LokasiActivity::class.java)
            intent.putExtra("lokasiDipilih", lokasiDipilih)
            startActivityForResult(intent, 123)
        }
        if(v?.id==R.id.inPengendalian){
            var intent = Intent(this@NewHazardActivity, SumberBahayaActivity::class.java)
            intent.putExtra("hirarkiDipilh", hirarkiDipilih)
            startActivityForResult(intent, 456)
        }
        if(v?.id==R.id.inKemungkinan){
            var intent = Intent(this@NewHazardActivity, KemungkinanActivity::class.java)
            intent.putExtra("kemungkinanDipilih", kemungkinanDipilih)
            startActivityForResult(intent, 457)
        }
        if(v?.id==R.id.inKeparahan){
            var intent = Intent(this@NewHazardActivity, KeparahanActivity::class.java)
            intent.putExtra("keparahanDipilih", keparahanDipilih)
            startActivityForResult(intent, 458)
        }
        if(v?.id==R.id.inKemungkinanSesudah){
            var intent = Intent(this@NewHazardActivity, KemungkinanActivity::class.java)
            intent.putExtra("kemungkinanDipilih", kemungkinanDipilihSesudah)
            startActivityForResult(intent, 477)
        }
        if(v?.id==R.id.inKeparahanSesudah){
            var intent = Intent(this@NewHazardActivity, KeparahanActivity::class.java)
            intent.putExtra("keparahanDipilih", keparahanDipilihSesudah)
            startActivityForResult(intent, 488)
        }
        if(v?.id==R.id.matrikResiko || v?.id==R.id.matrikResikoSesudah){
            var intent = Intent(this@NewHazardActivity, MatrikResikoWebViewActivity::class.java)
            startActivity(intent)
        }
        if(v?.id==R.id.inPerusaan){
            var intent = Intent(this@NewHazardActivity, CompanyActivity::class.java)
            intent.putExtra("companyDipilih", companyDipilih)
            startActivityForResult(intent, 987)
        }
    }
    //    VIEW LISTENER
//    onCreateOptionsMenu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_submit, menu)
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
    private fun takeWithCamera(c: Activity, requestCode: Int) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, requestCode)
        } catch (e: ActivityNotFoundException) {
            Log.d("ErrorCamera", e.toString())
        }
    }
    private fun cameraIntent(c: Activity, requestCode: Int, fName: String){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile(fName)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                        Log.d("errorCreate", ex.toString())
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI = FileProvider.getUriForFile(
                        c,
                        "com.misit.abpenergy.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, requestCode)
                }
            }
        }

    }
//    verifyStoragePermissions
    private fun verifyStoragePermissions(context: Context, activity: Activity) {
        val permission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val permission1 = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val permission2 = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_PHONE_STATE
        )
        val permission3 = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("FaceId", "READ_EXTERNAL_STORAGE Permission to record denied")
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                11
            )
//            finish()
        }
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            Log.i("FaceId", "WRITE_EXTERNAL_STORAGE Permission to record denied")
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                12
            )
//            finish()
        }
        if (permission2 != PackageManager.PERMISSION_GRANTED) {
            Log.i("FaceId", "READ_PHONE_STATE Permission to record denied")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                13
            )
//            finish()
        }
        if (permission3 != PackageManager.PERMISSION_GRANTED) {
            Log.i("FaceId", "READ_PHONE_STATE Permission to record denied")
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA), 13)
//            finish()
        }
    }
//    verifyStoragePermissions
    //OPEN GALERY
    private fun openGalleryForImage(codeRequest: Int) {
    btnFLMenu.collapse()
    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
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
//            Toasty.info(this@NewHazardActivity,"${kemungkinanIDSesudah}").show()
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
//            GALERY INTENT SEBELUM
            try {
                fileUpload = data!!.data
                try {
                   bitmap = BitmapFactory.decodeStream(
                       contentResolver.openInputStream(fileUpload!!)
                   )
                    imgView.setImageBitmap(bitmap);
                    imgIn = 1
                } catch (e: IOException) {
                    imgIn = 0
                }
            } catch (e: IOException) {
                imgIn = 0
            }
        }else if(resultCode==Activity.RESULT_OK && requestCode==333){
//            Camera Inten Sebelum
            try {
                fileUpload = "file:///${pathFileSebelum}".toUri()
                try {
                    bitmap = BitmapFactory.decodeStream(
                        contentResolver.openInputStream(fileUpload!!)
                    )
                    Glide.with(this@NewHazardActivity).load(fileUpload).into(imgView)
                } catch (e: IOException) {
                    e.printStackTrace();
                }
                imgIn = 1
            } catch (e: IOException) {
                imgIn = 0
                e.printStackTrace();
            }
        }else if(resultCode==Activity.RESULT_OK && requestCode==422) {
//            Galery Inten Selesai
            try {
                fileUploadSelesai = data!!.data
                try {
                    bitmapBuktiSelesai = BitmapFactory.decodeStream(
                        contentResolver.openInputStream(fileUploadSelesai!!)
                    )
                    imgBuktiSelesai.setImageBitmap(bitmapBuktiSelesai);
                    imgSelesai = 1
                } catch (e: IOException) {
                    imgSelesai = 0
                }
            } catch (e: IOException) {
                imgSelesai = 0
            }
        }else if(resultCode==Activity.RESULT_OK && requestCode==433){
//            Camera Intent Selesai
            try {
                fileUploadSelesai = "file:///${pathFileSelesai}".toUri()
                try {
                    bitmapBuktiSelesai = BitmapFactory.decodeStream(
                        contentResolver.openInputStream(fileUploadSelesai!!)
                    )
                    Glide.with(this@NewHazardActivity).load(fileUploadSelesai).into(imgBuktiSelesai)
                } catch (e: IOException) {
                    e.printStackTrace();
                }
                imgSelesai = 1
            } catch (e: IOException) {
                imgSelesai = 0
                e.printStackTrace();
            }
        }else if(resultCode==Activity.RESULT_OK && requestCode==522) {
//            Galery Intent
            try {
//                data.clipData
                fileUploadPJ = data!!.data
                try {
                    bitmapPJ = BitmapFactory.decodeStream(
                        contentResolver.openInputStream(fileUploadPJ!!)
                    )
                    pjFOTO.setImageBitmap(bitmapPJ);
                    imgPJ = 1
                } catch (e: IOException) {
                    imgPJ = 0
                }
            } catch (e: IOException) {
                imgPJ = 0
            }
        }else if(resultCode==Activity.RESULT_OK && requestCode==533){
//            camera intent
            try {
                fileUploadPJ = "file:///${pathFilePJ}".toUri()
                try {
                    bitmapPJ = BitmapFactory.decodeStream(
                        contentResolver.openInputStream(fileUploadPJ!!)
                    )
                    Glide.with(this@NewHazardActivity).load(fileUploadPJ).into(pjFOTO)
                } catch (e: IOException) {
                    e.printStackTrace();
                }
                imgIn = 1
            } catch (e: IOException) {
                imgIn = 0
                e.printStackTrace();
            }
        }else if(resultCode==Activity.RESULT_CANCELED){
        }else if (requestCode == 999 && resultCode == RESULT_OK) {
            try {
                fileUploadPJ = "file:///${pathFilePJ}".toUri()
                try {
                    bitmapPJ = BitmapFactory.decodeStream(
                        contentResolver.openInputStream(fileUploadPJ!!)
                    )
                    Glide.with(this@NewHazardActivity).load(fileUploadPJ).into(pjFOTO)
                } catch (e: IOException) {
                    e.printStackTrace();
                }
                    imgIn = 1
            } catch (e: IOException) {
                imgIn = 0
                e.printStackTrace();
            }
        }else if(requestCode==2626 && resultCode== RESULT_OK){
            userPick = data!!.getStringExtra(userPick)
            val nama = data!!.getStringExtra("nama")
            val nik = data!!.getStringExtra("nik")
            val profileIMG = data!!.getStringExtra("profileIMG")
            val url = URL(profileIMG)
            val result: Deferred<Bitmap?> = GlobalScope.async {
                PopupUtil.showProgress(this@NewHazardActivity, "Loading...", "Membuat Hazard Report!")
                url.toBitmap()
            }
            GlobalScope.launch(Dispatchers.Main) {
                // show bitmap on image view when available
                bitmapPJ = result.await()
                PopupUtil.dismissDialog()
            }
//            bitmapPJ = ConfigUtil.getBitmapFromURL(profileIMG)
            Glide.with(this@NewHazardActivity).load(profileIMG).into(pjFOTOPilih)
            inPenanggungJawabPilih.setText(nama.toString())
            inNikPJPilih.setText(nik.toString())
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
    //ACTIVITY RESULT
// extension function to get bitmap from url
    fun URL.toBitmap(): Bitmap?{
        return try {
            BitmapFactory.decodeStream(openStream())
        }catch (e:IOException){
            null
        }
    }
//    Dialog PICK PICTURE
    fun showDialogOption(camera: Int, galery: Int,fName:String){
    val c = this@NewHazardActivity
    val alertDialog = AlertDialog.Builder(c)
    alertDialog.setTitle("Silahkan Pilih")
    val animals = arrayOf<String>(
        "Ambil Sebuah Gambar",
        "Pilih Gambar dari galery"
    )
    alertDialog!!.setItems(animals, DialogInterface.OnClickListener { dialog, which ->
        when (which) {
            0 -> cameraIntent(c, camera,fName)
            1 -> openGalleryForImage(galery)
        }
    })
    alertDialog.create()
    alertDialog.show()
}
    @Throws(IOException::class)
    private fun createImageFile(fName: String): File {
        // Create an image file name
        var jam = "${cal.get(Calendar.HOUR_OF_DAY)}${cal.get(Calendar.MINUTE)}${cal.get(Calendar.SECOND)}"
        val fileName = "${jam}_${fName}"
        return File.createTempFile(
            "${fName}", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            if(fName=="sebelum"){
                pathFileSebelum = absolutePath
            }else if (fName=="selesai"){
                pathFileSelesai = absolutePath
            }else if (fName=="penanggung_jawab"){
                pathFilePJ = absolutePath
            }
        }
    }

    //    Dialog PICK PICTURE
    fun openCamera(codeRequest: Int){
        btnFLMenu.collapse()
        var intent = Intent(this@NewHazardActivity, PhotoHazardActivity::class.java)
        startActivityForResult(intent, codeRequest)
    }
//       Simpan Hazard
    fun simpanHazard(){

//    Toasty.info(this@NewHazardActivity,"Sebelum : ${kemungkinanID} | Sesudah ${kemungkinanIDSesudah}").show()
        if(rbSelesai.isChecked) {
            if(pjOption==1){
                if(!isValidate2()){
                    return
                }
                Toasty.info(this@NewHazardActivity,"option 1 validate 2").show()
            }else{
                if(!isValidate1()){
                    return
                }
                Toasty.info(this@NewHazardActivity,"option 2 validate 1").show()

            }

        }
        else{
            if(pjOption==1){
                if (!isValidate3()) {
                    return
                }
                Toasty.info(this@NewHazardActivity,"option 1 validate 3").show()
            }else{
                if (!isValidate()) {
                    return
                }
                Toasty.info(this@NewHazardActivity,"option 2 validate").show()

            }

        }
    PopupUtil.showProgress(this@NewHazardActivity, "Loading...", "Membuat Hazard Report!")

    var inPerusahan = inPerusaan.text.toString().toRequestBody(MultipartBody.FORM)
    var inTanggal = inTanggal.text.toString().toRequestBody(MultipartBody.FORM)
    var inJam = inJam.text.toString().toRequestBody(MultipartBody.FORM)
    var lokasi = lokasiID.toString().toRequestBody(MultipartBody.FORM)
    var inLokasiDet = inLokasiDet.text.toString().toRequestBody(MultipartBody.FORM)
    var inBahaya = inBahaya.text.toString().toRequestBody(MultipartBody.FORM)
    var kemungkinanID = kemungkinanID.toString().toRequestBody(MultipartBody.FORM)
    var keparahanID = keparahanID.toString().toRequestBody(MultipartBody.FORM)
    var kemungkinanSesudahID = kemungkinanIDSesudah.toString().toRequestBody(MultipartBody.FORM)
    var keparahanSesudahID =keparahanIDSesudah.toString().toRequestBody(MultipartBody.FORM)
    var hirarkiID = hirarkiID.toString().toRequestBody(MultipartBody.FORM)
    var inPerbaikan = inPerbaikan.text.toString().toRequestBody(MultipartBody.FORM)
    var pjNama:RequestBody?=null
    var pjNik:RequestBody?=null
    if(pjOption==1) {
        pjNama = inPenanggungJawabPilih.text.toString().toRequestBody(MultipartBody.FORM)
        pjNik = inNikPJPilih.text.toString().toRequestBody(MultipartBody.FORM)
    }else{

        pjNama = inPenanggungJawab.text.toString().toRequestBody(MultipartBody.FORM)
        pjNik = inNikPJ.text.toString().toRequestBody(MultipartBody.FORM)
    }
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
    var tglTenggat = inTGLTenggat.text.toString().toRequestBody(MultipartBody.FORM)
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
        ConfigUtil.streamFoto(bitmap!!, file)
        var fileUri = file.asRequestBody("image/*".toMediaTypeOrNull())
        var bukti = MultipartBody.Part.createFormData("fileToUpload", file.name, fileUri)
            //        FOTO PENANGGUNG JAWAB
            var filePJ = wrapper.getDir("images", Context.MODE_PRIVATE)
            filePJ = File(filePJ, "${jam}_penanggung_jawab.jpg")
            //    var reqFile = RequestBody.create("image/*".toMediaTypeOrNull(),file!!);
            ConfigUtil.streamFoto(bitmapPJ!!, filePJ)
            var fileUriPJ = filePJ.asRequestBody("image/*".toMediaTypeOrNull())
            var fotoPJ = MultipartBody.Part.createFormData("fileToUploadPJ", filePJ.name, fileUriPJ)
    //        FOTO PENANGGUNG JAWAB

    var fileUriSelsai:RequestBody?=null
        var buktiSelesai :MultipartBody.Part?=null
        var fileSelesai = wrapper.getDir("images", Context.MODE_PRIVATE)
        if(bitmapBuktiSelesai!=null) {
//        Bukti Selesai
            fileSelesai = File(fileSelesai, "${jam}_selesai.jpg")
            ConfigUtil.streamFoto(bitmapBuktiSelesai!!, fileSelesai)
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
                pjNama,
                pjNik,
                rbStatus!!,
                inTGLSelesai,
                inJamSelesai,
                inKeteranganPJ,
                username,
                _token, "Bukti_Progress",
                kemungkinanSesudahID,
                keparahanSesudahID,
                tglTenggat
            )
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
                pjNama,
                pjNik,
                rbStatus!!,
                inTGLSelesai,
                inJamSelesai,
                inKeteranganPJ,
                username,
                _token, "Bukti_Selesai",
                kemungkinanSesudahID,
                keparahanSesudahID,
                tglTenggat
            )
        }
    }
//        Simpan Hazard
//    Save Hazard
    private fun hazardPost(
    fileBukti: MultipartBody.Part,
    filePJ: MultipartBody.Part?,
    fileSelesai: MultipartBody.Part?,
    perusahaan: RequestBody,
    tanggal: RequestBody,
    jam: RequestBody,
    bahaya: RequestBody,
    lokasi: RequestBody,
    lokasiDet: RequestBody,
    kemungkinan: RequestBody,
    keparahan: RequestBody,
    kondisi: RequestBody,
    hirarki: RequestBody,
    perbaikan: RequestBody,
    namaPJ: RequestBody,
    nikPJ: RequestBody,
    status: RequestBody,
    tglSelesai: RequestBody,
    jamSelesai: RequestBody,
    keteranganPJ: RequestBody,
    user: RequestBody,
    token: RequestBody,
    tipe: String,
    kemungkinanSesudah: RequestBody,
    keparahanSesudah: RequestBody, tglTenggat: RequestBody
){
    //    API POST
    val apiEndPoint = ApiClient.getClient(this)!!.create(ApiEndPoint::class.java)
     var call:Call<HazardReportResponse>?=null
        if(tipe=="Bukti_Progress"){
            call = apiEndPoint.postHazardReport(
                fileBukti, filePJ, perusahaan, tanggal, jam, lokasi,
                lokasiDet, bahaya, kemungkinan, keparahan, kondisi, hirarki, perbaikan,
                namaPJ, nikPJ, status, tglTenggat, user, token
            )
        }else if(tipe=="Bukti_Selesai"){
             call = apiEndPoint.postHazardReportSelesai(
                 fileBukti,
                 filePJ,
                 fileSelesai,
                 perusahaan,
                 tanggal,
                 jam,
                 lokasi,
                 lokasiDet,
                 bahaya,
                 kemungkinan,
                 keparahan,
                 kemungkinanSesudah,
                 keparahanSesudah,
                 kondisi,
                 hirarki,
                 perbaikan,
                 namaPJ,
                 nikPJ,
                 status,
                 tglSelesai,
                 jamSelesai,
                 keteranganPJ,
                 user,
                 token
             )
        }
    call?.enqueue(object : Callback<HazardReportResponse> {
        override fun onFailure(call: Call<HazardReportResponse>, t: Throwable) {
            Toast.makeText(this@NewHazardActivity, "Error : $t", Toast.LENGTH_SHORT).show()
            PopupUtil.dismissDialog()
        }

        override fun onResponse(
            call: Call<HazardReportResponse>,
            response: Response<HazardReportResponse>
        ) {
            var sResponse = response.body()
            if (sResponse != null) {
                if (sResponse.success!!) {
                    if(ConfigUtil.deleteInABPIMAGES(this@NewHazardActivity)){
                        Toasty.success(this@NewHazardActivity, "Hazard Report Telah Dibuat! ").show()
                        resultIntent(this@NewHazardActivity)
                        PopupUtil.dismissDialog()
                        finish()
                    }else{
                        ConfigUtil.deleteInABPIMAGES(this@NewHazardActivity)
                    }

                } else {
                    Toasty.error(this@NewHazardActivity, "Gagal Membuat Hazard Report! ").show()
                    PopupUtil.dismissDialog()
                }
            } else {
                Toasty.error(this@NewHazardActivity, "Gagal Membuat Hazard Report! ").show()
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
            Toast.makeText(this@NewHazardActivity, "Error : $t", Toast.LENGTH_SHORT).show()
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
    if(inNikPJ.text!!.isEmpty()){
        tilNikPJ.error="Please Input Someting"
        inNikPJ.requestFocus()
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
    if(imgPJ<=0){
        Toasty.error(
            this@NewHazardActivity,
            "Harap Memilih Gambar Penanggung Jawab",
            Toasty.LENGTH_LONG
        ).show()
        pjFOTO.performClick()
        return false
    }
    return true
}
    fun isValidate2():Boolean{
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
        if(inPenanggungJawabPilih.text!!.isEmpty()){
            tilPenanggungJawabPilih.error="Please Input Someting"
            cvPilihPJ.performClick()
            return false
        }
        if(inNikPJPilih.text!!.isEmpty()){
            tilNikPJPilih.error="Please Input Someting"
            cvPilihPJ.performClick()
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

        if(imgPJ<=0){
            Toasty.error(
                this@NewHazardActivity,
                "Harap Memilih Gambar Bukti Selesai",
                Toasty.LENGTH_LONG
            ).show()
            pjFOTO.performClick()
            return false
        }
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
            Toasty.error(this@NewHazardActivity, "Harap Memilih Gambar", Toasty.LENGTH_LONG).show()
            imagePicker.performClick()
            return false
        }
        if (imgSelesai <= 0 )
        {
            Toasty.error(
                this@NewHazardActivity,
                "Harap Memilih Gambar Penanggung Jawab",
                Toasty.LENGTH_LONG
            ).show()
            imagePickerBuktiSelesai.performClick()
            return false
        }
        return true
    }
    fun isValidate3():Boolean{
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
        if(inPenanggungJawabPilih.text!!.isEmpty()){
            tilPenanggungJawabPilih.error="Please Input Someting"
            cvPilihPJ.performClick()
            return false
        }
        if(inNikPJPilih.text!!.isEmpty()){
            tilNikPJPilih.error="Please Input Someting"
            cvPilihPJ.performClick()
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
            Toasty.error(this@NewHazardActivity, "Harap Memilih Gambar", Toasty.LENGTH_LONG).show()
            imagePicker.performClick()
            return false
        }
        if (imgSelesai <= 0 )
        {
            Toasty.error(
                this@NewHazardActivity,
                "Harap Memilih Gambar Bukti Selesai",
                Toasty.LENGTH_LONG
            ).show()
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
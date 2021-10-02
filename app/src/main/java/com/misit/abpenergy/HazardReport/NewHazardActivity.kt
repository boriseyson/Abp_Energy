package com.misit.abpenergy.HazardReport

import android.Manifest
import android.app.Activity
import android.app.job.JobScheduler
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
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.misit.abpenergy.Api.ApiClient
import com.misit.abpenergy.Api.ApiEndPoint
import com.misit.abpenergy.HazardReport.SQLite.DataSource.HazardDetailDataSource
import com.misit.abpenergy.HazardReport.SQLite.DataSource.HazardHeaderDataSource
import com.misit.abpenergy.HazardReport.SQLite.DataSource.HazardValidationDataSource
import com.misit.abpenergy.HazardReport.SQLite.Model.HazardDetailModel
import com.misit.abpenergy.HazardReport.SQLite.Model.HazardHeaderModel
import com.misit.abpenergy.HazardReport.SQLite.Model.HazardValidationModel
import com.misit.abpenergy.HazardReport.Service.BgHazardService
import com.misit.abpenergy.Login.CompanyActivity
import com.misit.abpenergy.Master.ListUserActivity
import com.misit.abpenergy.R
import com.misit.abpenergy.Rkb.Response.CsrfTokenResponse
import com.misit.abpenergy.Service.ConnectionService
import com.misit.abpenergy.Service.MatrikResikoWebViewActivity
import com.misit.abpenergy.Utils.*
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_new_hazard.*
import kotlinx.coroutines.*
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.net.URL
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
    private var tokenPassingReceiver: BroadcastReceiver?=null
    lateinit var bgHazardService:Intent
    lateinit var connectionService:Intent
    private var scheduler: JobScheduler?=null
    var builder : AlertDialog.Builder?=null
    var dialog : AlertDialog?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_hazard)
        title="Form Hazard Report"
        PrefsUtil.initInstance(this)
        verifyStoragePermissions(this, this)
        if(PrefsUtil.getInstance().getBooleanState("IS_LOGGED_IN", false)){
            USERNAME = PrefsUtil.getInstance().getStringState(PrefsUtil.USER_NAME, "")
        }
        connectionService = Intent(this@NewHazardActivity, ConnectionService::class.java)
        scheduler = this@NewHazardActivity.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
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
        bgHazardService = Intent(this@NewHazardActivity, BgHazardService::class.java)
        if(ConfigUtil.isJobServiceOn(this@NewHazardActivity, Constants.JOB_SERVICE_ID)){
            ConfigUtil.stopJobScheduler(scheduler)
            Log.d("JobService", "Not Running")
        }
    }

    override fun onResume() {
        storageDir = getExternalFilesDir("ABP_IMAGES")
        LocalBroadcastManager.getInstance(this@NewHazardActivity).registerReceiver(
            tokenPassingReceiver!!, IntentFilter(
                "com.misit.abpenergy"
            )
        )

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
            intent.putExtra(ListUserActivity.DataExtra, "Hazard")
            intent.putExtra(USEPICK, userPick)
            startActivityForResult(intent, Constants.PJ_CODE_OPTION)
        }
        if(v!!.id==R.id.inTanggal){
            ConfigUtil.showDialogTgl(inTanggal, c)
        }
        if (v!!.id==R.id.inJam){
            ConfigUtil.showDialogTime(inJam, c)
        }
        if(v!!.id==R.id.inTGLSelesai){
            ConfigUtil.showDialogTgl(inTGLSelesai, c)
        }
        if(v!!.id==R.id.inTGLTenggat){
            ConfigUtil.showDialogTgl(inTGLTenggat, c)
        }
        if(v!!.id==R.id.inJamSelesai){
            ConfigUtil.showDialogTime(inJamSelesai, c)
        }
        if(v!!.id==R.id.imagePicker){
            showDialogOption(Constants.BUKTI_CODE_CAMERA, Constants.BUKTI_CODE_GALERY, SEBELUM)
        }
        if(v!!.id==R.id.btnSimpan){
//            simpanHazard()
            simpanOffline()
        }
        if(v!!.id==R.id.btnBatalHazard){
            areYouSure("Informasi", "Apakah anda yakin?")
        }
        if(v!!.id==R.id.btnGambarHazard){
            bitmap=null
            imgIn=0
            showDialogOption(Constants.BUKTI_CODE_CAMERA, Constants.BUKTI_CODE_GALERY, SEBELUM)
        }
        if(v!!.id==R.id.btnFotoPJ){
//            PENSNGGUNGJAWAB
            bitmapPJ=null
            imgPJ=0
            showDialogOption(Constants.PJ_CODE_CAMERA, Constants.PJ_CODE_GALERY, PENANGGUNG_JAWAB)
        }
        if(v!!.id==R.id.pjFOTO){
//            PENSNGGUNGJAWAB
            bitmapPJ=null
            imgPJ=0
            showDialogOption(Constants.PJ_CODE_CAMERA, Constants.PJ_CODE_GALERY, PENANGGUNG_JAWAB)
//            cameraIntent(this@NewHazardActivity, 999, "penanggung_jawab")
        }
        if(v?.id==R.id.imgBuktiSelesai){
//            BUKTI PERBAIKAN
            showDialogOption(Constants.SELESAI_CODE_CAMERA, Constants.SELESAI_CODE_GALERY, SELESAI)
        }
        if (v?.id==R.id.btnPerbaikan){
//            BUKTI PERBAIKAN
            showDialogOption(Constants.SELESAI_CODE_CAMERA, Constants.SELESAI_CODE_GALERY, SELESAI)
        }
        if(v?.id==R.id.inLokasi){
            var intent = Intent(this@NewHazardActivity, LokasiActivity::class.java)
            intent.putExtra("lokasiDipilih", lokasiDipilih)
            startActivityForResult(intent, Constants.LOKASI_CODE)
        }
        if(v?.id==R.id.inPengendalian){
            var intent = Intent(this@NewHazardActivity, SumberBahayaActivity::class.java)
            intent.putExtra("hirarkiDipilh", hirarkiDipilih)
            startActivityForResult(intent, Constants.HIRARKI_CODE)
        }
        if(v?.id==R.id.inKemungkinan){
            var intent = Intent(this@NewHazardActivity, KemungkinanActivity::class.java)
            intent.putExtra("kemungkinanDipilih", kemungkinanDipilih)
            startActivityForResult(intent, Constants.KEMUNGKINAN_SEBELUM_CODE)
        }
        if(v?.id==R.id.inKeparahan){
            var intent = Intent(this@NewHazardActivity, KeparahanActivity::class.java)
            intent.putExtra("keparahanDipilih", keparahanDipilih)
            startActivityForResult(intent, Constants.KEPARAHAN_SEBELUM_CODE)
        }
        if(v?.id==R.id.inKemungkinanSesudah){
            var intent = Intent(this@NewHazardActivity, KemungkinanActivity::class.java)
            intent.putExtra("kemungkinanDipilih", kemungkinanDipilihSesudah)
            startActivityForResult(intent, Constants.KEMUNGKINAN_SESUDAH_CODE)
        }
        if(v?.id==R.id.inKeparahanSesudah){
            var intent = Intent(this@NewHazardActivity, KeparahanActivity::class.java)
            intent.putExtra("keparahanDipilih", keparahanDipilihSesudah)
            startActivityForResult(intent, Constants.KEMAPARAHAN_SESUDAH_CODE)
        }
        if(v?.id==R.id.matrikResiko || v?.id==R.id.matrikResikoSesudah){
            var intent = Intent(this@NewHazardActivity, MatrikResikoWebViewActivity::class.java)
            startActivity(intent)
        }
        if(v?.id==R.id.inPerusaan){
            var intent = Intent(this@NewHazardActivity, CompanyActivity::class.java)
            intent.putExtra("companyDipilih", companyDipilih)
            startActivityForResult(intent, Constants.COMPANY_CODE)
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
//    areYouSure("Informasi","Apakah anda yakin?")
    onBackPressed()
        return super.onSupportNavigateUp()
    }
//    onSupportNavigateUp
    private fun areYouSure(titleDialog: String, msgDialog: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titleDialog)
        builder.setMessage(msgDialog)
        builder.setPositiveButton("Tidak") { dialog, which ->
        }
        builder.setNegativeButton("Ya") { dialog, which ->
                LocalBroadcastManager.getInstance(this@NewHazardActivity).unregisterReceiver(
                    tokenPassingReceiver!!
                )
                finish()
        }
        builder.show()
    }

    override fun onBackPressed() {
        areYouSure("Informasi", "Apakah anda yakin?")
//        super.onBackPressed()
    }
//    onOptionsItemSelected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.btnSubmit){
            simpanOffline()
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

        if(resultCode== Activity.RESULT_OK && requestCode==Constants.COMPANY_CODE){
            companyDipilih = data!!.getStringExtra("companyDipilih")
            inPerusaan.setText(companyDipilih)
        }else if(resultCode== Activity.RESULT_OK && requestCode==Constants.KEMUNGKINAN_SEBELUM_CODE){
            kemungkinanDipilih = data!!.getStringExtra("kemungkinanDipilih")
            kemungkinanID = data.getStringExtra("kemungkinanID")
            inKemungkinan.setText(kemungkinanDipilih)
        }else if(resultCode== Activity.RESULT_OK && requestCode==Constants.KEMUNGKINAN_SESUDAH_CODE){
            kemungkinanDipilihSesudah = data!!.getStringExtra("kemungkinanDipilih")
            kemungkinanIDSesudah = data.getStringExtra("kemungkinanID")
//            Toasty.info(this@NewHazardActivity,"${kemungkinanIDSesudah}").show()
            inKemungkinanSesudah.setText(kemungkinanDipilihSesudah)
        }else if(resultCode== Activity.RESULT_OK && requestCode==Constants.LOKASI_CODE){
            lokasiDipilih = data!!.getStringExtra("lokasiDipilih")
            lokasiID = data.getStringExtra("lokasiID")
            inLokasi.setText(lokasiDipilih)
        }else if(resultCode== Activity.RESULT_OK && requestCode==Constants.HIRARKI_CODE){
            hirarkiDipilih = data!!.getStringExtra("hirarkiDipilih")
            hirarkiID = data.getStringExtra("hirarkiID")
            inPengendalian.setText(hirarkiDipilih)
        }else if(resultCode== Activity.RESULT_OK && requestCode==Constants.KEPARAHAN_SEBELUM_CODE){
            keparahanDipilih = data!!.getStringExtra("keparahanDipilih")
            keparahanID = data.getStringExtra("keparahanID")
            inKeparahan.setText(keparahanDipilih)
        }else if(resultCode== Activity.RESULT_OK && requestCode==Constants.KEMAPARAHAN_SESUDAH_CODE){
            keparahanDipilihSesudah = data!!.getStringExtra("keparahanDipilih")
            keparahanIDSesudah = data.getStringExtra("keparahanID")
            inKeparahanSesudah.setText(keparahanDipilihSesudah)
        }else if(resultCode==Activity.RESULT_OK && requestCode==Constants.BUKTI_CODE_GALERY) {
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
        }else if(resultCode==Activity.RESULT_OK && requestCode==Constants.BUKTI_CODE_CAMERA){
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
        }else if(resultCode==Activity.RESULT_OK && requestCode==Constants.SELESAI_CODE_GALERY) {
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
        }else if(resultCode==Activity.RESULT_OK && requestCode==Constants.SELESAI_CODE_CAMERA){
//            Camera Intent Selesai
            try {
                fileUploadSelesai = "file:///${pathFileSelesai}".toUri()
                try {
                    bitmapBuktiSelesai = BitmapFactory.decodeStream(
                        contentResolver.openInputStream(fileUploadSelesai!!)
                    )
                    imgSelesai = 1
                    Glide.with(this@NewHazardActivity).load(fileUploadSelesai).into(imgBuktiSelesai)
                } catch (e: IOException) {
                    e.printStackTrace();
                    imgSelesai = 0
                }
            } catch (e: IOException) {
                imgSelesai = 0
                e.printStackTrace();
            }
        }else if(resultCode==Activity.RESULT_OK && requestCode==Constants.PJ_CODE_GALERY) {
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
        }else if(resultCode==Activity.RESULT_OK && requestCode==Constants.PJ_CODE_CAMERA){
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
                imgPJ = 1
            } catch (e: IOException) {
                imgPJ = 0
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
                    imgPJ = 1
            } catch (e: IOException) {
                imgPJ = 0
                e.printStackTrace();
            }
        }else if(requestCode==Constants.PJ_CODE_OPTION && resultCode== RESULT_OK){
            try {
                userPick = data!!.getStringExtra(userPick)
            val nama = data!!.getStringExtra("nama")
            val nik = data!!.getStringExtra("nik")
            val profileIMG = data!!.getStringExtra("profileIMG")
                val dir = this@NewHazardActivity!!.getExternalFilesDir("PROFILE_IMAGE")
                val file = File(dir, profileIMG)
                fileUploadPJ = file.toUri()
                Log.d("fileUploadPJ", fileUploadPJ.toString())
                try {
                    bitmapPJ = BitmapFactory.decodeStream(
                        contentResolver.openInputStream(fileUploadPJ!!)
                    )
                    Glide.with(this@NewHazardActivity).load(fileUploadPJ).into(pjFOTOPilih)
                } catch (e: IOException) {
                    e.printStackTrace();
                }
                inPenanggungJawabPilih.setText(nama.toString())
                inNikPJPilih.setText(nik.toString())
            }catch (e: Exception){
                Toasty.error(
                    this@NewHazardActivity,
                    "Penanggung Jawab Tidak Mempunyai Foto, Silahkan Memilih Manual Penanggung Jawab"
                ).show()
                Log.d("LoadImage", e.toString())
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
    //ACTIVITY RESULT
// extension function to get bitmap from url
    fun URL.toBitmap(): Bitmap?{
        return try {
            BitmapFactory.decodeStream(openStream())
        }catch (e: IOException){
            null
        }
    }
//    Dialog PICK PICTURE
    fun showDialogOption(camera: Int, galery: Int, fName: String){
    val c = this@NewHazardActivity
    val alertDialog = AlertDialog.Builder(c)
    alertDialog.setTitle("Silahkan Pilih")
    val animals = arrayOf<String>(
        "Ambil Sebuah Gambar",
        "Pilih Gambar dari galery"
    )
    alertDialog!!.setItems(animals, DialogInterface.OnClickListener { dialog, which ->
        when (which) {
            0 -> cameraIntent(c, camera, fName)
            1 -> openGalleryForImage(galery)
        }
    })
    alertDialog.create()
    alertDialog.show()
}
    @Throws(IOException::class)
    private fun createImageFile(fName: String): File {
        // Create an image file name
        return File.createTempFile(
            "${fName}", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            if(fName== SEBELUM){
                pathFileSebelum = absolutePath
            }else if (fName==SELESAI){
                pathFileSelesai = absolutePath
            }else if (fName==PENANGGUNG_JAWAB){
                pathFilePJ = absolutePath
            }
            Log.d("absolutePath","${absolutePath}")
        }
    }
    //    Dialog PICK PICTURE
    fun openCamera(codeRequest: Int){
        btnFLMenu.collapse()
        var intent = Intent(this@NewHazardActivity, PhotoHazardActivity::class.java)
        startActivityForResult(intent, codeRequest)
    }

    //    Simpan Offline
    private fun simpanOffline(){
        if(rbSelesai.isChecked) {
        if(pjOption==1){
            Log.d("CheckStatus", "validate 2")
            if(!isValidate2()){
                return
            }
        }else{
            Log.d("CheckStatus", "validate 1")
            if(!isValidate1()){
                return
            }
        }
    }
    else{
        if(pjOption==1){
            Log.d("CheckStatus", "validate 3")

            if (!isValidate3()) {
                return
            }
        }else{
            Log.d("CheckStatus", "validate")

            if (!isValidate()) {
                return
            }
        }

        }
        showLoading(this@NewHazardActivity, "Membuat Hazard Report", "abp")
        //    initial
    var inPerusahan = inPerusaan.text.toString()
    var inTanggal = inTanggal.text.toString()
    var inJam = inJam.text.toString()
    var lokasi = lokasiID.toString()
    var inLokasiDet = inLokasiDet.text.toString()
    var inBahaya = inBahaya.text.toString()
    var kemungkinanID = kemungkinanID.toString()
    var keparahanID = keparahanID.toString()
    var kemungkinanSesudahID = kemungkinanIDSesudah
    var keparahanSesudahID =keparahanIDSesudah
    var hirarkiID = hirarkiID
    var inPerbaikan = inPerbaikan.text.toString()
    var inTGLSelesai = inTGLSelesai.text.toString()
    var inJamSelesai = inJamSelesai.text.toString()
    var tglTenggat = inTGLTenggat.text.toString()
    var inKeteranganPJ = inKeteranganPJ.text.toString()
    var pjNama = ""
    var pjNik = ""

    if(pjOption==1) {
        pjNama = inPenanggungJawabPilih.text.toString()
        pjNik = inNikPJPilih.text.toString()
    }else{

        pjNama = inPenanggungJawab.text.toString()
        pjNik = inNikPJ.text.toString()
    }
    val dir = this.getExternalFilesDir("HAZARD_OFFLINE")
    var waktu = Date()
    val cal = Calendar.getInstance()
    cal.time = waktu
    var jam = "${cal.get(Calendar.HOUR_OF_DAY)}${cal.get(Calendar.MINUTE)}${cal.get(Calendar.SECOND)}"
    val wrapper = ContextWrapper(applicationContext)
    //    var filenya = File(fileUpload!!.path, jam)
    var file = wrapper.getDir("images", Context.MODE_PRIVATE)
    file = File(file, "${jam}_sebelum.jpg")
    ConfigUtil.streamFoto(bitmap!!, file)
    //        FOTO PENANGGUNG JAWAB
    var filePJ = wrapper.getDir("images", Context.MODE_PRIVATE)
    filePJ = File(filePJ, "${jam}_penanggung_jawab.jpg")
        //    var reqFile = RequestBody.create("image/*".toMediaTypeOrNull(),file!!);
//    ConfigUtil.streamFoto(bitmapPJ!!, filePJ)
    //        FOTO PENANGGUNG JAWAB
    var buktiSelesai=""
    if(bitmapBuktiSelesai!=null) {
//        Bukti Selesai
        var fileSelesai = wrapper.getDir("images", Context.MODE_PRIVATE)
        fileSelesai = File(fileSelesai, "${jam}_selesai.jpg")
        ConfigUtil.streamFoto(bitmapBuktiSelesai!!, fileSelesai)
        buktiSelesai = fileSelesai.name
        ConfigUtil.saveFile(
            bitmapBuktiSelesai!!,
            this@NewHazardActivity,
            "HAZARD_OFFLINE",
            buktiSelesai
        )
    }
    var bukti = file.name
    var pjFoto = filePJ.name
    ConfigUtil.saveFile(bitmap!!, this@NewHazardActivity, "HAZARD_OFFLINE", bukti)
    ConfigUtil.saveFile(bitmapPJ!!, this@NewHazardActivity, "HAZARD_OFFLINE", pjFoto)
    var plKondisi =""
    var rbStatus = ""
    if(plKta.isChecked){
        plKondisi = plKta.text.toString()
    }else if(plTta.isChecked){
        plKondisi = plTta.text.toString()
    }
    if(rbSelesai.isChecked){
        rbStatus= rbSelesai.text.toString()
    }else if(rbBelumSelesai.isChecked){
        rbStatus= rbBelumSelesai.text.toString()
    }else if(rbBerlanjut.isChecked){
        rbStatus= rbBerlanjut.text.toString()
    }else if(rbDLMpengerjaan.isChecked){
        rbStatus= rbDLMpengerjaan.text.toString()
    }

    val hazardHeaderModel = HazardHeaderModel()
    GlobalScope.launch(Dispatchers.IO) {

        val hazardHeader = HazardHeaderDataSource(this@NewHazardActivity)
        hazardHeaderModel.perusahaan = inPerusahan
        hazardHeaderModel.tgl_hazard = inTanggal
        hazardHeaderModel.jam_hazard = inJam
        hazardHeaderModel.idKemungkinan = kemungkinanID.toInt()
        hazardHeaderModel.idKeparahan = keparahanID.toInt()
        hazardHeaderModel.deskripsi = inBahaya
        hazardHeaderModel.lokasi = lokasi
        hazardHeaderModel.lokasi_detail = inLokasiDet
        hazardHeaderModel.status_perbaikan = rbStatus
        hazardHeaderModel.user_input = USERNAME
        var inHeader = async { hazardHeader.insertItem(hazardHeaderModel) }
        if(inHeader.await()>0){
            val hazardDetail = HazardDetailDataSource(this@NewHazardActivity)
            val hazardDetailModel = HazardDetailModel()
            hazardDetailModel.tindakan = inPerbaikan
            hazardDetailModel.namaPJ = pjNama
            hazardDetailModel.nikPJ = pjNik
            hazardDetailModel.fotoPJ = pjFoto
            hazardDetailModel.katBahaya = plKondisi
            hazardDetailModel.idPengendalian = hirarkiID?.toInt()
            hazardDetailModel.tgl_selesai = inTGLSelesai
            hazardDetailModel.jam_selesai = inJamSelesai
            hazardDetailModel.bukti = bukti
            hazardDetailModel.update_bukti = buktiSelesai
            hazardDetailModel.keterangan_update = inKeteranganPJ
            hazardDetailModel.idKemungkinanSesudah = kemungkinanSesudahID?.toInt()
            hazardDetailModel.idKeparahanSesudah = keparahanSesudahID?.toInt()
            hazardDetailModel.tgl_tenggat = tglTenggat
            var inDetail = async { hazardDetail.insertItem(hazardDetailModel) }
            if(inDetail.await()>0){
                val hazardValidation = HazardValidationDataSource(this@NewHazardActivity)
                val hazardValidationModel = HazardValidationModel()
                hazardValidationModel.uid = null
                hazardValidationModel.user_valid = null
                hazardValidationModel.tgl_valid = null
                hazardValidationModel.jam_valid= null
                var inValidate = async { hazardValidation.insertItem(hazardValidationModel) }
                if(inValidate.await()>0){
                    Log.d("SimpanOffline", "Sukses")
                    var deleteImg = async { ConfigUtil.deleteInABPIMAGES(
                        this@NewHazardActivity,
                        "ABP_IMAGES"
                    ) }
                    if(deleteImg.await()){
                        Log.d("JobService", "Is Running")
//                        var intent = Intent()
//                        setResult(Activity.RESULT_OK, intent)
//                        finish()
                    }else{
                        ConfigUtil.deleteInABPIMAGES(this@NewHazardActivity, "ABP_IMAGES")
                    }
                }else{
                    Log.d("SimpanOffline", "Gagal")
                }
            }else{
                Log.d("SimpanOffline", "Gagal 1")
            }
        }else{
            Log.d("SimpanOffline", "Gagal 2")
        }
    }
//    initial
    }
//    Simpan Offline
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
        if(inTGLTenggat.text!!.isEmpty()){
            tilTGLTenggat.error="Please Input Someting"
            inTGLTenggat.requestFocus()
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
            rbSelesai.requestFocus()
            return false
        }
        if (imgIn <= 0 )
        {
            Toasty.error(this@NewHazardActivity, "Harap Memilih Gambar", Toasty.LENGTH_LONG).show()
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
        var SEBELUM = "sebelum"
        var PENANGGUNG_JAWAB = "penanggung_jawab"
        var SELESAI = "selesai"
        var USEPICK = "USEPICK"
    }
//    OBJECT
    private fun showLoading(c: Context, title: String, option: String){
        builder = AlertDialog.Builder(this)
        var layout = layoutInflater.inflate(R.layout.custom_loading, null)
        var titleDialog = layout.findViewById<View>(R.id.tvTitleDialog) as TextView
        var circle = layout.findViewById<View>(R.id.circleProgress) as ProgressBar
        var imgLoading = layout.findViewById<View>(R.id.abpLoading) as ImageView
        titleDialog.text = title
        Glide.with(c).load(R.drawable.abp).into(imgLoading)
        if(option=="circle"){
            circle.visibility = View.VISIBLE
            imgLoading.visibility = View.GONE
        }else if(option=="abp"){
            circle.visibility = View.GONE
            imgLoading.visibility = View.VISIBLE
        }
        builder?.setView(layout)
        builder?.setCancelable(false)
        dialog = builder?.show()
    }
    override fun onDestroy() {
        super.onDestroy()
        if (dialog != null) {
            dialog?.dismiss()
            dialog = null
        }
    }
}
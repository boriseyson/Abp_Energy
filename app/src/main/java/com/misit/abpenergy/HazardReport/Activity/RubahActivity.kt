package com.misit.abpenergy.HazardReport.Activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.misit.abpenergy.HazardReport.Response.DetailHazardResponse
import com.misit.abpenergy.HazardReport.ViewModel.HazardDetailViewModel
import com.misit.abpenergy.R
import com.misit.abpenergy.Utils.Constants
import com.misit.abpenergy.Utils.PrefsUtil
import kotlinx.android.synthetic.main.activity_new_hazard.*
import kotlinx.android.synthetic.main.activity_rubah.*
import kotlinx.android.synthetic.main.activity_rubah.cvResiko
import kotlinx.android.synthetic.main.activity_rubah.imgView
import kotlinx.android.synthetic.main.activity_rubah.pjFOTO
import kotlinx.android.synthetic.main.activity_rubah.tvKDresiko
import kotlinx.android.synthetic.main.activity_rubah.tvNilaiResiko
import kotlinx.android.synthetic.main.activity_rubah.tvRisk
import kotlinx.android.synthetic.main.activity_rubah.tvTotalResiko
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.io.File
import java.io.IOException

class RubahActivity : AppCompatActivity() , View.OnClickListener{
    private var uid:String?=null
    private var bukti:String?=null
    private var updateBukti:String?=null
    private var adminHazard:String?=null
    private var fotoPJ:String?=null
    lateinit var viewModel: HazardDetailViewModel
    var method:String? = null

    private var storageDir:File? = null

    private var bitmap: Bitmap?=null
    private var bitmapBuktiSelesai: Bitmap?=null
    private var bitmapPJ: Bitmap?=null
    private var fileUpload: Uri?=null
    private var fileUploadSelesai: Uri?=null
    private var fileUploadPJ: Uri?=null

    private var imgIn:Int=0
    private var imgSelesai:Int=0
    private var imgPJ:Int=0
    private var pathFileSebelum:String?=null
    private var pathFileSelesai:String?=null
    private var pathFilePJ:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rubah)
        title="Detail Hazard Report"
        var actionBar = supportActionBar
        PrefsUtil.initInstance(this)
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        uid = intent.getStringExtra(DetailHazardActivity.UID)
        adminHazard = intent.getStringExtra("ALLHazard")
        method = intent.getStringExtra("Method")
//        loadDetail(uid.toString())
        viewModel = ViewModelProvider(this@RubahActivity).get(HazardDetailViewModel::class.java)
        if(method!=null){
            if(method=="Online"){
                GlobalScope.launch(Dispatchers.IO) {
                    viewModel?.loadDetailOnline("${uid}",this@RubahActivity)
                }
            }else if (method =="Offline"){
                GlobalScope.launch(Dispatchers.IO) {
                    viewModel?.loadDetailOnline("${uid}",this@RubahActivity)
                }
            }
        }
        initViewModel()
        btnClick(this@RubahActivity)
    }

    override fun onResume() {
        storageDir = getExternalFilesDir("ABP_IMAGES")
        super.onResume()
    }
    fun btnClick(c:View.OnClickListener){
        btnGantiTemuan.setOnClickListener(c)
    }
    override fun onClick(v: View?) {
        if(v?.id==R.id.btnGantiTemuan){
            showDialogOption(
                Constants.BUKTI_CODE_GALERY, Constants.BUKTI_CODE_GALERY,
                SEBELUM
            )
        }
    }
    //    Dialog PICK PICTURE
    fun showDialogOption(camera: Int, galery: Int, fName: String){
        val c = this@RubahActivity
        val alertDialog = AlertDialog.Builder(c)
        alertDialog.setTitle("Silahkan Pilih")
        val animals = arrayOf(
            "Ambil Sebuah Gambar",
            "Pilih Gambar dari galery"
        )
        alertDialog!!.setItems(animals, { dialog, which ->
            when (which) {
                0 -> cameraIntent(c, camera, fName)
                1 -> openGalleryForImage(galery)
            }
        })
        alertDialog.create()
        alertDialog.show()
    }
    private fun cameraIntent(c: Activity, requestCode: Int, fName: String){

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R){
            Log.d("CameraError","a")

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
                            this@RubahActivity,
                            "com.misit.abpenergy.fileprovider",
                            it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, requestCode)
                    }
                }
            }
        }else{
            Log.d("CameraError","b")
            val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, requestCode)
            }
        }

    }
    //OPEN GALERY
    private fun openGalleryForImage(codeRequest: Int) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, codeRequest)
    }
    //OPEN GALERY
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
            }else if (fName== SELESAI){
                pathFileSelesai = absolutePath
            }else if (fName== PENANGGUNG_JAWAB){
                pathFilePJ = absolutePath
            }
            Log.d("absolutePath","${absolutePath}")
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
    private fun itemHazardList(dataHazard: DetailHazardResponse){
        val fmt: DateTimeFormatter = DateTimeFormat.forPattern("d MMMM, yyyy")
        var itemHazard =dataHazard.itemHazardList
        if(itemHazard!=null){
            tvPerusahaanD.text = itemHazard.perusahaan
            tvTanggalD.text = LocalDate.parse(itemHazard.tglHazard).toString(fmt)
            tvJamD.text = itemHazard.jamHazard
            tvLokasiD.text = itemHazard.lokasiHazard
            tvLokasiDetails.text= itemHazard.lokasiDetail
            tvBahayaD.text = itemHazard.deskripsi
            tvKemungkinan.text = itemHazard.kemungkinanSebelum
            tvKeparahan.text = itemHazard.keparahanSebelum
            tvKemungkinanSesudah.text = itemHazard.kemungkinanSesudah
            tvKeparahanSesudah.text = itemHazard.keparahanSesudah
            tvPengendalian.text = itemHazard.namaPengendalian
            tvKatBahayaD.text = itemHazard.katBahaya
            tvPerbaikanD.text = itemHazard.tindakan
            tvStatusPerbaikanD.text = itemHazard.statusPerbaikan
            tvDibuat.text = itemHazard.namaLengkap
            tvNilaiKeparahan.text = itemHazard.nilaiKeparahan.toString()
            tvNilaiKemungkinan.text = itemHazard.nilaiKemungkinan.toString()
            if(itemHazard.tgl_tenggat!=null){
                lnDueDate.visibility = View.VISIBLE
                tvDueDate.text = LocalDate.parse(itemHazard.tgl_tenggat).toString(fmt)
            }else{
                lnDueDate.visibility=View.GONE
                tvDueDate.text = "-"
            }
            if(itemHazard.tglSelesai!=null){
                tvTGLSelesaiD.text = LocalDate.parse(itemHazard.tglSelesai).toString(fmt)
            }else{
                imgStatus.visibility=View.GONE
                tvTGLSelesaiD.text = "-"
            }
            if(itemHazard.jamSelesai!=null){
                tvJamSelesaiD.text = itemHazard.jamSelesai
            }else{
                tvJamSelesaiD.text = "-"
            }
            namaPJ.text = itemHazard.namaPJ
            nikPJ.text = itemHazard.nikPJ
            Glide.with(this@RubahActivity)
                .load("https://abpjobsite.com/bukti_hazard/"+itemHazard?.bukti)
                .into(imgView)

            Glide.with(this@RubahActivity)
                .load("https://abpjobsite.com/bukti_hazard/penanggung_jawab/"+itemHazard?.fotoPJ)
                .into(pjFOTO)
            bukti = itemHazard?.bukti
            fotoPJ = itemHazard.fotoPJ
            if(itemHazard.updateBukti!=null){
                Glide.with(this@RubahActivity)
                    .load("https://abpjobsite.com/bukti_hazard/update/"+itemHazard?.updateBukti)
                    .into(imgStatus)
                imgStatus.visibility=View.VISIBLE
                tvStatusPerbaikan.visibility=View.VISIBLE
                cvStatusPerbaikan.visibility=View.VISIBLE
                updateBukti = itemHazard?.updateBukti
            }else{
                cvStatusPerbaikan.visibility=View.GONE
                tvStatusPerbaikan.visibility=View.GONE
                imgStatus.visibility=View.GONE
            }
            if(itemHazard.keteranganUpdate!=null){
                lnKetPerbaikan.visibility = View.VISIBLE
                tvKetPerbaikan.text = itemHazard.keteranganUpdate
            }else{
                lnKetPerbaikan.visibility = View.GONE
                tvKetPerbaikan.text = ""
            }
        }
        if(dataHazard.nilaiRiskSebelum!=null){
            var itemHazard =  dataHazard.itemHazardList
            Log.d("riskSebelum","${dataHazard}")
            tvTotalResiko.text = "${itemHazard!!.nilaiKemungkinan} x ${itemHazard!!.nilaiKeparahan} = ${dataHazard.nilaiRiskSebelum}"
            tvKDresiko.text ="${dataHazard.riskSebelum!!.kodeBahaya}"
            tvRisk.text = "${dataHazard.riskSebelum!!.kategori} "
            tvNilaiResiko.text = "${dataHazard.riskSebelum!!.min} - ${dataHazard.riskSebelum!!.max}"
            cvResiko.setCardBackgroundColor(Color.parseColor(dataHazard.riskSebelum!!.bgColor))
            tvRisk.setBackgroundColor(Color.parseColor(dataHazard.riskSebelum!!.bgColor))
            tvRisk.setTextColor(Color.parseColor(dataHazard.riskSebelum!!.txtColor))
        }
        Log.d("RiskSesudah","${dataHazard.riskSesudah}")
        if(dataHazard.nilaiRiskSesudah!! > 0){
            lnDetMatrikResiko.visibility = View.VISIBLE
            var itemHazard =  dataHazard.itemHazardList
            tvTotalResikoSesudah.text = "${itemHazard!!.nilaiKemungkinanSesudah} x ${itemHazard!!.nilaiKeparahanSesudah} = ${dataHazard.nilaiRiskSesudah}"
            tvNilaiKemungkinanSesudah.text ="${itemHazard.nilaiKemungkinanSesudah}"
            tvNilaiKeparahanSesudah.text = "${itemHazard.nilaiKeparahanSesudah} "
            tvRiskSesudah.text = "${dataHazard.riskSesudah!!.kategori} "
            tvNilaiResikoSesudah.text = "${dataHazard.riskSesudah!!.min} - ${dataHazard.riskSesudah!!.max}"
            tvKDresikoSesudah.text = "${dataHazard.riskSesudah!!.kodeBahaya} "
            cvResikoSesudah.setCardBackgroundColor(Color.parseColor(dataHazard.riskSesudah!!.bgColor))
            tvRiskSesudah.setBackgroundColor(Color.parseColor(dataHazard.riskSesudah!!.bgColor))
            tvRiskSesudah.setTextColor(Color.parseColor(dataHazard.riskSesudah!!.txtColor))
        }else{
            lnDetMatrikResiko.visibility = View.GONE
        }
    }
    private fun initViewModel(){
        viewModel.hazardDetailObserver()?.observe(this@RubahActivity, Observer{
            if(it!=null){
                itemHazardList(it)
            }
        })
        viewModel.progressObserver()?.observe(this@RubahActivity, Observer{
            if(it){
                crlDetail.visibility = View.VISIBLE
            }else{
                crlDetail.visibility = View.GONE
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode==Activity.RESULT_OK && requestCode==Constants.BUKTI_CODE_CAMERA){
//            Camera Inten Sebelum
            try {
                fileUpload = "file:///${pathFileSebelum}".toUri()
                try {
                    bitmap = BitmapFactory.decodeStream(
                        contentResolver.openInputStream(fileUpload!!)
                    )
                    Log.d("fileUpload","${fileUpload}")
                    Glide.with(this@RubahActivity).load(fileUpload).into(imgView)
                } catch (e: IOException) {
                    e.printStackTrace();
                }
                imgIn = 1
            } catch (e: IOException) {
                imgIn = 0
                e.printStackTrace();
            }
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
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    companion object{
        var UID = "UID"
        var USERNAME = "USERNAME"
        var SEBELUM = "sebelum"
        var PENANGGUNG_JAWAB = "penanggung_jawab"
        var SELESAI = "selesai"
        var USEPICK = "USEPICK"
    }
}
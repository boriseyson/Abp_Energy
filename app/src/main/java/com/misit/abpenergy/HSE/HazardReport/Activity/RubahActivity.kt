package com.misit.abpenergy.HSE.HazardReport.Activity

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.misit.abpenergy.Api.ApiClient
import com.misit.abpenergy.Api.ApiEndPoint
import com.misit.abpenergy.Api.ApiEndPointTwo
import com.misit.abpenergy.HSE.HazardReport.Response.DetailHazardResponse
import com.misit.abpenergy.HSE.HazardReport.ViewModel.HazardDetailViewModel
import com.misit.abpenergy.Main.Model.SuccessResponse
import com.misit.abpenergy.R
import com.misit.abpenergy.Utils.ConfigUtil
import com.misit.abpenergy.Utils.Constants
import com.misit.abpenergy.Utils.PrefsUtil
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_rubah.*
import kotlinx.android.synthetic.main.activity_rubah.cvResiko
import kotlinx.android.synthetic.main.activity_rubah.imgView
import kotlinx.android.synthetic.main.activity_rubah.pjFOTO
import kotlinx.android.synthetic.main.activity_rubah.tvKDresiko
import kotlinx.android.synthetic.main.activity_rubah.tvNilaiResiko
import kotlinx.android.synthetic.main.activity_rubah.tvRisk
import kotlinx.android.synthetic.main.activity_rubah.tvTotalResiko
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.*

class RubahActivity : AppCompatActivity() , View.OnClickListener{
    private var uid:String?=null
    private var bukti:String?=null
    private var updateBukti:String?=null
    private var adminHazard:String?=null
    private var fotoPJ:String?=null
    lateinit var viewModel: HazardDetailViewModel
    private var method:String? = null
    private var dialog:AlertDialog?=null
    private var storageDir:File? = null
    private var kemungkinanDipilih:String?=null
    private var kemungkinanDipilihSesudah:String?=null
    private var kemungkinanID:String?=null
    private var kemungkinanIDSesudah:String?=null
    private var keparahanID:String?=null
    private var keparahanIDSesudah:String?=null
    private var bitmap: Bitmap?=null
    private var bitmapBuktiSelesai: Bitmap?=null
    private var bitmapPJ: Bitmap?=null
    private var fileUpload: Uri?=null
    private var fileUploadSelesai: Uri?=null
    private var fileUploadPJ: Uri?=null
    private var csrf_token:String?=null
    private var imgIn:Int=0
    private var imgSelesai:Int=0
    private var imgPJ:Int=0
    private var pathFileSebelum:String?=null
    private var pathFileSelesai:String?=null
    private var pathFilePJ:String?=null
    private var keparahanDipilih:String?=null
    private var keparahanDipilihSesudah:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rubah)
        title="Rubah Hazard Report"
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
            loadingDialog(this)
            if(method=="Online"){
                GlobalScope.launch(Dispatchers.Main) {
                    viewModel?.loadDetailOnline("${uid}",this@RubahActivity)
                }
            }else if (method =="Offline"){
                GlobalScope.launch(Dispatchers.Main) {
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
        btnRubahBahaya.setOnClickListener(c)
        btnRubahTindakan.setOnClickListener(c)
        btnRubahPerbaikan.setOnClickListener(c)
        rbKemungkinanSebelum.setOnClickListener(c)
        rbKeparahanSebelum.setOnClickListener(c)
        rbKmSesudah.setOnClickListener(c)
        rbKprSesudah.setOnClickListener(c)
        btnPerbaikanRB.setOnClickListener(c)
    }
    override fun onClick(v: View?) {
        if(v?.id==R.id.btnPerbaikanRB){
            showDialogOption(Constants.SELESAI_CODE_CAMERA, Constants.SELESAI_CODE_GALERY,
                SELESAI
            )
        }
        if(v?.id==R.id.btnGantiTemuan){
            showDialogOption(
                Constants.BUKTI_CODE_CAMERA, Constants.BUKTI_CODE_GALERY,
                SEBELUM
            )
        }
        if(v?.id==R.id.btnRubahBahaya){
            GlobalScope.launch(Dispatchers.Main) {
                async { corotineToken(this@RubahActivity) }.await()
                hazardDialog(this@RubahActivity,uid!!,"bahaya","Deskripsi Bahaya")
            }
        }
        if(v?.id==R.id.btnRubahTindakan){
            GlobalScope.launch(Dispatchers.Main) {
                async { corotineToken(this@RubahActivity) }.await()
                hazardDialog(this@RubahActivity,uid!!,"tindakan","Tindakan Perbaikan")
            }

        }
        if(v?.id==R.id.btnRubahPerbaikan){
            GlobalScope.launch(Dispatchers.Main) {
                async { corotineToken(this@RubahActivity) }.await()
                hazardDialog(this@RubahActivity,uid!!,"perbaikan","Keterangan Perbaikan")
            }

        }
        if(v?.id==R.id.rbKemungkinanSebelum){
            GlobalScope.launch(Dispatchers.Main) {
                async { corotineToken(this@RubahActivity) }.await()
                var intent = Intent(this@RubahActivity, KemungkinanActivity::class.java)
                intent.putExtra("kemungkinanDipilih", kemungkinanDipilih)
                startActivityForResult(intent, Constants.KEMUNGKINAN_SEBELUM_CODE)
//                mrResiko(this@RubahActivity,uid!!,"kemungkinan_sebelum")
            }
        }
        if(v?.id==R.id.rbKeparahanSebelum){
            GlobalScope.launch(Dispatchers.Main) {
                async { corotineToken(this@RubahActivity) }.await()
                var intent = Intent(this@RubahActivity, KeparahanActivity::class.java)
                intent.putExtra("keparahanDipilih", keparahanDipilih)
                startActivityForResult(intent, Constants.KEPARAHAN_SEBELUM_CODE)
            }
        }
        if(v?.id==R.id.rbKmSesudah){
            GlobalScope.launch(Dispatchers.Main) {
                async { corotineToken(this@RubahActivity) }.await()
                var intent = Intent(this@RubahActivity, KemungkinanActivity::class.java)
                intent.putExtra("kemungkinanDipilih", kemungkinanDipilihSesudah)
                startActivityForResult(intent, Constants.KEMUNGKINAN_SESUDAH_CODE)
            }
        }
        if(v?.id==R.id.rbKprSesudah){
            GlobalScope.launch(Dispatchers.Main) {
                async { corotineToken(this@RubahActivity) }.await()
                var intent = Intent(this@RubahActivity, KeparahanActivity::class.java)
                intent.putExtra("keparahanDipilih", keparahanDipilihSesudah)
                startActivityForResult(intent, Constants.KEMAPARAHAN_SESUDAH_CODE)
            }
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
        GlobalScope.launch(Dispatchers.Main){
            async { corotineToken(this@RubahActivity) }.await()
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
                Log.d("CameraError", "a")
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
            } else {
                Log.d("CameraError", "b")
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
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
                    try {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(intent, requestCode)
                    } catch (e: Exception) {
                        Log.d("errorCreate", e.message.toString())

                    }
                }
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
            kemungkinanDipilih = itemHazard.kemungkinanSebelum
            kemungkinanDipilihSesudah = itemHazard.kemungkinanSesudah
            keparahanDipilih = itemHazard.keparahanSebelum
            keparahanDipilihSesudah = itemHazard.keparahanSesudah
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
            dialog?.dismiss()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode==Activity.RESULT_OK && requestCode==Constants.BUKTI_CODE_CAMERA){
//            Camera Inten Sebelum
            val c = this@RubahActivity
            loadingDialog(c)
            try {
                fileUpload = "file:///${pathFileSebelum}".toUri()
                try {
                    bitmap = BitmapFactory.decodeStream(
                        contentResolver.openInputStream(fileUpload!!)
                    )
                    Log.d("fileUpload","${fileUpload}")
//                    Glide.with(this@RubahActivity).load(fileUpload).into(imgView)
                    GlobalScope.launch(Dispatchers.Main) {
                        updateBukti(c,uid!!,bitmap!!,"bukti_sebelum")
                    }
                } catch (e: IOException) {
                    e.printStackTrace();
                }
                imgIn = 1
            } catch (e: IOException) {
                imgIn = 0
                e.printStackTrace();
            }
        }else
        if(resultCode==Activity.RESULT_OK && requestCode==Constants.BUKTI_CODE_GALERY) {
//            GALERY INTENT SEBELUM
            val c = this@RubahActivity
            loadingDialog(c)
            try {
                fileUpload = data!!.data
                try {
                    bitmap = BitmapFactory.decodeStream(
                        contentResolver.openInputStream(fileUpload!!)
                    )
                    GlobalScope.launch(Dispatchers.Main) {
                        updateBukti(this@RubahActivity,uid!!,bitmap!!,"bukti_sebelum")
                    }
//                    imgView.setImageBitmap(bitmap);
                    imgIn = 1
                } catch (e: IOException) {
                    imgIn = 0
                }
            } catch (e: IOException) {
                imgIn = 0
            }
        }else
        if(resultCode== Activity.RESULT_OK && requestCode==Constants.KEMUNGKINAN_SEBELUM_CODE){
            kemungkinanDipilih = data!!.getStringExtra("kemungkinanDipilih")
            kemungkinanID = data.getStringExtra("kemungkinanID")
            if (kemungkinanID!=null){
                loadingDialog(this@RubahActivity)
                GlobalScope.launch(Dispatchers.Main) {
                    mrResiko(this@RubahActivity,uid!!,"kemungkinan_sebelum", kemungkinanID!!)
                }
            }
        }else
        if(resultCode== Activity.RESULT_OK && requestCode==Constants.KEMUNGKINAN_SESUDAH_CODE){
            kemungkinanDipilihSesudah = data!!.getStringExtra("kemungkinanDipilih")
            kemungkinanIDSesudah = data.getStringExtra("kemungkinanID")
            if (kemungkinanIDSesudah!=null){
                loadingDialog(this@RubahActivity)
                GlobalScope.launch(Dispatchers.Main) {
                    mrResiko(this@RubahActivity,uid!!,"kemungkinan_sesudah", kemungkinanIDSesudah!!)
                }
            }
        }else
        if(resultCode== Activity.RESULT_OK && requestCode==Constants.KEPARAHAN_SEBELUM_CODE){
            keparahanDipilih = data!!.getStringExtra("keparahanDipilih")
            keparahanID = data.getStringExtra("keparahanID")
            if (keparahanID!=null){
                loadingDialog(this@RubahActivity)
                GlobalScope.launch(Dispatchers.Main) {
                    mrResiko(this@RubahActivity,uid!!,"keparahan_sebelum", keparahanID!!)
                }
            }
        }else
        if(resultCode== Activity.RESULT_OK && requestCode==Constants.KEMAPARAHAN_SESUDAH_CODE){
            keparahanDipilihSesudah = data!!.getStringExtra("keparahanDipilih")
            keparahanIDSesudah = data.getStringExtra("keparahanID")
            if (keparahanIDSesudah!=null){
                loadingDialog(this@RubahActivity)
                GlobalScope.launch(Dispatchers.Main) {
                    mrResiko(this@RubahActivity,uid!!,"keparahan_sesudah", keparahanIDSesudah!!)
                }
            }
        }else
        if(resultCode==Activity.RESULT_OK && requestCode==Constants.SELESAI_CODE_CAMERA){
//            Camera Intent Selesai
            try {
                fileUploadSelesai = "file:///${pathFileSelesai}".toUri()
                try {
                    bitmapBuktiSelesai = BitmapFactory.decodeStream(
                        contentResolver.openInputStream(fileUploadSelesai!!)
                    )
                    imgSelesai = 1
                    GlobalScope.launch(Dispatchers.Main) {
                        updateBukti(this@RubahActivity,uid!!,bitmapBuktiSelesai!!,"bukti_selesai")
                    }
                } catch (e: IOException) {
                    e.printStackTrace();
                    imgSelesai = 0
                }
            } catch (e: IOException) {
                imgSelesai = 0
                e.printStackTrace();
            }
        }else
        if(resultCode==Activity.RESULT_OK && requestCode==Constants.SELESAI_CODE_GALERY) {
//            Galery Inten Selesai
            try {
                fileUploadSelesai = data!!.data
                try {
                    bitmapBuktiSelesai = BitmapFactory.decodeStream(
                        contentResolver.openInputStream(fileUploadSelesai!!)
                    )

                    GlobalScope.launch(Dispatchers.Main) {
                        updateBukti(this@RubahActivity,uid!!,bitmapBuktiSelesai!!,"bukti_selesai")
                    }
                    imgSelesai = 1
                } catch (e: IOException) {
                    imgSelesai = 0
                }
            } catch (e: IOException) {
                imgSelesai = 0
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    suspend fun corotineToken(c: Context){
        try {
            val apiEndPoint = ApiClient.getClient(c)!!.create(ApiEndPoint::class.java)
            CoroutineScope(Dispatchers.Main).launch {
                val call = async { apiEndPoint.getTokenCorutine("csrf_token") }
                val result = call.await()

                if (result != null) {
                    if (result.isSuccessful) {
                        val tokenRes = async { result.body() }.await()
                        if (tokenRes != null) {
                            csrf_token = tokenRes.csrfToken
                        } else {
                            corotineToken(c)
                        }
                    } else {
                        corotineToken(c)
                    }
                }
            }
        }catch (e:Exception){
            Log.d("Error","${e.message}")
            corotineToken(c)
        }
    }
    private fun updateBukti(c:Context,uid:String,bitmap:Bitmap,tipe:String){

        GlobalScope.launch(Dispatchers.Main) {
            var deviceId = ConfigUtil.deviceId(c);
            var waktu = Date()
            val cal = Calendar.getInstance()
            cal.time = waktu
            var jam = "${cal.get(Calendar.HOUR_OF_DAY)}${cal.get(Calendar.MINUTE)}${cal.get(Calendar.SECOND)}"
            val wrapper = ContextWrapper(applicationContext)
            //    var filenya = File(fileUpload!!.path, jam)
            var file = wrapper.getDir("images", Context.MODE_PRIVATE)
            file = File(file, "${jam}_${deviceId}_${tipe}_${ConfigUtil.uniqueID()}.jpg")
            ConfigUtil.streamFoto(bitmap!!, file)
            var fileUri = file.asRequestBody("image/*".toMediaTypeOrNull())
            var bukti = MultipartBody.Part.createFormData(tipe, file.name, fileUri)
            var uidRequestBody = uid.toRequestBody(MultipartBody.FORM)
            var _token = csrf_token?.toRequestBody(MultipartBody.FORM)
            try {
                val apiEndPoint = ApiClient.getClient(c)!!.create(ApiEndPointTwo::class.java)
                var updateGambar: Response<SuccessResponse>?=null;
                if(tipe=="bukti_sebelum"){
                    updateGambar = apiEndPoint.updateBuktiSebelum(bukti,uidRequestBody,_token!!)
                }else if(tipe=="bukti_selesai"){
                    updateGambar = apiEndPoint.updateBuktiSelesai(bukti,uidRequestBody,_token!!)
                }
                if(updateGambar!=null){
                    if(updateGambar.isSuccessful){
                        var result = updateGambar.body()?.success
                        if(result!=null){
                            if(result){
                                viewModel?.loadDetailOnline(uid,c)
                            }else{
                                dialog?.dismiss()
                                Toasty.error(c,"Gagal, Coba Lagi!").show()
                            }
                        }else{
                            dialog?.dismiss()
                            Toasty.error(c,"Gagal, Coba Lagi!").show()
                        }
                    }else{
                        dialog?.dismiss()
                        Toasty.error(c,"Gagal, Coba Lagi!").show()
                    }
                }else{
                    dialog?.dismiss()
                    Toasty.error(c,"Gagal, Coba Lagi!").show()
                }
            }catch (e:Exception){
                Log.d("ErrorUpdate","{${e.message}")
                dialog?.dismiss()

            }
        }
    }
    companion object{
        var UID = "UID"
        var USERNAME = "USERNAME"
        var SEBELUM = "sebelum"
        var PENANGGUNG_JAWAB = "penanggung_jawab"
        var SELESAI = "selesai"
        var USEPICK = "USEPICK"
    }
    private fun loadingDialog(c:Context){
        var  mDialogView = LayoutInflater.from(c).inflate(R.layout.loading_abp,null)
        val mBuilder = AlertDialog.Builder(c)
        var loadingAbp = mDialogView?.findViewById<View>(R.id.loadingAbp) as ImageView
        Glide.with(c).load(R.drawable.abp).into(loadingAbp)
        mBuilder.setView(mDialogView)
        dialog =mBuilder.show()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)
    }
    private fun hazardDialog(c:Context,uid:String,tipe:String,title:String){
        var  mDialogView = LayoutInflater.from(c).inflate(R.layout.hazard_dialog,null)
        val mBuilder = AlertDialog.Builder(c)
        var tvTitle = mDialogView?.findViewById<View>(R.id.hazardDoalogTitle) as TextView
        var btnCancel = mDialogView?.findViewById<View>(R.id.dialogCancel) as Button
        var btnSave = mDialogView?.findViewById<View>(R.id.dialogSave) as Button
        var tilDeskripsi = mDialogView?.findViewById<View>(R.id.tilDeskripsi) as TextInputLayout
        var inDeskripsi = mDialogView?.findViewById<View>(R.id.inDeskripsi) as TextInputEditText

        tilDeskripsi.hint = title
        tvTitle.text=title

        btnCancel.setOnClickListener {
            dialog?.dismiss()
        }
        btnSave.setOnClickListener {
            tilDeskripsi.error=null
            if(inDeskripsi.text!!.length>0){
                GlobalScope.launch(Dispatchers.Main) {
                    simpanDeskripsi(c,uid,tipe,inDeskripsi.text.toString())
                }
            }else{
                tilDeskripsi.error ="$title Tidak Boleh Kosong!"
                inDeskripsi.requestFocus()
            }

        }
        mBuilder.setView(mDialogView)
        dialog =mBuilder.show()
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)
    }
    suspend private fun simpanDeskripsi(c:Context,uid:String,tipe: String,deskripsi:String){
        dialog?.dismiss()
        loadingDialog(c)
        try {
            var _uid = uid?.toRequestBody(MultipartBody.FORM)
            var _tipe = tipe?.toRequestBody(MultipartBody.FORM)
            var _deskripsi = deskripsi?.toRequestBody(MultipartBody.FORM)
            var _token = csrf_token?.toRequestBody(MultipartBody.FORM)
            val apiEndPoint = ApiClient.getClient(c)!!.create(ApiEndPointTwo::class.java)
            CoroutineScope(Dispatchers.Main).launch {
                val call = async { apiEndPoint.updateDeskripsiHazard(_uid,_tipe,_deskripsi,_token!!) }
                    var result = call.await()
                    if (result != null) {
                        if(result.isSuccessful){
                            var success = result.body()?.success
                            if (success!!){
                                viewModel?.loadDetailOnline(uid,c)
                            }else{
                                Toasty.error(c,"Error Update $title , Coba Lagi").show()
                                dialog?.dismiss()
                            }
                        }else{
                            Toasty.error(c,"Error Update $title , Coba Lagi").show()
                            dialog?.dismiss()
                        }
                    }else{
                        Toasty.error(c,"Error Update $title , Coba Lagi").show()
                        dialog?.dismiss()
                    }
            }
        }catch (e:Exception){
            Toasty.error(c,"Error Update $title , Coba Lagi").show()
            Log.d("erroDeskripsi","${e.message}")
            dialog?.dismiss()
        }
    }
    suspend private fun mrResiko(c:Context,uid:String,tipe: String,idResiko:String){
        try {
            var _uid = uid?.toRequestBody(MultipartBody.FORM)
            var _tipe = tipe?.toRequestBody(MultipartBody.FORM)
            var idResiko = idResiko?.toRequestBody(MultipartBody.FORM)
            var _token = csrf_token?.toRequestBody(MultipartBody.FORM)
            val apiEndPoint = ApiClient.getClient(c)!!.create(ApiEndPointTwo::class.java)
            CoroutineScope(Dispatchers.Main).launch {
                val call = async { apiEndPoint.updateResiko(_uid,_tipe,idResiko,_token!!) }
                var result = call.await()
                if (result != null) {
                    if(result.isSuccessful){
                        var success = result.body()?.success
                        if (success!!){
                            viewModel?.loadDetailOnline(uid,c)
                        }else{
                            Toasty.error(c,"Error Update Resiko , Coba Lagi").show()
                            dialog?.dismiss()
                        }
                    }else{
                        Toasty.error(c,"Error Update Resiko , Coba Lagi").show()
                        dialog?.dismiss()
                    }
                }else{
                    Toasty.error(c,"Error Update Resiko , Coba Lagi").show()
                    dialog?.dismiss()
                }
            }
        }catch (e:Exception){
            Toasty.error(c,"Error Update Resiko , Coba Lagi").show()
            Log.d("erroDeskripsi","${e.message}")
            dialog?.dismiss()
        }
    }
}
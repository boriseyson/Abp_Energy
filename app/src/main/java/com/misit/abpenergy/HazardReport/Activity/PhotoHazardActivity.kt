package com.misit.abpenergy.HazardReport.Activity
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.misit.abpenergy.R
import com.misit.abpenergy.Utils.PrefsUtil
//import com.wonderkiln.camerakit.*
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_photo_hazard.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
class PhotoHazardActivity : AppCompatActivity(),View.OnClickListener {
    private var bitmap:Bitmap?=null
    private var bs :ByteArrayOutputStream?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_hazard)
        PrefsUtil.initInstance(this)
        btnDonePick.isEnabled=false
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
//        cameraKit.addCameraKitListener(object: CameraKitEventListener {
//            override fun onVideo(p0: CameraKitVideo?) {
//
//            }
//
//            override fun onEvent(p0: CameraKitEvent?) {
//
//            }
//
//            override fun onImage(p0: CameraKitImage?) {
//                btnDonePick.isClickable=false
//                Log.d("CatpureImage","proses")
//                try {
//                    Log.d("CatpureImage","proses1")
//
//                    var bitmap1 = p0!!.bitmap
//                    Log.d("CatpureImage","proses2")
//                    bitmap = Bitmap.createScaledBitmap(bitmap1!!,cameraKit.width,cameraKit.height,false)
//                    Log.d("CatpureImage","proses3")
//                    bs = ByteArrayOutputStream()
//                    Log.d("CatpureImage","proses4")
//                    bitmap!!.compress(Bitmap.CompressFormat.JPEG,50,bs)
//                    Log.d("CatpureImage","proses5")
//                    if(bitmap!=null){
//                        btnDonePick.isClickable=true
//                        btnDonePick.isEnabled=true
//                        btnReCapture.visibility=View.VISIBLE
//                        Log.d("CatpureImage","proses8")
//                    }else{
//                        btnDonePick.isEnabled=false
//                        btnDonePick.isClickable=false
//                        Log.d("CatpureImage","proses9")
//                    }
//                    Log.d("CatpureImage","proses10")
//                    cameraKit.stop()
//                }catch (e:IOException){
//                    Toasty.error(this@PhotoHazardActivity,e.printStackTrace().toString(),Toasty.LENGTH_LONG).show()
//                }
//
//            }
//
//            override fun onError(p0: CameraKitError?) {
//
//            }
//
//        })

        btnFacing.setOnClickListener(this)
        btnCapture.setOnClickListener(this)
        btnReCapture.setOnClickListener(this)
        btnDonePick.setOnClickListener(this)
//        cameraKit.setOnClickListener(this)
    }
    override fun onBackPressed() {
//        finish()
        super.onBackPressed()
    }

    override fun onResume() {


//        cameraKit.start().let {
//            btnDonePick.visibility=View.GONE
//            btnReCapture.visibility=View.GONE
//            btnCapture.visibility=View.VISIBLE
//            btnFacing.visibility=View.VISIBLE
//        }
        super.onResume()
    }

    override fun onPause() {
//        cameraKit.stop()
        super.onPause()
    }

    override fun onClick(v: View?) {
        if(v!!.id==R.id.btnFacing){
//            cameraKit.toggleFacing()
        }
        if (v!!.id==R.id.btnCapture){
//            cameraKit.captureImage()
                btnDonePick.visibility=View.VISIBLE
                btnCapture.visibility=View.GONE
                btnFacing.visibility=View.GONE
        }
        if(v!!.id==R.id.btnReCapture){
//            cameraKit.start().let {
                btnDonePick.visibility=View.GONE
                btnReCapture.visibility=View.GONE
                btnCapture.visibility=View.VISIBLE
                btnFacing.visibility=View.VISIBLE
            }

        }
//        if(v!!.id==R.id.btnDonePick){
//                val intent: Intent = Intent()
//                intent.putExtra("gambarDiFoto",bs?.toByteArray())
//                setResult(Activity.RESULT_OK,intent)
//                finish()
//
//        }
//        if(v?.id==R.id.cameraKit){
//            cameraKit.setFocus(CameraKit.Constants.FOCUS_CONTINUOUS)
//        }

}
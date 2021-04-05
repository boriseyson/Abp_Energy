package com.misit.abpenergy.HazardReport

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.misit.abpenergy.R
import com.misit.abpenergy.Utils.PrefsUtil
import com.wonderkiln.camerakit.*
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

        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        cameraKit.addCameraKitListener(object: CameraKitEventListener {
            override fun onVideo(p0: CameraKitVideo?) {

            }

            override fun onEvent(p0: CameraKitEvent?) {
            }

            override fun onImage(p0: CameraKitImage?) {
                btnDonePick.isEnabled=false
                try {
                    var bitmap1 = p0!!.bitmap
                    bitmap = Bitmap.createScaledBitmap(bitmap1!!,cameraKit.width,cameraKit.height,false)
                    bs = ByteArrayOutputStream()
                    bitmap!!.compress(Bitmap.CompressFormat.JPEG,50,bs)
                    if(bs!=null){
                        btnDonePick.isEnabled=true
                    }else{
                        btnDonePick.isEnabled=false
                    }
                    if(bitmap!=null){
                        btnDonePick.isEnabled=true
                    }else{
                        btnDonePick.isEnabled=false
                    }
                    cameraKit.stop()
                }catch (e:IOException){
                    Toasty.error(this@PhotoHazardActivity,e.printStackTrace().toString(),Toasty.LENGTH_LONG).show()
                }

            }

            override fun onError(p0: CameraKitError?) {

            }

        })

        btnFacing.setOnClickListener(this)
        btnCapture.setOnClickListener(this)
        btnReCapture.setOnClickListener(this)
        btnDonePick.setOnClickListener(this)
    }
    override fun onBackPressed() {
//        finish()
        super.onBackPressed()
    }

    override fun onResume() {


        cameraKit.start().let {
            btnDonePick.visibility=View.GONE
            btnReCapture.visibility=View.GONE
            btnCapture.visibility=View.VISIBLE
            btnFacing.visibility=View.VISIBLE
        }
        super.onResume()
    }

    override fun onPause() {
        cameraKit.stop()
        super.onPause()
    }

    override fun onClick(v: View?) {
        if(v!!.id==R.id.btnFacing){
            cameraKit.toggleFacing()
        }
        if (v!!.id==R.id.btnCapture){
            cameraKit.captureImage()
                btnDonePick.visibility=View.VISIBLE
                btnReCapture.visibility=View.VISIBLE
                btnCapture.visibility=View.GONE
                btnFacing.visibility=View.GONE



        }
        if(v!!.id==R.id.btnReCapture){
            cameraKit.start().let {
                btnDonePick.visibility=View.GONE
                btnReCapture.visibility=View.GONE
                btnCapture.visibility=View.VISIBLE
                btnFacing.visibility=View.VISIBLE
            }

        }
        if(v!!.id==R.id.btnDonePick){

                val intent: Intent = Intent()
                intent.putExtra("gambarDiFoto",bs?.toByteArray())
                setResult(Activity.RESULT_OK,intent)
                finish()

        }
    }


}
package com.misit.abpenergy.HazardReport

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.Surface
import android.view.TextureView
import androidx.core.app.ActivityCompat
import java.util.*


class CameraFrame(private var cameraHandler: Handler,private val textureView : TextureView?=null,lebar:Int,tinggi:Int) {
    private var cameraDevice : CameraDevice? = null
    private var previewRequestBuilder : CaptureRequest.Builder? = null
    private var previewRequest : CaptureRequest? = null
    private lateinit var cameraCaptureSession: CameraCaptureSession
    private var MAX_PREVIEW_WIDTH = 1920
    private var MAX_PREVIEW_HEIGHT = 1080

    fun openCamera(context:Context){
        val cameraManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        } else {
            Log.v("CameraHazard","Android Version < LOLLIPOP")
            TODO("VERSION.SDK_INT < LOLLIPOP")
        }
        var camIds : Array<String> = emptyArray()
        camIds = cameraManager.cameraIdList
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.v("CameraHazard","Permission OK")
            return
        }
        cameraManager.openCamera(camIds[0],stateCallBack,cameraHandler)
    }
    private val stateCallBack  = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        object :CameraDevice.StateCallback(){
            override fun onOpened(camera: CameraDevice) {
                cameraDevice = camera
                MAX_PREVIEW_WIDTH = lebar
                MAX_PREVIEW_HEIGHT = tinggi

                createPreview()
                Log.d("CameraHazard","Camera Opened")
            }

            override fun onDisconnected(camera: CameraDevice) {
                Log.d("CameraHazard","Camera Disconnect")
            }

            override fun onError(camera: CameraDevice, error: Int) {
                Log.d("CameraHazard","Camera Error")
            }

        }
    } else {
        Log.d("CameraHazard","VERSION.SDK_INT < LOLLIPOP")

        TODO("VERSION.SDK_INT < LOLLIPOP")
    }
    private fun createPreview(){
        Log.d("CameraHazard","createPreview")

        if(textureView == null || cameraDevice==null){
            Log.d("CameraHazard","textureView Null")

            return
        }
        if(textureView.surfaceTexture==null){
            Log.d("CameraHazard","textureView only Null")

            return
        }
        val texture = textureView.surfaceTexture
        texture?.setDefaultBufferSize(MAX_PREVIEW_WIDTH,MAX_PREVIEW_HEIGHT)
        val surface = Surface(texture)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d("CameraHazard","Build.VERSION_CODES.LOLLIPOP")

            cameraDevice!!.createCaptureSession(
                    Collections.singletonList(surface),
                    object : CameraCaptureSession.StateCallback(){
                        override fun onConfigureFailed(session: CameraCaptureSession) {
                            Log.d("CameraHazard",session.toString())
                          }

                        override fun onConfigured(session: CameraCaptureSession) {
                            if(cameraDevice == null){
                                Log.d("CameraHazard","cameraDevice")
                                return
                            }
                            cameraCaptureSession = session
                            previewRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                            previewRequestBuilder!!.addTarget(surface)
                            previewRequest = previewRequestBuilder!!.build()
                            cameraCaptureSession!!.setRepeatingRequest(previewRequest!!,captureCallback ,cameraHandler)
                            Log.d("CameraHazard",session.toString())

                        }

                    },null

            )
            Log.d("CameraHazard","Handler Null")

        }else{
            Log.d("CameraHazard","Under Build.VERSION_CODES.LOLLIPOP")

        }
    }
    private var captureCallback =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        object :CameraCaptureSession.CaptureCallback(){

        }
    } else {
        Log.d("CameraHazard","VERSION.SDK_INT < LOLLIPOP")

        TODO("VERSION.SDK_INT < LOLLIPOP")
    }
    private fun shutdownCamera(){

    }
}
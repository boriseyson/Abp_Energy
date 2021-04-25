package com.misit.abpenergy.Serviceimport android.Manifestimport android.annotation.SuppressLintimport android.content.pm.PackageManagerimport androidx.appcompat.app.AppCompatActivityimport android.os.Bundleimport android.util.SparseArrayimport android.view.SurfaceHolderimport androidx.core.app.ActivityCompatimport androidx.core.content.ContextCompatimport androidx.core.util.isNotEmptyimport com.google.android.gms.vision.CameraSourceimport com.google.android.gms.vision.Detectorimport com.google.android.gms.vision.barcode.Barcodeimport com.google.android.gms.vision.barcode.BarcodeDetectorimport com.misit.abpenergy.Rimport es.dmoral.toasty.Toastyimport kotlinx.android.synthetic.main.activity_barcode_scanner.*import java.lang.Exceptionclass BarcodeScannerActivity : AppCompatActivity() {    private val requestCodeCameraPermission = 1001    private lateinit var cameraSource : CameraSource    private lateinit var detector: BarcodeDetector    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        setContentView(R.layout.activity_barcode_scanner)        if(ContextCompat.checkSelfPermission(this@BarcodeScannerActivity,Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){            askForCameraPermission()        }else{            setupControl()        }    }    private fun setupControl() {        detector = BarcodeDetector.Builder(this@BarcodeScannerActivity).build()        cameraSource = CameraSource.Builder(this@BarcodeScannerActivity,detector).setAutoFocusEnabled(true).build()        cameraSrufaceView.holder.addCallback(surgaceCallBack)        detector.setProcessor(processor)    }    private fun askForCameraPermission(){        ActivityCompat.requestPermissions(this@BarcodeScannerActivity,        arrayOf(Manifest.permission.CAMERA),requestCodeCameraPermission)    }    override fun onRequestPermissionsResult(        requestCode: Int,        permissions: Array<out String>,        grantResults: IntArray    ) {        if(requestCode == requestCodeCameraPermission && grantResults.isNotEmpty()){            setupControl()        }else{            Toasty.error(this@BarcodeScannerActivity,"Permission Denied!").show()        }        super.onRequestPermissionsResult(requestCode, permissions, grantResults)    }    private val surgaceCallBack = object : SurfaceHolder.Callback{        @SuppressLint("MissingPermission")        override fun surfaceCreated(holder: SurfaceHolder?) {            try {                cameraSource.start(holder)            }catch (e:Exception){                Toasty.error(this@BarcodeScannerActivity,"Something Went Wrong!").show()            }        }        override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {        }        override fun surfaceDestroyed(holder: SurfaceHolder?) {            cameraSource.stop()        }    }    private val processor = object :Detector.Processor<Barcode>{        override fun release() {        }        override fun receiveDetections(p0: Detector.Detections<Barcode>?) {            if (p0!=null && p0.detectedItems.isNotEmpty()){                var qCodes : SparseArray<Barcode> = p0.detectedItems                var code = qCodes.valueAt(0)                textScanResult.text = code.displayValue            }else{//                textScanResult.text = ""            }        }    }}
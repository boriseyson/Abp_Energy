package com.misit.abpenergy.HazardReport.ViewModelimport android.content.Contextimport android.util.Logimport androidx.lifecycle.LiveDataimport androidx.lifecycle.MutableLiveDataimport androidx.lifecycle.ViewModelimport com.misit.abpenergy.Api.ApiClientimport com.misit.abpenergy.Api.ApiEndPointimport com.misit.abpenergy.HazardReport.HazardReportActivityimport com.misit.abpenergy.HazardReport.Response.HazardItemimport com.misit.abpenergy.HazardReport.SQLite.DataSource.DetailDataSourceOfflineimport com.misit.abpenergy.HazardReport.SQLite.DataSource.HazardUserDataSourceimport com.misit.abpenergy.HazardReport.SQLite.DataSource.HeaderDataSourceOfflineimport com.misit.abpenergy.HazardReport.SQLite.DataSource.ValidationDataSourceOfflineimport com.misit.abpenergy.HazardReport.SQLite.Model.*import com.misit.abpenergy.Utils.ConfigUtilimport com.misit.abpenergy.Utils.PrefsUtilimport kotlinx.coroutines.*import org.joda.time.LocalDateimport org.joda.time.format.DateTimeFormatimport org.joda.time.format.DateTimeFormatterimport kotlin.math.ceilclass HeaderViewModel:ViewModel() {    var hazardList : MutableLiveData<MutableList<HeaderListModel>>    var hazardListItem : MutableLiveData<MutableList<HazardItem>>    var totalHazardUsers : MutableLiveData<String>    var hazardUserVerify : MutableLiveData<String>    var listHazard : MutableList<HeaderListModel>    var listHazardItem : MutableList<HazardItem>    var totalHalaman : MutableLiveData<Int>    var status :MutableLiveData<Boolean>    lateinit var offlineHeader :HeaderDataSourceOffline    lateinit var offlineDetail :DetailDataSourceOffline    lateinit var offlineValidate :ValidationDataSourceOffline    lateinit var hazardUser :HazardUserDataSource    var prev = 0    var next = 0    init {        if(PrefsUtil.getInstance().getBooleanState("IS_LOGGED_IN", false)){            NIK = PrefsUtil.getInstance().getStringState(PrefsUtil.NIK, "")            USERNAME = PrefsUtil.getInstance().getStringState(PrefsUtil.USER_NAME, "")            TOTAL_HAZARD_USER = PrefsUtil.getInstance().getStringState(PrefsUtil.TOTAL_HAZARD_USER!!,"0")        }        hazardList = MutableLiveData()        hazardListItem = MutableLiveData()        listHazard = ArrayList()        listHazardItem =ArrayList()        totalHalaman= MutableLiveData()        totalHazardUsers = MutableLiveData()        hazardUserVerify = MutableLiveData()        status = MutableLiveData(false)    }    fun hazardObserver():MutableLiveData<MutableList<HeaderListModel>>{        return hazardList    }    fun hazardsObserver():MutableLiveData<MutableList<HazardItem>>{        return hazardListItem    }    fun verifyHazard():MutableLiveData<String>{        return hazardUserVerify    }    fun userHazard():MutableLiveData<String>{        return totalHazardUsers    }    fun hazardPaginate():MutableLiveData<Int>{        return totalHalaman    }    fun setStatus():MutableLiveData<Boolean> {       return status    }    suspend fun offlineHazard(c:Context,hal:Int?,dari: String,sampai: String){        val fmt: DateTimeFormatter = DateTimeFormat.forPattern("d MMMM yyyy")        val dariTgl =fmt.parseLocalDate(dari).toString()        val sampaiTgl = fmt.parseLocalDate(sampai).toString()        Log.d("tgl",dariTgl)        Log.d("tgl",sampaiTgl)        offlineHeader = HeaderDataSourceOffline(c)        var batas = 5        var halaman = hal ?: 1        var halaman_awal = if(halaman>1) (halaman * batas) - batas else 0        coroutineScope {            listHazard?.clear()            listHazardItem?.clear()            var jumlahData = async { offlineHeader.getPage(dariTgl,sampaiTgl)  }            val waitJumlah = jumlahData.await()            if(waitJumlah>0){                var halamanTotal = ceil(waitJumlah.toFloat()/batas).toInt()                totalHalaman.postValue(halamanTotal)                prev = if(halaman>1)halaman - 1 else 1                next = if(halaman < halamanTotal) halaman + 1 else 1                val deferred = async {                    offlineHeader.getPaginate(USERNAME,halaman_awal,batas,dariTgl,sampaiTgl)                }                var resultData = deferred.await()                if(resultData!=null){                    var hazardDiVerify = TOTAL_HAZARD_USER.toInt()                    listHazardItem.addAll(resultData)                    resultData.forEach {                        if(it.uservalid!=null){                            hazardDiVerify++                        }else{                            hazardDiVerify = TOTAL_HAZARD_USER.toInt()                        }                    }                    totalHazardUsers.postValue("${resultData.size}")                    hazardUserVerify.postValue("${hazardDiVerify}")                    hazardListItem.postValue(listHazardItem)                }else{                    hazardListItem.postValue(listHazardItem)                }                Log.d("resultData","$resultData")            }        }    }    suspend fun onlineHazard(c:Context,dari:String,sampai:String){        var z =0        offlineHeader = HeaderDataSourceOffline(c)        offlineDetail = DetailDataSourceOffline(c)        offlineValidate = ValidationDataSourceOffline(c)        hazardUser = HazardUserDataSource(c)        listHazard.clear()        coroutineScope {            val deleted = async { offlineHeader.deleteAll()  }            val deleted1 = async { offlineDetail.deleteAll()  }            val deleted2 = async { offlineValidate.deleteAll()  }            val deleted3 = async { hazardUser.deleteAll() }            if(deleted.await() && deleted1.await() && deleted2.await() && deleted3.await()){                async {                    val apiEndPoint = ApiClient.getClient(c)!!.create(ApiEndPoint::class.java)                    val call = apiEndPoint.getHazardOffline(USERNAME,dari,sampai)                    if(call!=null){                        if(call.isSuccessful)                        {                            val result = call.body()                            if(result!=null){                                var headerModel = HazardHeaderModel()                                var detailModel = HazardDetailModel()                                var validateModel = HazardValidationModel()                                val looping = async {                                    result.data?.forEach {                                        Log.d("IncHazard","$it")                                        headerModel.idHazard = it.idHazard                                        headerModel.uid = it.uid                                        headerModel.perusahaan = it.perusahaan                                        headerModel.tgl_hazard = it.tglHazard                                        headerModel.jam_hazard = it.jamHazard                                        headerModel.idKemungkinan = it.idKemungkinan                                        headerModel.idKeparahan = it.idKeparahan                                        headerModel.lokasi = it.lokasi                                        headerModel.lokasi_detail = it.lokasiDetail                                        headerModel.lokasiHazard = it.lokasiHazard                                        headerModel.status_perbaikan = it.statusPerbaikan                                        headerModel.user_input = it.userInput                                        headerModel.time_input = it.timeInput                                        headerModel.deskripsi = it.deskripsi                                        headerModel.status = it.status                                        headerModel.nama_lengkap = it.namaLengkap                                        headerModel.tgl_input = it.tglInput                                        detailModel.idHazard=it.idHazard                                        detailModel.uid=it.uid                                        detailModel.tindakan=it.tindakan                                        detailModel.nikPJ=it.nikPJ                                        detailModel.namaPJ=it.namaPJ                                        detailModel.fotoPJ=it.fotoPJ                                        detailModel.idKemungkinan = it.idKemungkinan                                        detailModel.idKeparahan = it.idKeparahan                                        detailModel.katBahaya=it.katBahaya                                        detailModel.idPengendalian=it.idPengendalian                                        detailModel.tgl_selesai=it.tglSelesai                                        detailModel.jam_selesai=it.jamSelesai                                        detailModel.lokasiHazard = it.lokasiHazard                                        detailModel.bukti=it.bukti                                        detailModel.update_bukti=it.updateBukti                                        detailModel.keterangan_update=it.keteranganUpdate                                        detailModel.idKemungkinanSesudah=it.idKemungkinanSesudah                                        detailModel.idKeparahanSesudah=it.idKeparahanSesudah                                        detailModel.tgl_tenggat=it.tgl_tenggat                                        detailModel.fotoPJ_option=it.fotoPJ_option                                        validateModel.idValidation = it.idvalidation                                        validateModel.uid = it.uid                                        validateModel.user_valid = it.uservalid                                        validateModel.tgl_valid = it.tglvalid                                        validateModel.jam_valid = it.jamvarid                                        val createHeader = async { offlineHeader.insertItem(headerModel) }                                        if(createHeader.await()>0){                                            val createDetail = async { offlineDetail.insertItem(detailModel) }                                            if (createDetail.await()>0){                                                val createValidate = async { offlineValidate.insertItem(validateModel) }                                                if(createValidate.await()>0){                                                    if(it.bukti!=null){                                                        ConfigUtil.downloadImage(c, "https://abpjobsite.com/bukti_hazard/"+it.bukti, it.bukti!!,"BUKTI")                                                    }                                                    if(it.updateBukti!=null){                                                        ConfigUtil.downloadImage(c, "https://abpjobsite.com/bukti_hazard/update/"+it.updateBukti, it.updateBukti!!,"BUKTI")                                                    }                                                    if(it.fotoPJ!=null){                                                        ConfigUtil.downloadImage(c, "https://abpjobsite.com/bukti_hazard/penanggung_jawab/"+it.fotoPJ, it.fotoPJ!!,"BUKTI")                                                    }                                                    z++                                                }                                            }                                        }                                    }                                    if(result.data!!.size==z) {                                        true                                    }else {                                        false                                    }                                }                                if(looping.await()){                                    status.postValue(true)                                }                            }else{                                status.postValue(false)                            }                        }                    }                }.await()            }        }    }    companion object{        var NIK="NIK"        var USERNAME="USERNAME"        var TOTAL_HAZARD_USER= "TOTAL_HAZARD_USER"    }}
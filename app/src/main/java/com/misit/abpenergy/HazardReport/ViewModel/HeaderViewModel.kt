package com.misit.abpenergy.HazardReport.ViewModelimport android.content.Contextimport android.util.Logimport androidx.lifecycle.LiveDataimport androidx.lifecycle.MutableLiveDataimport androidx.lifecycle.ViewModelimport com.misit.abpenergy.Api.ApiClientimport com.misit.abpenergy.Api.ApiEndPointimport com.misit.abpenergy.HazardReport.HazardReportActivityimport com.misit.abpenergy.HazardReport.SQLite.DataSource.HeaderDataSourceOfflineimport com.misit.abpenergy.HazardReport.SQLite.Model.HazardHeaderModelimport com.misit.abpenergy.HazardReport.SQLite.Model.HeaderListModelimport com.misit.abpenergy.Utils.PrefsUtilimport kotlinx.coroutines.GlobalScopeimport kotlinx.coroutines.asyncimport kotlinx.coroutines.coroutineScopeimport kotlinx.coroutines.launchimport org.joda.time.LocalDateimport org.joda.time.format.DateTimeFormatimport org.joda.time.format.DateTimeFormatterimport kotlin.math.ceilclass HeaderViewModel:ViewModel() {    var hazardList : MutableLiveData<MutableList<HeaderListModel>>    var listHazard : MutableList<HeaderListModel>    var totalHalaman : MutableLiveData<Int>    lateinit var status :MutableLiveData<Boolean>    lateinit var offlineHeader :HeaderDataSourceOffline    var prev = 0    var next = 0    init {        if(PrefsUtil.getInstance().getBooleanState("IS_LOGGED_IN", false)){            NIK = PrefsUtil.getInstance().getStringState(PrefsUtil.NIK, "")            USERNAME = PrefsUtil.getInstance().getStringState(PrefsUtil.USER_NAME, "")        }        hazardList = MutableLiveData()        listHazard = ArrayList()        totalHalaman= MutableLiveData()        status = MutableLiveData(false)    }    fun hazardObserver():MutableLiveData<MutableList<HeaderListModel>>{        return hazardList    }    fun hazardPaginate():MutableLiveData<Int>{        return totalHalaman    }    fun setStatus():MutableLiveData<Boolean> {       return status    }    suspend fun offlineHazard(c:Context,hal:Int?,dari: String,sampai: String){        val fmt: DateTimeFormatter = DateTimeFormat.forPattern("d MMMM yyyy")        val dariTgl =fmt.parseLocalDate(dari).toString()        val sampaiTgl = fmt.parseLocalDate(sampai).toString()        Log.d("tgl",dariTgl)        Log.d("tgl",sampaiTgl)        offlineHeader = HeaderDataSourceOffline(c)        var batas = 5        var halaman = hal ?: 1        var halaman_awal = if(halaman>1) (halaman * batas) - batas else 0        coroutineScope {            listHazard?.clear()            var jumlahData = async { offlineHeader.getPage()  }            val waitJumlah = jumlahData.await()            if(waitJumlah>0){                var halamanTotal = ceil(waitJumlah.toFloat()/batas).toInt()                totalHalaman.postValue(halamanTotal)                prev = if(halaman>1)halaman - 1 else 1                next = if(halaman < halamanTotal) halaman + 1 else 1                val deferred = async {                    offlineHeader.getPaginate(USERNAME,halaman_awal,batas,dariTgl,sampaiTgl)                }                var resultData = deferred.await()                if(resultData!=null){                    deferred.await().forEach {//                    Log.d("dataList","${it.tgl_hazard}")                        var modelHazard = HeaderListModel()                        modelHazard.idHazard = it.idHazard                        modelHazard.uid = it.uid                        modelHazard.perusahaan = it.perusahaan                        modelHazard.tgl_hazard = it.tgl_hazard                        modelHazard.jam_hazard = it.jam_hazard                        modelHazard.idKemungkinan = it.idKemungkinan                        modelHazard.idKeparahan = it.idKeparahan                        modelHazard.deskripsi = it.deskripsi                        modelHazard.lokasi = it.lokasi                        modelHazard.lokasi_detail = it.lokasi_detail                        modelHazard.status_perbaikan = it.status_perbaikan                        modelHazard.user_input = it.user_input                        modelHazard.time_input = it.time_input                        modelHazard.deskripsi = it.deskripsi                        modelHazard.status = it.status                        listHazard.add(modelHazard)                    }//                Log.d("dataList","${listHazard}")                    hazardList.postValue(listHazard)                }else{                    hazardList.postValue(listHazard)                }            }        }    }    suspend fun onlineHazard(c:Context,dari:String,sampai:String){        var z =0        offlineHeader = HeaderDataSourceOffline(c)        listHazard.clear()        coroutineScope {            val deleted = async { offlineHeader.deleteAll()  }            if(deleted.await()){                async {                    val apiEndPoint = ApiClient.getClient(c)!!.create(ApiEndPoint::class.java)                    val call = apiEndPoint.getHazardOffline(USERNAME,dari,sampai)                    if(call!=null){                        if(call.isSuccessful)                        {                            val result = call.body()                            if(result!=null){                                var hazardModel = HazardHeaderModel()                                val looping = async {                                    result.data?.forEach {                                        Log.d("IncHazard","$it")                                        hazardModel.idHazard = it.idHazard                                        hazardModel.uid = it.uid                                        hazardModel.perusahaan = it.perusahaan                                        hazardModel.tgl_hazard = it.tglHazard                                        hazardModel.jam_hazard = it.jamHazard                                        hazardModel.idKemungkinan = it.idKemungkinan                                        hazardModel.idKeparahan = it.idKeparahan                                        hazardModel.lokasi = it.lokasi                                        hazardModel.lokasi_detail = it.lokasiDetail                                        hazardModel.status_perbaikan = it.statusPerbaikan                                        hazardModel.user_input = it.userInput                                        hazardModel.time_input = it.timeInput                                        hazardModel.deskripsi = it.deskripsi                                        hazardModel.status = it.status                                        offlineHeader.insertItem(hazardModel)                                        z++                                    }                                    if(result.data!!.size==z) {                                        true                                    }else {                                        false                                    }                                }                                if(looping.await()){                                    status.postValue(true)                                }                            }else{                                status.postValue(false)                            }                        }                    }                }.await()            }        }    }    companion object{        var NIK="NIK"        var USERNAME="USERNAME"    }}
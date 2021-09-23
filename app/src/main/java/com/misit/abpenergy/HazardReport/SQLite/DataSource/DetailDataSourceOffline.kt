package com.misit.abpenergy.HazardReport.SQLite.DataSourceimport android.content.ContentValuesimport android.content.Contextimport android.database.Cursorimport android.database.sqlite.SQLiteDatabaseimport android.util.Logimport androidx.core.database.getIntOrNullimport com.misit.abpenergy.HazardReport.Response.DetailHazardResponseimport com.misit.abpenergy.HazardReport.Response.ItemHazardListimport com.misit.abpenergy.HazardReport.Response.RiskItemimport com.misit.abpenergy.HazardReport.SQLite.Model.HazardDetailModelimport com.misit.abpenergy.HazardReport.SQLite.Model.MetrikModelimport com.misit.abpenergy.SQLite.DbHelperimport es.dmoral.toasty.Toastyimport kotlinx.coroutines.asyncimport kotlinx.coroutines.coroutineScopeclass DetailDataSourceOffline(val c: Context) {    var dbHelper : DbHelper    var sqlDatabase : SQLiteDatabase?=null    var listItem :ArrayList<HazardDetailModel>?=null    init {        listItem = ArrayList()        dbHelper = DbHelper(c)    }    private fun openAccess(){        sqlDatabase = dbHelper.writableDatabase    }    private fun closeAccess(){        sqlDatabase?.close()        dbHelper?.close()    }    suspend fun insertItem(item: HazardDetailModel):Long{        openAccess()        var cv = createCV(item)        var hasil = sqlDatabase?.insertOrThrow("$tbItem",null,cv)        closeAccess()        return hasil!!    }    suspend fun getItem(uid: String): ItemHazardList {        openAccess()        val c = sqlDatabase?.rawQuery("SELECT " +                "a.*,b.*,c.*,d.lokasi as lokasiHazard,e.namaLengkap,f.nilai as nilaiKemungkinan,g.nilai as nilaiKeparahan,i.nilai as nilaiKemungkinanSesudah," +                "j.nilai as nilaiKeparahanSesudah,h.*,f.kemungkinan as kemungkinanSebelum,g.keparahan as keparahanSebelum,i.kemungkinan as kemungkinanSesudah,j.keparahan as keparahanSesudah" +                " FROM "+                "$tbHeader a " +                "LEFT JOIN $tbDetail b ON b.uid=a.uid " +                "LEFT JOIN $tbValid c ON c.uid=a.uid " +                "LEFT JOIN $tbLokasi d ON d.idLok=a.lokasi " +                "LEFT JOIN $tbUser e ON e.username=a.user_input " +                "LEFT JOIN $tbKemungkinan f ON f.idKemungkinan=a.idKemungkinan " +                "LEFT JOIN $tbKeparahan g ON g.idKeparahan=a.idKeparahan " +                "LEFT JOIN $tbPengendalian h ON h.idHirarki=b.idPengendalian " +                "LEFT JOIN $tbKemungkinan i ON i.idKemungkinan=b.idKemungkinanSesudah " +                "LEFT JOIN $tbKeparahan j ON j.idKeparahan=b.idKeparahanSesudah " +                " WHERE a.uid = ?", arrayOf(uid))        var itemModels = ItemHazardList()        coroutineScope {            var first = async { c?.moveToFirst() }            if(first.await() == true) {                val sendItem = async {                    fetchRowResponse(c!!)                }                if(sendItem.await()!=null){                    itemModels = sendItem.await()                    c?.close()                    closeAccess()                }            }        }        return itemModels    }    suspend fun getMatrikResiko(nilai:String): MetrikModel {        openAccess()        val c = sqlDatabase?.rawQuery("SELECT * FROM " +                "$tbMetrikResiko " +                "WHERE max >= '"+nilai+"' and min <='"+nilai+"'",null)        var riskItem = MetrikModel()        coroutineScope {            var first = async { c?.moveToFirst() }            if(first.await()==true){                val sendItem = async { fetchRisk(c!!) }                if(sendItem.await()!=null){                    riskItem = sendItem.await()                    c?.close()                    closeAccess()                }            }        }        return riskItem    }    private fun fetchRisk(cursor:Cursor):MetrikModel{        val idResiko = cursor.getInt(cursor.getColumnIndex("idResiko"))        val min = cursor.getInt(cursor.getColumnIndex("min"))        val max = cursor.getInt(cursor.getColumnIndex("max"))        val kategori = cursor.getString(cursor.getColumnIndex("kategori"))        val tindakan = cursor.getString(cursor.getColumnIndex("tindakan"))        val bgColor = cursor.getString(cursor.getColumnIndex("bgColor"))        val txtColor = cursor.getString(cursor.getColumnIndex("txtColor"))        val flag = cursor.getInt(cursor.getColumnIndex("flag"))        var metrikModel = MetrikModel()        metrikModel.idResiko = idResiko        metrikModel.min = min        metrikModel.max = max        metrikModel.kategori = kategori        metrikModel.tindakan = tindakan        metrikModel.bgColor = bgColor        metrikModel.flag = flag        metrikModel.txtColor = txtColor        return metrikModel    }    private fun fetchRowResponse(cursor: Cursor): ItemHazardList {        val idHazard = cursor.getInt(cursor.getColumnIndex("idHazard"))        val uid = cursor.getString(cursor.getColumnIndex("uid"))        val tindakan = cursor.getString(cursor.getColumnIndex("tindakan"))        val namaPJ = cursor.getString(cursor.getColumnIndex("namaPJ"))        val nikPJ = cursor.getString(cursor.getColumnIndex("nikPJ"))        val fotoPJ = cursor.getString(cursor.getColumnIndex("fotoPJ"))        val katBahaya = cursor.getString(cursor.getColumnIndex("katBahaya"))        val idPengendalian = cursor.getInt(cursor.getColumnIndex("idPengendalian"))        val tgl_selesai = cursor.getString(cursor.getColumnIndex("tgl_selesai"))        val jam_selesai = cursor.getString(cursor.getColumnIndex("jam_selesai"))        val bukti = cursor.getString(cursor.getColumnIndex("bukti"))        val update_bukti = cursor.getString(cursor.getColumnIndex("update_bukti"))        val keterangan_update = cursor.getString(cursor.getColumnIndex("keterangan_update"))        val idKemungkinanSesudah = cursor.getInt(cursor.getColumnIndex("idKemungkinanSesudah"))        val idKeparahanSesudah = cursor.getInt(cursor.getColumnIndex("idKeparahanSesudah"))        val tgl_tenggat = cursor.getString(cursor.getColumnIndex("tgl_tenggat"))        val fotoPJ_option = cursor.getInt(cursor.getColumnIndex("fotoPJ_option"))        val lokasiHazard = cursor.getString(cursor.getColumnIndex("lokasiHazard"))        val tglHazard = cursor.getString(cursor.getColumnIndex("tgl_hazard"))        val perusahaan = cursor.getString(cursor.getColumnIndex("perusahaan"))        val jamHazard = cursor.getString(cursor.getColumnIndex("jam_hazard"))        val idKemungkinan = cursor.getInt(cursor.getColumnIndex("idKemungkinan"))        val idKeparahan = cursor.getInt(cursor.getColumnIndex("idKeparahan"))        val deskripsi = cursor.getString(cursor.getColumnIndex("deskripsi"))        val lokasi = cursor.getString(cursor.getColumnIndex("lokasi"))        val lokasiDetail = cursor.getString(cursor.getColumnIndex("lokasi_detail"))        val statusPerbaikan = cursor.getString(cursor.getColumnIndex("status_perbaikan"))        val userInput = cursor.getString(cursor.getColumnIndex("user_input"))        val namaLengkap = cursor.getString(cursor.getColumnIndex("nama_lengkap"))        val timeInput = cursor.getString(cursor.getColumnIndex("time_input"))        val tglInput = cursor.getString(cursor.getColumnIndex("tglInput"))        val status = cursor.getInt(cursor.getColumnIndex("status"))        val user_valid = cursor.getString(cursor.getColumnIndex("user_valid"))        val tgl_valid = cursor.getString(cursor.getColumnIndex("tgl_valid"))        val jam_valid = cursor.getString(cursor.getColumnIndex("jam_valid"))        val keparahanSebelum = cursor.getString(cursor.getColumnIndex("keparahanSebelum"))        val kemungkinanSebelum = cursor.getString(cursor.getColumnIndex("kemungkinanSebelum"))        val namaPengendalian = cursor.getString(cursor.getColumnIndex("namaPengendalian"))        val keparahanSesudah = cursor.getString(cursor.getColumnIndex("keparahanSesudah"))        val kemungkinanSesudah = cursor.getString(cursor.getColumnIndex("kemungkinanSesudah"))        val kodeBahaya = cursor.getString(cursor.getColumnIndex("tindakan"))        val nilaiKemungkinan = cursor.getInt(cursor.getColumnIndex("nilaiKemungkinan"))        val nilaiKeparahan = cursor.getInt(cursor.getColumnIndex("nilaiKeparahan"))        val nilaiKemungkinanSesudah = cursor.getInt(cursor.getColumnIndex("nilaiKemungkinanSesudah"))        val nilaiKeparahanSesudah = cursor.getInt(cursor.getColumnIndex("nilaiKeparahanSesudah"))        val detailItem = ItemHazardList()        detailItem.idHazard = idHazard        detailItem.uid = uid        detailItem.tindakan = tindakan        detailItem.perusahaan = perusahaan        detailItem.namaPJ = namaPJ        detailItem.nikPJ = nikPJ        detailItem.fotoPJ = fotoPJ        detailItem.katBahaya = katBahaya        detailItem.idPengendalian = idPengendalian        detailItem.tglHazard = tglHazard        detailItem.jamHazard = jamHazard        detailItem.idKemungkinan = idKemungkinan        detailItem.idKeparahan = idKeparahan        detailItem.kemungkinanSebelum = kemungkinanSebelum        detailItem.keparahanSebelum = keparahanSebelum        detailItem.deskripsi = deskripsi        detailItem.lokasi = lokasi        detailItem.lokasiDetail = lokasiDetail        detailItem.statusPerbaikan = statusPerbaikan        detailItem.userInput = userInput        detailItem.namaLengkap = namaLengkap        detailItem.timeInput = timeInput        detailItem.tglInput = tglInput        detailItem.status = status        detailItem.tglSelesai = tgl_selesai        detailItem.lokasiHazard = lokasiHazard        detailItem.jamSelesai = jam_selesai        detailItem.bukti = bukti        detailItem.updateBukti = update_bukti        detailItem.keteranganUpdate = keterangan_update        detailItem.idKemungkinanSesudah = idKemungkinanSesudah        detailItem.idKeparahanSesudah = idKeparahanSesudah        detailItem.fotoPjOption = fotoPJ_option        detailItem.nilaiKemungkinan = nilaiKemungkinan        detailItem.nilaiKeparahan = nilaiKeparahan        detailItem.nilaiKemungkinanSesudah = nilaiKemungkinanSesudah        detailItem.nilaiKeparahanSesudah = nilaiKeparahanSesudah        detailItem.tgl_tenggat = tgl_tenggat        detailItem.uservarid = user_valid        detailItem.tglvarid = tgl_valid        detailItem.jamvarid = jam_valid        detailItem.namaPengendalian = namaPengendalian        detailItem.keparahanSesudah = keparahanSesudah        detailItem.kemungkinanSesudah = kemungkinanSesudah        detailItem.tindakan = kodeBahaya        return detailItem    }    fun getAll(): ArrayList<HazardDetailModel> {        val listItem : ArrayList<HazardDetailModel> = ArrayList()        openAccess()        val c = sqlDatabase?.rawQuery("SELECT * FROM "+                "$tbItem ",null)        if(c!!.moveToFirst()){            do {                listItem?.add(fetchRow(c))            }while (c.moveToNext())        }        c?.close()        closeAccess()        return listItem!!    }    private fun fetchRow(cursor: Cursor): HazardDetailModel {        val idHazard = cursor.getInt(cursor.getColumnIndex("idHazard"))        val uid = cursor.getString(cursor.getColumnIndex("uid"))        val tindakan = cursor.getString(cursor.getColumnIndex("tindakan"))        val namaPJ = cursor.getString(cursor.getColumnIndex("namaPJ"))        val nikPJ = cursor.getString(cursor.getColumnIndex("nikPJ"))        val fotoPJ = cursor.getString(cursor.getColumnIndex("fotoPJ"))        val katBahaya = cursor.getString(cursor.getColumnIndex("katBahaya"))        val idPengendalian = cursor.getInt(cursor.getColumnIndex("idPengendalian"))        val tgl_selesai = cursor.getString(cursor.getColumnIndex("tgl_selesai"))        val jam_selesai = cursor.getString(cursor.getColumnIndex("jam_selesai"))        val bukti = cursor.getString(cursor.getColumnIndex("bukti"))        val update_bukti = cursor.getString(cursor.getColumnIndex("update_bukti"))        val keterangan_update = cursor.getString(cursor.getColumnIndex("keterangan_update"))        val idKemungkinanSesudah = cursor.getInt(cursor.getColumnIndex("idKemungkinanSesudah"))        val idKeparahanSesudah = cursor.getInt(cursor.getColumnIndex("idKeparahanSesudah"))        val tgl_tenggat = cursor.getString(cursor.getColumnIndex("tgl_tenggat"))        val fotoPJ_option = cursor.getInt(cursor.getColumnIndex("fotoPJ_option"))        val lokasiHazard = cursor.getString(cursor.getColumnIndex("lokasiHazard"))        val hazardDetailModel = HazardDetailModel()        hazardDetailModel.idHazard = idHazard        hazardDetailModel.uid = uid        hazardDetailModel.tindakan = tindakan        hazardDetailModel.namaPJ = namaPJ        hazardDetailModel.nikPJ = nikPJ        hazardDetailModel.fotoPJ = fotoPJ        hazardDetailModel.katBahaya = katBahaya        hazardDetailModel.idPengendalian = idPengendalian        hazardDetailModel.tgl_selesai = tgl_selesai        hazardDetailModel.lokasiHazard = lokasiHazard        hazardDetailModel.jam_selesai = jam_selesai        hazardDetailModel.bukti = bukti        hazardDetailModel.update_bukti = update_bukti        hazardDetailModel.keterangan_update = keterangan_update        hazardDetailModel.idKemungkinanSesudah = idKemungkinanSesudah        hazardDetailModel.idKeparahanSesudah = idKeparahanSesudah        hazardDetailModel.tgl_tenggat = tgl_tenggat        hazardDetailModel.fotoPJ_option = fotoPJ_option        return hazardDetailModel    }    fun deleteItem(item:Int):Boolean{        openAccess()        val hasil = sqlDatabase?.delete("$tbItem","idHazard = ? ", arrayOf(item.toString()))        if(hasil!! <0 ){            Toasty.error(c!!,"Gagal Hapus").show()            Log.d("ServiceJob","Gagal Hapus")            return false        }else{            Log.d("ServiceJob","Berhasil Hapus")            Toasty.success(c!!,"Hapus Berhasil").show()        }        closeAccess()        return true    }    fun deleteAll():Boolean{        openAccess()        val hasil = sqlDatabase?.delete("$tbItem",null,null)        if(hasil!! <0 ){            return false        }        closeAccess()        return true    }    fun updateItem(item: HazardDetailModel, idHazard:Int):Boolean{        openAccess()        val items = ContentValues()        items.put("idHazard",item.idHazard)        items.put("uid",item.uid)        items.put("tindakan",item.tindakan)        items.put("namaPJ",item.namaPJ)        items.put("nikPJ",item.nikPJ)        items.put("fotoPJ",item.fotoPJ)        items.put("katBahaya",item.katBahaya)        items.put("idPengendalian",item.idPengendalian)        items.put("tgl_selesai",item.tgl_selesai)        items.put("jam_selesai",item.jam_selesai)        items.put("bukti",item.bukti)        items.put("update_bukti",item.update_bukti)        items.put("keterangan_update",item.keterangan_update)        items.put("idKemungkinanSesudah",item.idKemungkinanSesudah)        items.put("idKeparahanSesudah",item.idKeparahanSesudah)        items.put("tgl_tenggat",item.tgl_tenggat)        items.put("fotoPJ_option",item.fotoPJ_option)        items.put("lokasiHazard",item.lokasiHazard)        val hasil = sqlDatabase?.update("$tbItem",items,"idHazard = ?",            arrayOf("${idHazard}"))        if(hasil!! < 0){            return false        }        closeAccess()        return true    }    private fun createCV(item : HazardDetailModel): ContentValues {        var items = ContentValues()        items.put("idHazard",item.idHazard)        items.put("uid",item.uid)        items.put("tindakan",item.tindakan)        items.put("lokasiHazard",item.lokasiHazard)        items.put("namaPJ",item.namaPJ)        items.put("nikPJ",item.nikPJ)        items.put("fotoPJ",item.fotoPJ)        items.put("katBahaya",item.katBahaya)        items.put("idPengendalian",item.idPengendalian)        items.put("tgl_selesai",item.tgl_selesai)        items.put("jam_selesai",item.jam_selesai)        items.put("bukti",item.bukti)        items.put("update_bukti",item.update_bukti)        items.put("keterangan_update",item.keterangan_update)        items.put("idKeparahan",item.idKeparahan)        items.put("idKemungkinan",item.idKemungkinan)        items.put("idKemungkinanSesudah",item.idKemungkinanSesudah)        items.put("idKeparahanSesudah",item.idKeparahanSesudah)        items.put("tgl_tenggat",item.tgl_tenggat)        items.put("fotoPJ_option",item.fotoPJ_option)        return items    }    companion object{        val tbItem = "HAZARD_DETAIL_OFFLINE"        val tbHeader = "HAZARD_HEADER_OFFLINE"        val tbDetail = "HAZARD_DETAIL_OFFLINE"        val tbValid = "HAZARD_VALIDATION_OFFLINE"        val tbLokasi = "LOKASI"        val tbUser = "USERS"        val tbKemungkinan = "KEMUNGKINAN"        val tbKeparahan = "KEPARAHAN"        val tbPengendalian = "PENGENDALIAN"        val tbMetrikResiko = "metrik_resiko"    }}
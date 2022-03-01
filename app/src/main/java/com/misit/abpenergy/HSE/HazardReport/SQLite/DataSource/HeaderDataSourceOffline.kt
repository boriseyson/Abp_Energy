package com.misit.abpenergy.HSE.HazardReport.SQLite.DataSourceimport android.content.ContentValuesimport android.content.Contextimport android.database.Cursorimport android.database.sqlite.SQLiteDatabaseimport android.util.Logimport androidx.core.database.getIntOrNullimport com.misit.abpenergy.HSE.HazardReport.Response.HazardItemimport com.misit.abpenergy.HSE.HazardReport.SQLite.Model.HazardHeaderModelimport com.misit.abpenergy.SQLite.DbHelperimport es.dmoral.toasty.Toastyimport java.lang.Exceptionclass HeaderDataSourceOffline(val c: Context) {    var dbHelper : DbHelper    var sqlDatabase : SQLiteDatabase?=null    var listItem :ArrayList<HazardHeaderModel>?=null    init {        listItem = ArrayList()        dbHelper = DbHelper(c)    }    fun openAccess(){        sqlDatabase = dbHelper.writableDatabase    }    fun closeAccess(){        sqlDatabase?.close()        dbHelper?.close()    }    fun insertItem(item: HazardHeaderModel):Long{        var cv = createCV(item)        var hasil = sqlDatabase?.insertOrThrow("$tbItem",null,cv)        return hasil!!    }    fun cekHeader(): Int {        openAccess()        val c = sqlDatabase?.rawQuery("SELECT count(*) FROM "+                "${tbItem} ",null)        c?.let {            if(it.moveToFirst()){                return it?.getIntOrNull(0) ?: 0            }        }        c?.close()        closeAccess()        return 0    }    fun getItem(idHazard: String): HazardHeaderModel {        openAccess()        val c = sqlDatabase?.rawQuery("SELECT * FROM "+                "$tbItem WHERE idHazard = ?", arrayOf(idHazard))        c?.moveToFirst()        var itemModels = HazardHeaderModel()        c?.let {            itemModels = fetchRow(it)        }        c?.close()        closeAccess()        return itemModels    }    fun getPage(dari:String,sampai:String): Int {        var countRows=0        val numRows = sqlDatabase!!.rawQuery(                "SELECT count(*) as jumlah FROM " +                        "$tbItem " +                        " WHERE (tgl_hazard >= '" + dari + "' AND  tgl_hazard <= '" + sampai + "')",                null            )        numRows.let {            if(it.moveToFirst()){                val sendItem = numRows?.getIntOrNull(0) ?: 0                return sendItem                Log.d("GetPage","$numRows")            }            it.close()        }                Log.d("GetPage","$numRows")        return countRows    }     fun getPaginate(username:String,halaman_awal:Int,batas:Int,dari:String,sampai:String,validasi:Int): ArrayList<HazardItem>{         var cekValidasi :String?=null         val listItem : ArrayList<HazardItem> = ArrayList()         var c:Cursor         if(validasi!=null){             if (validasi==0){                 cekValidasi = " and h.user_valid IS NULL"             }else if(validasi==1){                 cekValidasi = " and ( h.option_flag='1' or h.option_flag IS NULL) and h.user_valid IS NOT NULL"             }else if(validasi==2){                 cekValidasi = " and h.option_flag ='0'"             }             c = sqlDatabase!!.rawQuery("SELECT "+                     "a.*, b.*,c.*,d.*,e.*,f.namaLengkap,g.*,h.*"+                     " FROM "+                     "$tbItem a "+                     "INNER JOIN HAZARD_DETAIL_OFFLINE b ON b.uid=a.uid "+                     "LEFT JOIN KEMUNGKINAN c ON c.idKemungkinan=a.idKemungkinan "+                     "LEFT JOIN KEPARAHAN d ON d.idKeparahan=a.idKeparahan "+                     "LEFT JOIN LOKASI e ON e.idLok=a.lokasi "+                     "LEFT JOIN USERS f ON f.username=a.user_input "+                     "LEFT JOIN PENGENDALIAN g ON g.idHirarki=b.idPengendalian "+                     "LEFT JOIN HAZARD_VALIDATION_OFFLINE h ON h.uid=a.uid "+                     " WHERE (a.tgl_hazard >= '"+dari+"' AND  a.tgl_hazard <= '"+sampai+"') AND a.user_input = '"+username+"' $cekValidasi "+                     " ORDER BY a.tgl_hazard desc LIMIT $halaman_awal,$batas ",null)         }else{             c = sqlDatabase!!.rawQuery("SELECT "+                     "a.*, b.*,c.*,d.*,e.*,f.namaLengkap,g.*,h.*"+                     " FROM "+                     "$tbItem a "+                     "INNER JOIN HAZARD_DETAIL_OFFLINE b ON b.uid=a.uid "+                     "LEFT JOIN KEMUNGKINAN c ON c.idKemungkinan=a.idKemungkinan "+                     "LEFT JOIN KEPARAHAN d ON d.idKeparahan=a.idKeparahan "+                     "LEFT JOIN LOKASI e ON e.idLok=a.lokasi "+                     "LEFT JOIN USERS f ON f.username=a.user_input "+                     "LEFT JOIN PENGENDALIAN g ON g.idHirarki=b.idPengendalian "+                     "LEFT JOIN HAZARD_VALIDATION_OFFLINE h ON h.uid=a.uid "+                     " WHERE (a.tgl_hazard >= '"+dari+"' AND  a.tgl_hazard <= '"+sampai+"') AND a.user_input = '"+username+"' "+                     " ORDER BY a.tgl_hazard desc LIMIT $halaman_awal,$batas ",null)         }            c.let {                if(c.moveToFirst()){                    do {                        listItem?.add(fetchJoinRow(c))                    }while (c.moveToNext())                }                if (c.isLast){                    return listItem                    c?.close()                }        }        return listItem    }    fun getAll(): ArrayList<HazardHeaderModel> {        val listItem : ArrayList<HazardHeaderModel> = ArrayList()        openAccess()        val c = sqlDatabase?.rawQuery("SELECT * FROM "+                "$tbItem ",null)        if(c!!.moveToFirst()){            do {                listItem?.add(fetchRow(c))            }while (c.moveToNext())        }        c?.close()        closeAccess()        return listItem!!    }    private fun fetchRow(cursor: Cursor): HazardHeaderModel {        val idHazard = cursor.getInt(cursor.getColumnIndex("idHazard"))        val uid = cursor.getString(cursor.getColumnIndex("uid"))        val perusahaan = cursor.getString(cursor.getColumnIndex("perusahaan"))        val tgl_hazard = cursor.getString(cursor.getColumnIndex("tgl_hazard"))        val jam_hazard = cursor.getString(cursor.getColumnIndex("jam_hazard"))        val idKemungkinan = cursor.getInt(cursor.getColumnIndex("idKemungkinan"))        val idKeparahan = cursor.getInt(cursor.getColumnIndex("idKeparahan"))        val deskripsi = cursor.getString(cursor.getColumnIndex("deskripsi"))        val lokasi = cursor.getString(cursor.getColumnIndex("lokasi"))        val lokasi_detail = cursor.getString(cursor.getColumnIndex("lokasi_detail"))        val status_perbaikan = cursor.getString(cursor.getColumnIndex("status_perbaikan"))        val user_input = cursor.getString(cursor.getColumnIndex("user_input"))        val time_input = cursor.getString(cursor.getColumnIndex("time_input"))        val nama_lengkap = cursor.getString(cursor.getColumnIndex("nama_lengkap"))        val hazardHeaderModel = HazardHeaderModel()        hazardHeaderModel.idHazard = idHazard        hazardHeaderModel.uid = uid        hazardHeaderModel.perusahaan = perusahaan        hazardHeaderModel.tgl_hazard = tgl_hazard        hazardHeaderModel.jam_hazard = jam_hazard        hazardHeaderModel.idKemungkinan = idKemungkinan        hazardHeaderModel.idKeparahan = idKeparahan        hazardHeaderModel.deskripsi = deskripsi        hazardHeaderModel.lokasi = lokasi        hazardHeaderModel.lokasi_detail = lokasi_detail        hazardHeaderModel.status_perbaikan = status_perbaikan        hazardHeaderModel.user_input = user_input        hazardHeaderModel.time_input = time_input        hazardHeaderModel.nama_lengkap = nama_lengkap        return hazardHeaderModel    }    private fun fetchJoinRow(cursor: Cursor): HazardItem {        val idHazard = cursor.getInt(cursor.getColumnIndex("idHazard"))        val uid = cursor.getString(cursor.getColumnIndex("uid"))        val perusahaan = cursor.getString(cursor.getColumnIndex("perusahaan"))        val tgl_hazard = cursor.getString(cursor.getColumnIndex("tgl_hazard"))        val jam_hazard = cursor.getString(cursor.getColumnIndex("jam_hazard"))        val idKemungkinan = cursor.getInt(cursor.getColumnIndex("idKemungkinan"))        val idKeparahan = cursor.getInt(cursor.getColumnIndex("idKeparahan"))        val deskripsi = cursor.getString(cursor.getColumnIndex("deskripsi"))        val lokasi = cursor.getString(cursor.getColumnIndex("lokasi"))        val lokasi_detail = cursor.getString(cursor.getColumnIndex("lokasi_detail"))        val status_perbaikan = cursor.getString(cursor.getColumnIndex("status_perbaikan"))        val user_input = cursor.getString(cursor.getColumnIndex("user_input"))        val kemungkinan = cursor.getString(cursor.getColumnIndex("kemungkinan"))        val flag = cursor.getInt(cursor.getColumnIndex("flag"))        val updateBukti = cursor.getString(cursor.getColumnIndex("update_bukti"))        val nilai = cursor.getInt(cursor.getColumnIndex("nilai"))        val statusPerbaikan = cursor.getString(cursor.getColumnIndex("status_perbaikan"))        val namaLengkap = cursor.getString(cursor.getColumnIndex("nama_lengkap"))        val tglSelesai = cursor.getString(cursor.getColumnIndex("tgl_selesai"))        val namaPengendalian = cursor.getString(cursor.getColumnIndex("namaPengendalian"))        val tglvalid = cursor.getString(cursor.getColumnIndex("tgl_valid"))        val idKeparahanSesudah = cursor.getInt(cursor.getColumnIndex("idKeparahanSesudah"))        val tindakan = cursor.getString(cursor.getColumnIndex("tindakan"))        val katBahaya = cursor.getString(cursor.getColumnIndex("katBahaya"))        val keteranganUpdate = cursor.getString(cursor.getColumnIndex("keterangan_update"))        val lokasiHazard = cursor.getString(cursor.getColumnIndex("lokasiHazard"))        val fotoPJ = cursor.getString(cursor.getColumnIndex("fotoPJ"))        val idvalidation = cursor.getInt(cursor.getColumnIndex("idValidation"))        val jamSelesai = cursor.getString(cursor.getColumnIndex("jam_selesai"))        val idKemungkinanSesudah = cursor.getInt(cursor.getColumnIndex("idKemungkinanSesudah"))        val jamvarid = cursor.getString(cursor.getColumnIndex("jam_valid"))        val idHirarki = cursor.getInt(cursor.getColumnIndex("idHirarki"))        val tglInput = cursor.getString(cursor.getColumnIndex("tglInput"))        val nikPJ = cursor.getString(cursor.getColumnIndex("nikPJ"))        val bukti = cursor.getString(cursor.getColumnIndex("bukti"))        val uservalid = cursor.getString(cursor.getColumnIndex("user_valid"))        val keparahan = cursor.getString(cursor.getColumnIndex("keparahan"))        val fotoPJ_option = cursor.getInt(cursor.getColumnIndex("fotoPJ_option"))        val status = cursor.getInt(cursor.getColumnIndex("status"))        val namaPJ = cursor.getString(cursor.getColumnIndex("namaPJ"))        val tgl_tenggat = cursor.getString(cursor.getColumnIndex("tgl_tenggat"))        val time_input = cursor.getString(cursor.getColumnIndex("time_input"))        val option_flag = cursor.getInt(cursor.getColumnIndex("option_flag"))        val keterangan_admin = cursor.getString(cursor.getColumnIndex("keterangan_admin"))        val hazardHeaderModel = HazardItem()        hazardHeaderModel.kemungkinan = kemungkinan        hazardHeaderModel.flag = flag        hazardHeaderModel.updateBukti = updateBukti        hazardHeaderModel.nilai = nilai        hazardHeaderModel.statusPerbaikan = statusPerbaikan        hazardHeaderModel.namaLengkap = namaLengkap        hazardHeaderModel.tglSelesai = tglSelesai        hazardHeaderModel.namaPengendalian = namaPengendalian        hazardHeaderModel.tglvalid = tglvalid        hazardHeaderModel.idKeparahanSesudah = idKeparahanSesudah        hazardHeaderModel.tindakan = tindakan        hazardHeaderModel.katBahaya = katBahaya        hazardHeaderModel.keteranganUpdate = keteranganUpdate        hazardHeaderModel.lokasiHazard = lokasiHazard        hazardHeaderModel.fotoPJ = fotoPJ        hazardHeaderModel.idvalidation = idvalidation        hazardHeaderModel.jamSelesai = jamSelesai        hazardHeaderModel.idKemungkinanSesudah = idKemungkinanSesudah        hazardHeaderModel.jamvarid = jamvarid        hazardHeaderModel.idHirarki = idHirarki        hazardHeaderModel.tglInput = tglInput        hazardHeaderModel.nikPJ = nikPJ        hazardHeaderModel.bukti = bukti        hazardHeaderModel.uservalid = uservalid        hazardHeaderModel.keparahan = keparahan        hazardHeaderModel.fotoPJ_option = fotoPJ_option        hazardHeaderModel.status = status        hazardHeaderModel.namaPJ = namaPJ        hazardHeaderModel.tgl_tenggat = tgl_tenggat        hazardHeaderModel.idHazard = idHazard        hazardHeaderModel.uid = uid        hazardHeaderModel.perusahaan = perusahaan        hazardHeaderModel.tglHazard = tgl_hazard        hazardHeaderModel.jamHazard = jam_hazard        hazardHeaderModel.idKemungkinan = idKemungkinan        hazardHeaderModel.idKeparahan = idKeparahan        hazardHeaderModel.deskripsi = deskripsi        hazardHeaderModel.lokasi = lokasi        hazardHeaderModel.lokasiDetail = lokasi_detail        hazardHeaderModel.statusPerbaikan = status_perbaikan        hazardHeaderModel.userInput = user_input        hazardHeaderModel.timeInput = time_input        hazardHeaderModel.option_flag = option_flag        hazardHeaderModel.keterangan_admin = keterangan_admin        return hazardHeaderModel    }    fun deleteItem(item:Int):Boolean{        openAccess()        val hasil = sqlDatabase?.delete("$tbItem","idHazard = ? ", arrayOf(item.toString()))        if(hasil!! <0 ){            Toasty.error(c!!,"Gagal Hapus").show()            Log.d("ServiceJob","Gagal Hapus")            return false        }else{            Log.d("ServiceJob","Berhasil Hapus")            Toasty.success(c!!,"Hapus Berhasil").show()        }        closeAccess()        return true    }    fun deleteAll():Boolean{        try {            val hasil = sqlDatabase!!.delete("$tbItem", null, null)            if (hasil < 0) {                return false            } else {                return true            }        }catch (e:Exception){            return false        }    }    fun updateItem(item: HazardHeaderModel, idKemungkinan:Int):Boolean{        openAccess()        val items = ContentValues()        items.put("idHazard",item.idHazard)        items.put("uid",item.uid)        items.put("perusahaan",item.perusahaan)        items.put("tgl_hazard",item.tgl_hazard)        items.put("jam_hazard",item.jam_hazard)        items.put("idKemungkinan",item.idKemungkinan)        items.put("idKeparahan",item.idKeparahan)        items.put("deskripsi",item.deskripsi)        items.put("lokasi",item.lokasi)        items.put("lokasi_detail",item.lokasi_detail)        items.put("status_perbaikan",item.status_perbaikan)        items.put("user_input",item.user_input)        items.put("time_input",item.time_input)        items.put("status",item.status)        items.put("nama_lengkap",item.nama_lengkap)        items.put("lokasiHazard",item.lokasiHazard)        items.put("tglInput",item.tgl_input)        val hasil = sqlDatabase?.update("$tbItem",items,"idHazard = ?", arrayOf("${idKemungkinan}"))        if(hasil!! < 0){            return false        }        closeAccess()        return true    }    private fun createCV(item : HazardHeaderModel): ContentValues {        var cv = ContentValues()        cv.put("idHazard",item.idHazard)        cv.put("uid",item.uid)        cv.put("perusahaan",item.perusahaan)        cv.put("tgl_hazard",item.tgl_hazard)        cv.put("jam_hazard",item.jam_hazard)        cv.put("idKemungkinan",item.idKemungkinan)        cv.put("idKeparahan",item.idKeparahan)        cv.put("deskripsi",item.deskripsi)        cv.put("lokasi",item.lokasi)        cv.put("lokasiHazard",item.lokasiHazard)        cv.put("lokasi_detail",item.lokasi_detail)        cv.put("status_perbaikan",item.status_perbaikan)        cv.put("user_input",item.user_input)        cv.put("time_input",item.time_input)        cv.put("status",item.status)        cv.put("nama_lengkap",item.nama_lengkap)        cv.put("tglInput",item.tgl_input)        return cv    }    companion object{        val tbItem = "HAZARD_HEADER_OFFLINE"    }}
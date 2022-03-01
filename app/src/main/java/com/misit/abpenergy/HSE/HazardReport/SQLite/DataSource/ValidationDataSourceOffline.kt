package com.misit.abpenergy.HSE.HazardReport.SQLite.DataSourceimport android.content.ContentValuesimport android.content.Contextimport android.database.Cursorimport android.database.sqlite.SQLiteDatabaseimport android.util.Logimport androidx.core.database.getIntOrNullimport com.misit.abpenergy.HSE.HazardReport.SQLite.Model.HazardValidationModelimport com.misit.abpenergy.SQLite.DbHelperimport es.dmoral.toasty.Toastyclass ValidationDataSourceOffline(val c: Context) {    var dbHelper : DbHelper    var sqlDatabase : SQLiteDatabase?=null    var listItem :ArrayList<HazardValidationModel>?=null    init {        listItem = ArrayList()        dbHelper = DbHelper(c)    }    fun openAccess(){        sqlDatabase = dbHelper.writableDatabase    }    fun closeAccess(){        sqlDatabase?.close()        dbHelper?.close()    }    fun insertItem(item: HazardValidationModel):Long{        var cv = createCV(item)        var hasil = sqlDatabase?.insertOrThrow("$tbItem",null,cv)        return hasil!!    }    fun cekHeader(): Int {        openAccess()        val c = sqlDatabase?.rawQuery("SELECT count(*) FROM "+                "${tbItem} ",null)        c?.let {            if(it.moveToFirst()){                return it?.getIntOrNull(0) ?: 0            }        }        c?.close()        closeAccess()        return 0    }    suspend fun getItem(idValidation: String): HazardValidationModel {        openAccess()        val c = sqlDatabase?.rawQuery("SELECT * FROM "+                "$tbItem WHERE idValidation = ?", arrayOf(idValidation))        c?.moveToFirst()        var itemModels = HazardValidationModel()        c?.let {            itemModels = fetchRow(it)        }        c?.close()        closeAccess()        return itemModels    }    fun getAll(): ArrayList<HazardValidationModel> {        val listItem : ArrayList<HazardValidationModel> = ArrayList()        openAccess()        val c = sqlDatabase?.rawQuery("SELECT * FROM "+                "$tbItem ",null)        if(c!!.moveToFirst()){            do {                listItem?.add(fetchRow(c))            }while (c.moveToNext())        }        c?.close()        closeAccess()        return listItem!!    }    private fun fetchRow(cursor: Cursor): HazardValidationModel {        val idValidation = cursor.getInt(cursor.getColumnIndex("idValidation"))        val user_valid = cursor.getString(cursor.getColumnIndex("user_valid"))        val tgl_valid = cursor.getString(cursor.getColumnIndex("tgl_valid"))        val keterangan_admin = cursor.getString(cursor.getColumnIndex("keterangan_admin"))        val jam_valid = cursor.getString(cursor.getColumnIndex("jam_valid"))        val option_flag = cursor.getInt(cursor.getColumnIndex("option_flag"))        val hazardValidationModel = HazardValidationModel()        hazardValidationModel.idValidation = idValidation        hazardValidationModel.user_valid = user_valid        hazardValidationModel.tgl_valid = tgl_valid        hazardValidationModel.jam_valid = jam_valid        hazardValidationModel.option_flag = option_flag        hazardValidationModel.keterangan_admin = keterangan_admin        return hazardValidationModel    }    fun deleteItem(item:Int):Boolean{        openAccess()        val hasil = sqlDatabase?.delete("$tbItem","idValidation = ? ", arrayOf(item.toString()))        if(hasil!! <0 ){            Toasty.error(c!!,"Gagal Hapus").show()            Log.d("ServiceJob","Gagal Hapus")            return false        }else{            Log.d("ServiceJob","Berhasil Hapus")            Toasty.success(c!!,"Hapus Berhasil").show()        }        closeAccess()        return true    }    fun deleteAll():Boolean{        try {            val hasil =  sqlDatabase!!.delete("$tbItem",null,null)            if(hasil <0 ){                return false            }else{                return true            }        }catch (e:Exception){            return false        }    }    fun updateItem(item: HazardValidationModel, idValidation:Int):Boolean{        openAccess()        val items = ContentValues()        items.put("idValidation",item.idValidation)        items.put("uid",item.uid)        items.put("user_valid",item.user_valid)        items.put("tgl_valid",item.tgl_valid)        items.put("jam_valid",item.jam_valid)        items.put("option_flag",item.option_flag)        items.put("keterangan_admin",item.keterangan_admin)        val hasil = sqlDatabase?.update("$tbItem",items,"idValidation = ?", arrayOf("${idValidation}"))        if(hasil!! < 0){            return false        }        closeAccess()        return true    }    private fun createCV(item : HazardValidationModel): ContentValues {        var items = ContentValues()        items.put("idValidation",item.idValidation)        items.put("uid",item.uid)        items.put("user_valid",item.user_valid)        items.put("tgl_valid",item.tgl_valid)        items.put("jam_valid",item.jam_valid)        items.put("option_flag",item.option_flag)        items.put("keterangan_admin",item.keterangan_admin)        return items    }    companion object{        val tbItem = "HAZARD_VALIDATION_OFFLINE"    }}
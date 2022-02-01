package com.misit.abpenergy.Main.DataSourceimport android.content.ContentValuesimport android.content.Contextimport android.database.Cursorimport android.database.sqlite.SQLiteDatabaseimport androidx.core.database.getIntOrNullimport com.misit.abpenergy.Main.Model.DataUsersModelimport com.misit.abpenergy.SQLite.DbHelperimport es.dmoral.toasty.Toastyclass DataUsersSource(val c: Context) {    var dbHelper : DbHelper    var sqlDatabase : SQLiteDatabase?=null    var listItem :ArrayList<DataUsersModel>?=null    init {        listItem = ArrayList()        dbHelper = DbHelper(c)    }    fun openAccess(){        sqlDatabase = dbHelper.writableDatabase    }    fun closeAccess(){        sqlDatabase?.close()        dbHelper?.close()    }    fun insertItem(item: DataUsersModel):Long{        openAccess()        var cv = createCV(item)        var hasil = sqlDatabase?.insertOrThrow("${tbItem}",null,cv)        closeAccess()        return hasil!!    }    fun getItem(idUser: String): DataUsersModel? {        var item= DataUsersModel()        openAccess()        sqlDatabase!!.rawQuery("SELECT * FROM "+                "${tbItem} WHERE nik = '"+idUser+"'",null).use {            if(it.moveToFirst()){                item = fetchRow(it)                return item            }            it.close()        }        closeAccess()        return null    }    fun cekUser(username: String,user_update:String): Int {        openAccess()        val c = sqlDatabase?.rawQuery("SELECT count(*) FROM "+                "${tbItem} WHERE username = '"+username+"' and data_user_update = '"+user_update+"'",null)        c?.let {            if(it.moveToFirst()){                return it?.getIntOrNull(0) ?: 0            }        }        c?.close()        closeAccess()        return 0    }    fun searchItems(cari: String?): ArrayList<DataUsersModel> {        openAccess()        val c = sqlDatabase?.rawQuery("SELECT * FROM "+                "${tbItem} WHERE namaLengkap like '%"+cari+"%' or namaPerusahaan like '%"+cari+"%' or nik like '%"+cari+"%' or sect like '%"+cari+"%' or dept like '%"+cari+"%'",            null)        if(c!!.moveToFirst()){            do {                listItem?.add(fetchRow(c))            }while (c.moveToNext())        }        c?.close()        closeAccess()        return listItem!!    }    fun getAll(): ArrayList<DataUsersModel> {        openAccess()        val c = sqlDatabase?.rawQuery("SELECT * FROM "+                "${tbItem}",null)        if(c!!.moveToFirst()){            do {                listItem?.add(fetchRow(c))            }while (c.moveToNext())        }        c?.close()        closeAccess()        return listItem!!    }    private fun fetchRow(cursor: Cursor): DataUsersModel {        val compString = cursor.getInt(cursor.getColumnIndex("compString"))        val department = cursor.getString(cursor.getColumnIndex("department"))        val dept = cursor.getString(cursor.getColumnIndex("dept"))        val email = cursor.getString(cursor.getColumnIndex("email"))        val flag = cursor.getInt(cursor.getColumnIndex("flag"))        val id_dept = cursor.getString(cursor.getColumnIndex("id_dept"))        val id_perusahaan = cursor.getInt(cursor.getColumnIndex("id_perusahaan"))        val id_sect = cursor.getString(cursor.getColumnIndex("id_sect"))        val id_session = cursor.getString(cursor.getColumnIndex("id_session"))        val id_user = cursor.getInt(cursor.getColumnIndex("id_user"))        val id = cursor.getInt(cursor.getColumnIndex("id"))        val level = cursor.getString(cursor.getColumnIndex("level"))        val nama_lengkap = cursor.getString(cursor.getColumnIndex("nama_lengkap"))        val nama_perusahaan = cursor.getString(cursor.getColumnIndex("nama_perusahaan"))        val nik = cursor.getString(cursor.getColumnIndex("nik"))        val password = cursor.getString(cursor.getColumnIndex("password"))        val perusahaan = cursor.getInt(cursor.getColumnIndex("perusahaan"))        val photo_profile = cursor.getString(cursor.getColumnIndex("photo_profile"))        val rule = cursor.getString(cursor.getColumnIndex("rule"))        val sect = cursor.getString(cursor.getColumnIndex("sect"))        val section = cursor.getString(cursor.getColumnIndex("section"))        val status = cursor.getInt(cursor.getColumnIndex("status"))        val tglentry = cursor.getString(cursor.getColumnIndex("tglentry"))        val time_in = cursor.getString(cursor.getColumnIndex("time_in"))        val timelog = cursor.getString(cursor.getColumnIndex("timelog"))        val ttd = cursor.getString(cursor.getColumnIndex("ttd"))        val user_entry = cursor.getString(cursor.getColumnIndex("user_entry"))        val username = cursor.getString(cursor.getColumnIndex("username"))        val dataHazard = cursor.getInt(cursor.getColumnIndex("dataHazard"))        val dataInspeksi = cursor.getInt(cursor.getColumnIndex("dataInspeksi"))        val data_user_update = cursor.getString(cursor.getColumnIndex("data_user_update"))        val offline_profile = cursor.getString(cursor.getColumnIndex("offline_profile"))        val usersModel = DataUsersModel()        usersModel.compString = compString        usersModel.department = department        usersModel.dept = dept        usersModel.email = email        usersModel.flag = flag        usersModel.id_dept = id_dept        usersModel.id_perusahaan = id_perusahaan        usersModel.nama_lengkap = nama_lengkap        usersModel.id_sect = id_sect        usersModel.id_session = id_session        usersModel.id_user = id_user        usersModel.id = id        usersModel.level = level        usersModel.nama_perusahaan = nama_perusahaan        usersModel.nik = nik        usersModel.password = password        usersModel.perusahaan = perusahaan        usersModel.photo_profile = photo_profile        usersModel.rule = rule        usersModel.sect = sect        usersModel.section = section        usersModel.status = status        usersModel.tglentry = tglentry        usersModel.time_in = time_in        usersModel.timelog = timelog        usersModel.ttd = ttd        usersModel.user_entry = user_entry        usersModel.username = username        usersModel.dataHazard = dataHazard        usersModel.dataInspeksi = dataInspeksi        usersModel.user_update = data_user_update        usersModel.offline_profile = offline_profile        return usersModel    }    fun deleteItem(item:Int){        openAccess()        val hasil = sqlDatabase?.delete("${tbItem}","idUser = ? ", arrayOf(item.toString()))        if(hasil!! <0 ){            Toasty.error(c!!,"Gagal Hapus").show()        }else{            Toasty.success(c!!,"Hapus Berhasil").show()        }        closeAccess()    }    fun deleteAll():Boolean{        openAccess()        val hasil = sqlDatabase?.delete("${tbItem}", null,null)        if(hasil!! < 0 ){            return false        }        closeAccess()        return true    }    fun deleteItem(username: String):Boolean{        val hasil = sqlDatabase?.delete("${tbItem}","username = ? ", arrayOf(username))        if(hasil!! <0 ){            return false        }        return true    }    fun updateItem(item: DataUsersModel, idUser:Int):Boolean{        openAccess()        val cv = ContentValues()        cv.put("compString",item.compString)        cv.put("department",item.department)        cv.put("dept",item.dept)        cv.put("email",item.email)        cv.put("flag",item.flag)        cv.put("id_dept",item.id_dept)        cv.put("id_perusahaan",item.id_perusahaan)        cv.put("id_sect",item.id_sect)        cv.put("id_session",item.id_session)        cv.put("id_user",item.id_user)        cv.put("id",item.id)        cv.put("level",item.level)        cv.put("nama_lengkap",item.nama_lengkap)        cv.put("nama_perusahaan",item.nama_perusahaan)        cv.put("nik",item.nik)        cv.put("password",item.password)        cv.put("perusahaan",item.perusahaan)        cv.put("photo_profile",item.photo_profile)        cv.put("rule",item.rule)        cv.put("sect",item.sect)        cv.put("section",item.section)        cv.put("status",item.status)        cv.put("tglentry",item.tglentry)        cv.put("time_in",item.time_in)        cv.put("timelog",item.timelog)        cv.put("ttd",item.ttd)        cv.put("user_entry",item.user_entry)        cv.put("username",item.username)        cv.put("dataHazard",item.dataHazard)        cv.put("dataInspeksi",item.dataInspeksi)        cv.put("data_user_update",item.user_update)        cv.put("offline_profile",item.offline_profile)        val hasil = sqlDatabase?.update("${tbItem}",cv,"idUser = ?", arrayOf("${idUser}"))        if(hasil!! < 0){            return false        }        closeAccess()        return true    }    private fun createCV(item : DataUsersModel): ContentValues {        var cv = ContentValues()        cv.put("id",item.id)        cv.put("compString",item.compString)        cv.put("department",item.department)        cv.put("dept",item.dept)        cv.put("email",item.email)        cv.put("flag",item.flag)        cv.put("id_dept",item.id_dept)        cv.put("id_perusahaan",item.id_perusahaan)        cv.put("id_sect",item.id_sect)        cv.put("id_session",item.id_session)        cv.put("id_user",item.id_user)        cv.put("id",item.id)        cv.put("level",item.level)        cv.put("nama_lengkap",item.nama_lengkap)        cv.put("nama_perusahaan",item.nama_perusahaan)        cv.put("nik",item.nik)        cv.put("password",item.password)        cv.put("perusahaan",item.perusahaan)        cv.put("photo_profile",item.photo_profile)        cv.put("rule",item.rule)        cv.put("sect",item.sect)        cv.put("section",item.section)        cv.put("status",item.status)        cv.put("tglentry",item.tglentry)        cv.put("time_in",item.time_in)        cv.put("timelog",item.timelog)        cv.put("ttd",item.ttd)        cv.put("user_entry",item.user_entry)        cv.put("username",item.username)        cv.put("dataHazard",item.dataHazard)        cv.put("dataInspeksi",item.dataInspeksi)        cv.put("data_user_update",item.user_update)        cv.put("offline_profile",item.offline_profile)        return cv    }    companion object{        val tbItem = "DATAUSER"    }}
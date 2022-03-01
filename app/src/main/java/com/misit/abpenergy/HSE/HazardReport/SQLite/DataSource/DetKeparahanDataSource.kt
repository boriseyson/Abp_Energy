package com.misit.abpenergy.HSE.HazardReport.SQLite.DataSourceimport android.content.ContentValuesimport android.content.Contextimport android.database.Cursorimport android.database.sqlite.SQLiteDatabaseimport com.misit.abpenergy.HSE.HazardReport.SQLite.Model.DetKeparahanimport com.misit.abpenergy.SQLite.DbHelperimport es.dmoral.toasty.Toastyclass DetKeparahanDataSource(val c: Context) {    var dbHelper : DbHelper    var sqlDatabase : SQLiteDatabase?=null    init {        dbHelper = DbHelper(c)    }    private fun openAccess(){        sqlDatabase = dbHelper.writableDatabase    }    private fun closeAccess(){        sqlDatabase?.close()        dbHelper?.close()    }    fun insertItem(item: DetKeparahan):Long{        openAccess()        var cv = createCV(item)        var hasil = sqlDatabase?.insertOrThrow("$tbItem",null,cv)        closeAccess()        return hasil!!    }    fun getItem(idKeparahan: String): DetKeparahan {        openAccess()        val c = sqlDatabase?.rawQuery("SELECT * FROM "+                "$tbItem WHERE idKeparahan = ?", arrayOf(idKeparahan))        c?.moveToFirst()        var itemModels = DetKeparahan()        c?.let {            itemModels = fetchRow(it)        }        c?.close()        closeAccess()        return itemModels    }    fun getAll(): ArrayList<DetKeparahan> {        val listItem : ArrayList<DetKeparahan> = ArrayList()        openAccess()        val c = sqlDatabase?.rawQuery("SELECT * FROM "+                "$tbItem ",null)        if(c!!.moveToFirst()){            do {                listItem?.add(fetchRow(c))            }while (c.moveToNext())        }        c?.close()        closeAccess()        return listItem!!    }    fun getBy(idKeparahan: String): ArrayList<DetKeparahan> {        val listItem : ArrayList<DetKeparahan> = ArrayList()        openAccess()        val c = sqlDatabase?.rawQuery("SELECT * FROM "+                "$tbItem WHERE idKeparahan = ?", arrayOf(idKeparahan))        if(c!!.moveToFirst()){            do {                listItem?.add(fetchRow(c))            }while (c.moveToNext())        }        c?.close()        closeAccess()        return listItem!!    }    private fun fetchRow(cursor: Cursor): DetKeparahan {        val id_det = cursor.getInt(cursor.getColumnIndex("id_det"))        val idKeparahan = cursor.getString(cursor.getColumnIndex("idKeparahan"))        val keterangan = cursor.getString(cursor.getColumnIndex("keterangan"))        val ket_input = cursor.getString(cursor.getColumnIndex("ket_input"))        val time_input = cursor.getString(cursor.getColumnIndex("time_input"))        val detHirarki = DetKeparahan()        detHirarki.id_det = id_det        detHirarki.idKeparahan = idKeparahan        detHirarki.keterangan = keterangan        detHirarki.ket_input = ket_input        detHirarki.time_input = time_input        return detHirarki    }    fun deleteItem(item:Int){        openAccess()        val hasil = sqlDatabase?.delete("$tbItem","id_det = ? ", arrayOf(item.toString()))        if(hasil!! <0 ){            Toasty.error(c!!,"Gagal Hapus").show()        }else{            Toasty.success(c!!,"Hapus Berhasil").show()        }        closeAccess()    }    fun deleteAll():Boolean{        openAccess()        val hasil = sqlDatabase?.delete("$tbItem",null,null)        if(hasil!! <0 ){            return false        }        closeAccess()        return true    }    fun updateItem(item: DetKeparahan, id_det:Int):Boolean{        openAccess()        val items = ContentValues()        items.put("idKeparahan",item.idKeparahan)        items.put("keterangan",item.keterangan)        items.put("ket_input",item.ket_input)        items.put("time_input",item.time_input)        val hasil = sqlDatabase?.update("$tbItem",items,"id_det = ?",            arrayOf("${id_det}"))        if(hasil!! < 0){            return false        }        closeAccess()        return true    }    private fun createCV(item : DetKeparahan): ContentValues {        var items = ContentValues()        items.put("idKeparahan",item.idKeparahan)        items.put("keterangan",item.keterangan)        items.put("ket_input",item.ket_input)        items.put("time_input",item.time_input)        return items    }    companion object{        val tbItem = "DET_KEPARAHAN"    }}
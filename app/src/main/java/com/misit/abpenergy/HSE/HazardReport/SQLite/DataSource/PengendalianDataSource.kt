package com.misit.abpenergy.HSE.HazardReport.SQLite.DataSourceimport android.content.ContentValuesimport android.content.Contextimport android.database.Cursorimport android.database.sqlite.SQLiteDatabaseimport com.misit.abpenergy.HSE.HazardReport.SQLite.Model.PengendalianFullModelimport com.misit.abpenergy.HSE.HazardReport.SQLite.Model.PengendalianModelimport com.misit.abpenergy.SQLite.DbHelperimport es.dmoral.toasty.Toastyclass PengendalianDataSource(val c: Context) {    var dbHelper : DbHelper    var sqlDatabase : SQLiteDatabase?=null    var listItem :ArrayList<PengendalianModel>?=null    init {        listItem = ArrayList()        dbHelper = DbHelper(c)    }    private fun openAccess(){        sqlDatabase = dbHelper.writableDatabase    }    private fun closeAccess(){        sqlDatabase?.close()        dbHelper?.close()    }    fun insertItem(item: PengendalianModel):Long{        openAccess()        var cv = createCV(item)        var hasil = sqlDatabase?.insertOrThrow("$tbItem",null,cv)        closeAccess()        return hasil!!    }    fun getItem(idHirarki: String): PengendalianModel {        openAccess()        val c = sqlDatabase?.rawQuery("SELECT * FROM "+                "$tbItem WHERE idHirarki = ?", arrayOf(idHirarki))        c?.moveToFirst()        var itemModels = PengendalianModel()        c?.let {            itemModels = fetchRow(it)        }        c?.close()        closeAccess()        return itemModels    }    fun getAll(): ArrayList<PengendalianModel> {        val listItem : ArrayList<PengendalianModel> = ArrayList()        openAccess()        val c = sqlDatabase?.rawQuery("SELECT * FROM "+                "$tbItem ",null)        if(c!!.moveToFirst()){            do {                listItem?.add(fetchRow(c))            }while (c.moveToNext())        }        c?.close()        closeAccess()        return listItem!!    }    fun getJoin(): ArrayList<PengendalianFullModel> {        val listItem : ArrayList<PengendalianFullModel> = ArrayList()        openAccess()        val c = sqlDatabase?.rawQuery("SELECT * FROM "+                "$tbItem ",null)        if(c!!.moveToFirst()){            do {                listItem?.add(fetchJoinRow(c))            }while (c.moveToNext())        }        c?.close()        closeAccess()        return listItem!!    }    private fun fetchRow(cursor: Cursor): PengendalianModel {        val idHirarki = cursor.getInt(cursor.getColumnIndex("idHirarki"))        val namaPengendalian = cursor.getString(cursor.getColumnIndex("namaPengendalian"))        val userInput = cursor.getString(cursor.getColumnIndex("userInput"))        val flag = cursor.getInt(cursor.getColumnIndex("flag"))        val tglInput = cursor.getString(cursor.getColumnIndex("tglInput"))        val pengendalianModel = PengendalianModel()        pengendalianModel.idHirarki = idHirarki        pengendalianModel.namaPengendalian = namaPengendalian        pengendalianModel.userInput = userInput        pengendalianModel.flag = flag        pengendalianModel.tglInput = tglInput        return pengendalianModel    }    private fun fetchJoinRow(cursor: Cursor): PengendalianFullModel {        val idHirarki = cursor.getInt(cursor.getColumnIndex("idHirarki"))        val namaPengendalian = cursor.getString(cursor.getColumnIndex("namaPengendalian"))        val userInput = cursor.getString(cursor.getColumnIndex("userInput"))        val flag = cursor.getInt(cursor.getColumnIndex("flag"))        val tglInput = cursor.getString(cursor.getColumnIndex("tglInput"))        val pengendalianModel = PengendalianFullModel()        pengendalianModel.idHirarki = idHirarki        pengendalianModel.namaPengendalian = namaPengendalian        pengendalianModel.userInput = userInput        pengendalianModel.flag = flag        pengendalianModel.tglInput = tglInput//        pengendalianModel.listKet =        return pengendalianModel    }    fun deleteItem(item:Int){        openAccess()        val hasil = sqlDatabase?.delete("$tbItem","idHirarki = ? ", arrayOf(item.toString()))        if(hasil!! <0 ){            Toasty.error(c!!,"Gagal Hapus").show()        }else{            Toasty.success(c!!,"Hapus Berhasil").show()        }        closeAccess()    }    fun deleteAll():Boolean{        openAccess()        val hasil = sqlDatabase?.delete("$tbItem",null,null)        if(hasil!! <0 ){            return false        }        closeAccess()        return true    }    fun updateItem(item: PengendalianModel, idHazard:Int):Boolean{        openAccess()        val items = ContentValues()        items.put("idHirarki",item.idHirarki)        items.put("namaPengendalian",item.namaPengendalian)        items.put("userInput",item.userInput)        items.put("flag",item.flag)        items.put("tglInput",item.tglInput)        val hasil = sqlDatabase?.update("$tbItem",items,"idHirarki = ?",            arrayOf("${idHazard}"))        if(hasil!! < 0){            return false        }        closeAccess()        return true    }    private fun createCV(item : PengendalianModel): ContentValues {        var items = ContentValues()        items.put("idHirarki",item.idHirarki)        items.put("namaPengendalian",item.namaPengendalian)        items.put("userInput",item.userInput)        items.put("flag",item.flag)        items.put("tglInput",item.tglInput)        return items    }    companion object{        val tbItem = "PENGENDALIAN"    }}
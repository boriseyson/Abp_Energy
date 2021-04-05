package com.misit.abpenergy.Sarpras.Realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class PenumpangModel(): RealmObject() {
    @PrimaryKey
    var id:Long=0
    var nik:String?=null
    var nama:String?=null
    var jabatan:String?=null
}
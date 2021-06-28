package com.misit.abpenergy.Sarpras.SaranaResponse

import android.util.Log
import com.google.gson.annotations.SerializedName

data class PenumpangListModel(
    @field:SerializedName("id")
    var id: Long? = null,
    @field:SerializedName("nik")
    var nik: String? = null,
    @field:SerializedName("nama")
    var nama: String? = null,
    @field:SerializedName("jabatan")
    var jabatan: String? = null
)
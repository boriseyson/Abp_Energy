package com.misit.abpenergy.HGE.Sarpras.SaranaResponse

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class PenumpangListModel(
    @field:SerializedName("id")
    var id: Long? = null,
    @field:SerializedName("nik")
    var nik: String? = null,
    @field:SerializedName("nama")
    var nama: String? = null,
    @field:SerializedName("jabatan")
    var jabatan: String? = null,
    @field:SerializedName("penumpang_update")
    var penumpang_update: String? = null
)
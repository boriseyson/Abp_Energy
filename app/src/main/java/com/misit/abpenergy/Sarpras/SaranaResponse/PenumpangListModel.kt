package com.misit.abpenergy.Sarpras.SaranaResponse

import com.google.gson.annotations.SerializedName

data class PenumpangListModel(
    @field:SerializedName("id")
    var id: Int? = null,
    @field:SerializedName("nik")
    var nik: String? = null,
    @field:SerializedName("nama")
    var nama: String? = null,
    @field:SerializedName("jabatan")
    var jabatan: String? = null
)
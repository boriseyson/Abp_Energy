package com.misit.abpenergy.Sarpras.SarprasResponse

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DataPenumpang(
    @field:SerializedName("nik")
    var nik: String? = null,
    @field:SerializedName("nama")
    var nama: String? = null,
    @field:SerializedName("jabatan")
    var jabatan: String? = null
)
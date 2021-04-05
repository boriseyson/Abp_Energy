package com.misit.abpenergy.Sarpras.SaranaResponse

import com.google.gson.annotations.SerializedName

data class ListPenumpang(
    @field:SerializedName("karyawan")
    var karyawan: List<PenumpangListModel>? = null

)
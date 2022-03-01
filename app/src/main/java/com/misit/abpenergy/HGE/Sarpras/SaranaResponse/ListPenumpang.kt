package com.misit.abpenergy.HGE.Sarpras.SaranaResponse

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ListPenumpang(
    @field:SerializedName("karyawan")
    var karyawan: List<PenumpangListModel>? = null

)
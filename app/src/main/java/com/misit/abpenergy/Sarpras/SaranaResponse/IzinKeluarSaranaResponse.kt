package com.misit.abpenergy.Sarpras.SaranaResponse

import com.google.gson.annotations.SerializedName

data class IzinKeluarSaranaResponse(

    @field:SerializedName("success")
    var success: Boolean? = false
)
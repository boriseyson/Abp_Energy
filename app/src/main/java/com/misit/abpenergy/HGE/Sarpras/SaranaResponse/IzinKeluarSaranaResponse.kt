package com.misit.abpenergy.HGE.Sarpras.SaranaResponse

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class IzinKeluarSaranaResponse(

    @field:SerializedName("success")
    var success: Boolean? = false
)
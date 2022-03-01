package com.misit.abpenergy.HGE.Rkb.Response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CancelRKBResponse(

    @field:SerializedName("success")
    val success: Boolean? = null
)
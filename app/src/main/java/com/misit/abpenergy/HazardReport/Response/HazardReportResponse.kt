package com.misit.abpenergy.HazardReport.Response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class HazardReportResponse(

    @field:SerializedName("success")
    var success: Boolean? = false
)
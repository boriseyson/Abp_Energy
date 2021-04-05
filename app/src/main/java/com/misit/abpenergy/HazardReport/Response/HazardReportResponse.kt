package com.misit.abpenergy.HazardReport.Response

import com.google.gson.annotations.SerializedName

data class HazardReportResponse(

    @field:SerializedName("success")
    var success: Boolean? = false
)
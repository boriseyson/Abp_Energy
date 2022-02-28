package com.misit.abpenergy.HazardReport.Response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class SumberBahayaResponse(

	@field:SerializedName("sumber")
	var sumber: List<SumberItem>? = null
)
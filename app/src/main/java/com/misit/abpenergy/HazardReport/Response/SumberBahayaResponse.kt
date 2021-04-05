package com.misit.abpenergy.HazardReport.Response

import com.google.gson.annotations.SerializedName

data class SumberBahayaResponse(

	@field:SerializedName("sumber")
	var sumber: List<SumberItem>? = null
)
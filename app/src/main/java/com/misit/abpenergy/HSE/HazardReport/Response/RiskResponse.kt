package com.misit.abpenergy.HSE.HazardReport.Response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RiskResponse(

	@field:SerializedName("risk")
	var risk: List<RiskItem>? = null
)

@Keep
data class RiskItem(

	@field:SerializedName("tgl_input")
	var tglInput: String? = null,

	@field:SerializedName("desc_risk")
	var descRisk: String? = null,

	@field:SerializedName("bgColor")
	var bgColor: String? = null,

	@field:SerializedName("user_input")
	var userInput: String? = null,

	@field:SerializedName("txtColor")
	var txtColor: String? = null,

	@field:SerializedName("risk")
	var risk: String? = null,

	@field:SerializedName("idRisk")
	var idRisk: Int? = null
)

package com.misit.abpenergy.HazardReport.Response

import com.google.gson.annotations.SerializedName

data class RiskResponse(

	@field:SerializedName("risk")
	var risk: List<RiskItem>? = null
)

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

package com.misit.abpenergy.HazardReport.Response

import com.google.gson.annotations.SerializedName

data class SumberItem(

	@field:SerializedName("bahaya")
	var bahaya: String? = null,

	@field:SerializedName("flag")
	var flag: Int? = null,

	@field:SerializedName("user_input")
	var userInput: String? = null,

	@field:SerializedName("idBahaya")
	var idBahaya: Int? = null,

	@field:SerializedName("time_input")
	var timeInput: String? = null
)
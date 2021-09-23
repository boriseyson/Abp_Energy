package com.misit.abpenergy.HazardReport.Response

import com.google.gson.annotations.SerializedName

data class HirarkiResponse(

	@field:SerializedName("hirarki")
	var hirarki: List<HirarkiItem>? = null
)

data class HirarkiItem(

	@field:SerializedName("tgl_input")
	var tglInput: String? = null,

	@field:SerializedName("flag")
	var flag: Int? = null,

	@field:SerializedName("userInput")
	var userInput: String? = null,

	@field:SerializedName("namaPengendalian")
	var namaPengendalian: String? = null,

	@field:SerializedName("idHirarki")
	var idHirarki: Int? = null
)

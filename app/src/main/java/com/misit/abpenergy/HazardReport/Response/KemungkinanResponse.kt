package com.misit.abpenergy.HazardReport.Response

import com.google.gson.annotations.SerializedName

data class KemungkinanResponse(

	@field:SerializedName("kemungkinan")
	var kemungkinan: List<KemungkinanItem>? = null
)

data class KemungkinanItem(

	@field:SerializedName("kemungkinan")
	var kemungkinan: String? = null,

	@field:SerializedName("flag")
	var flag: Int? = null,

	@field:SerializedName("nilai")
	var nilai: Int? = null,

	@field:SerializedName("idKemungkinan")
	var idKemungkinan: Int? = null
)

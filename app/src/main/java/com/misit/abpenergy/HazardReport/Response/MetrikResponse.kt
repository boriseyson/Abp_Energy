package com.misit.abpenergy.HazardReport.Response

import com.google.gson.annotations.SerializedName

data class MetrikResponse(

	@field:SerializedName("metrikResiko")
	var metrikResiko: List<MetrikResikoItem?>? = null
)

data class MetrikResikoItem(

	@field:SerializedName("min")
	var min: Int? = null,

	@field:SerializedName("flag")
	var flag: Int? = null,

	@field:SerializedName("bgColor")
	var bgColor: String? = null,

	@field:SerializedName("idResiko")
	var idResiko: Int? = null,

	@field:SerializedName("kodeBahaya")
	var kodeBahaya: String? = null,

	@field:SerializedName("max")
	var max: Int? = null,

	@field:SerializedName("tindakan")
	var tindakan: String? = null,

	@field:SerializedName("txtColor")
	var txtColor: String? = null,

	@field:SerializedName("kategori")
	var kategori: String? = null,

	@field:SerializedName("batas")
	var batas: Int? = null
)

package com.misit.abpenergy.HazardReport.Response

import com.google.gson.annotations.SerializedName

data class KeparahanResponse(

	@field:SerializedName("keparahan")
	var keparahan: List<KeparahanItem>? = null
)

data class KeparahanItem(

	@field:SerializedName("flag")
	var flag: Int? = null,

	@field:SerializedName("idKeparahan")
	var idKeparahan: Int? = null,

	@field:SerializedName("nilai")
	var nilai: Int? = null,

	@field:SerializedName("keparahan")
	var keparahan: String? = null
)
data class KeparahanItemFull(

	@field:SerializedName("flag")
	var flag: Int? = null,

	@field:SerializedName("idKeparahan")
	var idKeparahan: Int? = null,

	@field:SerializedName("nilai")
	var nilai: Int? = null,

	@field:SerializedName("keparahan")
	var keparahan: String? = null,

	@field:SerializedName("listKet")
	var listKet: MutableList<DetKeparahanItem>? = null
)

package com.misit.abpenergy.HazardReport.Response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class KemungkinanResponse(

	@field:SerializedName("kemungkinan")
	var kemungkinan: List<KemungkinanItem>? = null
)

@Keep
data class KemungkinanItem(

	@field:SerializedName("kemungkinan")
	var kemungkinan: String? = null,

	@field:SerializedName("flag")
	var flag: Int? = null,

	@field:SerializedName("nilai")
	var nilai: Int? = null,

	@field:SerializedName("idKemungkinan")
	var idKemungkinan: Int? = null,

	@field:SerializedName("kemungkinan_update")
	var kemungkinan_update: String? = null,

	@field:SerializedName("keterangan")
	var keterangan: String? = null

)

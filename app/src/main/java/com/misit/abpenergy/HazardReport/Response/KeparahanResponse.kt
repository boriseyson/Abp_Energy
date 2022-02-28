package com.misit.abpenergy.HazardReport.Response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class KeparahanResponse(

	@field:SerializedName("keparahan")
	var keparahan: List<KeparahanItem>? = null
)

@Keep
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
@Keep
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

package com.misit.abpenergy.HazardReport.Response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class LokasiResponse(

	@field:SerializedName("lokasi")
	var lokasi: List<LokasiItem>? = null
)

@Keep
data class LokasiItem(

	@field:SerializedName("tgl_input")
	var tglInput: String? = null,

	@field:SerializedName("userInput")
	var userInput: String? = null,

	@field:SerializedName("lokasi")
	var lokasi: String? = null,

	@field:SerializedName("idLok")
	var idLok: Int? = null
)

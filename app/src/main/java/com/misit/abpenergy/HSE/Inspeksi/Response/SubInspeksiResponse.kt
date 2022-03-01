package com.misit.abpenergy.HSE.Inspeksi.Response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class SubInspeksiResponse(

	@field:SerializedName("dataSub")
	var dataSub: List<DataSubItem>? = null
)

@Keep
data class DataSubItem(

	@field:SerializedName("idForm")
	var idForm: Int? = null,

	@field:SerializedName("tgl_input")
	var tglInput: String? = null,

	@field:SerializedName("numSub")
	var numSub: String? = null,

	@field:SerializedName("idSub")
	var idSub: Int? = null,

	@field:SerializedName("flag")
	var flag: Int? = null,

	@field:SerializedName("user_input")
	var userInput: String? = null,

	@field:SerializedName("nameSub")
	var nameSub: String? = null
)

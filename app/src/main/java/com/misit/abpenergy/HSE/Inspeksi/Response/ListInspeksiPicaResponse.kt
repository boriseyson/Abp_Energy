package com.misit.abpenergy.HSE.Inspeksi.Response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ListInspeksiPicaResponse(

	@field:SerializedName("inspeksiPica")
	val inspeksiPica: List<InspeksiPicaItem>? = null
)

@Keep
data class InspeksiPicaItem(

	@field:SerializedName("idForm")
	val idForm: Int? = null,

	@field:SerializedName("idPica")
	val idPica: Int? = null,

	@field:SerializedName("temuan")
	val temuan: String? = null,

	@field:SerializedName("buktiTemuan")
	val buktiTemuan: Any? = null,

	@field:SerializedName("nikPJ")
	val nikPJ: String? = null,

	@field:SerializedName("tglTenggat")
	val tglTenggat: String? = null,

	@field:SerializedName("idTemp")
	val idTemp: String? = null,

	@field:SerializedName("namaPJ")
	val namaPJ: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

package com.misit.abpenergy.Inspeksi.Response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class InspeksiPicaDetailResponse(

	@field:SerializedName("inspeksiPicaDetail")
	var inspeksiPicaDetail: List<InspeksiPicaDetailItem>? = null
)

@Keep
data class InspeksiPicaDetailItem(

	@field:SerializedName("picaTemuan")
	var picaTemuan: String? = null,

	@field:SerializedName("picaSesudah")
	var picaSesudah: String? = null,

	@field:SerializedName("picaTenggat")
	var picaTenggat: String? = null,

	@field:SerializedName("picaSebelum")
	var picaSebelum: String? = null,

	@field:SerializedName("picaNikPJ")
	var picaNikPJ: String? = null,

	@field:SerializedName("picaTindakan")
	var picaTindakan: String? = null,

	@field:SerializedName("idPika")
	var idPika: Int? = null,

	@field:SerializedName("idInspeksi")
	var idInspeksi: String? = null,

	@field:SerializedName("picaNamaPJ")
	var picaNamaPJ: String? = null,

	@field:SerializedName("status")
	var status: String? = null
)

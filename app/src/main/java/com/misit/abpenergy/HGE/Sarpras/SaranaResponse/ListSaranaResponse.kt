package com.misit.abpenergy.HGE.Sarpras.SaranaResponse

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ListSaranaResponse(

	@field:SerializedName("data")
	var data: List<DataItem>? = null,

	@field:SerializedName("karyawan")
	var karyawan: List<KaryawanItem>? = null,
	@field:SerializedName("awalBulan")
	var awalBulan: String? = null,
	@field:SerializedName("akhirBulan")
	var akhirBulan: String? = null

)
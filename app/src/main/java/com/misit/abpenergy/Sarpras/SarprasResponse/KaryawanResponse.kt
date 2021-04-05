package com.misit.abpenergy.Sarpras.SarprasResponse

import com.google.gson.annotations.SerializedName

data class KaryawanResponse(

	@field:SerializedName("karyawan")
	var karyawan: List<KaryawanItem?>? = null
)
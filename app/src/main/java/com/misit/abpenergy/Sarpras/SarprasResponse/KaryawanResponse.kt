package com.misit.abpenergy.Sarpras.SarprasResponse

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class KaryawanResponse(

	@field:SerializedName("karyawan")
	var karyawan: List<KaryawanItem?>? = null
)
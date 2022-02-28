package com.misit.abpenergy.Monitoring_Produksi.Response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ProduksiResponse(

	@field:SerializedName("ProduksiDaily")
	var produksiDaily: List<ProduksiDailyItem>? = null,

	@field:SerializedName("ProduksiTotal")
	var produksiTotal: Double? = null
)
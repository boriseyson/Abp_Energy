package com.misit.abpenergy.Monitoring_Produksi.Response

import com.google.gson.annotations.SerializedName

data class ProduksiResponse(

	@field:SerializedName("ProduksiDaily")
	var produksiDaily: List<ProduksiDailyItem>? = null,

	@field:SerializedName("ProduksiTotal")
	var produksiTotal: Double? = null
)
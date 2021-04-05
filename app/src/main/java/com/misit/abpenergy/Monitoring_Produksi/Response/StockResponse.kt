package com.misit.abpenergy.Monitoring_Produksi.Response

import com.google.gson.annotations.SerializedName

data class StockResponse(

	@field:SerializedName("Coal")
	var coal: List<CoalItem>? = null
)
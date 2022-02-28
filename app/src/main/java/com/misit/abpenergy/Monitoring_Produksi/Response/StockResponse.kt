package com.misit.abpenergy.Monitoring_Produksi.Response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class StockResponse(

	@field:SerializedName("Coal")
	var coal: List<CoalItem>? = null
)
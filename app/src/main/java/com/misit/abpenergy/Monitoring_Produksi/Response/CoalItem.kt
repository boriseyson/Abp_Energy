package com.misit.abpenergy.Monitoring_Produksi.Response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CoalItem(

	@field:SerializedName("flag")
	var flag: String? = null,

	@field:SerializedName("stock_product")
	var stockProduct: Double? = null,

	@field:SerializedName("dl_daily_actual")
	var dlDailyActual: Double? = null,

	@field:SerializedName("user_input")
	var userInput: String? = null,

	@field:SerializedName("tgl")
	var tgl: String? = null,

	@field:SerializedName("remark")
	var remark: String? = null,

	@field:SerializedName("dl_mtd_actual")
	var dlMtdActual: Double? = null,

	@field:SerializedName("stock_rom")
	var stockRom: Double? = null,

	@field:SerializedName("time_input")
	var timeInput: String? = null
)
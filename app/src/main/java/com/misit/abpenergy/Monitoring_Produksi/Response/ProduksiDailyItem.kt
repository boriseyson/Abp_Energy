package com.misit.abpenergy.Monitoring_Produksi.Response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ProduksiDailyItem(

	@field:SerializedName("flag")
	var flag: String? = null,

	@field:SerializedName("actual_daily")
	var actualDaily: Double? = null,

	@field:SerializedName("user_input")
	var userInput: String? = null,

	@field:SerializedName("tgl")
	var tgl: String? = null,

	@field:SerializedName("plan_daily")
	var planDaily: Double? = null,

	@field:SerializedName("remark")
	var remark: String? = null,

	@field:SerializedName("mtd_actual")
	var mtdActual: Double? = null,

	@field:SerializedName("mtd_plan")
	var mtdPlan: Double? = null,

	@field:SerializedName("time_input")
	var timeInput: String? = null
)
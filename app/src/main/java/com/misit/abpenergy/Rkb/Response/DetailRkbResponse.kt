package com.misit.abpenergy.Rkb.Response

import com.google.gson.annotations.SerializedName

data class DetailRkbResponse(

	@field:SerializedName("detailRkb")
	var detailRkb: List<DetailRkbItem>? = null
)
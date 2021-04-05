package com.misit.abpenergy.Rkb.Response

import com.google.gson.annotations.SerializedName

data class ApproveRkbResponse(

	@field:SerializedName("aprrove")
	var aprrove: Boolean? = null
)
package com.misit.abpenergy.HGE.Rkb.Response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ApproveRkbResponse(

	@field:SerializedName("aprrove")
	var aprrove: Boolean? = null
)
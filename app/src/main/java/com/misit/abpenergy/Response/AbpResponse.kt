package com.misit.abpenergy.Response

import com.google.gson.annotations.SerializedName

data class AbpResponse(

	@field:SerializedName("area")
	var area: String? = null
)
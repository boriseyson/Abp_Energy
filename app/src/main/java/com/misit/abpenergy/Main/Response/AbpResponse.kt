package com.misit.abpenergy.Main.Response

import com.google.gson.annotations.SerializedName

data class AbpResponse(

	@field:SerializedName("area")
	var area: String? = null
)
package com.misit.abpenergy.Rkb.Response

import com.google.gson.annotations.SerializedName

data class AppVersionResponse(

	@field:SerializedName("version")
	var version: String? = null
)
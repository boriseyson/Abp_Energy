package com.misit.abpenergy.Main.Response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AbpResponse(

	@field:SerializedName("area")
	var area: String? = null
)
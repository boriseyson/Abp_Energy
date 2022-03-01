package com.misit.abpenergy.HGE.Rkb.Response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class UserResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("user")
	val user: User? = null
)
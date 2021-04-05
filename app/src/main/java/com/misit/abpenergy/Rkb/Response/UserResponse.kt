package com.misit.abpenergy.Rkb.Response

import com.google.gson.annotations.SerializedName

data class UserResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("user")
	val user: User? = null
)
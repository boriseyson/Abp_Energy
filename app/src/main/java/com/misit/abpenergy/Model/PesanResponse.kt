package com.misit.abpenergy.Model

import com.google.gson.annotations.SerializedName

data class PesanResponse(

	@field:SerializedName("message")
	val message: List<String>? = null
)

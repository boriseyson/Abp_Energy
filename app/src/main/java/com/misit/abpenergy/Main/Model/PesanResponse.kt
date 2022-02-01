package com.misit.abpenergy.Main.Model

import com.google.gson.annotations.SerializedName

data class PesanResponse(

	@field:SerializedName("message")
	val message: List<String>? = null
)

package com.misit.abpenergy.Model

import com.google.gson.annotations.SerializedName

data class NotifGroupResponse(

	@field:SerializedName("hazardNotClose")
	val hazardNotClose: List<HazardNotCloseItem?>? = null
)

data class HazardNotCloseItem(

	@field:SerializedName("uid")
	val uid: String? = null,

	@field:SerializedName("pesan")
	val pesan: List<String?>? = null,

	@field:SerializedName("phone_token")
	val phoneToken: String? = null,

	@field:SerializedName("judul")
	val judul: String? = null
)

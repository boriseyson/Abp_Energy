package com.misit.abpenergy.Inspeksi.Response

import com.google.gson.annotations.SerializedName

data class InspeksiGroupsResponse(

	@field:SerializedName("itemInspeksi")
	val itemInspeksi: List<ItemInspeksiItem>? = null
)

data class ItemInspeksiItem(

	@field:SerializedName("nameSub")
	val nameSub: String? = null,
	@field:SerializedName("numSub")
	val numSub: String? = null,
	@field:SerializedName("items")
	val items: List<ItemsItem>? = null
)

data class ItemsItem(

	@field:SerializedName("idForm")
	val idForm: Int? = null,

	@field:SerializedName("tgl_input")
	val tglInput: String? = null,

	@field:SerializedName("idSub")
	val idSub: Int? = null,

	@field:SerializedName("flag")
	val flag: Int? = null,

	@field:SerializedName("user_input")
	val userInput: String? = null,

	@field:SerializedName("listInspeksi")
	val listInspeksi: String? = null,

	@field:SerializedName("idList")
	val idList: Int? = null
)

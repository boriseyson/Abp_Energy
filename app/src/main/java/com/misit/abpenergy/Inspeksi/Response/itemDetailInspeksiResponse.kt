package com.misit.abpenergy.Inspeksi.Response

import com.google.gson.annotations.SerializedName

data class ItemsDetail(

	@field:SerializedName("idForm")
	var idForm: Int? = null,

	@field:SerializedName("tgl_input")
	var tglInput: String? = null,

	@field:SerializedName("inspeksi")
	var inspeksi: Int? = null,

	@field:SerializedName("idSub")
	var idSub: Int? = null,

	@field:SerializedName("flag")
	var flag: Int? = null,

	@field:SerializedName("answer")
	var answer: Int? = null,

	@field:SerializedName("user_input")
	var userInput: String? = null,

	@field:SerializedName("listInspeksi")
	var listInspeksi: String? = null,

	@field:SerializedName("idDetail")
	var idDetail: Int? = null,

	@field:SerializedName("idList")
	var idList: Int? = null,

	@field:SerializedName("idInspeksi")
	var idInspeksi: String? = null
)

data class ItemDetailInspeksi(

	@field:SerializedName("numSub")
	var numSub: String? = null,

	@field:SerializedName("nameSub")
	var nameSub: String? = null,

	@field:SerializedName("items")
	var items: List<ItemsDetail>? = null
)

data class ItemDetailInspeksiResponse(

	@field:SerializedName("itemDetailInspeksi")
	var itemDetailInspeksi: List<ItemDetailInspeksi>? = null
)

package com.misit.abpenergy.HSE.Inspeksi.Response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class FormInspeksiResponse(

    @field:SerializedName("path")
	var path: String? = null,

    @field:SerializedName("per_page")
	var perPage: Int? = null,

    @field:SerializedName("total")
	var total: Int? = null,

    @field:SerializedName("data")
	var data: List<FormItem>? = null,

    @field:SerializedName("last_page")
	var lastPage: Int? = null,

    @field:SerializedName("next_page_url")
	var nextPageUrl: String? = null,

    @field:SerializedName("from")
	var from: Int? = null,

    @field:SerializedName("to")
	var to: Int? = null,

    @field:SerializedName("prev_page_url")
	var prevPageUrl: String? = null,

    @field:SerializedName("current_page")
	var currentPage: Int? = null
)

@Keep
data class FormItem(

	@field:SerializedName("idForm")
	var idForm: Int? = null,

	@field:SerializedName("flag")
	var flag: Int? = null,

	@field:SerializedName("userEntry")
	var userEntry: String? = null,

	@field:SerializedName("tglEntry")
	var tglEntry: String? = null,

	@field:SerializedName("namaForm")
	var namaForm: String? = null,

	@field:SerializedName("kodeForm")
	var kodeForm: String? = null
)

package com.misit.abpenergy.Inspeksi.Response

import com.google.gson.annotations.SerializedName

data class ALLInspeksiResponse(

	@field:SerializedName("path")
	var path: String? = null,

	@field:SerializedName("per_page")
	var perPage: Int? = null,

	@field:SerializedName("total")
	var total: Int? = null,

	@field:SerializedName("data")
	var data: List<DataItem?>? = null,

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

data class DataItem(

	@field:SerializedName("flag")
	var flag: Int? = null,

	@field:SerializedName("tgl_inspeksi")
	var tglInspeksi: String? = null,

	@field:SerializedName("perusahaan")
	var perusahaan: Int? = null,

	@field:SerializedName("nama_perusahaan")
	var namaPerusahaan: String? = null,

	@field:SerializedName("idInspeksi")
	var idInspeksi: String? = null,

	@field:SerializedName("userEntry")
	var userEntry: String? = null,

	@field:SerializedName("tglEntry")
	var tglEntry: String? = null,

	@field:SerializedName("kodeForm")
	var kodeForm: String? = null,

	@field:SerializedName("idForm")
	var idForm: Int? = null,

	@field:SerializedName("saran")
	var saran: String? = null,

	@field:SerializedName("id_perusahaan")
	var idPerusahaan: Int? = null,

	@field:SerializedName("time_in")
	var timeIn: String? = null,

	@field:SerializedName("userInput")
	var userInput: String? = null,

	@field:SerializedName("tglInput")
	var tglInput: String? = null,

	@field:SerializedName("namaForm")
	var namaForm: String? = null,

	@field:SerializedName("INC")
	var iNC: Int? = null,

	@field:SerializedName("lokasiInspeksi")
	var lokasiInspeksi: String? = null,

	@field:SerializedName("status")
	var status: String? = null
)

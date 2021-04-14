package com.misit.abpenergy.HazardReport.Response

import com.google.gson.annotations.SerializedName

data class ListHazard(

	@field:SerializedName("path")
	var path: String? = null,

	@field:SerializedName("per_page")
	var perPage: Int? = null,

	@field:SerializedName("total")
	var total: Int? = null,

	@field:SerializedName("data")
	var data: List<DataItem>? = null,

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

	@field:SerializedName("update_bukti")
	var updateBukti: String? = null,

	@field:SerializedName("status_perbaikan")
	var statusPerbaikan: String? = null,

	@field:SerializedName("nama_lengkap")
	var namaLengkap: String? = null,

	@field:SerializedName("perusahaan")
	var perusahaan: String? = null,

	@field:SerializedName("penanggung_jawab")
	var penanggungJawab: String? = null,

	@field:SerializedName("tgl_selesai")
	var tglSelesai: String? = null,

	@field:SerializedName("lokasi_detail")
	var lokasiDetail: String? = null,

	@field:SerializedName("uid")
	var uid: String? = null,

	@field:SerializedName("tgl_valid")
	var tglvalid: String? = null,

	@field:SerializedName("bgColor")
	var bgColor: String? = null,

	@field:SerializedName("user_input")
	var userInput: String? = null,

	@field:SerializedName("tindakan")
	var tindakan: String? = null,

	@field:SerializedName("txtColor")
	var txtColor: String? = null,

	@field:SerializedName("kat_bahaya")
	var katBahaya: String? = null,

	@field:SerializedName("keterangan_update")
	var keteranganUpdate: String? = null,

	@field:SerializedName("lokasiHazard")
	var lokasiHazard: String? = null,

	@field:SerializedName("idRisk")
	var idRisk: String? = null,

	@field:SerializedName("idvalidation")
	var idvalidation: Int? = null,

	@field:SerializedName("desc_risk")
	var descRisk: String? = null,

	@field:SerializedName("jam_selesai")
	var jamSelesai: String? = null,

	@field:SerializedName("jam_valid")
	var jamvalid: String? = null,

	@field:SerializedName("tgl_input")
	var tglInput: String? = null,

	@field:SerializedName("jam_hazard")
	var jamHazard: String? = null,

	@field:SerializedName("lokasi")
	var lokasi: String? = null,

	@field:SerializedName("bukti")
	var bukti: String? = null,

	@field:SerializedName("sumber_bahaya")
	var sumberBahaya: String? = null,

	@field:SerializedName("idHazard")
	var idHazard: Int? = null,

	@field:SerializedName("risk")
	var risk: String? = null,

	@field:SerializedName("deskripsi")
	var deskripsi: String? = null,

	@field:SerializedName("user_valid")
	var uservalid: String? = null,

	@field:SerializedName("time_input")
	var timeInput: String? = null,

	@field:SerializedName("tgl_hazard")
	var tglHazard: String? = null,

	@field:SerializedName("status")
	var status: Int? = null
)

package com.misit.abpenergy.HazardReport.Response

import com.google.gson.annotations.SerializedName

data class DataItem(

	@field:SerializedName("status_perbaikan")
	var statusPerbaikan: String? = null,

	@field:SerializedName("jam_selesai")
	var jamSelesai: String? = null,

	@field:SerializedName("perusahaan")
	var perusahaan: String? = null,

	@field:SerializedName("penanggung_jawab")
	var penanggungJawab: String? = null,

	@field:SerializedName("tgl_selesai")
	var tglSelesai: String? = null,

	@field:SerializedName("jam_varid")
	var jamvarid: String? = null,

	@field:SerializedName("uid")
	var uid: String? = null,

	@field:SerializedName("tgl_varid")
	var tglvarid: String? = null,

	@field:SerializedName("jam_hazard")
	var jamHazard: String? = null,

	@field:SerializedName("user_input")
	var userInput: String? = null,

	@field:SerializedName("lokasi")
	var lokasi: String? = null,

	@field:SerializedName("bukti")
	var bukti: String? = null,

	@field:SerializedName("tindakan")
	var tindakan: String? = null,

	@field:SerializedName("sumber_bahaya")
	var sumberBahaya: String? = null,

	@field:SerializedName("idHazard")
	var idHazard: Int? = null,

	@field:SerializedName("kat_bahaya")
	var katBahaya: String? = null,

	@field:SerializedName("deskripsi")
	var deskripsi: String? = null,

	@field:SerializedName("user_varid")
	var uservarid: String? = null,

	@field:SerializedName("time_input")
	var timeInput: String? = null,

	@field:SerializedName("tgl_hazard")
	var tglHazard: String? = null,

	@field:SerializedName("status")
	var status: Int? = null,

	@field:SerializedName("idvaridation")
	var idvaridation: Int? = null
)
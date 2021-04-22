package com.misit.abpenergy.HazardReport.Response

import com.google.gson.annotations.SerializedName

data class DetailHazardResponse(

	@field:SerializedName("ItemHazardList")
	var itemHazardList: ItemHazardList? = null,

	@field:SerializedName("Risk")
	var risk: Risk? = null,

	@field:SerializedName("nilairRisk")
	var nilairRisk: Int? = null
)

data class Risk(

	@field:SerializedName("min")
	var min: Int? = null,

	@field:SerializedName("flag")
	var flag: Int? = null,

	@field:SerializedName("bgColor")
	var bgColor: String? = null,

	@field:SerializedName("idResiko")
	var idResiko: Int? = null,

	@field:SerializedName("kodeBahaya")
	var kodeBahaya: String? = null,

	@field:SerializedName("max")
	var max: Int? = null,

	@field:SerializedName("tindakan")
	var tindakan: String? = null,

	@field:SerializedName("txtColor")
	var txtColor: String? = null,

	@field:SerializedName("kategori")
	var kategori: String? = null
)

data class ItemHazardList(

	@field:SerializedName("kemungkinan")
	var kemungkinan: String? = null,

	@field:SerializedName("flag")
	var flag: Int? = null,

	@field:SerializedName("update_bukti")
	var updateBukti: String? = null,

	@field:SerializedName("nilai")
	var nilai: Int? = null,

	@field:SerializedName("status_perbaikan")
	var statusPerbaikan: String? = null,

	@field:SerializedName("nama_lengkap")
	var namaLengkap: String? = null,

	@field:SerializedName("perusahaan")
	var perusahaan: String? = null,

	@field:SerializedName("tgl_selesai")
	var tglSelesai: String? = null,

	@field:SerializedName("namaPengendalian")
	var namaPengendalian: String? = null,

	@field:SerializedName("lokasi_detail")
	var lokasiDetail: String? = null,

	@field:SerializedName("uid")
	var uid: String? = null,

	@field:SerializedName("tgl_varid")
	var tglvarid: String? = null,

	@field:SerializedName("idKeparahan")
	var idKeparahan: Int? = null,

	@field:SerializedName("user_input")
	var userInput: String? = null,

	@field:SerializedName("idPengendalian")
	var idPengendalian: Int? = null,

	@field:SerializedName("tindakan")
	var tindakan: String? = null,

	@field:SerializedName("katBahaya")
	var katBahaya: String? = null,

	@field:SerializedName("keterangan_update")
	var keteranganUpdate: String? = null,

	@field:SerializedName("lokasiHazard")
	var lokasiHazard: String? = null,

	@field:SerializedName("fotoPJ")
	var fotoPJ: String? = null,

	@field:SerializedName("idvaridation")
	var idvaridation: Int? = null,

	@field:SerializedName("nilaiKemungkinan")
	var nilaiKemungkinan: Int? = null,

	@field:SerializedName("jam_selesai")
	var jamSelesai: String? = null,

	@field:SerializedName("idKemungkinan")
	var idKemungkinan: Int? = null,

	@field:SerializedName("jam_varid")
	var jamvarid: String? = null,

	@field:SerializedName("idHirarki")
	var idHirarki: Int? = null,

	@field:SerializedName("tgl_input")
	var tglInput: String? = null,

	@field:SerializedName("jam_hazard")
	var jamHazard: String? = null,

	@field:SerializedName("nikPJ")
	var nikPJ: String? = null,

	@field:SerializedName("lokasi")
	var lokasi: String? = null,

	@field:SerializedName("bukti")
	var bukti: String? = null,

	@field:SerializedName("nilaiKeparahan")
	var nilaiKeparahan: Int? = null,

	@field:SerializedName("idHazard")
	var idHazard: Int? = null,

	@field:SerializedName("deskripsi")
	var deskripsi: String? = null,

	@field:SerializedName("user_varid")
	var uservarid: String? = null,

	@field:SerializedName("time_input")
	var timeInput: String? = null,

	@field:SerializedName("tgl_hazard")
	var tglHazard: String? = null,

	@field:SerializedName("keparahan")
	var keparahan: String? = null,

	@field:SerializedName("status")
	var status: Int? = null,

	@field:SerializedName("namaPJ")
	var namaPJ: String? = null
)

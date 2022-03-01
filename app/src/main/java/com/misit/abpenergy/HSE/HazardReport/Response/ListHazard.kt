package com.misit.abpenergy.HSE.HazardReport.Response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ListHazard(

	@field:SerializedName("path")
	var path: String? = null,

	@field:SerializedName("per_page")
	var perPage: Int? = null,

	@field:SerializedName("total")
	var total: Int? = null,

	@field:SerializedName("data")
	var data: List<HazardItem>? = null,

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
data class HazardList (
	@field:SerializedName("data")
	var data: MutableList<HazardItem>? = null
)
@Keep
data class HazardItem(

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

	@field:SerializedName("tgl_valid")
	var tglvalid: String? = null,

	@field:SerializedName("idKeparahan")
	var idKeparahan: Int? = null,

	@field:SerializedName("idKeparahanSesudah")
	var idKeparahanSesudah: Int? = null,

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

	@field:SerializedName("idValidation")
	var idvalidation: Int? = null,

	@field:SerializedName("jam_selesai")
	var jamSelesai: String? = null,

	@field:SerializedName("idKemungkinan")
	var idKemungkinan: Int? = null,

	@field:SerializedName("idKemungkinanSesudah")
	var idKemungkinanSesudah: Int? = null,

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

	@field:SerializedName("idHazard")
	var idHazard: Int? = null,

	@field:SerializedName("deskripsi")
	var deskripsi: String? = null,

	@field:SerializedName("user_valid")
	var uservalid: String? = null,

	@field:SerializedName("time_input")
	var timeInput: String? = null,

	@field:SerializedName("tgl_hazard")
	var tglHazard: String? = null,

	@field:SerializedName("keparahan")
	var keparahan: String? = null,

	@field:SerializedName("fotoPJ_option")
	var fotoPJ_option: Int? = null,

	@field:SerializedName("status")
	var status: Int? = null,

	@field:SerializedName("namaPJ")
	var namaPJ: String? = null,

	@field:SerializedName("tgl_tenggat")
	var tgl_tenggat: String? = null,

	@field:SerializedName("keterangan_admin")
	var keterangan_admin: String? = null,

	@field:SerializedName("option_flag")
	var option_flag: Int? = null

)

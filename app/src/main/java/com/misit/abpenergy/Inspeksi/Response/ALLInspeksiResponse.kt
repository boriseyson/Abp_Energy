package com.misit.abpenergy.Inspeksi.Response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class ALLInspeksiResponse(

	@field:SerializedName("allInspection")
	var allInspection: AllInspection? = null
)

@Keep
data class InpeksiItem(

	@field:SerializedName("tglentry")
	var tglentry: String? = null,

	@field:SerializedName("flag")
	var flag: Int? = null,

	@field:SerializedName("nama_lengkap")
	var namaLengkap: String? = null,

	@field:SerializedName("id_session")
	var idSession: String? = null,

	@field:SerializedName("perusahaan")
	var perusahaan: Int? = null,

	@field:SerializedName("rule")
	var rule: String? = null,

	@field:SerializedName("section")
	var section: String? = null,

	@field:SerializedName("nama_perusahaan")
	var namaPerusahaan: String? = null,

	@field:SerializedName("kodeForm")
	var kodeForm: String? = null,

	@field:SerializedName("nik")
	var nik: String? = null,

	@field:SerializedName("password")
	var password: String? = null,

	@field:SerializedName("saran")
	var saran: String? = null,

	@field:SerializedName("tglInput")
	var tglInput: String? = null,

	@field:SerializedName("department")
	var department: String? = null,

	@field:SerializedName("namaForm")
	var namaForm: String? = null,

	@field:SerializedName("email")
	var email: String? = null,

	@field:SerializedName("lokasiInspeksi")
	var lokasiInspeksi: String? = null,

	@field:SerializedName("level")
	var level: String? = null,

	@field:SerializedName("ttd")
	var ttd: String? = null,

	@field:SerializedName("photo_profile")
	var photoProfile: String? = null,

	@field:SerializedName("tgl_inspeksi")
	var tglInspeksi: String? = null,

	@field:SerializedName("id_user")
	var idUser: Int? = null,

	@field:SerializedName("idInspeksi")
	var idInspeksi: String? = null,

	@field:SerializedName("userEntry")
	var userEntry: String? = null,

	@field:SerializedName("tglEntry")
	var tglEntry: String? = null,

	@field:SerializedName("idForm")
	var idForm: Int? = null,

	@field:SerializedName("id_perusahaan")
	var idPerusahaan: Int? = null,

	@field:SerializedName("time_in")
	var timeIn: String? = null,

	@field:SerializedName("userInput")
	var userInput: String? = null,

	@field:SerializedName("INC")
	var iNC: Int? = null,

	@field:SerializedName("status")
	var status: Int? = null,

	@field:SerializedName("username")
	var username: String? = null
)

@Keep
data class AllInspection(

	@field:SerializedName("path")
	var path: String? = null,

	@field:SerializedName("per_page")
	var perPage: Int? = null,

	@field:SerializedName("total")
	var total: Int? = null,

	@field:SerializedName("data")
	var inpeksiItem: List<InpeksiItem>? = null,

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

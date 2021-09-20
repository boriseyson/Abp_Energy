package com.misit.abpenergy.Response

import com.google.gson.annotations.SerializedName

data class GetUserResponse(

	@field:SerializedName("dataHazard")
	var dataHazard: Int? = null,

	@field:SerializedName("datInspeksi")
	var datInspeksi: Int? = null,

	@field:SerializedName("dataUser")
	var dataUser: DataUser? = null
)

data class DataUser(

	@field:SerializedName("tglentry")
	var tglentry: String? = null,

	@field:SerializedName("flag")
	var flag: Int? = null,

	@field:SerializedName("nama_lengkap")
	var namaLengkap: String? = null,

	@field:SerializedName("id_session")
	var idSession: String? = null,

	@field:SerializedName("rule")
	var rule: String? = null,

	@field:SerializedName("perusahaan")
	var perusahaan: Int? = null,

	@field:SerializedName("section")
	var section: String? = null,

	@field:SerializedName("nama_perusahaan")
	var namaPerusahaan: String? = null,

	@field:SerializedName("nik")
	var nik: String? = null,

	@field:SerializedName("password")
	var password: String? = null,

	@field:SerializedName("id_dept")
	var idDept: String? = null,

	@field:SerializedName("department")
	var department: String? = null,

	@field:SerializedName("email")
	var email: String? = null,

	@field:SerializedName("inc")
	var inc: Int? = null,

	@field:SerializedName("id_sect")
	var idSect: String? = null,

	@field:SerializedName("level")
	var level: String? = null,

	@field:SerializedName("ttd")
	var ttd: String? = null,

	@field:SerializedName("photo_profile")
	var photoProfile: String? = null,

	@field:SerializedName("id_user")
	var idUser: Int? = null,
	@field:SerializedName("id")
	var id: Int? = null,

	@field:SerializedName("dept")
	var dept: String? = null,

	@field:SerializedName("user_entry")
	var userEntry: String? = null,

	@field:SerializedName("id_perusahaan")
	var idPerusahaan: Int? = null,

	@field:SerializedName("sect")
	var sect: String? = null,

	@field:SerializedName("timelog")
	var timelog: String? = null,

	@field:SerializedName("time_in")
	var timeIn: String? = null,

	@field:SerializedName("username")
	var username: String? = null,

	@field:SerializedName("status")
	var status: Int? = null
)

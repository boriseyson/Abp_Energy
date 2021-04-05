package com.misit.abpenergy.Rkb.Response

import com.google.gson.annotations.SerializedName

data class User(

	@field:SerializedName("tglentry")
	val tglentry: String? = null,

	@field:SerializedName("level")
	val level: String? = null,

	@field:SerializedName("ttd")
	val ttd: Any? = null,

	@field:SerializedName("nama_lengkap")
	val namaLengkap: String? = null,

	@field:SerializedName("id_session")
	val idSession: String? = null,

	@field:SerializedName("rule")
	val rule: String? = null,

	@field:SerializedName("section")
	val section: String? = null,

	@field:SerializedName("id_user")
	val idUser: Int? = null,

	@field:SerializedName("nik")
	val nik: String? = null,

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("department")
	val department: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)
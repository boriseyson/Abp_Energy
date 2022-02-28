package com.misit.abpenergy.Rkb.Response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class User(

	@field:SerializedName("tglentry")
	var tglentry: String? = null,

	@field:SerializedName("level")
	var level: String? = null,

	@field:SerializedName("ttd")
	var ttd: String? = null,

	@field:SerializedName("nama_lengkap")
	var namaLengkap: String? = null,

	@field:SerializedName("id_session")
	var idSession: String? = null,

	@field:SerializedName("rule")
	var rule: String? = null,

	@field:SerializedName("section")
	var section: String? = null,

	@field:SerializedName("id_user")
	var idUser: Int? = null,

	@field:SerializedName("nik")
	var nik: String? = null,

	@field:SerializedName("password")
	var password: String? = null,

	@field:SerializedName("department")
	var department: String? = null,

	@field:SerializedName("email")
	var email: String? = null,

	@field:SerializedName("username")
	var username: String? = null,

	@field:SerializedName("status")
	var status: Int? = null,

	@field:SerializedName("perusahaan")
	var perusahaan: String? = null,

	@field:SerializedName("user_update")
	var user_update: String? = null,

	@field:SerializedName("token_password")
	var token_password: String? = null,

	@field:SerializedName("photo_profile")
	var photoProfile: String? = null
)
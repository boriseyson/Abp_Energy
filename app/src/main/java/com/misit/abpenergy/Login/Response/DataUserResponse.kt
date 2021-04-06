package com.misit.abpenergy.Login.Response

import com.google.gson.annotations.SerializedName

data class DataUserResponse(

	@field:SerializedName("dataUser")
	var dataUser: DataUser? = null
)

data class DataUser(

	@field:SerializedName("no")
	var no: Int? = null,

	@field:SerializedName("devisi")
	var devisi: String? = null,

	@field:SerializedName("flag")
	var flag: Int? = null,

	@field:SerializedName("jabatan")
	var jabatan: String? = null,

	@field:SerializedName("dept")
	var dept: String? = null,

	@field:SerializedName("show_absen")
	var showAbsen: Int? = null,

	@field:SerializedName("departemen")
	var departemen: String? = null,

	@field:SerializedName("nik")
	var nik: String? = null,

	@field:SerializedName("tgl_up")
	var tglUp: String? = null,

	@field:SerializedName("password")
	var password: String? = null,

	@field:SerializedName("nama")
	var nama: String? = null,

	@field:SerializedName("user_entry")
	var userEntry: String? = null,

	@field:SerializedName("id_dept")
	var idDept: String? = null,

	@field:SerializedName("tgl_entry")
	var tglEntry: String? = null,

	@field:SerializedName("timelog")
	var timelog: String? = null
)

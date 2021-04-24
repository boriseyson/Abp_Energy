package com.misit.abpenergy.Response

data class GetUserResponse(
	var dataHazard: Int? = null,
	var datInspeksi: Int? = null,
	var dataUser: DataUser? = null
)

data class DataUser(
	var tglentry: String? = null,
	var idSect: String? = null,
	var level: String? = null,
	var ttd: String? = null,
	var photoProfile: String? = null,
	var namaLengkap: String? = null,
	var idSession: String? = null,
	var rule: String? = null,
	var perusahaan: Int? = null,
	var section: String? = null,
	var idUser: Int? = null,
	var dept: String? = null,
	var nik: String? = null,
	var password: String? = null,
	var idDept: String? = null,
	var userEntry: String? = null,
	var sect: String? = null,
	var timelog: String? = null,
	var department: String? = null,
	var email: String? = null,
	var username: String? = null,
	var status: Int? = null,
	var inc: Int? = null
)


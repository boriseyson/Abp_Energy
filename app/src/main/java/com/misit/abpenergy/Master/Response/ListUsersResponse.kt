package com.misit.abpenergy.Master.Response

import com.google.gson.annotations.SerializedName

data class ListUsersResponse(

	@field:SerializedName("listUser")
	val listUser: ListUser? = null
)

data class UserItem(

	@field:SerializedName("tglentry")
	val tglentry: String? = null,

	@field:SerializedName("level")
	val level: String? = null,

	@field:SerializedName("ttd")
	val ttd: String? = null,

	@field:SerializedName("photo_profile")
	val photoProfile: String? = null,

	@field:SerializedName("nama_lengkap")
	val namaLengkap: String? = null,

	@field:SerializedName("id_session")
	val idSession: String? = null,

	@field:SerializedName("rule")
	val rule: String? = null,

	@field:SerializedName("perusahaan")
	val perusahaan: Int? = null,

	@field:SerializedName("section")
	val section: String? = null,

	@field:SerializedName("nama_perusahaan")
	val namaPerusahaan: String? = null,

	@field:SerializedName("id_user")
	val idUser: Int? = null,

	@field:SerializedName("dept")
	val dept: String? = null,

	@field:SerializedName("nik")
	val nik: Int? = null,

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("sect")
	val sect: String? = null,

	@field:SerializedName("department")
	val department: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class ListUser(

	@field:SerializedName("path")
	val path: String? = null,

	@field:SerializedName("per_page")
	val perPage: Int? = null,

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("data")
	val data: List<UserItem>? = null,

	@field:SerializedName("last_page")
	val lastPage: Int? = null,

	@field:SerializedName("next_page_url")
	val nextPageUrl: String? = null,

	@field:SerializedName("from")
	val from: Int? = null,

	@field:SerializedName("to")
	val to: Int? = null,

	@field:SerializedName("prev_page_url")
	val prevPageUrl: String? = null,

	@field:SerializedName("current_page")
	val currentPage: Int? = null
)

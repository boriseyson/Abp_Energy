package com.misit.abpenergy.Inspeksi.Response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class TeamInspeksiTempResponse(

	@field:SerializedName("teamInspeksiTemp")
	val teamInspeksiTemp: List<TeamInspeksiTempItem>? = null
)

@Keep
data class TeamInspeksiTempItem(

	@field:SerializedName("tglentry")
	val tglentry: String? = null,

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

	@field:SerializedName("idTeam")
	val idTeam: Int? = null,

	@field:SerializedName("nik")
	val nik: String? = null,

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("id_dept")
	val idDept: String? = null,

	@field:SerializedName("idTemp")
	val idTemp: String? = null,

	@field:SerializedName("department")
	val department: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("inc")
	val inc: Int? = null,

	@field:SerializedName("id_sect")
	val idSect: String? = null,

	@field:SerializedName("level")
	val level: String? = null,

	@field:SerializedName("ttd")
	val ttd: Any? = null,

	@field:SerializedName("photo_profile")
	val photoProfile: Any? = null,

	@field:SerializedName("nikTeam")
	val nikTeam: String? = null,

	@field:SerializedName("id_user")
	val idUser: Int? = null,

	@field:SerializedName("dept")
	val dept: String? = null,

	@field:SerializedName("idForm")
	val idForm: Int? = null,

	@field:SerializedName("user_entry")
	val userEntry: String? = null,

	@field:SerializedName("sect")
	val sect: String? = null,

	@field:SerializedName("timelog")
	val timelog: String? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

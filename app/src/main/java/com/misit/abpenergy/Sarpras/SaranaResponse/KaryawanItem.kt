package com.misit.abpenergy.Sarpras.SaranaResponse

import com.google.gson.annotations.SerializedName

data class KaryawanItem(

	@field:SerializedName("nik")
	var nik: String? = null,

	@field:SerializedName("flag")
	var flag: String? = null,

	@field:SerializedName("nama")
	var nama: String? = null,

	@field:SerializedName("tanggal_entry")
	var tanggalEntry: String? = null,

	@field:SerializedName("user_entry")
	var userEntry: String? = null,

	@field:SerializedName("id_dept")
	var idDept: String? = null,

	@field:SerializedName("jabatan")
	var jabatan: String? = null,

	@field:SerializedName("timelog")
	var timelog: String? = null,

	@field:SerializedName("section")
	var section: String? = null,

	@field:SerializedName("dept")
	var dept: String? = null,

	@field:SerializedName("department")
	var department: String? = null,

	@field:SerializedName("dt_update")
	var dt_update: String? = null

)
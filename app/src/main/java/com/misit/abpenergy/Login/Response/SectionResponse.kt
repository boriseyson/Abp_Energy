package com.misit.abpenergy.Login.Response

import com.google.gson.annotations.SerializedName

data class SectionResponse(

	@field:SerializedName("section")
	var section: List<SectionItem>? = null
)

data class SectionItem(

	@field:SerializedName("id_sect")
	var idSect: String? = null,

	@field:SerializedName("id_dept")
	var idDept: String? = null,

	@field:SerializedName("user_entry")
	var userEntry: String? = null,

	@field:SerializedName("sect")
	var sect: String? = null,

	@field:SerializedName("timelog")
	var timelog: String? = null,

	@field:SerializedName("dept")
	var dept: String? = null,

	@field:SerializedName("inc")
	var inc: Int? = null
)

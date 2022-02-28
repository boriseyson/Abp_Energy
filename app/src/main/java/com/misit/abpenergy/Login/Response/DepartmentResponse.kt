package com.misit.abpenergy.Login.Response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DepartmentResponse(

	@field:SerializedName("department")
	var department: List<DepartmentItem>? = null
)

@Keep
data class DepartmentItem(

	@field:SerializedName("id_dept")
	var idDept: String? = null,

	@field:SerializedName("user_entry")
	var userEntry: String? = null,

	@field:SerializedName("timelog")
	var timelog: String? = null,

	@field:SerializedName("company")
	var company: Int? = null,

	@field:SerializedName("id")
	var id: Int? = null,

	@field:SerializedName("dept")
	var dept: String? = null
)

package com.misit.abpenergy.HGE.Rkb.Response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DetailRkbItem(

	@field:SerializedName("item")
	var item: String? = null,

	@field:SerializedName("keterangan")
	var keterangan: String? = null,

	@field:SerializedName("quantity")
	var quantity: Double? = null,

	@field:SerializedName("po_item")
	var poItem: String? = null,

	@field:SerializedName("due_date")
	var dueDate: String? = null,

	@field:SerializedName("no_rkb")
	var noRkb: String? = null,

	@field:SerializedName("user_entry")
	var userEntry: String? = null,

	@field:SerializedName("satuan")
	var satuan: String? = null,

	@field:SerializedName("timelog")
	var timelog: String? = null,

	@field:SerializedName("part_number")
	var partNumber: String? = null,

	@field:SerializedName("id")
	var id: Int? = null,

	@field:SerializedName("part_name")
	var partName: String? = null,

	@field:SerializedName("remarks")
	var remarks: String? = null
)
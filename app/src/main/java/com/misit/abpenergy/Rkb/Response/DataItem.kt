package com.misit.abpenergy.Rkb.Response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DataItem(

	@field:SerializedName("tglentry")
	var tglentry: String? = null,

	@field:SerializedName("tgl_order")
	var tglOrder: String? = null,

	@field:SerializedName("keterangan")
	var keterangan: String? = null,

	@field:SerializedName("po_item")
	var poItem: String? = null,

	@field:SerializedName("nama_lengkap")
	var namaLengkap: String? = null,

	@field:SerializedName("id_session")
	var idSession: String? = null,

	@field:SerializedName("rule")
	var rule: String? = null,

	@field:SerializedName("section")
	var section: String? = null,

	@field:SerializedName("tgl_disetujui")
	var tglDisetujui: String? = null,

	@field:SerializedName("diketahui")
	var diketahui: String? = null,

	@field:SerializedName("disetujui")
	var disetujui: String? = null,

	@field:SerializedName("nik")
	var nik: String? = null,

	@field:SerializedName("password")
	var password: String? = null,

	@field:SerializedName("id_dept")
	var idDept: String? = null,

	@field:SerializedName("user_expired")
	var userExpired: String? = null,

	@field:SerializedName("myStatus")
	var myStatus: String? = null,

	@field:SerializedName("tgl_diketahui")
	var tglDiketahui: String? = null,

	@field:SerializedName("id")
	var id: Int? = null,

	@field:SerializedName("time_status")
	var timeStatus: String? = null,

	@field:SerializedName("department")
	var department: String? = null,

	@field:SerializedName("email")
	var email: String? = null,

	@field:SerializedName("inc")
	var inc: Int? = null,

	@field:SerializedName("item")
	var item: String? = null,

	@field:SerializedName("cancel_user")
	var cancelUser: String? = null,

	@field:SerializedName("quantity")
	var quantity: Double? = null,

	@field:SerializedName("tgl_cancel_user")
	var tglCancelUser: String? = null,

	@field:SerializedName("id_sect")
	var idSect: String? = null,

	@field:SerializedName("level")
	var level: String? = null,

	@field:SerializedName("ttd")
	var ttd: String? = null,

	@field:SerializedName("cancel_section")
	var cancelSection: String? = null,

	@field:SerializedName("due_date")
	var dueDate: String? = null,

	@field:SerializedName("user_close")
	var userClose: String? = null,

	@field:SerializedName("dept")
	var dept: String? = null,

	@field:SerializedName("id_user")
	var idUser: Int? = null,

	@field:SerializedName("tgl_expired")
	var tglExpired: String? = null,

	@field:SerializedName("no_rkb")
	var noRkb: String? = null,

	@field:SerializedName("user_entry")
	var userEntry: String? = null,

	@field:SerializedName("sect")
	var sect: String? = null,

	@field:SerializedName("satuan")
	var satuan: String? = null,

	@field:SerializedName("expired_remarks")
	var expiredRemarks: String? = null,

	@field:SerializedName("timelog")
	var timelog: String? = null,

	@field:SerializedName("part_number")
	var partNumber: String? = null,

	@field:SerializedName("remark_cancel")
	var remarkCancel: String? = null,

	@field:SerializedName("part_name")
	var partName: String? = null,

	@field:SerializedName("remarks")
	var remarks: String? = null,

	@field:SerializedName("status")
	var status: Int? = null,

	@field:SerializedName("no_po")
	var noPo: String? = null,

	@field:SerializedName("username")
	var username: String? = null
)
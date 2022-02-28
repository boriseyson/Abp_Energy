package com.misit.abpenergy.Sarpras.SarprasResponse

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DataItem(

	@field:SerializedName("jam_out")
	var jamOut: String? = null,

	@field:SerializedName("keterangan")
	var keterangan: String? = null,

	@field:SerializedName("tgl_peminjaman")
	var tglPeminjaman: String? = null,

	@field:SerializedName("flag")
	var flag: Int? = null,

	@field:SerializedName("tanggal_appr")
	var tanggalAppr: String? = null,

	@field:SerializedName("noid_out")
	var noidOut: String? = null,

	@field:SerializedName("keperluan")
	var keperluan: String? = null,

	@field:SerializedName("tanggal_entry")
	var tanggalEntry: String? = null,

	@field:SerializedName("penumpang_out")
	var penumpangOut: String? = null,

	@field:SerializedName("jam_in")
	var jamIn: String? = null,

	@field:SerializedName("no_lv")
	var noLv: String? = null,

	@field:SerializedName("flag_out")
	var flagOut: String? = null,

	@field:SerializedName("nomor")
	var nomor: String? = null,

	@field:SerializedName("flag_appr")
	var flagAppr: Int? = null,

	@field:SerializedName("nik")
	var nik: String? = null,

	@field:SerializedName("no_pol")
	var noPol: String? = null,

	@field:SerializedName("user_appr")
	var userAppr: String? = null,

	@field:SerializedName("driver")
	var driver: String? = null,

	@field:SerializedName("flag_note")
	var flagNote: String? = null,

	@field:SerializedName("user_entry")
	var userEntry: String? = null,

	@field:SerializedName("tgl_in")
	var tglIn: String? = null,

	@field:SerializedName("tgl_out")
	var tglOut: String? = null,

	@field:SerializedName("keterangan_in")
	var keteranganIn: String? = null,

	@field:SerializedName("edit_at")
	var editAt: String? = null,

	@field:SerializedName("userPemohon")
	var userPemohon: String? = null

)
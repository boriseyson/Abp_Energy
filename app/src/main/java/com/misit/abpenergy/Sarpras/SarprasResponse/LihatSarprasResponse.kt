package com.misit.abpenergy.Sarpras.SarprasResponse

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class LihatSarprasResponse(

	@field:SerializedName("jam_out")
	val jamOut: String? = null,

	@field:SerializedName("keterangan")
	val keterangan: String? = null,

	@field:SerializedName("tgl_peminjaman")
	val tglPeminjaman: String? = null,

	@field:SerializedName("flag")
	val flag: String? = null,

	@field:SerializedName("tanggal_appr")
	val tanggalAppr: String? = null,

	@field:SerializedName("noid_out")
	val noidOut: String? = null,

	@field:SerializedName("keperluan")
	val keperluan: String? = null,

	@field:SerializedName("tanggal_entry")
	val tanggalEntry: String? = null,

	@field:SerializedName("penumpang_out")
	val penumpangOut: String? = null,

	@field:SerializedName("jam_in")
	val jamIn: String? = null,

	@field:SerializedName("no_lv")
	val noLv: String? = null,

	@field:SerializedName("userPemohon")
	val userPemohon: String? = null,

	@field:SerializedName("flag_out")
	val flagOut: String? = null,

	@field:SerializedName("nomor")
	val nomor: String? = null,

	@field:SerializedName("flag_appr")
	val flagAppr: String? = null,

	@field:SerializedName("nik")
	val nik: String? = null,

	@field:SerializedName("no_pol")
	val noPol: String? = null,

	@field:SerializedName("user_appr")
	val userAppr: String? = null,

	@field:SerializedName("driver")
	val driver: String? = null,

	@field:SerializedName("flag_note")
	val flagNote: String? = null,

	@field:SerializedName("user_entry")
	val userEntry: String? = null,

	@field:SerializedName("tgl_in")
	val tglIn: String? = null,

	@field:SerializedName("tgl_out")
	val tglOut: String? = null,

	@field:SerializedName("edit_at")
	val editAt: String? = null
)
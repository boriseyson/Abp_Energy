package com.misit.abpenergy.HSE.HazardReport.SQLite.Modelimport java.io.Serializableclass PengendalianModel : Serializable {    var tglInput: String? = null    var flag: Int? = null    var userInput: String? = null    var namaPengendalian: String? = null    var idHirarki: Int? = null}class PengendalianFullModel : Serializable {    var tglInput: String? = null    var flag: Int? = null    var userInput: String? = null    var namaPengendalian: String? = null    var idHirarki: Int? = null    var listKet: List<DetHirarki>? = null}
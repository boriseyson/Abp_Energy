package com.misit.abpenergy.SQLiteimport android.database.sqlite.SQLiteDatabaseobject dbQuery {    fun tbItemInspeksi(db:SQLiteDatabase?){        db?.execSQL("CREATE TABLE INSPEKSI_ITEM_COUNTER "+                "(ID INTEGER PRIMARY KEY AUTOINCREMENT, "+                "UNIQUEID TEXT,YES INTEGER,NO INTEGER,TOTAL INTEGER)")    }    fun tbpenumpang(db:SQLiteDatabase?){        db?.execSQL("CREATE TABLE PENUMPANG "+                "(id INTEGER,"+                " nik TEXT ,"+                " nama TEXT ,"+                " jabatan TEXT)")    }    fun tbKemungkinan(db:SQLiteDatabase?){        db?.execSQL("CREATE TABLE KEMUNGKINAN "+                "(id INTEGER PRIMARY KEY AUTOINCREMENT,"+                "idKemungkinan INTEGER ,"+                " kemungkinan TEXT ,"+                " flag INTEGER ,"+                " nilai INTEGER)")    }    fun tbKkeparahan(db:SQLiteDatabase?){        db?.execSQL("CREATE TABLE KEPARAHAN "+                "(id INTEGER PRIMARY KEY AUTOINCREMENT,"+                "idKeparahan INTEGER,"+                " keparahan TEXT ,"+                " flag INTEGER ,"+                " nilai INTEGER)")    }    fun tbPerusahaan(db:SQLiteDatabase?){        db?.execSQL("CREATE TABLE PERUSAHAAN "+                "(id INTEGER PRIMARY KEY AUTOINCREMENT,"+                "idPerusahaan INTEGER,"+                " namaPerusahaan TEXT ,"+                " flag INTEGER ,"+                " timeIn TEXT)")    }    fun tbLokasi(db:SQLiteDatabase?){        db?.execSQL("CREATE TABLE LOKASI "+                "(id INTEGER PRIMARY KEY AUTOINCREMENT," +                "idLok INTEGER,"+                " userInput TEXT ,"+                " lokasi TEXT ,"+                " tglInput TEXT)")    }    fun tbPengendalian(db:SQLiteDatabase?) {        db?.execSQL(            "CREATE TABLE PENGENDALIAN " +                    "(id INTEGER PRIMARY KEY AUTOINCREMENT,"+                    "idHirarki INTEGER ," +                    " namaPengendalian TEXT ," +                    " userInput TEXT ," +                    " flag TEXT ," +                    " tglInput TEXT)"        )    }    fun tbUsers(db:SQLiteDatabase?){        db?.execSQL("CREATE TABLE USERS "+                "(id INTEGER PRIMARY KEY AUTOINCREMENT ,"+                "idUser INTEGER ,"+                " tglentry TEXT ,"+                " level TEXT ,"+                " ttd TEXT ,"+                " photoProfile TEXT ,"+                " offlinePhoto TEXT ,"+                " namaLengkap TEXT ,"+                " idSession TEXT ,"+                " rule TEXT ,"+                " perusahaan INTEGER ,"+                " section TEXT ,"+                " namaPerusahaan TEXT ,"+                " dept TEXT ,"+                " nik TEXT ,"+                " password TEXT ,"+                " sect TEXT ,"+                " department TEXT ,"+                " email TEXT ,"+                " username TEXT ,"+                " status INTEGER)")    }    fun tbHazardHeader(db:SQLiteDatabase?){        db?.execSQL("CREATE TABLE HAZARD_HEADER "+                "(idHazard  INTEGER PRIMARY KEY AUTOINCREMENT,"+                " uid TEXT ,"+                " perusahaan TEXT ,"+                " tgl_hazard TEXT ,"+                " jam_hazard TEXT ,"+                " idKemungkinan INTEGER ,"+                " idKeparahan INTEGER ,"+                " deskripsi TEXT ,"+                " lokasi TEXT ,"+                " lokasi_detail INTEGER ,"+                " status_perbaikan TEXT ,"+                " user_input TEXT ,"+                " time_input TEXT ,"+                " status INTEGER)")    }    fun tbHazardDetail(db:SQLiteDatabase?){        db?.execSQL("CREATE TABLE HAZARD_DETAIL "+                "(idHazard INTEGER PRIMARY KEY AUTOINCREMENT,"+                " uid TEXT ,"+                " tindakan TEXT ,"+                " namaPJ TEXT ,"+                " nikPJ TEXT ,"+                " fotoPJ TEXT ,"+                " idKeparahan TEXT ,"+                " katBahaya TEXT ,"+                " idPengendalian INTEGER ,"+                " tgl_selesai TEXT ,"+                " jam_selesai TEXT ,"+                " bukti TEXT ,"+                " update_bukti TEXT ,"+                " keterangan_update TEXT ,"+                " idKemungkinanSesudah INTEGER ,"+                " idKeparahanSesudah INTEGER ,"+                " tgl_tenggat TEXT ,"+                " fotoPJ_option INTEGER)")    }    fun tbHazardValidation(db:SQLiteDatabase?){        db?.execSQL("CREATE TABLE HAZARD_VALIDATION "+                "(idValidation INTEGER PRIMARY KEY AUTOINCREMENT,"+                " uid TEXT ,"+                " user_valid TEXT ,"+                " tgl_valid TEXT ,"+                " jam_valid TEXT)")    }    fun tbRISK(db:SQLiteDatabase?){        db?.execSQL("CREATE TABLE RISK "+                "(idRisk INTEGER PRIMARY KEY AUTOINCREMENT,"+                " risk TEXT ,"+                " descRisk TEXT ,"+                " bgColor TEXT ,"+                " txtColor TEXT ,"+                " userInput TEXT ,"+                " tglInput TEXT)")    }    fun tbDataUser(db:SQLiteDatabase?){        db?.execSQL("CREATE TABLE DATAUSER "+                "(id INTEGER ,"+                " compString INTEGER ,"+                " dept TEXT ,"+                " nik TEXT ,"+                " email TEXT ,"+                " flag INTEGER ,"+                " id_dept TEXT ,"+                " id_perusahaan INTEGER ,"+                " id_sect TEXT ,"+                " department TEXT ,"+                " id_session TEXT ,"+                " id_user INTEGER ,"+                " level TEXT ,"+                " nama_lengkap TEXT ,"+                " nama_perusahaan TEXT ,"+                " password TEXT ,"+                " perusahaan INTEGER ,"+                " photo_profile TEXT ,"+                " rule TEXT ,"+                " sect TEXT ,"+                " section TEXT ,"+                " status INTEGER ,"+                " tglentry TEXT ,"+                " time_in TEXT ,"+                " timelog TEXT ,"+                " ttd TEXT ,"+                " user_entry TEXT ,"+                " username TEXT ,"+                " dataHazard INTEGER ,"+                " dataInspeksi INTEGER)")    }    fun tbHazardHeaderOffline(db:SQLiteDatabase?){        db?.execSQL("CREATE TABLE HAZARD_HEADER_OFFLINE "+                "(idHazard  INTEGER,"+                " uid TEXT ,"+                " perusahaan TEXT ,"+                " tgl_hazard TEXT ,"+                " jam_hazard TEXT ,"+                " idKemungkinan INTEGER ,"+                " idKeparahan INTEGER ,"+                " deskripsi TEXT ,"+                " lokasi TEXT ,"+                " lokasi_detail INTEGER ,"+                " status_perbaikan TEXT ,"+                " user_input TEXT ,"+                " time_input TEXT ,"+                " status INTEGER)")    }    fun tbHazardDetailOffline(db:SQLiteDatabase?){        db?.execSQL("CREATE TABLE HAZARD_DETAIL_OFFLINE "+                "(idHazard INTEGER,"+                " uid TEXT ,"+                " tindakan TEXT ,"+                " namaPJ TEXT ,"+                " nikPJ TEXT ,"+                " fotoPJ TEXT ,"+                " idKeparahan TEXT ,"+                " katBahaya TEXT ,"+                " idPengendalian INTEGER ,"+                " tgl_selesai TEXT ,"+                " jam_selesai TEXT ,"+                " bukti TEXT ,"+                " update_bukti TEXT ,"+                " keterangan_update TEXT ,"+                " idKemungkinanSesudah INTEGER ,"+                " idKeparahanSesudah INTEGER ,"+                " tgl_tenggat TEXT ,"+                " fotoPJ_option INTEGER)")    }    fun tbHazardValidationOffline(db:SQLiteDatabase?){        db?.execSQL("CREATE TABLE HAZARD_VALIDATION_OFFLINE "+                "(idValidation INTEGER,"+                " uid TEXT ,"+                " user_valid TEXT ,"+                " tgl_valid TEXT ,"+                " jam_valid TEXT)")    }}
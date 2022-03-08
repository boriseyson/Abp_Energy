package com.misit.abpenergy.Apiimport com.misit.abpenergy.HSE.Inspeksi.Response.*import com.misit.abpenergy.HSE.HazardReport.Response.ListHazardimport com.misit.abpenergy.HSE.HazardReport.Response.MetrikResponseimport com.misit.abpenergy.Login.Response.*import com.misit.abpenergy.Main.Master.Response.UsersListResponseimport com.misit.abpenergy.Main.Model.NotifGroupResponseimport com.misit.abpenergy.Main.Model.PesanResponseimport com.misit.abpenergy.Main.Model.SuccessResponseimport com.misit.abpenergy.Main.Response.MessageInfoResponseimport com.misit.abpenergy.Main.Response.UserListResponseimport okhttp3.MultipartBodyimport okhttp3.RequestBodyimport retrofit2.Callimport retrofit2.Responseimport retrofit2.http.*interface ApiEndPointTwo {    @GET("/android/api/get/perusahaan")    fun getCompany()            : Call<CompanyResponse>?    @GET("/android/api/get/perusahaan")    suspend fun perusahaanCorutine()            : Response<CompanyResponse>?    @GET("/android/api/get/department")    fun getDepartment(@Query("company") company:String)            : Call<DepartmentResponse>?    @FormUrlEncoded    @POST("/android/api/daftar/akun/baru/mitra")    fun daftarkanAkunMitra(@Field("nik") nik:String?,                            @Field("username") username:String?,                            @Field("password") password:String?,                            @Field("nama") nama:String?,                            @Field("email") email:String?,                            @Field("perusahaan") perusahaan:String?,                            @Field("departemen") departemen:String?,                            @Field("devisi") devisi:String?,                            @Field("_token") csrf_token:String?                        ) : Call<DaftarAkunResponse>?    @Multipart    @POST("/hse/android/inspeksi/new/submit")    fun inspeksiSave(        @Part("idTemp") idTemp: RequestBody?,        @Part("idForm") idForm: RequestBody?,        @Part("user") user: RequestBody?,        @Part("tglInspeksi") tglInspeksi: RequestBody?,        @Part("perusahaan") perusahaan: RequestBody?,        @Part("lokasi") lokasi: RequestBody?,        @Part("saran") saran: RequestBody?,        @Part("_token") csrf_token: RequestBody?    )            : Call<ItemTempResponse>?    @GET("/hse/android/inspeksi/list/user")    fun getInspeksiUser(@Query("userInput") userInput:String,                        @Query("idForm") idForm:String)            : Call<DataInspeksiResponse>?    @GET("/hse/android/inspeksi/pica/detail")    fun listInspeksiPica(@Query("idInspeksi") idInspeksi:String?)            : Call<InspeksiPicaDetailResponse>?    @GET("/hse/android/inspeksi/list/team")    fun teamInspeksi(@Query("idInspeksi") idInspeksi:String?)            : Call<TeamDetailResponse>?    @GET("/hse/android/inspeksi/detail")    fun getListDetInspeksi(@Query("idForm") idForm:String,                           @Query("idInspeksi") idInspeksi:String):            Call<ItemDetailInspeksiResponse>?//   saveNewSandi    @FormUrlEncoded    @POST("/hse/android/ganti/sandi")    fun saveNewSandi(            @Field("username") username:String?,            @Field("oldPass") oldPass:String?,            @Field("newPass") newPass:String?,            @Field("reNewPass") reNewPass:String?,            @Field("_token") csrf_token:String?    ) : Call<DaftarAkunResponse>?//updateProfile    @GET("/hse/android/load/data/profile")    fun updateProfile(@Query("username") username:String):            Call<DataProfileResponse>?//    simpanDataProfile    @FormUrlEncoded    @POST("/hse/android/load/data/profile")    fun simpanDataProfile(        @Field("nik") nik:String?,        @Field("namaLengkap") namaLengkap:String?,        @Field("email") email:String?,        @Field("perusahaan") perusahaan:String?,        @Field("department") department:String?,        @Field("devisi") devisi:String?,        @Field("_token") csrf_token:String?    ) : Call<DaftarAkunResponse>?//    saveCompany@FormUrlEncoded@POST("/hse/android/save/data/company")fun saveCompany(    @Field("perusahaan") perusahaan:String?,    @Field("_token") csrf_token:String?) : Call<DaftarAkunResponse>?//    saveCompany//updateCompany    @FormUrlEncoded    @POST("/hse/android/save/data/company")    fun updateCompany(    @Field("idCompany") idCompany:String?,    @Field("perusahaan") perusahaan:String?,    @Field("_method") _method:String?,        @Field("_token") csrf_token:String?    ) : Call<DaftarAkunResponse>?//    updateCompany//    getListHazardSaya@GET("/android/api/hse/list/hazard/report/saya")fun getListHazardSaya(@Query("nik") nik:String,                      @Query("dari") dari:String,                      @Query("sampai") sampai:String,                      @Query("page") page:String)        : Call<ListHazard>?//getListHazardSaya//    getListHazardSaya@GET("/android/api/hse/list/hazard/report/hse")fun getListHazardHSE(@Query("dari") dari:String,                     @Query("sampai") sampai:String,                     @Query("page") page:String,                     @Query("user_valid") user_valid:Int?)        : Call<ListHazard>?//getListHazardSaya    //    doVerifyHazard    @GET("/android/api/hse/hazard/verify")    fun doVerifyHazard(@Query("option") option:Int,                       @Query("uid") uid:String,                       @Query("username") username:String,                       @Query("keterangan") keterangan:String?)            : Call<DaftarAkunResponse>?//doVerifyHazard//    UPLOAD FOTO PROFILE@Multipart@POST("/android/api/user/foto/profile")fun postFotoProfile(    @Part fileToUpload: MultipartBody.Part,    @Part("nik") nik:RequestBody,    @Part("_token") _token:RequestBody) : Call<DaftarAkunResponse>?    //    doVerifyHazard    @GET("android/api/list/users")    fun getUsersList(@Query("cari") cari:String?,                     @Query("page") page:Int)            : Call<UserListResponse>?    @GET("android/api/list/users/all")    suspend fun userAllCorutine()            : Response<UsersListResponse>?    @GET("android/api/hse/inspeksi/all")    suspend fun allInspectionCorutine(        @Query("page") page:Int)            : Response<ALLInspeksiResponse>?    @GET("android/api/hse/inspeksi/user")    suspend fun userInspectionCorutine(        @Query("page") page:Int,        @Query("nikTeam") nikTeam:String,        @Query("userInput") userInput:String)            : Response<ALLInspeksiResponse>?    @GET("/android/api/matrik/resiko")    suspend fun corotineMatrikResiko() : Response<MetrikResponse>?//UpdateToken    @FormUrlEncoded    @POST("/api/abpenergy/update/token")    suspend fun updatePhoneToken(        @Field("nik") nik:String?,        @Field("phone_token") phone_token:String?,        @Field("app_version") app_version:String?,        @Field("app") app:String?,        @Field("imei") imei:String?    ) : Response<SuccessResponse>?//UpdateToken//Pesan@GET("/api/abpenergy/tenggat/notification/group")    suspend fun pesanNotifikasi() : Response<PesanResponse>?//    Pesan//notif group    @GET("/api/abpenergy/tenggat/notification/group")    suspend fun notifGroup() : Response<NotifGroupResponse>?//notif group//notif verify@GET("/api/abpenergy/hazard/verify")    suspend fun notifVerify() : Response<NotifGroupResponse>?//notif verify@GET("/api/abpenergy/send/notification/to")    suspend fun notifUser(@Query("token") token:String?) : Response<NotifGroupResponse>?    @GET("/api/abpenergy/tenggat/users")    suspend fun tenggatUsers(@Query("token") token:String?) : Response<NotifGroupResponse>?    @GET("/android/api/hse/hazard/delete")    suspend fun deleteHazard(@Query("uid") uid:String?) : Response<SuccessResponse>?    @GET("/android/api/request/password/reset")    suspend fun cekAkun(@Query("login") login:String?) : Response<CekAkunResponse>?    @GET("/android/api/request/password/reset/create")    suspend fun createToken(@Query("login") login:String?) : Response<TokenResponse>?    @Multipart    @POST("/android/api/hazard/rubah/gambar/temuan")    suspend fun updateBuktiSebelum(        @Part bukti_sebelum: MultipartBody.Part,        @Part("uid") uid:RequestBody,        @Part("_token") _token:RequestBody    ) : Response<SuccessResponse>?    @Multipart    @POST("/android/api/hazard/rubah/gambar/perbaikan")    suspend fun updateBuktiSelesai(        @Part bukti_selesai: MultipartBody.Part,        @Part("uid") uid:RequestBody,        @Part("_token") _token:RequestBody    ) : Response<SuccessResponse>?    @Multipart    @POST("/android/api/hazard/rubah/deskripsi")    suspend fun updateDeskripsiHazard(        @Part("uid") uid:RequestBody,        @Part("tipe") tipe:RequestBody,        @Part("deskripsi") deksripsi:RequestBody,        @Part("_token") _token:RequestBody    ) : Response<SuccessResponse>?    @Multipart    @POST("android/api/hazard/rubah/metrik/resiko")    suspend fun updateResiko(        @Part("uid") uid:RequestBody,        @Part("tipe") tipe:RequestBody,        @Part("idResiko") idResiko:RequestBody,        @Part("_token") _token:RequestBody    ) : Response<SuccessResponse>?    @GET("/api/message/info")    suspend fun getMessageInfo() : Response<MessageInfoResponse>?    @FormUrlEncoded    @POST("/api/save/buletin")    suspend fun saveBuletinApi(@Field("judul") judul:String?,                               @Field("pesan") pesan:String?,    ) : Response<SuccessResponse>?    @DELETE("/api/save/buletin")    suspend fun deleteBuletinApi(@Query("idInfo") idInfo:String?    ) : Response<SuccessResponse>?}
package com.misit.abpenergy.Api

import com.misit.abpenergy.HazardReport.Response.*
import com.misit.abpenergy.Inspeksi.Response.*
import com.misit.abpenergy.Login.Response.DaftarAkunResponse
import com.misit.abpenergy.Login.Response.DataUserResponse
import com.misit.abpenergy.Login.Response.SectionResponse
import com.misit.abpenergy.Monitoring_Produksi.Response.ProduksiResponse
import com.misit.abpenergy.Monitoring_Produksi.Response.StockResponse
import com.misit.abpenergy.Response.AbpResponse
import com.misit.abpenergy.Response.GetUserResponse
import com.misit.abpenergy.Sarpras.SaranaResponse.ListSaranaResponse
import com.misit.abpenergy.Rkb.Response.*
import com.misit.abpenergy.Sarpras.SaranaResponse.IzinKeluarSaranaResponse
import com.misit.abpenergy.Sarpras.SarprasResponse.KaryawanResponse
import com.misit.abpenergy.Sarpras.SarprasResponse.LihatSarprasResponse
import com.misit.abpenergy.Sarpras.SarprasResponse.UserSarprasResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiEndPoint{
    @GET("refresh-csrf")
    fun getToken(@Query("refresh-csrf") tokenId:String): Call<CsrfTokenResponse>?

    @FormUrlEncoded
    @POST("/android/api/login/validate")
    fun loginChecklogin(@Field("username") username:String?,
                        @Field("password") password:String?,
                        @Field("_token") csrf_token:String?,
                        @Field("android_token") android_token:String?,
                        @Field("app_version") app_version:String?,
                        @Field("app_name") app_name:String?): Call<UserResponse>

    @GET("api/android/get/rkb/user")
    fun getRkbUser(@Query("username") username:String ,
                   @Query("department") department:String,
                   @Query("close_rkb") close_rkb:String?,
                   @Query("search") search:String?,
                   @Query("disetujui") disetujui:String?,
                   @Query("diketahui") diketahui:String?,
                   @Query("approve") approve:String?,
                   @Query("cancel") cancel:String?,
                   @Query("page") page:String?,
                   @Query("level") level:String?)
            : Call<RkbResponse>?
    @GET("api/android/get/rkb/admin")

    fun getRkbAdmin(@Query("username") username:String ,
                    @Query("department") department:String,
                    @Query("close_rkb") close_rkb:String?,
                    @Query("search") search:String?,
                    @Query("disetujui") disetujui:String?,
                    @Query("diketahui") diketahui:String?,
                    @Query("approve") approve:String?,
                    @Query("cancel") cancel:String?,
                    @Query("page") page:String?,
                    @Query("level") level:String?)
            : Call<RkbResponse>?

    @GET("api/android/get/rkb/detail")
    fun getRkbDetail(@Query("no_rkb") no_rkb:String): Call<DetailRkbResponse>?

    @GET("/v3/rkb")
    fun getRkbKtt(@Query("android") tokenId:String): Call<RkbResponse>?

    @FormUrlEncoded
    @POST("api/android/post/rkb/kabag/approve")
    fun approveKabag(@Field("username") username:String?,
                     @Field("no_rkb") password:String?,
                     @Field("_token") _token:String?): Call<ApproveRkbResponse>

    @FormUrlEncoded
    @POST("api/android/post/rkb/ktt/approve")
    fun approveKTT(@Field("username") username:String?,
                     @Field("no_rkb") password:String?,
                     @Field("_token") _token:String?): Call<ApproveRkbResponse>

    @GET("api/android/app/version")
    fun getAppVersion(@Query("app") app:String?)
            : Call<AppVersionResponse>?

    @FormUrlEncoded
    @POST("api/android/api/cancel/rkb")
    fun cancelRkb(@Field("username") username:String?,
                  @Field("no_rkb") password:String?,
                  @Field("section") section:String?,
                  @Field("remarks") remarks:String?,
                   @Field("_token") _token:String?): Call<CancelRKBResponse>

    @GET("api/android/sarpras/user")
    fun getSarprasUser(@Query("nik") nik:String?,
                       @Query("page") page:Int)
            : Call<UserSarprasResponse>?
    @GET("api/android/sarpras/all")
    fun getSarprasAll(@Query("page") page:Int)
            : Call<UserSarprasResponse>?

    @GET("/sarpras/android/get/sarana")
    fun getAllSarana()
            : Call<ListSaranaResponse>?

    @GET("/sarpras/android/get/sarana")
    suspend fun corutineAllSarana()
            : Response<ListSaranaResponse>

    @GET("/sarpras/android/get/karyawan")
    fun getAllKaryawan()
            : Call<KaryawanResponse>?

    @FormUrlEncoded
    @POST("/sarpras/android/keluar-masuk/post")
    fun keluarSarana(
                    @Field("username") username:String?,
                    @Field("pemohon") pemohon:String?,
                    @Field("no_lv") no_lv:String?,
                    @Field("driver") driver:String?,
                    @Field("no_pol") no_pol:String?,
                    @Field("penumpang[]") penumpang:List<String>?,
                    @Field("keterangan") keterangan:String?,
                    @Field("tgl_keluar") tgl_keluar:String?,
                    @Field("jam_keluar") jam_keluar:String?,
                    @Field("waktu_masuk") waktu_masuk:Boolean?,
                    @Field("tgl_masuk") tgl_masuk:String?,
                    @Field("jam_masuk") jam_masuk:String?,
                    @Field("_token") _token:String?
    ): Call<IzinKeluarSaranaResponse>

    @GET("/sarpras/android/keluar-masuk/kabag")
    fun getSarprasKabag(@Query("dept") dept:String?,
                        @Query("sect") sect:String?,
                        @Query("page") page:Int)
            : Call<UserSarprasResponse>?

    @GET("/api/android/sarpras/user/lihat")
    fun getLihatSarpras(@Query("noid_out") dept:String)
            : Call<LihatSarprasResponse>?

    @GET("abp.php")
    fun cekLokasi(): Call<AbpResponse>?

    @GET("/android/api/get/user")
    fun getDataUser(@Query("username") username:String)
            : Call<GetUserResponse>?

    @GET("/android/api/monitoring/ob")
    fun getOBList(@Query("mtr") mtr:String,
                  @Query("fDate") fDate:String,
                  @Query("lDate") lDate:String)
            : Call<ProduksiResponse>?

    @GET("/android/api/monitoring/stock")
    fun getStockList(@Query("fDate") fDate:String,
                  @Query("lDate") lDate:String)
            : Call<StockResponse>?

    @GET("/hse/admin/hiraiki/pengendalian")
    fun getHirarkiPengendalian()
            : Call<HirarkiResponse>?
    @GET("/hse/admin/resiko/kemungkinan")
    fun resikoKemungkinan()
            : Call<KemungkinanResponse>?
    @GET("/hse/admin/resiko/keparahan")
    fun resikoKeparahan()
            : Call<KeparahanResponse>?

    @Multipart
    @POST("/android/api/hse/hazard/reportPost")
    fun postHazardReport(
        @Part fileToUpload: MultipartBody.Part?,
        @Part fileToUploadPJ: MultipartBody.Part?,
        @Part("perusahaan") perusahaan: RequestBody?,
        @Part("tgl_hazard") tgl_hazard:RequestBody?,
        @Part("jam_hazard") jam_hazard:RequestBody?,
        @Part("lokasi") lokasi:RequestBody?,
        @Part("lokasi_detail") lokasi_detail:RequestBody?,
        @Part("deskripsi") deskripsi:RequestBody?,
        @Part("kemungkinan") kemungkinan:RequestBody?,
        @Part("keparahan") keparahan:RequestBody?,
        @Part("katBahaya") katBahaya:RequestBody?,
        @Part("pengendalian") pengendalian:RequestBody?,
        @Part("tindakan") tindakan:RequestBody?,
        @Part("namaPJ") namaPJ:RequestBody?,
        @Part("nikPJ") nikPJ:RequestBody?,
        @Part("status") status:RequestBody?,
        @Part("tglTenggat") tglTenggat:RequestBody?,
        @Part("user_input") user_input:RequestBody?,
        @Part("_token") _token:RequestBody?
                         ) : Call<HazardReportResponse>?
    @Multipart
    @POST("/android/api/hse/hazard/reportPost/selesai")
    fun postHazardReportSelesai(
        @Part fileToUpload: MultipartBody.Part?,
        @Part fileToUploadPJ: MultipartBody.Part?,
        @Part fileToUploadSelesai: MultipartBody.Part?,
        @Part("perusahaan") perusahaan: RequestBody?,
        @Part("tgl_hazard") tgl_hazard:RequestBody?,
        @Part("jam_hazard") jam_hazard:RequestBody?,
        @Part("lokasi") lokasi:RequestBody?,
        @Part("lokasi_detail") lokasi_detail:RequestBody?,
        @Part("deskripsi") deskripsi:RequestBody?,
        @Part("kemungkinan") kemungkinan:RequestBody?,
        @Part("keparahan") keparahan:RequestBody?,
        @Part("kemungkinanSesudah") kemungkinanSesudah:RequestBody?,
        @Part("keparahanSesudah") keparahanSesudah:RequestBody?,
        @Part("katBahaya") katBahaya:RequestBody?,
        @Part("pengendalian") pengendalian:RequestBody?,
        @Part("tindakan") tindakan:RequestBody?,
        @Part("namaPJ") namaPJ:RequestBody?,
        @Part("nikPJ") nikPJ:RequestBody?,
        @Part("status") status:RequestBody?,
        @Part("tglSelesai") tglSelesai:RequestBody?,
        @Part("jamSelesai") jamSelesai:RequestBody?,
        @Part("keteranganPJ") keteranganPJ:RequestBody?,
        @Part("user_input") user_input:RequestBody?,
        @Part("_token") _token:RequestBody?
    ) : Call<HazardReportResponse>?

    @GET("/android/api/hse/list/hazard/report")
    fun getListHazard(@Query("username") username:String,
                      @Query("dari") dari:String,
                      @Query("sampai") sampai:String,
                      @Query("page") page:String)
            : Call<ListHazard>?
    @GET("/android/api/hse/list/hazard/report/all")
    fun getListHazardAll(@Query("page") page:String)
            : Call<ListHazard>?

    @GET("/android/api/hse/item/hazard/report")
    fun getItemHazard(@Query("uid") uid:String)
            : Call<DetailHazardResponse>?

    @GET("/android/api/user/check/data")
    fun getDataForNewUser(@Query("nik") nik:String)
            : Call<DataUserResponse>?

    @GET("/android/api/check/section/dept")
    fun checkSection(@Query("idDept") idDept:String)
            : Call<SectionResponse>?

    @FormUrlEncoded
    @POST("/android/api/daftar/akun/baru")
    fun daftarkanAkun(@Field("nik") nik:String?,
                      @Field("username") username:String?,
                      @Field("password") password:String?,
                      @Field("nama") nama:String?,
                      @Field("email") email:String?,
                      @Field("departemen") departemen:String?,
                      @Field("devisi") devisi:String?,
                      @Field("_token") csrf_token:String?
    )
            : Call<DaftarAkunResponse>?

    @Multipart
    @POST("/android/api/hse/hazard/report/update/bukti/bergambar")
    fun updateBuktiBergambar(
        @Part fileToUpload: MultipartBody.Part?,
        @Part("uid") uid:RequestBody?,
        @Part("tgl_selesai") tgl_selesai:RequestBody?,
        @Part("jam_selesai") jam_selesai:RequestBody?,
        @Part("idKemungkinanSesudah") idKemungkinanSesudah:RequestBody?,
        @Part("idKeparahanSesudah") idKeparahanSesudah:RequestBody?,
        @Part("keterangan") keterangan:RequestBody?,
        @Part("_token") _token:RequestBody?
    ) : Call<HazardReportResponse>?

    @Multipart
    @POST("/android/api/hse/hazard/report/update/bukti")
    fun updateBukti(
        @Part("uid") uid:RequestBody?,
        @Part("tgl_selesai") tgl_selesai:RequestBody?,
        @Part("jam_selesai") jam_selesai:RequestBody?,
        @Part("keterangan") keterangan:RequestBody?,
        @Part("idKemungkinanSesudah") idKemungkinanSesudah:RequestBody?,
        @Part("idKeparahanSesudah") idKeparahanSesudah:RequestBody?,
        @Part("_token") _token:RequestBody?
    ) : Call<HazardReportResponse>?
    @GET("/android/api/lokasi/get")
    fun getLokasiList(): Call<LokasiResponse>?
    @GET("/android/api/risk/get")
    fun getRiskList(): Call<RiskResponse>?
    @GET("/hse/android/inspeksi/form")
    fun getListFormInspeksi(): Call<FormInspeksiResponse>?
    @GET("/hse/android/inspeksi/new")
    fun getListSubInspeksi(@Query("idForm") idForm:String):
            Call<InspeksiGroupsResponse>?
    @GET("/hse/android/inspeksi/new/item/temp")
    fun itemInspeksiTemp(@Query("idTemp") idTemp:String?,
                         @Query("idForm") idForm:String?,
                         @Query("idItem") idItem:String?,
                         @Query("answer") answer:String?,
                         @Query("user_create") user_create:String?
    )
            : Call<ItemTempResponse>?
    @GET("/hse/android/inspeksi/new/add/team/temp")
    fun addTeamInspeksi(@Query("idTemp") idTemp:String?,
                        @Query("idForm") idForm:String?,
                        @Query("nikTeam") nikTeam:String?
    )
            : Call<ItemTempResponse>?
    @GET("/hse/android/inspeksi/delete/temp")
    fun deleteInspeksiTemp(@Query("idTemp") idTemp:String?)
            : Call<ItemTempResponse>?
    @GET("/hse/android/inspeksi/new/list/team/temp")
    fun teamInspeksiTemp(@Query("idTemp") idTemp:String?)
            : Call<TeamInspeksiTempResponse>?
    @Multipart
    @POST("/hse/android/inspeksi/pica/temp")
    fun inspeksiPicaTemp(
            @Part buktiTemuan: MultipartBody.Part?,
            @Part("idTemp") idTemp:RequestBody?,
            @Part("idForm") idForm:RequestBody?,
            @Part("temuan") temuan:RequestBody?,
            @Part("nikPJ") nikPJ:RequestBody?,
            @Part("namaPJ") namaPJ:RequestBody?,
            @Part("tglTenggat") tglTenggat:RequestBody?,
            @Part("status") status:RequestBody?,
            @Part("_token") csrf_token:RequestBody?    )
            : Call<ItemTempResponse>?
    @GET("/hse/android/inspeksi/pica/temp")
    fun listInspeksiPica(@Query("idTemp") idTemp:String?)
            : Call<ListInspeksiPicaResponse>?

}
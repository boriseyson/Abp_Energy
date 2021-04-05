package com.misit.abpenergy.Response

import com.google.gson.annotations.SerializedName

data class GetUserResponse(
    @field:SerializedName("username")
    var username: String? = null,
    @field:SerializedName("rule")
    var rule: String? = null
)
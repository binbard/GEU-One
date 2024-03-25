package com.binbard.geu.one.models

import com.google.gson.annotations.SerializedName

data class AppUpdate (
    @SerializedName("0") val p0: List<String>? = null,
    @SerializedName("1") val p1: List<String>? = null,
    @SerializedName("2") val p2: List<String>? = null,
    @SerializedName("3") val p3: List<String>? = null,
    @SerializedName("4") val p4: List<String>? = null,
    @SerializedName("5") val p5: List<String>? = null,
)
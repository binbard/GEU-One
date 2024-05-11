package com.binbard.geu.one.models


data class QrScanInput(
    val url: String,
    val token: String,
    val name: String,
    val uid: String,
    val type: String
)

data class QrScanResult(
    val status: String,
    val title: String,
    val msg: String,
    val no: Int
)
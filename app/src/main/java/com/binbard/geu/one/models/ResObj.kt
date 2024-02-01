package com.binbard.geu.one.models

data class ResObj(
    val name: String,
    val url: String,
    val type: String,
)

data class ResSection(
    val title: String,
    val content: List<ResObj>
)

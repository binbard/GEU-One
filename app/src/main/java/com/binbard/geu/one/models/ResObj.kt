package com.binbard.geu.one.models

data class ResObj(
    val name: String,
    val url: String,
    val type: String,
    val options: String? = null,
    val imgUrl: String? = null,
    val onlyFor: String? = null,
)

data class ResSection(
    val title: String,
    val content: List<ResObj>
)

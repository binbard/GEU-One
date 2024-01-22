package com.binbard.geu.one.models

import java.util.*

class FeedPostWrapper(
    val status: String,
    val post: FeedPost
)

class FeedPost(
    val id: Int,
    val slug: String,
    var title: String,
    val date: Date,
    val modified: Date,
    val content: String,
    val attachments: List<Attachment>
)

data class Attachment(
    val id: Int,
    val url: String,
)
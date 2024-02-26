package com.binbard.geu.one.models

import java.text.SimpleDateFormat
import java.util.*

class FeedPostWrapperDeemed(
    val status: String,
    val post: FeedPost
)

class FeedPostMultiWrapperDeemed(
    val status: String,
    val posts: List<FeedPost>
)

class ObjRendered(
    val rendered: String
)
class FeedPostHill(
    val id: Int,
    val slug: String,
    var title: ObjRendered,
    val date: String,
    val modified: String,
    val content: ObjRendered
) {
    fun toFeedPost(): FeedPost {
        val mDate = if (date.contains('T')) {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT).parse(date) ?: Date()
        } else {
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT).parse(date) ?: Date()
        }
        val mModified = if (date.contains('T')) {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT).parse(modified) ?: Date()
        } else {
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT).parse(modified) ?: Date()
        }
        return FeedPost(id, slug, title.rendered, mDate, mModified, content.rendered)
    }
}


class FeedPost(
    val id: Int,
    val slug: String,
    val title: String,
    val date: Date,
    val modified: Date,
    val content: String
)

data class Attachment(
    val id: Int,
    val url: String,
)
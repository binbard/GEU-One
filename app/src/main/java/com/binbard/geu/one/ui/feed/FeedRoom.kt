package com.binbard.geu.one.ui.feed

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

@Entity(tableName = "feeds")
data class FeedEntity(
    @PrimaryKey val id: Int,
    val slug: String,
    val title: String,
    val date: Long
)

@Dao
interface FeedDao {
    @Query("SELECT * FROM feeds ORDER BY date DESC")
    fun getAllFeeds(): List<FeedEntity>

    @Query("SELECT * FROM feeds ORDER BY date DESC LIMIT 10")
    fun getSomeFeeds(): List<FeedEntity>

    @Query("SELECT id FROM feeds ORDER BY date DESC LIMIT 1")
    fun getLatestPostId(): Int

    @Query("SELECT date FROM feeds ORDER BY date DESC LIMIT 1")
    fun getLatestFeedDate(): Long

    @Query("SELECT * FROM feeds WHERE title LIKE :search AND date > :mDate ORDER BY date DESC LIMIT :skip,:limit")
    fun getSearchFeedsPaginated(search: String,skip: Int,limit: Int,mDate: Long): List<FeedEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFeeds(feeds: List<FeedEntity>)
}

@Database(entities = [FeedEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun feedDao(): FeedDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class FeedRepository(private val feedDao: FeedDao) {

    fun getAllFeeds(): List<Feed> {
        return feedDao.getAllFeeds().map { it.toFeed() }
    }

    fun getSomeFeeds(): List<Feed> {
        return feedDao.getSomeFeeds().map { it.toFeed() }
    }

    fun getLatestPostId(): Int {
        return feedDao.getLatestPostId()
    }

    fun getLatestFeedDate(): Date {
        return Date(feedDao.getLatestFeedDate())
    }

    suspend fun getSearchFeedsPaginated(search: String,skip: Int, limit: Int, showAllFeeds: Boolean): List<Feed> {
        val mDate = if(showAllFeeds) 0L else Date().time - 15552000000              // 15552000000 = 6M in ms
        return withContext(Dispatchers.IO) {
            feedDao.getSearchFeedsPaginated("%$search%",skip,limit,mDate).map { it.toFeed() }
        }
    }

    fun insertFeeds(feeds: List<Feed>) {
        feedDao.insertFeeds(feeds.map { it.toFeedEntity() })
    }
}

fun FeedEntity.toFeed(): Feed {
    return Feed(id = this.id, slug = this.slug, title = this.title, date = Date(this.date))
}

fun Feed.toFeedEntity(): FeedEntity {
    return FeedEntity(id = this.id, slug = this.slug, title = this.title, date = this.date.time)
}
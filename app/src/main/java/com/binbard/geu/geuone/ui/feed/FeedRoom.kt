package com.binbard.geu.geuone.ui.feed

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlinx.coroutines.withContext
import java.util.Date

@Entity(tableName = "feeds")
data class FeedEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
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

    @Query("SELECT * FROM feeds ORDER BY date DESC LIMIT :limit OFFSET :skip")
    fun getSearchFeedsPaginated(skip: Int,limit: Int): List<FeedEntity>
    // WHERE slug LIKE :search

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

    fun getAllFeeds(): List<FeedEntity> {
        return feedDao.getAllFeeds()
    }

    fun getSomeFeeds(): List<FeedEntity> {
        return feedDao.getSomeFeeds()
    }

    fun getLatestPostId(): Int {
        return feedDao.getLatestPostId()
    }

    suspend fun getSearchFeedsPaginated(search: String,page: Int, limit: Int): List<Feed> {
        val text = search.split(" ").map { it.lowercase() }.joinToString("-")
        val skip = page * limit
        return withContext(Dispatchers.IO) {
            feedDao.getSearchFeedsPaginated(skip,limit).map { it.toFeed() }     //"%$text%"
        }
    }

    fun insertFeeds(feeds: List<FeedEntity>) {
        feedDao.insertFeeds(feeds)
    }
}

fun FeedEntity.toFeed(): Feed {
    return Feed(id = this.id, slug = this.slug, title = this.title, date = Date(this.date))
}

fun Feed.toFeedEntity(): FeedEntity {
    return FeedEntity(id = this.id, slug = this.slug, title = this.title, date = this.date.time)
}
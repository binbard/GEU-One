package com.binbard.geu.geuone.ui.feed

import android.content.Context
import androidx.room.*

@Entity(tableName = "feeds")
data class FeedEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val link: String,
    val title: String,
    @Embedded
    val date: Feed.FDate
)

@Dao
interface FeedDao {
    @Query("SELECT * FROM feeds")
    fun getAllFeeds(): List<FeedEntity>

    @Query("SELECT * FROM feeds LIMIT 10")
    fun getSomeFeeds(): List<FeedEntity>

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

    fun insertFeeds(feeds: List<FeedEntity>) {
        feedDao.insertFeeds(feeds.mapIndexed { index, feedEntity -> feedEntity.copy(id = index.toLong()) })
    }
}

fun FeedEntity.toFeed(): Feed {
    return Feed(link = this.link, title = this.title, date = this.date)
}

fun Feed.toFeedEntity(): FeedEntity {
    return FeedEntity(link = this.link, title = this.title, date = this.date)
}


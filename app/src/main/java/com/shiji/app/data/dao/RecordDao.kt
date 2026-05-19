package com.shiji.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.shiji.app.data.entity.Record
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {

    @Query("SELECT * FROM records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<Record>>

    @Query("SELECT * FROM records WHERE id = :id")
    suspend fun getRecordById(id: Long): Record?

    @Query("SELECT * FROM records WHERE timestamp BETWEEN :startOfDay AND :endOfDay ORDER BY timestamp DESC")
    fun getRecordsByDay(startOfDay: Long, endOfDay: Long): Flow<List<Record>>

    @Query("SELECT * FROM records ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestRecord(): Record?

    @Query("SELECT * FROM records ORDER BY timestamp ASC LIMIT 1")
    suspend fun getFirstRecord(): Record?

    @Query("SELECT * FROM records ORDER BY timestamp ASC")
    suspend fun getAllRecordsAscending(): List<Record>

    @Query("SELECT COUNT(*) FROM records")
    fun getRecordCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM records WHERE timestamp BETWEEN :startOfDay AND :endOfDay")
    suspend fun getDayCount(startOfDay: Long, endOfDay: Long): Int

    @Query("SELECT * FROM records WHERE timestamp BETWEEN :start AND :end ORDER BY timestamp ASC")
    suspend fun getRecordsBetween(start: Long, end: Long): List<Record>

    @Query("SELECT DISTINCT strftime('%Y-%m-%d', timestamp / 1000, 'unixepoch') as day, COUNT(*) as count FROM records GROUP BY day ORDER BY day")
    fun getDailyCounts(): Flow<List<DayCount>>

    @Insert
    suspend fun insert(record: Record): Long

    @Update
    suspend fun update(record: Record)

    @Query("DELETE FROM records WHERE id = :id")
    suspend fun deleteById(id: Long)
}

data class DayCount(
    val day: String,
    val count: Int
)

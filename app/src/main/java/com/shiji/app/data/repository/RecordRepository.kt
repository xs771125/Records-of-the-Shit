package com.shiji.app.data.repository

import com.shiji.app.data.dao.DayCount
import com.shiji.app.data.dao.RecordDao
import com.shiji.app.data.entity.Record
import kotlinx.coroutines.flow.Flow

class RecordRepository(private val dao: RecordDao) {

    fun getAllRecords(): Flow<List<Record>> = dao.getAllRecords()

    suspend fun getRecordById(id: Long): Record? = dao.getRecordById(id)

    fun getRecordsByDay(startOfDay: Long, endOfDay: Long): Flow<List<Record>> =
        dao.getRecordsByDay(startOfDay, endOfDay)

    suspend fun getLatestRecord(): Record? = dao.getLatestRecord()

    suspend fun getFirstRecord(): Record? = dao.getFirstRecord()

    suspend fun getAllRecordsAscending(): List<Record> = dao.getAllRecordsAscending()

    fun getRecordCount(): Flow<Int> = dao.getRecordCount()

    suspend fun getDayCount(startOfDay: Long, endOfDay: Long): Int =
        dao.getDayCount(startOfDay, endOfDay)

    suspend fun getRecordsBetween(start: Long, end: Long): List<Record> =
        dao.getRecordsBetween(start, end)

    fun getDailyCounts(): Flow<List<DayCount>> = dao.getDailyCounts()

    suspend fun insert(record: Record): Long = dao.insert(record)

    suspend fun update(record: Record) = dao.update(record)

    suspend fun deleteById(id: Long) = dao.deleteById(id)
}

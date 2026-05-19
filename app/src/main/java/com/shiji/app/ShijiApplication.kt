package com.shiji.app

import android.app.Application
import com.shiji.app.data.AppDatabase
import com.shiji.app.data.repository.RecordRepository

class ShijiApplication : Application() {

    lateinit var database: AppDatabase
        private set
    lateinit var repository: RecordRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = AppDatabase.getInstance(this)
        repository = RecordRepository(database.recordDao())
    }

    companion object {
        lateinit var instance: ShijiApplication
            private set
    }
}

package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DocumentEntity::class], version = 1, exportSchema = false)
abstract class DocumentDatabase : RoomDatabase() {
    abstract val documentDao: DocumentDao
}

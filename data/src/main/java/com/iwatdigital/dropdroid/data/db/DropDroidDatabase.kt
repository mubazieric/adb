package com.iwatdigital.dropdroid.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [TransferHistoryEntity::class, TrustedPeerEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(RoomConverters::class)
abstract class DropDroidDatabase : RoomDatabase() {
    abstract fun dao(): DropDroidDao
}

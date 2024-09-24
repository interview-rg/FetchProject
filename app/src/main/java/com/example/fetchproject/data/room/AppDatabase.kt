package com.example.fetchproject.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fetchproject.data.model.Item

@Database(entities = [Item::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
}
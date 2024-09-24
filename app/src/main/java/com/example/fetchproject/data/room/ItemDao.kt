package com.example.fetchproject.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fetchproject.data.model.Item
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<Item>)

    @Query("SELECT * FROM items WHERE name IS NOT NULL AND name != '' ORDER BY listId, name")
    fun getItems(): Flow<List<Item>>
}
package com.example.fetchproject.data

import com.example.fetchproject.data.model.Item
import com.example.fetchproject.data.network.ApiService
import com.example.fetchproject.data.room.ItemDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ItemRepository @Inject constructor(
    private val apiService: ApiService,
    private val itemDao: ItemDao
) {

    fun getItems(): Flow<List<Item>> = itemDao.getItems()

    suspend fun refreshItems() {
        val response = apiService.getItems()
        if (response.isSuccessful) {
            response.body()?.let { items ->
                itemDao.insertItems(items)
            }
        } else {
            throw Exception("Failed to fetch items: ${response.message()}")
        }
    }
}
package com.example.fetchproject.domain.usecase

import com.example.fetchproject.data.ItemRepository
import com.example.fetchproject.data.model.Item
import com.example.fetchproject.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class FetchItemsUseCase @Inject constructor(
    private val repository: ItemRepository
) {
    operator fun invoke(): Flow<Resource<Map<Int, List<Item>>>> = flow {
        emit(Resource.Loading) // Emit loading state
        try {
            repository.refreshItems() // Try to update items from online
            repository.getItems() // Get items from DB
                .map { items ->
                    items
                        .filterValidItems()
                        .groupByListId()
                        .sortItemsByName()
                }
                .collect { groupedItems ->
                    emit(Resource.Success(groupedItems)) // Emit success state with data
                }
        } catch (httpException: HttpException) {
            emit(Resource.Error(httpException)) // Emit error state
        } catch (ioException: IOException) {
            emit(Resource.Error(ioException)) // Emit error state
        } catch (e: Exception) {
            emit(Resource.Error(e)) // Emit error state
        }
    }

    // Helper function to filter valid items
    private fun List<Item>.filterValidItems(): List<Item> {
        return this.filter { !it.name.isNullOrBlank() }
    }

    // Helper function to group items by listId
    private fun List<Item>.groupByListId(): Map<Int, List<Item>> {
        return this.groupBy { it.listId }
    }

    // Helper function to sort items by name
    private fun Map<Int, List<Item>>.sortItemsByName(): Map<Int, List<Item>> {
        return this.mapValues { (_, items) ->
            items.sortedBy { it.name }
        }
    }
}

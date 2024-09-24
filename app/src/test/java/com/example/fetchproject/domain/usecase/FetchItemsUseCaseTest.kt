package com.example.fetchproject.domain.usecase

import com.example.fetchproject.data.ItemRepository
import com.example.fetchproject.data.model.Item
import com.example.fetchproject.util.Resource
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class FetchItemsUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()

    private val repository: ItemRepository = mockk()

    private lateinit var useCase: FetchItemsUseCase

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        useCase = FetchItemsUseCase(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke emits Loading and then Success with processed data`() = runTest {
        // Given
        val items = listOf(
            Item(id = 1, listId = 1, name = "Item B"),
            Item(id = 2, listId = 1, name = "Item A"),
            Item(id = 3, listId = 2, name = null),
            Item(id = 4, listId = 2, name = "Item C"),
            Item(id = 5, listId = 2, name = "")
        )

        val expectedGroupedItems = mapOf(
            1 to listOf(
                Item(id = 2, listId = 1, name = "Item A"),
                Item(id = 1, listId = 1, name = "Item B")
            ),
            2 to listOf(
                Item(id = 4, listId = 2, name = "Item C")
            )
        )

        coEvery { repository.refreshItems() } just Runs
        coEvery { repository.getItems() } returns flowOf(items)

        // When
        val emissions = mutableListOf<Resource<Map<Int, List<Item>>>>()
        val job = launch {
            useCase().toList(emissions)
        }

        advanceUntilIdle()

        // Then
        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Success)

        val successData = (emissions[1] as Resource.Success).data
        assertEquals(expectedGroupedItems, successData)

        coVerify { repository.refreshItems() }
        coVerify { repository.getItems() }

        job.cancel()
    }

    @Test
    fun `invoke emits Loading and then Error when exception occurs`() = runTest {
        // Given
        val exception = Exception("Network error")

        coEvery { repository.refreshItems() } throws exception

        // When
        val emissions = mutableListOf<Resource<Map<Int, List<Item>>>>()
        val job = launch {
            useCase().toList(emissions)
        }

        advanceUntilIdle()

        // Then
        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Error)

        val errorException = (emissions[1] as Resource.Error).exception
        assertEquals(exception, errorException)

        coVerify { repository.refreshItems() }
        coVerify(exactly = 0) { repository.getItems() }

        job.cancel()
    }
}

package com.example.fetchproject.viewmodel

import com.example.fetchproject.data.model.Item
import com.example.fetchproject.domain.usecase.FetchItemsUseCase
import com.example.fetchproject.util.Resource
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class FetchViewModelTest {

    // Use the TestCoroutineDispatcher for controlling coroutine execution
    private val testDispatcher = StandardTestDispatcher()

    @RelaxedMockK
    private lateinit var fetchItemsUseCase: FetchItemsUseCase

    private lateinit var viewModel: FetchViewModel

    @Before
    fun setUp() {
        // Set the Main dispatcher to the test dispatcher
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this, relaxUnitFun = true)
        // Initialize the ViewModel with the mocked use case
        viewModel = FetchViewModel(fetchItemsUseCase)
    }

    @After
    fun tearDown() {
        // Reset the Main dispatcher
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchItems emits Loading state initially`() = runTest {
        // Given
        val flow = flow<Resource<Map<Int, List<Item>>>> {
            emit(Resource.Loading)
        }
        coEvery { fetchItemsUseCase.invoke() } returns flow

        // When
        viewModel = FetchViewModel(fetchItemsUseCase)
        advanceUntilIdle()

        assertEquals(true, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `fetchItems emits Success state with data`() = runTest {
        // Given
        val items = listOf(Item(1, 1, "Item A"), Item(2, 1, "Item B"))
        val groupedItems = mapOf(1 to items)
        val flow = flow {
            emit(Resource.Loading)
            emit(Resource.Success(groupedItems))
        }
        coEvery { fetchItemsUseCase.invoke() } returns flow

        // When
        viewModel = FetchViewModel(fetchItemsUseCase)
        advanceUntilIdle()

        // Then
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(null, viewModel.uiState.value.error)
        assertEquals(groupedItems, viewModel.uiState.value.items)
    }

    @Test
    fun `fetchItems emits Error state when exception occurs`() = runTest {
        // Given
        val exception = Exception("Network error")
        val flow = flow<Resource<Map<Int, List<Item>>>> {
            emit(Resource.Loading)
            emit(Resource.Error(exception))
        }
        coEvery { fetchItemsUseCase.invoke() } returns flow

        // When
        viewModel = FetchViewModel(fetchItemsUseCase)
        advanceUntilIdle()

        // Then
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals("Network error", viewModel.uiState.value.error)
    }
}

package com.example.fetchproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fetchproject.data.model.Item
import com.example.fetchproject.domain.usecase.FetchItemsUseCase
import com.example.fetchproject.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FetchViewModel @Inject constructor(
    private val fetchItemsUseCase: FetchItemsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ItemUiState())
    val uiState: StateFlow<ItemUiState> = _uiState.asStateFlow()

    init {
        fetchItems()
    }

    private fun fetchItems() {
        viewModelScope.launch {
            fetchItemsUseCase()
                .collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            _uiState.value = ItemUiState(isLoading = true)
                        }

                        is Resource.Success -> {
                            _uiState.value = ItemUiState(items = resource.data)
                        }

                        is Resource.Error -> {
                            _uiState.value = ItemUiState(error = resource.exception.message)
                        }
                    }
                }
        }
    }
}

data class ItemUiState(
    val items: Map<Int, List<Item>> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null
)
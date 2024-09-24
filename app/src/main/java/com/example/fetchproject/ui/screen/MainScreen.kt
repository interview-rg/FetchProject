package com.example.fetchproject.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fetchproject.data.model.Item
import com.example.fetchproject.ui.theme.Purple40
import com.example.fetchproject.ui.theme.Typography
import com.example.fetchproject.viewmodel.FetchViewModel

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    viewModel: FetchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        when {
            uiState.isLoading -> {
                LoadingScreen()
            }

            uiState.error != null -> {
                ErrorScreen(errorMessage = uiState.error)
            }

            else -> {
                ItemListScreen(groupedItems = uiState.items)
            }
        }
    }

}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(errorMessage: String?) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Error: $errorMessage")
    }
}

@Composable
fun ItemListScreen(groupedItems: Map<Int, List<Item>>) {
    LazyColumn {
        groupedItems.keys.sorted().forEach { listId ->
            // Header for each group
            item {
                ListHeader(listId = listId)
            }
            // List of items for each group
            items(items = groupedItems[listId] ?: emptyList()) { item ->
                ItemRow(item = item)
            }
        }
    }
}

@Composable
fun ListHeader(listId: Int) {
    Surface(color = Purple40) {
        Text(
            text = "List ID: $listId",
            style = Typography.bodyLarge,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Composable
fun ItemRow(item: Item) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = item.name ?: "No Name",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
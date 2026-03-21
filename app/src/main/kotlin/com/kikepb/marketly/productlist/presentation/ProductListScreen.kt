package com.kikepb.marketly.productlist.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kikepb.marketly.R.string as RS
import com.kikepb.marketly.productlist.domain.model.SortOptionModel
import com.kikepb.marketly.productlist.presentation.ProductListUiState.Error
import com.kikepb.marketly.productlist.presentation.ProductListUiState.Loading
import com.kikepb.marketly.productlist.presentation.ProductListUiState.Success
import com.kikepb.marketly.productlist.presentation.components.MarketlyFilterMenu
import com.kikepb.marketly.productlist.presentation.components.MarketlyHomeTopBar
import com.kikepb.marketly.productlist.presentation.components.MarketlyProductItem

@Composable
fun ProductListRoot(
    productListViewModel: ProductListViewModel = hiltViewModel()
) {
    val state by productListViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val isFiltersVisible by productListViewModel.filtersVisible.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        productListViewModel.events.collect { event ->
            when (event) {
                is ProductListEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(message = event.message)
                }
            }
        }
    }

    ProductListScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        isFilterVisible = isFiltersVisible,
        onCategorySelected = { category -> productListViewModel.setCategory(category = category) },
        onSortSelected = { sortOption -> productListViewModel.setSortOption(sortOption = sortOption) },
        onSetFiltersVisibility = { showFilters -> productListViewModel.setFilterVisible(showFilters = showFilters) }
    )
}

@Composable
fun ProductListScreen(
    state: ProductListUiState,
    snackbarHostState: SnackbarHostState,
    isFilterVisible: Boolean,
    onCategorySelected: (String?) -> Unit,
    onSortSelected: (SortOptionModel?) -> Unit,
    onSetFiltersVisibility: (Boolean) -> Unit
) {
    Scaffold(
        topBar = {
            MarketlyHomeTopBar(
                isFiltersVisible = isFilterVisible,
                onFiltersSelected = { showFilters -> onSetFiltersVisibility(showFilters) },
                onSettingsSelected = {}
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        when (state) {
            Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is Error -> TODO()
            is Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = paddingValues)
                ) {
                    AnimatedVisibility(
                        visible = isFilterVisible,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        MarketlyFilterMenu(
                            state = state,
                            onCategorySelected = onCategorySelected,
                            onSortSelected = onSortSelected
                        )
                    }

                    Text(
                        text = stringResource(RS.marketly_product_list_title, state.products.size),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    if (state.products.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = stringResource(RS.marketly_lens_icon), style = MaterialTheme.typography.displayMedium)
                                Text(
                                    text = "",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    } else {
                        LazyColumn {
                            items(state.products) { item ->
                                MarketlyProductItem(item = item, onClick = {})
                            }
                        }
                    }
                }
            }
        }
    }
}

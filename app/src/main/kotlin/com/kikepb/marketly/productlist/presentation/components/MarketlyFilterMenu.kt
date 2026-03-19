package com.kikepb.marketly.productlist.presentation.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kikepb.marketly.R.string as RS
import com.kikepb.marketly.productlist.domain.model.SortOptionModel
import com.kikepb.marketly.productlist.domain.model.SortOptionModel.DISCOUNT
import com.kikepb.marketly.productlist.domain.model.SortOptionModel.NONE
import com.kikepb.marketly.productlist.domain.model.SortOptionModel.PRICE_ASC
import com.kikepb.marketly.productlist.domain.model.SortOptionModel.PRICE_DESC
import com.kikepb.marketly.productlist.presentation.ProductListUiState.Success

@Composable
fun MarketlyFilterMenu(
    state: Success,
    modifier: Modifier = Modifier,
    onCategorySelected: (String?) -> Unit,
    onSortSelected: (SortOptionModel?) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(all = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = stringResource(RS.marketly_filter_menu_categories))
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(state = rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
            ) {
                FilterChip(
                    selected = state.selectedCategory == null,
                    onClick = { onCategorySelected(null) },
                    label = { Text(text = stringResource(RS.marketly_all_text), style = MaterialTheme.typography.labelSmall)}
                )
                state.categories.forEach { category ->
                    FilterChip(
                        selected = category.equals(other = state.selectedCategory, ignoreCase = true),
                        onClick = { onCategorySelected(category) },
                        label = { Text(text = category, style = MaterialTheme.typography.labelSmall)}
                    )
                }
            }

            HorizontalDivider()

            Text(text = stringResource(RS.marketly_filter_menu_order_by))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
            ) {
                FilterChip(
                    selected = state.sortOption == PRICE_ASC,
                    onClick = { onSortSelected(PRICE_ASC) },
                    label = { Text(text = stringResource(RS.marketly_filter_menu_price_high), style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = state.sortOption == PRICE_DESC,
                    onClick = { onSortSelected(PRICE_DESC) },
                    label = { Text(text = stringResource(RS.marketly_filter_menu_price_low), style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = state.sortOption == DISCOUNT,
                    onClick = { onSortSelected(NONE) },
                    label = { Text(text = stringResource(RS.marketly_filter_menu_discount), style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
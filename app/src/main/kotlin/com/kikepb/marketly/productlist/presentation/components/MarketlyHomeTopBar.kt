package com.kikepb.marketly.productlist.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import com.kikepb.marketly.R.string as RS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketlyHomeTopBar(
    modifier: Modifier = Modifier,
    isFiltersVisible: Boolean = true,
    onFiltersSelected: (Boolean) -> Unit,
    onSettingsSelected: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(RS.marketly_app_name),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = Bold
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        actions = {
            IconButton(onClick = { onFiltersSelected(!isFiltersVisible) }) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = if (isFiltersVisible) stringResource(RS.marketly_home_top_bar_hide_filters) else stringResource(RS.marketly_home_top_bar_show_filter),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            IconButton(onClick = { onSettingsSelected() }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    )
}
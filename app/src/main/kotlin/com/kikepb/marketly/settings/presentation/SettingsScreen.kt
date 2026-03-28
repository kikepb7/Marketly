package com.kikepb.marketly.settings.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kikepb.marketly.core.domain.model.ThemeModeModel
import com.kikepb.marketly.core.domain.model.ThemeModeModel.DARK
import com.kikepb.marketly.core.domain.model.ThemeModeModel.LIGHT
import com.kikepb.marketly.core.domain.model.ThemeModeModel.SYSTEM
import com.kikepb.marketly.core.presentation.components.MarketlyTopAppBar

@Composable
fun SettingsScreenRoot(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by settingsViewModel.state.collectAsStateWithLifecycle()

    SettingsScreen(
        state = state,
        onBack = onBack,
        onCheckInStockOnlyChange = { newState -> settingsViewModel.setInStockOnly(newState = newState) },
        onClickThemeMode = { themeMode -> settingsViewModel.setThemeMode(themeMode = themeMode) }
    )
}

@Composable
fun SettingsScreen(
    state: SettingsState,
    onBack: () -> Unit,
    onCheckInStockOnlyChange: (Boolean) -> Unit,
    onClickThemeMode: (ThemeModeModel) -> Unit
) {
    Scaffold(
        topBar = { MarketlyTopAppBar(title = "Ajustes", onBackSelected = onBack) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = paddingValues)
                .padding(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(space = 16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(size = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Column(
                    modifier = Modifier.padding(all = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(space = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Info",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(size = 24.dp)
                        )
                        Text(
                            text = "Filtros y visualización",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(space = 4.dp)) {
                            Text(
                                text = "Solo productos en stock",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Muestra únicamente productos disponibles",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = state.inStockOnly,
                            onCheckedChange = { onCheckInStockOnlyChange(it) }
                        )
                    }

                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(space = 4.dp)) {
                            Text(
                                text = "Mostrar impuestos incluídos",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Incluir impuestos de los precios mostrados",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = true,
                            onCheckedChange = {}
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(size = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(all = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(space = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.DarkMode,
                            contentDescription = "Info",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(size = 24.dp)
                        )
                        Text(
                            text = "Apariencia",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    HorizontalDivider()

                    Column(verticalArrangement = Arrangement.spacedBy(space = 4.dp)) {
                        Text(
                            text = "Tema de la aplicación",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Elige entre modo claro, oscuro o seguir el sistema",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(height = 4.dp))

                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = 0,
                                    count = 3
                                ),
                                onClick = { onClickThemeMode(LIGHT) },
                                selected = state.themeMode == LIGHT,
                                label = { Text(text = "Claro") }
                            )
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = 1,
                                    count = 3
                                ),
                                onClick = { onClickThemeMode(DARK) },
                                selected = state.themeMode == DARK,
                                label = { Text(text = "Oscuro") }
                            )
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = 2,
                                    count = 3
                                ),
                                onClick = { onClickThemeMode(SYSTEM) },
                                selected = state.themeMode == SYSTEM,
                                label = { Text(text = "Sistema") }
                            )
                        }
                    }
                }
            }
        }
    }
}
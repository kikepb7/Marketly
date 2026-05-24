package com.kikepb.marketly.settings.presentation

import app.cash.turbine.turbineScope
import com.kikepb.marketly.core.dispatcher.MainDispatcherRule
import com.kikepb.marketly.core.domain.model.ThemeModeModel.DARK
import com.kikepb.marketly.core.fakes.FakeSettingsRepository
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `GIVEN repository with values WHEN viewmodel is initialized THEN state is updated`() =
        runTest(context = mainDispatcherRule.scheduler) {
            turbineScope {
                // GIVEN
                val settingsRepository = FakeSettingsRepository().apply { setInStockOnly(value = true) }
                val viewModel = SettingsViewModel(settingsRepository = settingsRepository)

                // WHEN
                val state = viewModel.state.testIn(scope = this)

                // THEN
                assertTrue(state.awaitItem().inStockOnly)
                state.cancelAndIgnoreRemainingEvents()
            }
    }

    @Test
    fun `GIVEN viewmodel WHEN theme mode is changed THEN state state and repository are updated`() =
        runTest(context = mainDispatcherRule.scheduler) {
            turbineScope {
                // GIVEN
                val settingsRepository = FakeSettingsRepository().apply { setInStockOnly(value = true) }
                val viewModel = SettingsViewModel(settingsRepository = settingsRepository)
                val state = viewModel.state.testIn(scope = this)
                state.awaitItem()

                // WHEN
                viewModel.setThemeMode(themeMode = DARK)

                // THEN
                val updateState = state.awaitItem()
                assertEquals(DARK, updateState.themeMode)
                assertEquals(DARK, settingsRepository.themeMode.first())
                state.cancelAndIgnoreRemainingEvents()
            }
    }

    @Test
    fun `GIVEN viewmodel WHEN inStockOnly is changed THEN state state and repository are updated`() =
        runTest(context = mainDispatcherRule.scheduler) {
            turbineScope {
                // GIVEN
                val settingsRepository = FakeSettingsRepository()
                val viewModel = SettingsViewModel(settingsRepository = settingsRepository)
                val state = viewModel.state.testIn(scope = this)
                state.awaitItem()

                // WHEN
                viewModel.setInStockOnly(newState = true)
                val updateState = state.awaitItem()

                // THEN
                assertEquals(true, updateState.inStockOnly)
                assertEquals(true, settingsRepository.inStockOnly.first())
                state.cancelAndIgnoreRemainingEvents()
            }
    }

    @Test
    fun `GIVEN viewmodel WHEN repository change externally THEN state update automatically`() =
        runTest(context = mainDispatcherRule.scheduler) {
            turbineScope {
                // GIVEN
                val settingsRepository = FakeSettingsRepository()
                val viewModel = SettingsViewModel(settingsRepository = settingsRepository)
                val state = viewModel.state.testIn(scope = this)
                state.awaitItem()

                // WHEN
                settingsRepository.setInStockOnly(value = true)

                // THEN
                assertTrue(state.awaitItem().inStockOnly)
                state.cancelAndIgnoreRemainingEvents()
            }
    }
}
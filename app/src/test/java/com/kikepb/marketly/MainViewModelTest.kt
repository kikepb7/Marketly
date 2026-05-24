package com.kikepb.marketly

import app.cash.turbine.test
import com.kikepb.marketly.core.dispatcher.MainDispatcherRule
import com.kikepb.marketly.core.domain.model.ThemeModeModel.DARK
import com.kikepb.marketly.core.domain.model.ThemeModeModel.SYSTEM
import com.kikepb.marketly.core.fakes.FakeSettingsRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun createViewModel(fakeSettingsRepository: FakeSettingsRepository = FakeSettingsRepository()) =
        MainViewModel(settingsRepository = fakeSettingsRepository)

    @Test
    fun `GIVEN repository with dark mode WHEn initialized THEN emits dark theme mode`() =
        runTest(context = mainDispatcherRule.scheduler) {
            // GIVEN
            val fakeSettingsRepository = FakeSettingsRepository().apply { setThemeMode(value = DARK) }
            val viewModel = createViewModel(fakeSettingsRepository = fakeSettingsRepository)

            // WHEN
            viewModel.themeMode.test {
                // THEN
                assertEquals(DARK, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN default repository WHEN initialized THEN emits system theme mode`() =
        runTest(context = mainDispatcherRule.scheduler) {
            // GIVEN
            val viewModel = createViewModel()

            // WHEN
            viewModel.themeMode.test {
                // THEN
                assertEquals(SYSTEM, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }

        }
}
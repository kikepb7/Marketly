package com.kikepb.marketly.core.domain.data.utils

import com.kikepb.marketly.core.domain.utils.DispatchersProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject

class DefaultDispatchersProvider @Inject constructor(): DispatchersProvider {
    override val main: CoroutineDispatcher = Main
    override val io: CoroutineDispatcher = IO
    override val default: CoroutineDispatcher = Default
}
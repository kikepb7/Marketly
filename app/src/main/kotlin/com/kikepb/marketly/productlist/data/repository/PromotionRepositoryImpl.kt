package com.kikepb.marketly.productlist.data.repository

import com.kikepb.marketly.core.domain.utils.DispatchersProvider
import com.kikepb.marketly.productlist.data.local.LocalDataSource
import com.kikepb.marketly.productlist.data.mapper.toPromotionEntity
import com.kikepb.marketly.productlist.data.mapper.toPromotionModel
import com.kikepb.marketly.productlist.data.remote.RemoteDataSource
import com.kikepb.marketly.productlist.domain.model.PromotionModel
import com.kikepb.marketly.productlist.domain.repository.PromotionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

class PromotionRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val dispatchersProvider: DispatchersProvider,
    private val json: Json
) : PromotionRepository {

    private val refreshScope = CoroutineScope(context = SupervisorJob() + dispatchersProvider.io)
    private val refreshMutex = Mutex()

    override fun getActivePromotions(): Flow<List<PromotionModel>> {
        return localDataSource.getAllPromotions()
            .map { entities -> entities.mapNotNull { it.toPromotionModel(json = json) } }
            .onStart {
                refreshScope.launch {
                    if (!refreshMutex.tryLock()) return@launch
                    try {
                        refreshPromotions()
                    } catch (e: Exception) {

                    } finally { refreshMutex.unlock() }
                }
            }
            .catch {
                // Log to track
            }
    }

    override suspend fun refreshPromotions() {
        withContext(dispatchersProvider.io) {
            val promotions = remoteDataSource.getPromotions().getOrThrow()
            val promotionsEntity = promotions.mapNotNull { it.toPromotionEntity(json = json) }
            localDataSource.savePromotions(promotionsEntity = promotionsEntity)
        }
    }
}
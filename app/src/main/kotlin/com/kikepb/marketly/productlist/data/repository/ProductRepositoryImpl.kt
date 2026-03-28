package com.kikepb.marketly.productlist.data.repository

import com.kikepb.marketly.core.domain.utils.DispatchersProvider
import com.kikepb.marketly.productlist.data.local.LocalDataSource
import com.kikepb.marketly.productlist.data.mapper.toProductEntity
import com.kikepb.marketly.productlist.data.mapper.toProductModel
import com.kikepb.marketly.productlist.data.remote.RemoteDataSource
import com.kikepb.marketly.productlist.domain.model.ProductModel
import com.kikepb.marketly.productlist.domain.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProductRepositoryImpl
@Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val dispatchersProvider: DispatchersProvider
): ProductRepository {

    private val refreshScope by lazy { CoroutineScope(SupervisorJob() + dispatchersProvider.io) }
    private val refreshMutex = Mutex()

    override fun getProducts(): Flow<List<ProductModel>> {
        return localDataSource.getAllProducts()
            .map { entities -> entities.mapNotNull { it.toProductModel() } }
            .onStart {
                refreshScope.launch {
                    if (!refreshMutex.tryLock()) return@launch
                    try {
                        refreshProducts()
                    } catch (e: Exception) {

                    } finally {
                        refreshMutex.unlock()
                    }

                    /*try {
                        refreshMutex.withLock { refreshProducts() }
                    } catch (e: Exception) {
                        // Log to track
                    }*/
                }
            }
            .catch {
                // Log to track
            }
    }

    override fun getProductById(productId: String): Flow<ProductModel?> =
        localDataSource.getProductById(productId = productId)
            .map { entity -> entity?.toProductModel() }

    override suspend fun refreshProducts() {
        withContext(dispatchersProvider.io) {
            val products = remoteDataSource.getProducts().getOrThrow()
            val productsEntity = products.map { it.toProductEntity() }

            localDataSource.saveProducts(productsEntity = productsEntity)
        }
    }
}
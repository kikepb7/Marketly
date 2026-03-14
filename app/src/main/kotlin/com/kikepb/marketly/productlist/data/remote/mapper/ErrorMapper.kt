package com.kikepb.marketly.productlist.data.remote.mapper

import com.kikepb.marketly.core.domain.model.AppError
import com.kikepb.marketly.core.domain.model.AppError.NetworkError
import com.kikepb.marketly.core.domain.model.AppError.NotFoundError
import com.kikepb.marketly.core.domain.model.AppError.UnknownError
import okio.IOException
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun mapToDomainError(e: Exception): AppError =
    when(e) {
        is UnknownHostException, is SocketTimeoutException, is IOException -> NetworkError
        is HttpException -> {
            when (e.code()) {
                404 -> NotFoundError
                else -> NetworkError
            }
        }
        else -> UnknownError(message = e.message)
    }
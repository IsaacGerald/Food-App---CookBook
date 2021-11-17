package com.example.myapplication.domain.util

import kotlinx.coroutines.flow.*

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean

) = flow {

    emit(DataState.Loading())

    val data = query().first()

    val flow = if (shouldFetch(data)) {
        emit(DataState.Loading(data))

        try {
            saveFetchResult(fetch())
            query().map { DataState.Success(it) }
        } catch (throwable: Throwable) {
            query().map { DataState.Error(throwable, it) }
        }

    } else {
        query().map { DataState.Success(it) }
    }

    emitAll(flow)
}
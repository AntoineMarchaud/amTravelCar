package com.amarchaud.travelcar.data.remote

import retrofit2.Response
import java.net.SocketTimeoutException

inline fun <reified T> apiResult(response: Response<T>): Result<T> {
    return try {
        when (response.isSuccessful) {
            true -> {
                return if ((response.code() == 200 || response.code() == 201) && response.body() != null) {
                    Result.success(response.body()!!)
                } else if (response.code() == 204) {
                    when (T::class) {
                        Unit::class -> Result.success(Unit as T)
                        Boolean::class -> Result.success(true as T)
                        else -> Result.failure(ErrorApiDataModel.ApiNullBody())
                    }
                } else {
                    Result.failure(ErrorApiDataModel.ApiNullBody())
                }
            }

            false -> {
                Result.failure(
                    ErrorApiDataModel.ApiServerErrorWithCode(
                        responseCode = response.code(),
                        responseMessage = response.message()
                    )
                )
            }
        }
    } catch (e: Throwable) {
        Result.failure(
            when (e) {
                is SocketTimeoutException -> ErrorApiDataModel.SocketTimeOutError()
                is ErrorApiDataModel.NoInternetError -> ErrorApiDataModel.NoInternetError() // raised by HttpInterceptor
                else -> ErrorApiDataModel.ApiGenericServerError()
            }
        )
    }
}
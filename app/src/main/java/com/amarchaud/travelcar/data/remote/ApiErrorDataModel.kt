package com.amarchaud.travelcar.data.remote

sealed class ErrorApiDataModel : Throwable() {
    class ApiServerErrorWithCode(val responseCode: Int, val responseMessage: String) : ErrorApiDataModel()
    class ApiNullBody : ErrorApiDataModel()
    class ApiGenericServerError : ErrorApiDataModel()
    class SocketTimeOutError : ErrorApiDataModel()
    class NoInternetError : ErrorApiDataModel()
}

package com.doyoonkim.model.util

sealed class KnuticeNetworkException : Throwable() {

    // General Exception (100 - 199, 300 - 399)
    data class GeneralException(val code: Int, val reason: String) : KnuticeNetworkException()

    // Client-Side Exception (400 - 499)
    data class ClientSideException(val code: Int, val reason: String) : KnuticeNetworkException()

    // Server-Side Exception (500 - 599)
    data class ServerSideException(val code: Int, val reason: String) : KnuticeNetworkException()
}

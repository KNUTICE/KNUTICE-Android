package com.doyoonkim.network.util

import com.doyoonkim.model.util.KnuticeNetworkException
import model.Metadata

/**
 * @author kimdoyoon
 * Created 3/1/26 at 6:57 PM
 */
object NetworkResultUtil {

    fun resultCodeValidator(result: Metadata?): Throwable {
        val resultCode = result?.resultCode ?: 0
        val reason = result?.resultMessage ?: "Unknown Reason"

        return when (resultCode) {
            in 400..499 -> KnuticeNetworkException.ClientSideException(resultCode, reason)
            in 500..599 -> KnuticeNetworkException.ServerSideException(resultCode, reason)
            else -> {
                KnuticeNetworkException.GeneralException(resultCode, reason)
            }
        }
    }

}
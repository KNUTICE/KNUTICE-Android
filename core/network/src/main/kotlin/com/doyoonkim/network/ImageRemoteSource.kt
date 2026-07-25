package com.doyoonkim.network

import java.net.URL
import javax.inject.Inject

/**
 * @author kimdoyoon
 * Created 6/17/25 at 12:42 AM
 */
class ImageRemoteSource @Inject constructor() {
    suspend fun getByteArrayFromImageUrl(url: String): Result<ByteArray> {
        return runCatching {
            URL(url).readBytes()
        }.onFailure { throw it }
    }
}

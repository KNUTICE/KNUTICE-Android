package com.doyoonkim.data.repository.remote

import android.util.Log
import com.doyoonkim.domain.interfaces.ImageRemoteRepository
import com.doyoonkim.network.ImageRemoteSource
import javax.inject.Inject

/**
 * @author kimdoyoon
 * Created 6/17/25 at 12:41 AM
 */
class ImageRepositoryImpl @Inject constructor(
    private val remoteSource: ImageRemoteSource
) : ImageRemoteRepository {
    override suspend fun getImageByteArrayFromUrl(url: String): ByteArray? {
        remoteSource.getByteArrayFromImageUrl(url)
            .fold(
                onSuccess = {
                    return it
                },
                onFailure = {
                    Log.d(
                        "ImageRepositoryImpl",
                        "Unable to get ByteArray from the given url\nREASON: ${it.stackTrace}"
                    )
                    return null
                }
            )
    }
}

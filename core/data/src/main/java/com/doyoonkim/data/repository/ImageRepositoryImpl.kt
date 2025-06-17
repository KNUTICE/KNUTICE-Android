package com.doyoonkim.data.repository

import android.util.Log
import com.doyoonkim.domain.ImageRepository
import com.doyoonkim.network.ImageRemoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * @author kimdoyoon
 * Created 6/17/25 at 12:41 AM
 */
class ImageRepositoryImpl @Inject constructor(
    private val remoteSource: ImageRemoteSource
) : ImageRepository {
    override fun getImageByteArrayFromUrl(url: String): Flow<ByteArray?> = flow {
        remoteSource.getByteArrayFromImageUrl(url)
            .fold(
                onSuccess = {
                    emit(it)
                },
                onFailure = {
                    Log.d(
                        "ImageRepositoryImpl",
                        "Unable to get ByteArray\nREASON: ${it.stackTrace}"
                    )
                    emit(null)
                }
            )
    }
}
package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.LocalRepository
import com.doyoonkim.domain.RemoteRepository
import com.doyoonkim.model.BookmarkVO
import com.doyoonkim.model.NoticeVO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.withTimeout
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

interface SyncDataWithUpdateDatabase {

    fun syncBookmark(legacy: BookmarkVO): Flow<BookmarkVO>

    fun syncNotice(legacy: NoticeVO): Flow<NoticeVO>

}

class SyncDataWithUpdatedDatabaseImpl @Inject constructor(
    private val localRepository: LocalRepository,
    private val remoteRepository: RemoteRepository
) : SyncDataWithUpdateDatabase {

    override fun syncBookmark(legacy: BookmarkVO): Flow<BookmarkVO> {
        TODO("Not yet implemented")
    }

    override fun syncNotice(legacy: NoticeVO): Flow<NoticeVO> = flow {
        println("Need Sync: ${legacy.noticeName} ${legacy.noticeName.isBlank()}")
        if (legacy.noticeName.isBlank()) {
            println("Sync Initiated")
            runCatching {
                withTimeout(5000L) {
                    println("Request Notice with timeout 5sec")
                    emitAll(remoteRepository.queryNoticeById(legacy.nttId).transform { vo ->
                        println("Received VO: ${vo.toString()}")
                        vo?.let {
                            emit(
                                // Reason: Legacy has a entity id value, which vo does not.
                                legacy.copy(
                                    noticeName = vo.noticeName
                                )
                            )
                        }
                    })
                }
            }.onFailure {
                // Failure with either network error or timeout error.
                println("Timeout")
                emit(legacy)
            }
        } else {
            emit(legacy)
        }
    }

}
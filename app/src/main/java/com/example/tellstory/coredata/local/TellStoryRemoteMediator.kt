package com.example.tellstory.coredata.local

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.tellstory.common.UserDataPreferences
import com.example.tellstory.coredata.model.MainStory
import com.example.tellstory.coredata.remote.ApiService

@OptIn(ExperimentalPagingApi::class)
class TellStoryRemoteMediator(
    private val database: TellStoryDatabase,
    private val apiServices: ApiService,
    private val pref: UserDataPreferences
) : RemoteMediator<Int, MainStory>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
        private const val BEARER = "Bearer "
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MainStory>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        val listStories = mutableListOf<MainStory>()
        try {
            val token = pref.getToken()
            val bearerToken = BEARER+token
            val response = apiServices.listStories(
                token = bearerToken,
                page = page,
                size = state.config.pageSize,
                location = 0
            )

            val endOfPaginationReached = response.body()?.listStory.isNullOrEmpty()

            response.body()?.listStory?.forEach {
                listStories.add(
                    MainStory(
                        id = it.id,
                        name = it.name,
                        createdAt = it.createdAt,
                        photoUrl = it.photoUrl,
                        description = it.description,
                        lat = null,
                        lon = null
                    )
                )
            }

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().deleteRemoteKeys()
                    database.tellStoryDao().deleteAll()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = listStories.map {
                    RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }

                database.remoteKeysDao().insertAll(keys)
                database.tellStoryDao().insertStory(response.body()?.listStory ?: listOf())
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, MainStory>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, MainStory>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, MainStory>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

}
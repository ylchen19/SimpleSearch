package com.example.simplesearch.data


import android.util.Log
import androidx.paging.*
import com.example.simplesearch.api.Client
import com.example.simplesearch.db.ReposDataBase
import kotlinx.coroutines.flow.Flow

class Repository(private val client: Client, private val dataBase: ReposDataBase) {

    fun getPagingRepo(queryString: String): Flow<PagingData<Item>> {
        Log.d("Repository", "New query: $queryString")

        val dbQuery = "%${queryString.replace(' ', '%')}%"
        val pagingSourceFactory = {dataBase.reposDao().reposByName(dbQuery)}

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(NETWORK_PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = SearchRemoteMediator(
                queryString, client, dataBase
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 50
    }
}

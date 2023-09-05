package com.example.simplesearch.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.simplesearch.api.Client
import okio.IOException
import retrofit2.HttpException

//** Not use ** //

class RepoPagingSource(
    private val repoSearch: String,
    private val client: Client): PagingSource<Int, Item>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Item> {
        val page = params.key ?: 1
        val pageSize = params.loadSize
        return try {
            Log.d("pagingData", "success")
            val prevKey = if(page > 1) page - 1 else null
            val request = client.githubConnect.search(repoSearch, page, pageSize)
            val repoItems = request.items
            val nextKey = if(repoItems.isNotEmpty()) page + 1 else null
            LoadResult.Page(repoItems, prevKey, nextKey)
        }catch (exception: IOException) {
            Log.d("pagingSource", "IOException: $exception")
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            Log.d("pagingSource", "HttpException: $exception")
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Item>): Int?
    {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
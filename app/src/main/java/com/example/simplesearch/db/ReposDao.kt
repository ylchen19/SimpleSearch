package com.example.simplesearch.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.simplesearch.data.Item

@Dao
interface ReposDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repos: List<Item>)

    @Query(
        "SELECT * FROM repoItems WHERE " +
                "full_name LIKE :queryString OR description LIKE :queryString " +
                "ORDER BY full_name ASC"
    )
    fun reposByName(queryString: String): PagingSource<Int, Item>

    @Query("DELETE FROM repoItems")
    suspend fun clearRepos()
}
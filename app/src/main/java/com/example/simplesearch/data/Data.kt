package com.example.simplesearch.data

data class Data(
    val incomplete_results: Boolean,
    val items: List<Item>,
    val total_count: Int
)
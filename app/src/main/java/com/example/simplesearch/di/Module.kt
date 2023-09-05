package com.example.simplesearch.di

import com.example.simplesearch.api.Client
import com.example.simplesearch.data.Repository
import com.example.simplesearch.db.ReposDataBase
import com.example.simplesearch.viewmodel.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf

val Module =  module {



    single {
        ReposDataBase.getInstance(androidContext())
    }

    single {
        get<ReposDataBase>().reposDao()
        get<ReposDataBase>().remoteKeysDao()
    }

    singleOf(::Client)
    singleOf(::Repository)
    viewModelOf(::MainViewModel)

}
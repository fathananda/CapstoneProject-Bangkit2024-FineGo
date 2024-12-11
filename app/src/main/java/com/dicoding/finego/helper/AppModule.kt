package com.dicoding.finego.helper

import com.dicoding.finego.api.ApiClient

object AppModule {
    fun provideProfileRepository(): Repository {
        return Repository(ApiClient.instance)
    }
}
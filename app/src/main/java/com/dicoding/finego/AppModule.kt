package com.dicoding.finego

import com.dicoding.finego.api.ApiClient

object AppModule {
    fun provideProfileRepository(): Repository {
        return Repository(ApiClient.instance)
    }
}
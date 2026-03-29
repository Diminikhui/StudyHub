package com.secondbrain.mobile

import com.secondbrain.mobile.data.RawNoteRepository
import com.secondbrain.mobile.data.RawNoteRepositoryImpl
import com.secondbrain.mobile.network.ApiClient

class AppContainer {
    val repository: RawNoteRepository by lazy {
        RawNoteRepositoryImpl(ApiClient.api)
    }
}
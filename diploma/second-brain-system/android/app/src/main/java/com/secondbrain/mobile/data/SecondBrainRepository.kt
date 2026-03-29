package com.secondbrain.mobile.data

import com.secondbrain.mobile.network.SecondBrainApi

class SecondBrainRepository(
    api: SecondBrainApi
) : RawNoteRepository by RawNoteRepositoryImpl(api)
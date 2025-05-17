package com.practicum.playlistmaker.share.di

import com.practicum.playlistmaker.share.domain.ShareInteractor
import com.practicum.playlistmaker.share.domain.impl.ShareInteractorImpl
import org.koin.dsl.module

val shareInteractorModule = module {

    single<ShareInteractor> {
        ShareInteractorImpl(get())
    }

}

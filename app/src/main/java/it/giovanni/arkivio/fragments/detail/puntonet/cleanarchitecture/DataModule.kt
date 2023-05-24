package it.giovanni.arkivio.fragments.detail.puntonet.cleanarchitecture

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    /**
     * Provides an instance of RickMortyDataSource, which is an implementation of RickMortyRepository
     * that retrieves the data from a remote API.
     */
    @Provides
    @Singleton
    fun provideRickMortyDataSource(repository: RickMortyRepository): RickMortyDataSource {
        return repository
    }
}
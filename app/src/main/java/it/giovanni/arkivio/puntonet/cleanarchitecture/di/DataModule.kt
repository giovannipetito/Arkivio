package it.giovanni.arkivio.puntonet.cleanarchitecture.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import it.giovanni.arkivio.puntonet.cleanarchitecture.data.datasource.remote.RickMortyDataSource
import it.giovanni.arkivio.puntonet.cleanarchitecture.data.repository.RickMortyRepository
import javax.inject.Singleton

/**
 * - DataModule è un modulo Dagger (@Module) responsabile della fornitura di dipendenze relative
 *   al User Layer.
 * - La funzione provideRickMortyDataSource è annotata con @Provides e @Singleton. Fornisce
 *   un'istanza di RickMortyDataSource iniettando RickMortyRepository. RickMortyDataSource è
 *   un'implementazione di RickMortyRepository che recupera i dati dall'API remota. Specifica
 *   cioè come creare un'istanza di RickMortyDataSource utilizzando la dipendenza iniettata.
 */
@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideRickMortyDataSource(repository: RickMortyRepository): RickMortyDataSource {
        return repository
    }
}
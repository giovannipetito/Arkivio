package it.giovanni.arkivio.fragments.detail.puntonet.paging

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn

class PagingViewModel /* constructor(private val apiService: ApiService) */ : ViewModel() {

    val listData = Pager(PagingConfig(pageSize = 1)) {
        RickyMortyPagingSource(/* apiService */)

    }.flow.cachedIn(viewModelScope)
}
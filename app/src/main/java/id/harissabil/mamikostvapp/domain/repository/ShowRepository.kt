package id.harissabil.mamikostvapp.domain.repository

import androidx.paging.PagingData
import id.harissabil.mamikostvapp.domain.model.Show
import id.harissabil.mamikostvapp.domain.model.ShowDetail
import kotlinx.coroutines.flow.Flow

interface ShowRepository {
    fun getShows(): Flow<PagingData<Show>>

    suspend fun getShowDetail(id: Int): ShowDetail
}

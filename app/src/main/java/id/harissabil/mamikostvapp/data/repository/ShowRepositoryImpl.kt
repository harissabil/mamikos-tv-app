package id.harissabil.mamikostvapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import id.harissabil.mamikostvapp.data.mapper.toDomain
import id.harissabil.mamikostvapp.data.remote.ShowsPagingSource
import id.harissabil.mamikostvapp.data.remote.TvMazeApi
import id.harissabil.mamikostvapp.domain.model.Show
import id.harissabil.mamikostvapp.domain.model.ShowDetail
import id.harissabil.mamikostvapp.domain.repository.ShowRepository
import kotlinx.coroutines.flow.Flow

class ShowRepositoryImpl(private val api: TvMazeApi) : ShowRepository {

    override fun getShows(): Flow<PagingData<Show>> =
        Pager(
            config = PagingConfig(
                pageSize = TVMAZE_FIXED_PAGE_SIZE,
                initialLoadSize = TVMAZE_FIXED_PAGE_SIZE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = { ShowsPagingSource(api) },
        ).flow

    override suspend fun getShowDetail(id: Int): ShowDetail = api.getShowDetail(id).toDomain()

    private companion object {
        const val TVMAZE_FIXED_PAGE_SIZE: Int = 250
    }
}

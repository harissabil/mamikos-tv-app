package id.harissabil.mamikostvapp.fake

import id.harissabil.mamikostvapp.domain.model.Show
import id.harissabil.mamikostvapp.domain.model.ShowDetail
import id.harissabil.mamikostvapp.domain.repository.ShowRepository
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeShowRepository : ShowRepository {

    /** Swap between tests (and between calls) to return a show or throw. */
    var showDetailResponse: () -> ShowDetail = { error("showDetailResponse not set") }

    var showDetailCallCount: Int = 0
        private set

    override fun getShows(): Flow<PagingData<Show>> = flowOf(PagingData.empty())

    override suspend fun getShowDetail(id: Int): ShowDetail {
        showDetailCallCount++
        return showDetailResponse()
    }
}

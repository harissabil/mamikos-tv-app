package id.harissabil.mamikostvapp.domain.usecase

import androidx.paging.PagingData
import id.harissabil.mamikostvapp.domain.model.Show
import id.harissabil.mamikostvapp.domain.repository.ShowRepository
import kotlinx.coroutines.flow.Flow

class GetShowsUseCase(private val repository: ShowRepository) {
    operator fun invoke(): Flow<PagingData<Show>> = repository.getShows()
}

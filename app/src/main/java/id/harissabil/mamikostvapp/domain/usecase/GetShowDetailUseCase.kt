package id.harissabil.mamikostvapp.domain.usecase

import id.harissabil.mamikostvapp.domain.model.ShowDetail
import id.harissabil.mamikostvapp.domain.repository.ShowRepository

class GetShowDetailUseCase(private val repository: ShowRepository) {
    suspend operator fun invoke(id: Int): ShowDetail = repository.getShowDetail(id)
}

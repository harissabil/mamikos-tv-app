package id.harissabil.mamikostvapp.presentation.screen.showdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.harissabil.mamikostvapp.domain.usecase.GetShowDetailUseCase
import id.harissabil.mamikostvapp.presentation.common.toUserMessage
import id.harissabil.mamikostvapp.presentation.navigation.ShowDetailRoute
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
class ShowDetailViewModel(
    private val route: ShowDetailRoute,
    private val getShowDetail: GetShowDetailUseCase,
) : ViewModel() {

    private val retries = MutableStateFlow(0)

    val state: StateFlow<ShowDetailUiState> = retries
        .flatMapLatest { showDetailStream() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = ShowDetailUiState.Loading,
        )

    fun retry() {
        retries.update { it + 1 }
    }

    private fun showDetailStream(): Flow<ShowDetailUiState> =
        flow<ShowDetailUiState> {
            emit(ShowDetailUiState.Success(getShowDetail(route.showId)))
        }
            .onStart { emit(ShowDetailUiState.Loading) }
            .catch { throwable -> emit(ShowDetailUiState.Error(throwable.toUserMessage())) }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}

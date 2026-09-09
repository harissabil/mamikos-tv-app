package id.harissabil.mamikostvapp.presentation.screen.showdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.harissabil.mamikostvapp.domain.usecase.GetShowDetailUseCase
import id.harissabil.mamikostvapp.presentation.common.toUserMessage
import id.harissabil.mamikostvapp.presentation.navigation.ShowDetailRoute
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShowDetailViewModel(
    private val route: ShowDetailRoute,
    private val getShowDetail: GetShowDetailUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<ShowDetailUiState>(ShowDetailUiState.Loading)
    val state: StateFlow<ShowDetailUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun retry() = load()

    private fun load() {
        _state.value = ShowDetailUiState.Loading
        viewModelScope.launch {
            _state.value = try {
                ShowDetailUiState.Success(getShowDetail(route.showId))
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (failure: Exception) {
                ShowDetailUiState.Error(failure.toUserMessage())
            }
        }
    }
}

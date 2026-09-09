package id.harissabil.mamikostvapp.presentation.screen.showdetail

import id.harissabil.mamikostvapp.domain.model.ShowDetail

sealed interface ShowDetailUiState {
    data object Loading : ShowDetailUiState
    data class Error(val message: String) : ShowDetailUiState
    data class Success(val show: ShowDetail) : ShowDetailUiState
}

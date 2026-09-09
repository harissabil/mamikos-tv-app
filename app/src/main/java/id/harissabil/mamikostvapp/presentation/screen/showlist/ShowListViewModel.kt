package id.harissabil.mamikostvapp.presentation.screen.showlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import id.harissabil.mamikostvapp.domain.model.Show
import id.harissabil.mamikostvapp.domain.usecase.GetShowsUseCase
import kotlinx.coroutines.flow.Flow

class ShowListViewModel(getShows: GetShowsUseCase) : ViewModel() {

    val shows: Flow<PagingData<Show>> = getShows().cachedIn(viewModelScope)
}

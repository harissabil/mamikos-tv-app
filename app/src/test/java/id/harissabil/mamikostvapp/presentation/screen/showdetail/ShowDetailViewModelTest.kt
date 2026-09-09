package id.harissabil.mamikostvapp.presentation.screen.showdetail

import app.cash.turbine.test
import id.harissabil.mamikostvapp.domain.model.ShowDetail
import id.harissabil.mamikostvapp.domain.usecase.GetShowDetailUseCase
import id.harissabil.mamikostvapp.fake.FakeShowRepository
import id.harissabil.mamikostvapp.presentation.navigation.ShowDetailRoute
import id.harissabil.mamikostvapp.util.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class ShowDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeShowRepository()

    private val show = ShowDetail(
        id = SHOW_ID,
        name = "Dark",
        posterUrl = null,
        summary = null,
        premiered = null,
        genres = emptyList(),
        tvMazeUrl = "https://www.tvmaze.com/shows/$SHOW_ID",
        seasonCount = 3,
        episodeCount = 26,
        cast = emptyList(),
    )

    private fun viewModel() = ShowDetailViewModel(
        route = ShowDetailRoute(showId = SHOW_ID),
        getShowDetail = GetShowDetailUseCase(repository),
    )

    @Test
    fun `emits Loading then Success`() = runTest(mainDispatcherRule.testDispatcher) {
        repository.showDetailResponse = { show }

        viewModel().state.test {
            assertEquals(ShowDetailUiState.Loading, awaitItem())
            assertEquals(ShowDetailUiState.Success(show), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits Loading then Error when the fetch fails`() = runTest(mainDispatcherRule.testDispatcher) {
        repository.showDetailResponse = { throw IOException("offline") }

        viewModel().state.test {
            assertEquals(ShowDetailUiState.Loading, awaitItem())
            assertTrue(awaitItem() is ShowDetailUiState.Error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `retry after an error emits Success`() = runTest(mainDispatcherRule.testDispatcher) {
        repository.showDetailResponse = { throw IOException("offline") }
        val viewModel = viewModel()

        viewModel.state.test {
            assertEquals(ShowDetailUiState.Loading, awaitItem())
            assertTrue(awaitItem() is ShowDetailUiState.Error)

            repository.showDetailResponse = { show }
            viewModel.retry()

            assertEquals(ShowDetailUiState.Loading, awaitItem())
            assertEquals(ShowDetailUiState.Success(show), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    private companion object {
        const val SHOW_ID = 42
    }
}

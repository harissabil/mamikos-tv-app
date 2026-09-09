package id.harissabil.mamikostvapp.presentation.screen.showlist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import id.harissabil.mamikostvapp.domain.model.Show
import id.harissabil.mamikostvapp.presentation.common.ErrorState
import id.harissabil.mamikostvapp.presentation.common.LoadingState
import id.harissabil.mamikostvapp.presentation.common.toUserMessage
import id.harissabil.mamikostvapp.presentation.screen.showlist.components.ShowGrid
import id.harissabil.mamikostvapp.ui.theme.MamikosTvTheme
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowListScreen(
    onShowClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShowListViewModel = koinViewModel(),
) {
    val shows = viewModel.shows.collectAsLazyPagingItems()

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text(text = "TV Shows") }) },
    ) { innerPadding ->
        ShowListContent(
            shows = shows,
            onShowClick = onShowClick,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun ShowListContent(
    shows: LazyPagingItems<Show>,
    onShowClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val refresh = shows.loadState.refresh
    val isEmpty = refresh is LoadState.NotLoading && shows.itemCount == 0

    Box(modifier = modifier.fillMaxSize()) {
        when {
            refresh is LoadState.Loading -> LoadingState()
            refresh is LoadState.Error -> ErrorState(
                message = refresh.error.toUserMessage(),
                onRetry = shows::retry,
            )

            isEmpty -> Text(
                text = "No shows to display.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Center).padding(24.dp),
            )

            else -> ShowGrid(shows = shows, onShowClick = onShowClick)
        }
    }
}

@Preview
@Composable
private fun ShowListContentPreview() {
    val shows = MutableStateFlow(
        PagingData.from(
            listOf(
                Show(id = 1, name = "Under the Dome", posterUrl = null, rating = 6.6),
                Show(id = 2, name = "Person of Interest", posterUrl = null, rating = 8.8),
                Show(id = 3, name = "A Show Without A Rating", posterUrl = null, rating = null),
            ),
        ),
    ).collectAsLazyPagingItems()

    MamikosTvTheme {
        Surface {
            ShowListContent(shows = shows, onShowClick = {})
        }
    }
}

package id.harissabil.mamikostvapp.presentation.screen.showdetail

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.harissabil.mamikostvapp.domain.model.CastMember
import id.harissabil.mamikostvapp.domain.model.ShowDetail
import id.harissabil.mamikostvapp.presentation.common.ErrorState
import id.harissabil.mamikostvapp.presentation.common.LoadingState
import id.harissabil.mamikostvapp.presentation.screen.showdetail.components.ShowDetailContent
import id.harissabil.mamikostvapp.ui.theme.MamikosTvTheme
import java.time.LocalDate

@Composable
fun ShowDetailScreen(
    viewModel: ShowDetailViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val shareableShow = (state as? ShowDetailUiState.Success)?.show

    ShowDetailScreen(
        state = state,
        onBack = onBack,
        onRetry = viewModel::retry,
        onShare = { shareableShow?.let { context.startActivity(shareShowIntent(it)) } },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShowDetailScreen(
    state: ShowDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val successShow = (state as? ShowDetailUiState.Success)?.show

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = successShow?.name ?: "Show details",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onShare, enabled = successShow != null) {
                        Icon(imageVector = Icons.Filled.Share, contentDescription = "Share")
                    }
                },
            )
        },
    ) { innerPadding ->
        when (state) {
            is ShowDetailUiState.Loading -> LoadingState(Modifier.padding(innerPadding))
            is ShowDetailUiState.Error -> ErrorState(
                message = state.message,
                onRetry = onRetry,
                modifier = Modifier.padding(innerPadding),
            )

            is ShowDetailUiState.Success -> ShowDetailContent(
                show = state.show,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

private val previewShow = ShowDetail(
    id = 1,
    name = "Under the Dome",
    posterUrl = null,
    summary = "A small town is suddenly sealed off from the rest of the world by a massive transparent dome.",
    premiered = LocalDate.of(2013, 6, 24),
    genres = listOf("Drama", "Science-Fiction", "Thriller"),
    tvMazeUrl = "https://www.tvmaze.com/shows/1/under-the-dome",
    seasonCount = 3,
    episodeCount = 39,
    cast = listOf(
        CastMember(personId = 1, name = "Mike Vogel", characterName = "Dale Barbara", imageUrl = null),
        CastMember(personId = 2, name = "Rachelle Lefevre", characterName = null, imageUrl = null),
    ),
)

@Preview
@Composable
private fun ShowDetailScreenSuccessPreview() {
    MamikosTvTheme {
        ShowDetailScreen(
            state = ShowDetailUiState.Success(previewShow),
            onBack = {},
            onRetry = {},
            onShare = {},
        )
    }
}

@Preview
@Composable
private fun ShowDetailScreenLoadingPreview() {
    MamikosTvTheme {
        ShowDetailScreen(
            state = ShowDetailUiState.Loading,
            onBack = {},
            onRetry = {},
            onShare = {},
        )
    }
}

@Preview
@Composable
private fun ShowDetailScreenErrorPreview() {
    MamikosTvTheme {
        ShowDetailScreen(
            state = ShowDetailUiState.Error("You appear to be offline. Check your connection and try again."),
            onBack = {},
            onRetry = {},
            onShare = {},
        )
    }
}

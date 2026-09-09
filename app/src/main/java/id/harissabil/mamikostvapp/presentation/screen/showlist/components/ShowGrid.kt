package id.harissabil.mamikostvapp.presentation.screen.showlist.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import id.harissabil.mamikostvapp.domain.model.Show

@Composable
internal fun ShowGrid(
    shows: LazyPagingItems<Show>,
    onShowClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            count = shows.itemCount,
            key = shows.itemKey { it.id },
        ) { index ->
            shows[index]?.let { show ->
                ShowCard(show = show, onClick = { onShowClick(show.id) })
            }
        }

        when (shows.loadState.append) {
            is LoadState.Loading -> fullSpanItem { AppendLoading() }
            is LoadState.Error -> fullSpanItem { AppendError(onRetry = shows::retry) }
            is LoadState.NotLoading -> Unit
        }
    }
}

private inline fun LazyGridScope.fullSpanItem(
    crossinline content: @Composable () -> Unit,
) {
    item(span = { GridItemSpan(maxLineSpan) }) { content() }
}

@Composable
private fun AppendLoading() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun AppendError(onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        TextButton(onClick = onRetry) {
            Text(text = "Couldn't load more. Retry")
        }
    }
}

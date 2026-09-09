package id.harissabil.mamikostvapp.presentation.screen.showlist.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.harissabil.mamikostvapp.domain.model.Show
import id.harissabil.mamikostvapp.presentation.common.PosterImage
import id.harissabil.mamikostvapp.ui.theme.MamikosTvTheme

private const val POSTER_ASPECT_RATIO = 2f / 3f

@Composable
internal fun ShowCard(
    show: Show,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Column {
            PosterImage(
                url = show.posterUrl,
                contentDescription = show.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(POSTER_ASPECT_RATIO),
            )
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = show.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = show.rating.toRatingLabel(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private fun Double?.toRatingLabel(): String =
    if (this == null) "Not rated" else "★ " + "%.1f".format(this)

@Preview(widthDp = 180)
@Composable
private fun ShowCardPreview() {
    MamikosTvTheme {
        ShowCard(
            show = Show(id = 1, name = "Under the Dome", posterUrl = null, rating = 6.6),
            onClick = {},
        )
    }
}

@Preview(widthDp = 180)
@Composable
private fun ShowCardNoRatingPreview() {
    MamikosTvTheme {
        ShowCard(
            show = Show(id = 2, name = "A Show With A Rather Long Title To Clip", posterUrl = null, rating = null),
            onClick = {},
        )
    }
}

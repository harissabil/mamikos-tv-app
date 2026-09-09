package id.harissabil.mamikostvapp.presentation.screen.showdetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.harissabil.mamikostvapp.domain.model.CastMember
import id.harissabil.mamikostvapp.domain.model.ShowDetail
import id.harissabil.mamikostvapp.presentation.common.PosterImage
import id.harissabil.mamikostvapp.ui.theme.MamikosTvTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ShowDetailContent(
    show: ShowDetail,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        PosterImage(
            url = show.posterUrl,
            contentDescription = show.name,
            modifier = Modifier
                .width(200.dp)
                .aspectRatio(2f / 3f)
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(12.dp)),
        )

        Text(text = show.name, style = MaterialTheme.typography.headlineSmall)

        Text(
            text = show.premiered?.let { "Premiered ${it.formatMedium()}" } ?: "Premiere date unknown",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Text(
            text = "${show.seasonCount} seasons · ${show.episodeCount} episodes",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        if (show.genres.isEmpty()) {
            Text(
                text = "No genres listed",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                show.genres.forEach { genre ->
                    AssistChip(onClick = {}, label = { Text(genre) })
                }
            }
        }

        Text(
            text = show.summary ?: "No summary available.",
            style = MaterialTheme.typography.bodyLarge,
        )

        Text(text = "Cast", style = MaterialTheme.typography.titleMedium)
        if (show.cast.isEmpty()) {
            Text(
                text = "No cast information.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            CastRow(cast = show.cast)
        }
    }
}

private fun LocalDate.formatMedium(): String =
    format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))

@Preview
@Composable
private fun ShowDetailContentPreview() {
    MamikosTvTheme {
        ShowDetailContent(
            show = ShowDetail(
                id = 1,
                name = "Under the Dome",
                posterUrl = null,
                summary = "A small town is suddenly sealed off from the rest of the world by a massive transparent dome.\n\nThe residents must survive while searching for answers.",
                premiered = LocalDate.of(2013, 6, 24),
                genres = listOf("Drama", "Science-Fiction", "Thriller"),
                tvMazeUrl = "https://www.tvmaze.com/shows/1/under-the-dome",
                seasonCount = 3,
                episodeCount = 39,
                cast = listOf(
                    CastMember(personId = 1, name = "Mike Vogel", characterName = "Dale Barbara", imageUrl = null),
                    CastMember(personId = 2, name = "Rachelle Lefevre", characterName = null, imageUrl = null),
                ),
            ),
        )
    }
}

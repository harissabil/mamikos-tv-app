package id.harissabil.mamikostvapp.data.mapper

import android.util.Log
import id.harissabil.mamikostvapp.data.remote.dto.CastCreditDto
import id.harissabil.mamikostvapp.data.remote.dto.ImageDto
import id.harissabil.mamikostvapp.data.remote.dto.ShowDetailDto
import id.harissabil.mamikostvapp.data.remote.dto.ShowDto
import id.harissabil.mamikostvapp.domain.model.CastMember
import id.harissabil.mamikostvapp.domain.model.Show
import id.harissabil.mamikostvapp.domain.model.ShowDetail
import java.time.LocalDate
import java.time.format.DateTimeParseException

fun ShowDto.toDomain(): Show = Show(
    id = id,
    name = name,
    posterUrl = image?.medium,
    rating = rating?.average,
)

fun ShowDetailDto.toDomain(): ShowDetail {
    val episodes = embedded?.episodes.orEmpty()
    return ShowDetail(
        id = id,
        name = name,
        posterUrl = image.largest(),
        summary = summary?.let(::htmlToPlainText),
        premiered = premiered?.let(::parseApiDate),
        genres = genres,
        tvMazeUrl = url,
        seasonCount = episodes.mapNotNull { it.season }.distinct().size,
        episodeCount = episodes.size,
        cast = embedded?.cast.orEmpty().map(CastCreditDto::toDomain),
    )
}

fun CastCreditDto.toDomain(): CastMember = CastMember(
    personId = person.id,
    name = person.name,
    characterName = character?.name,
    imageUrl = person.image?.medium,
)

private fun ImageDto?.largest(): String? = this?.original ?: this?.medium

private fun parseApiDate(raw: String): LocalDate? =
    try {
        LocalDate.parse(raw)
    } catch (e: DateTimeParseException) {
        Log.e("ShowMappers", "Failed to parse date: $raw", e)
        null
    }

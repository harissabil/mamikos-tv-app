package id.harissabil.mamikostvapp.domain.model

import java.time.LocalDate

data class ShowDetail(
    val id: Int,
    val name: String,
    val posterUrl: String?,
    val summary: String?,
    val premiered: LocalDate?,
    val genres: List<String>,
    val tvMazeUrl: String,
    val seasonCount: Int,
    val episodeCount: Int,
    val cast: List<CastMember>,
)

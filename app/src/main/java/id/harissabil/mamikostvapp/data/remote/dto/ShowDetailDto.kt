package id.harissabil.mamikostvapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShowDetailDto(
    val id: Int,
    val name: String,
    val summary: String? = null,
    val premiered: String? = null,
    val genres: List<String> = emptyList(),
    val url: String,
    val rating: RatingDto? = null,
    val image: ImageDto? = null,
    @SerialName("_embedded") val embedded: EmbeddedDto? = null,
)

@Serializable
data class EmbeddedDto(
    val cast: List<CastCreditDto> = emptyList(),
    val episodes: List<EpisodeDto> = emptyList(),
)

@Serializable
data class CastCreditDto(
    val person: PersonDto,
    val character: CharacterDto? = null,
)

@Serializable
data class PersonDto(
    val id: Int,
    val name: String,
    val image: ImageDto? = null,
)

@Serializable
data class CharacterDto(
    val id: Int,
    val name: String,
    val image: ImageDto? = null,
)

@Serializable
data class EpisodeDto(
    val id: Int,
    val name: String? = null,
    val season: Int? = null,
    val number: Int? = null,
)

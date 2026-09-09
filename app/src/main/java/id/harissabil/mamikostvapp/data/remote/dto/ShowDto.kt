package id.harissabil.mamikostvapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ShowDto(
    val id: Int,
    val name: String,
    val summary: String? = null,
    val premiered: String? = null,
    val genres: List<String> = emptyList(),
    val url: String,
    val rating: RatingDto? = null,
    val image: ImageDto? = null,
)

@Serializable
data class RatingDto(
    val average: Double? = null,
)

@Serializable
data class ImageDto(
    val medium: String? = null,
    val original: String? = null,
)

package id.harissabil.mamikostvapp.data.mapper

import id.harissabil.mamikostvapp.data.remote.dto.CastCreditDto
import id.harissabil.mamikostvapp.data.remote.dto.CharacterDto
import id.harissabil.mamikostvapp.data.remote.dto.EmbeddedDto
import id.harissabil.mamikostvapp.data.remote.dto.EpisodeDto
import id.harissabil.mamikostvapp.data.remote.dto.ImageDto
import id.harissabil.mamikostvapp.data.remote.dto.PersonDto
import id.harissabil.mamikostvapp.data.remote.dto.ShowDetailDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class ShowMappersTest {

    @Test
    fun `season count is the number of distinct seasons across episodes`() {
        val domain = detailDto(
            episodes = listOf(episode(1), episode(1), episode(2), episode(null)),
        ).toDomain()

        assertEquals(2, domain.seasonCount)
        assertEquals(4, domain.episodeCount)
    }

    @Test
    fun `poster prefers the original image and falls back to medium then null`() {
        assertEquals(
            "orig",
            detailDto(image = ImageDto(medium = "med", original = "orig")).toDomain().posterUrl
        )
        assertEquals(
            "med",
            detailDto(image = ImageDto(medium = "med", original = null)).toDomain().posterUrl
        )
        assertNull(detailDto(image = null).toDomain().posterUrl)
    }

    @Test
    fun `premiered is parsed and unparseable or missing dates become null`() {
        assertEquals(
            LocalDate.of(2013, 6, 24),
            detailDto(premiered = "2013-06-24").toDomain().premiered
        )
        assertNull(detailDto(premiered = "not-a-date").toDomain().premiered)
        assertNull(detailDto(premiered = null).toDomain().premiered)
    }

    @Test
    fun `summary html is converted to plain text`() {
        assertEquals(
            "Hello world",
            detailDto(summary = "<p>Hello <b>world</b></p>").toDomain().summary
        )
    }

    @Test
    fun `cast keeps the actor name and allows a missing character name`() {
        val cast = detailDto(
            cast = listOf(
                castCredit(personId = 1, actor = "Mike Vogel", character = "Barbie"),
                castCredit(personId = 2, actor = "Rachelle Lefevre", character = null),
            ),
        ).toDomain().cast

        assertEquals(2, cast.size)
        assertEquals("Mike Vogel", cast[0].name)
        assertEquals("Barbie", cast[0].characterName)
        assertNull(cast[1].characterName)
    }

    private fun detailDto(
        summary: String? = null,
        premiered: String? = null,
        genres: List<String> = emptyList(),
        image: ImageDto? = null,
        episodes: List<EpisodeDto> = emptyList(),
        cast: List<CastCreditDto> = emptyList(),
    ): ShowDetailDto = ShowDetailDto(
        id = 1,
        name = "Under the Dome",
        summary = summary,
        premiered = premiered,
        genres = genres,
        url = "https://www.tvmaze.com/shows/1",
        rating = null,
        image = image,
        embedded = EmbeddedDto(cast = cast, episodes = episodes),
    )

    private fun episode(season: Int?): EpisodeDto = EpisodeDto(id = 0, season = season)

    private fun castCredit(personId: Int, actor: String, character: String?): CastCreditDto =
        CastCreditDto(
            person = PersonDto(id = personId, name = actor),
            character = character?.let { CharacterDto(id = personId, name = it) },
        )
}

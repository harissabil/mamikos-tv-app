package id.harissabil.mamikostvapp.fake

import id.harissabil.mamikostvapp.data.remote.TvMazeApi
import id.harissabil.mamikostvapp.data.remote.dto.ShowDetailDto
import id.harissabil.mamikostvapp.data.remote.dto.ShowDto

class FakeTvMazeApi : TvMazeApi {

    var showsResponse: (page: Int) -> List<ShowDto> = { emptyList() }
    var showDetailResponse: (id: Int) -> ShowDetailDto = { error("showDetailResponse not set") }

    override suspend fun getShows(page: Int): List<ShowDto> = showsResponse(page)

    override suspend fun getShowDetail(id: Int, embed: List<String>): ShowDetailDto =
        showDetailResponse(id)
}

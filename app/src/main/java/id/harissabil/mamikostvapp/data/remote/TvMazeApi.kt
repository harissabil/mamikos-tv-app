package id.harissabil.mamikostvapp.data.remote

import id.harissabil.mamikostvapp.data.remote.dto.ShowDetailDto
import id.harissabil.mamikostvapp.data.remote.dto.ShowDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TvMazeApi {

    @GET("shows")
    suspend fun getShows(@Query("page") page: Int): List<ShowDto>

    @GET("shows/{id}")
    suspend fun getShowDetail(
        @Path("id") id: Int,
        @Query("embed[]") embed: List<String> = listOf("cast", "episodes"),
    ): ShowDetailDto
}

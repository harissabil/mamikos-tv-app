package id.harissabil.mamikostvapp.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import id.harissabil.mamikostvapp.data.mapper.toDomain
import id.harissabil.mamikostvapp.domain.model.Show
import retrofit2.HttpException
import java.io.IOException

class ShowsPagingSource(private val api: TvMazeApi) : PagingSource<Int, Show>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Show> {
        val page = params.key ?: FIRST_PAGE
        return try {
            val shows = api.getShows(page).map { it.toDomain() }
            LoadResult.Page(
                data = shows,
                prevKey = page.previousPageOrNull(),
                nextKey = if (shows.isEmpty()) null else page + 1,
            )
        } catch (httpException: HttpException) {
            if (httpException.isEndOfPagination()) {
                endOfPaginationPage(page)
            } else {
                LoadResult.Error(httpException)
            }
        } catch (ioException: IOException) {
            LoadResult.Error(ioException)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Show>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val closestPage = state.closestPageToPosition(anchorPosition)
            closestPage?.prevKey?.plus(1) ?: closestPage?.nextKey?.minus(1)
        }

    private fun HttpException.isEndOfPagination(): Boolean = code() == HTTP_NOT_FOUND

    private fun endOfPaginationPage(page: Int): LoadResult.Page<Int, Show> =
        LoadResult.Page(
            data = emptyList(),
            prevKey = page.previousPageOrNull(),
            nextKey = null,
        )

    private fun Int.previousPageOrNull(): Int? = if (this == FIRST_PAGE) null else this - 1

    private companion object {
        const val FIRST_PAGE: Int = 0
        const val HTTP_NOT_FOUND: Int = 404
    }
}

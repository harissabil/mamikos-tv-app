package id.harissabil.mamikostvapp.data.remote

import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import id.harissabil.mamikostvapp.data.remote.dto.ShowDto
import id.harissabil.mamikostvapp.fake.FakeTvMazeApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ShowsPagingSourceTest {

    private val api = FakeTvMazeApi()

    private fun refresh(): LoadParams<Int> =
        LoadParams.Refresh(key = null, loadSize = PAGE_SIZE, placeholdersEnabled = false)

    private fun append(key: Int): LoadParams<Int> =
        LoadParams.Append(key = key, loadSize = PAGE_SIZE, placeholdersEnabled = false)

    @Test
    fun `first page has a null prevKey and points at the next page`() = runTest {
        api.showsResponse = { listOf(showDto(1), showDto(2)) }

        val result = ShowsPagingSource(api).load(refresh())

        assertTrue(result is LoadResult.Page)
        result as LoadResult.Page
        assertEquals(2, result.data.size)
        assertEquals(null, result.prevKey)
        assertEquals(1, result.nextKey)
    }

    @Test
    fun `a 404 ends pagination instead of failing`() = runTest {
        api.showsResponse = { throw httpException(404) }

        val result = ShowsPagingSource(api).load(append(key = 5))

        assertTrue(result is LoadResult.Page)
        result as LoadResult.Page
        assertTrue(result.data.isEmpty())
        assertEquals(null, result.nextKey)
        assertEquals(4, result.prevKey)
    }

    @Test
    fun `a non-404 http error is a load error`() = runTest {
        api.showsResponse = { throw httpException(500) }

        assertTrue(ShowsPagingSource(api).load(refresh()) is LoadResult.Error)
    }

    @Test
    fun `a network error is a load error`() = runTest {
        api.showsResponse = { throw IOException("offline") }

        assertTrue(ShowsPagingSource(api).load(refresh()) is LoadResult.Error)
    }

    private fun showDto(id: Int): ShowDto =
        ShowDto(id = id, name = "Show $id", url = "https://www.tvmaze.com/shows/$id")

    private fun httpException(code: Int): HttpException =
        HttpException(Response.error<Any>(code, "".toResponseBody(null)))

    private companion object {
        const val PAGE_SIZE = 250
    }
}

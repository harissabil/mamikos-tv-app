package id.harissabil.mamikostvapp.di

import id.harissabil.mamikostvapp.BuildConfig
import id.harissabil.mamikostvapp.data.remote.TvMazeApi
import id.harissabil.mamikostvapp.data.repository.ShowRepositoryImpl
import id.harissabil.mamikostvapp.domain.repository.ShowRepository
import id.harissabil.mamikostvapp.domain.usecase.GetShowDetailUseCase
import id.harissabil.mamikostvapp.domain.usecase.GetShowsUseCase
import id.harissabil.mamikostvapp.presentation.navigation.ShowDetailRoute
import id.harissabil.mamikostvapp.presentation.screen.showdetail.ShowDetailViewModel
import id.harissabil.mamikostvapp.presentation.screen.showlist.ShowListViewModel
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

private const val TVMAZE_BASE_URL = "https://api.tvmaze.com/"
private const val TIMEOUT_SECONDS = 30L

val networkModule = module {
    single {
        Json { ignoreUnknownKeys = true }
    }
    single {
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }
    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }
    single {
        Retrofit.Builder()
            .baseUrl(TVMAZE_BASE_URL)
            .client(get())
            .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaType()))
            .build()
    }
    single<TvMazeApi> {
        get<Retrofit>().create(TvMazeApi::class.java)
    }
}

val dataModule = module {
    single { ShowRepositoryImpl(get()) } bind ShowRepository::class
}

val domainModule = module {
    factoryOf(::GetShowsUseCase)
    factoryOf(::GetShowDetailUseCase)
}

val presentationModule = module {
    viewModel { ShowListViewModel(get()) }
    viewModel { (route: ShowDetailRoute) -> ShowDetailViewModel(route, get()) }
}

val appModules = listOf(networkModule, dataModule, domainModule, presentationModule)

package id.harissabil.mamikostvapp.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object ShowListRoute : NavKey

@Serializable
data class ShowDetailRoute(val showId: Int) : NavKey

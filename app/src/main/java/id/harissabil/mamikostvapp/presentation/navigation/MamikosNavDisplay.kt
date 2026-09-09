package id.harissabil.mamikostvapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import id.harissabil.mamikostvapp.presentation.screen.showdetail.ShowDetailScreen
import id.harissabil.mamikostvapp.presentation.screen.showlist.ShowListScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MamikosNavDisplay() {
    val backStack = rememberNavBackStack(ShowListRoute)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<ShowListRoute> {
                ShowListScreen(
                    onShowClick = { showId -> backStack.add(ShowDetailRoute(showId)) },
                )
            }
            entry<ShowDetailRoute> { route ->
                ShowDetailScreen(
                    viewModel = koinViewModel { parametersOf(route) },
                    onBack = dropUnlessResumed { backStack.removeLastOrNull() },
                )
            }
        },
    )
}

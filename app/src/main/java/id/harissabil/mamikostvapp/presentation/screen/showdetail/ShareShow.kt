package id.harissabil.mamikostvapp.presentation.screen.showdetail

import android.content.Intent
import id.harissabil.mamikostvapp.domain.model.ShowDetail

fun shareShowIntent(show: ShowDetail): Intent {
    val body = buildString {
        append(show.name)
        show.summary?.let { summary ->
            append("\n\n")
            append(summary)
        }
        append("\n\n")
        append(show.tvMazeUrl)
    }
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, show.name)
        putExtra(Intent.EXTRA_TEXT, body)
    }
    return Intent.createChooser(sendIntent, null)
}

package id.harissabil.mamikostvapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import id.harissabil.mamikostvapp.presentation.navigation.MamikosNavDisplay
import id.harissabil.mamikostvapp.ui.theme.MamikosTvTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            MamikosTvTheme {
                MamikosNavDisplay()
            }
        }
    }
}

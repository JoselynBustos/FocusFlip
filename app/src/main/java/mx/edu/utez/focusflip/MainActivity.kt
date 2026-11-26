package mx.edu.utez.focusflip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import mx.edu.utez.focusflip.navigation.AppNavGraph
import mx.edu.utez.focusflip.ui.theme.FocusFlipTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appContainer = (application as FocusFlipApplication).appContainer
        setContent {
            FocusFlipTheme {
                AppNavGraph(appContainer = appContainer)
            }
        }
    }
}

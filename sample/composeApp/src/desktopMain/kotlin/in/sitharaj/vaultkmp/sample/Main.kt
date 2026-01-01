package `in`.sitharaj.vaultkmp.sample

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "VaultKMP Sample",
    ) {
        App()
    }
}

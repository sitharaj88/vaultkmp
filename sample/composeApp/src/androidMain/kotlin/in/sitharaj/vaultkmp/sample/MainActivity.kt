package `in`.sitharaj.vaultkmp.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import `in`.sitharaj.vaultkmp.VaultInitializer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize VaultKMP with Android context
        VaultInitializer.initialize(applicationContext)
        
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}

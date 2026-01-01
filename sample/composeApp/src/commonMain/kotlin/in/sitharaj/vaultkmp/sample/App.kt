package `in`.sitharaj.vaultkmp.sample

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import `in`.sitharaj.vaultkmp.EncryptionLevel
import `in`.sitharaj.vaultkmp.VaultConfig
import `in`.sitharaj.vaultkmp.VaultStore
import `in`.sitharaj.vaultkmp.ConsoleAuditLogger
import kotlinx.coroutines.launch

/**
 * Main App composable demonstrating VaultKMP functionality.
 */
@Composable
fun App() {
    val vault = remember {
        VaultStore.create {
            name("demo_vault")
            encryptionLevel(EncryptionLevel.HIGH)
            enableConsoleLogging()
        }
    }
    
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            VaultDemoScreen(vault)
        }
    }
}

@Composable
fun VaultDemoScreen(vault: VaultStore) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    
    // State for inputs
    var stringKey by remember { mutableStateOf("username") }
    var stringValue by remember { mutableStateOf("") }
    var intKey by remember { mutableStateOf("age") }
    var intValue by remember { mutableStateOf("") }
    
    // State for results
    var statusMessage by remember { mutableStateOf("Ready to store secrets!") }
    var storedKeys by remember { mutableStateOf<Set<String>>(emptySet()) }
    
    // Load keys on start
    LaunchedEffect(Unit) {
        storedKeys = vault.keys()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "🔐 VaultKMP Demo",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Secure Encrypted Storage",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text(
                text = statusMessage,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // String Storage Section
        SectionCard(title = "📝 String Storage") {
            OutlinedTextField(
                value = stringKey,
                onValueChange = { stringKey = it },
                label = { Text("Key") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = stringValue,
                onValueChange = { stringValue = it },
                label = { Text("Value") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            vault.putString(stringKey, stringValue)
                            statusMessage = "✅ Stored: $stringKey = $stringValue"
                            storedKeys = vault.keys()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save")
                }
                
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            val result = vault.getString(stringKey)
                            stringValue = result ?: ""
                            statusMessage = if (result != null) {
                                "📖 Retrieved: $stringKey = $result"
                            } else {
                                "❌ Key not found: $stringKey"
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Load")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Integer Storage Section
        SectionCard(title = "🔢 Integer Storage") {
            OutlinedTextField(
                value = intKey,
                onValueChange = { intKey = it },
                label = { Text("Key") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = intValue,
                onValueChange = { intValue = it },
                label = { Text("Value (number)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            val value = intValue.toIntOrNull()
                            if (value != null) {
                                vault.putInt(intKey, value)
                                statusMessage = "✅ Stored: $intKey = $value"
                                storedKeys = vault.keys()
                            } else {
                                statusMessage = "❌ Invalid number"
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save")
                }
                
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            val result = vault.getInt(intKey)
                            intValue = result?.toString() ?: ""
                            statusMessage = if (result != null) {
                                "📖 Retrieved: $intKey = $result"
                            } else {
                                "❌ Key not found: $intKey"
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Load")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Stored Keys Section
        SectionCard(title = "🗝️ Stored Keys (${storedKeys.size})") {
            if (storedKeys.isEmpty()) {
                Text(
                    text = "No keys stored yet",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                storedKeys.forEach { key ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "• $key")
                        TextButton(
                            onClick = {
                                scope.launch {
                                    vault.remove(key)
                                    statusMessage = "🗑️ Removed: $key"
                                    storedKeys = vault.keys()
                                }
                            }
                        ) {
                            Text("Delete", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = {
                    scope.launch {
                        vault.clear()
                        statusMessage = "🧹 Cleared all keys!"
                        storedKeys = emptySet()
                        stringValue = ""
                        intValue = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Clear All")
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Info
        Text(
            text = "Encryption: AES-256-GCM (HIGH)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

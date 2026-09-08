package br.com.bloqfone


import android.Manifest
import android.app.role.RoleManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.bloqfone.ui.theme.BloqFoneTheme
import br.com.bloqfone.ui.theme.MainViewModel
import br.com.bloqfone.ui.theme.MainViewModelFactory

class MainActivity : ComponentActivity() {

    // Launcher to handle the Call Screening role request result
    private val roleRequestLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Toast.makeText(this, "Permissão concedida! O motor está ativo.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permissão negada. O app não vai bloquear nada.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BloqFoneTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val context = LocalContext.current

                    // Instantiate the ViewModel using our custom Factory
                    val viewModel: MainViewModel = viewModel(
                        factory = MainViewModelFactory(context.applicationContext)
                    )

                    AppScreen(
                        viewModel = viewModel,
                        onRequestRole = { requestCallScreeningRole() }
                    )
                }
            }
        }
    }

    // Requests the Android OS to set this app as the default Caller ID & Spam app
    private fun requestCallScreeningRole() {
        val roleManager = getSystemService(Context.ROLE_SERVICE) as RoleManager
        if (!roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)) {
            val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
            roleRequestLauncher.launch(intent)
        } else {
            Toast.makeText(this, "App is already set as the default blocker.", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun AppScreen(viewModel: MainViewModel, onRequestRole: () -> Unit) {
    val context = LocalContext.current

    // Launcher to handle the Contacts permission request
    val contactsPermissionLauncher = rememberLauncherForActivityResult (
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.toggleFocusMode(true)
        } else {
            Toast.makeText(context, "Contacts permission is required for Focus Mode.", Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "BloqFone", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(48.dp))

        // This button uses the onRequestRole parameter
        Button(onClick = onRequestRole) {
            Text(text = "Enable Call Screening Role")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Strict Focus Mode Toggle Section
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
        ) {
            Text(
                text = "Strict Focus Mode",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge
            )

            // This Switch uses the viewModel parameter
            Switch(
                checked = viewModel.isFocusModeOn,
                onCheckedChange = { isChecked ->
                    if (isChecked) {
                        // Check if we have permission before enabling the feature
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context, Manifest.permission.READ_CONTACTS
                        ) == PackageManager.PERMISSION_GRANTED

                        if (hasPermission) {
                            viewModel.toggleFocusMode(true)
                        } else {
                            // Request permission if we don't have it yet
                            contactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                        }
                    } else {
                        // Safely disable the feature
                        viewModel.toggleFocusMode(false)
                    }
                }
            )
        }

        Text(
            text = "When active, blocks all numbers not saved in your contacts.",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 8.dp, start = 32.dp, end = 32.dp)
        )
    }
}
package br.com.bloqfone

import android.app.role.RoleManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.bloqfone.ui.AppScreen
import br.com.bloqfone.ui.MainViewModel
import br.com.bloqfone.ui.MainViewModelFactory
import br.com.bloqfone.ui.theme.BloqFoneTheme

class MainActivity : ComponentActivity() {

    private val roleRequestLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Toast.makeText(this, getString(R.string.toast_role_granted), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, getString(R.string.toast_role_denied), Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BloqFoneTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
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

    private fun requestCallScreeningRole() {
        val roleManager = getSystemService(Context.ROLE_SERVICE) as RoleManager
        if (!roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)) {
            val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
            roleRequestLauncher.launch(intent)
        } else {
            Toast.makeText(this, getString(R.string.toast_role_already_set), Toast.LENGTH_SHORT).show()
        }
    }
}

package br.com.bloqfone

import android.Manifest
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import br.com.bloqfone.services.NotificationHelper
import br.com.bloqfone.ui.AppScreen
import br.com.bloqfone.ui.MainViewModel
import br.com.bloqfone.ui.MainViewModelFactory
import br.com.bloqfone.ui.theme.BloqFoneTheme

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "BloqFone:MainActivity"
    }

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(applicationContext)
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.i(TAG, "[SUCESSO] Permissão POST_NOTIFICATIONS concedida pelo usuário.")
        } else {
            Log.w(TAG, "[AVISO] Permissão POST_NOTIFICATIONS negada pelo usuário.")
        }
    }

    private val contactsPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.updateContactsPermission(isGranted)
        if (isGranted) {
            Log.i(TAG, "[SUCESSO] Permissão READ_CONTACTS concedida pelo usuário.")
            viewModel.showFeedback(getString(R.string.toast_contacts_granted), br.com.bloqfone.ui.FeedbackType.SUCCESS)
        } else {
            Log.w(TAG, "[AVISO] Permissão READ_CONTACTS negada pelo usuário.")
            viewModel.showFeedback(getString(R.string.toast_contacts_denied), br.com.bloqfone.ui.FeedbackType.WARNING)
        }
    }

    private val roleRequestLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            val roleManager = getSystemService(Context.ROLE_SERVICE) as RoleManager
            val isHeld = roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
            viewModel.updateRoleStatus(isHeld)
            if (result.resultCode == RESULT_OK || isHeld) {
                Log.i(TAG, "[SUCESSO] Permissão ROLE_CALL_SCREENING concedida pelo usuário. Role ativa: $isHeld.")
                viewModel.showFeedback(getString(R.string.toast_role_granted), br.com.bloqfone.ui.FeedbackType.SUCCESS)
            } else {
                Log.w(TAG, "[ERRO] Permissão ROLE_CALL_SCREENING recusada ou cancelada pelo usuário (resultCode=${result.resultCode}, isHeld=$isHeld).")
                viewModel.showFeedback(getString(R.string.toast_role_denied), br.com.bloqfone.ui.FeedbackType.WARNING)
            }
        } catch (e: Exception) {
            Log.e(TAG, "[ERRO] Falha ao verificar resultado da solicitação de ROLE_CALL_SCREENING.", e)
            viewModel.showFeedback(getString(R.string.toast_role_denied), br.com.bloqfone.ui.FeedbackType.ERROR)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "[SUCESSO] MainActivity inicializada com sucesso.")
        NotificationHelper.createNotificationChannel(this)
        checkAndRequestNotificationPermission()
        checkAndRequestContactsPermission()
        handleNotificationIntent(intent)

        setContent {
            BloqFoneTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppScreen(
                        viewModel = viewModel,
                        onRequestRole = { requestCallScreeningRole() },
                        onRequestContactsPermission = { contactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS) }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        if (intent?.getStringExtra("EXTRA_NAVIGATE_TO") == "report") {
            viewModel.selectNavTab(3)
        }
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val status = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            if (status != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun checkAndRequestContactsPermission() {
        val status = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
        val isGranted = status == PackageManager.PERMISSION_GRANTED
        viewModel.updateContactsPermission(isGranted)
        if (!isGranted) {
            contactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            val roleManager = getSystemService(Context.ROLE_SERVICE) as RoleManager
            val isHeld = roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
            viewModel.updateRoleStatus(isHeld)

            val contactsStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            viewModel.updateContactsPermission(contactsStatus == PackageManager.PERMISSION_GRANTED)

            viewModel.refreshSettings()
            Log.d(TAG, "[RASTREAMENTO] MainActivity retomada. Role isHeld=$isHeld, contactsGranted=${contactsStatus == PackageManager.PERMISSION_GRANTED}.")
        } catch (e: Exception) {
            Log.e(TAG, "[ERRO] Falha ao consultar status em onResume.", e)
        }
    }

    private fun requestCallScreeningRole() {
        try {
            val roleManager = getSystemService(Context.ROLE_SERVICE) as RoleManager
            if (!roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)) {
                Log.i(TAG, "[RASTREAMENTO] Disparando intenção do sistema para solicitar ROLE_CALL_SCREENING ao usuário.")
                val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
                roleRequestLauncher.launch(intent)
            } else {
                Log.i(TAG, "[SUCESSO] ROLE_CALL_SCREENING já está ativa. Nenhuma ação necessária.")
                viewModel.updateRoleStatus(true)
                viewModel.showFeedback(getString(R.string.toast_role_already_set), br.com.bloqfone.ui.FeedbackType.INFO)
            }
        } catch (e: Exception) {
            Log.e(TAG, "[ERRO] Falha ao iniciar solicitação de ROLE_CALL_SCREENING.", e)
            viewModel.showFeedback("Falha ao abrir solicitação do sistema.", br.com.bloqfone.ui.FeedbackType.ERROR)
        }
    }
}

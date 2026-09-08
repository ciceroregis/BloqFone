package br.com.bloqfone.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import br.com.bloqfone.features.AppFeature
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import br.com.bloqfone.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(viewModel: MainViewModel, onRequestRole: () -> Unit) {
    val context = LocalContext.current
    val contactsPermissionNeededText = stringResource(id = R.string.toast_contacts_permission_needed)
    val invalidNumberText = stringResource(id = R.string.toast_invalid_number)
    val invalidCountryCodeText = stringResource(id = R.string.toast_invalid_country_code)
    val invalidDddText = stringResource(id = R.string.toast_invalid_ddd)

    var blacklistInput by remember { mutableStateOf("") }
    var whitelistInput by remember { mutableStateOf("") }
    var countryCodeInput by remember { mutableStateOf("") }
    var dddInput by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }
    var showPremiumGate by remember { mutableStateOf(false) }
    var premiumGateLabel by remember { mutableStateOf("") }
    val tabs = listOf("Visão geral", "Proteção", "Listas", "Ações")
    val requestPremiumUpgrade: (String) -> Unit = { featureName ->
        premiumGateLabel = featureName
        showPremiumGate = true
    }

    val contactsPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.toggleFocusMode(true)
        } else {
            Toast.makeText(context, contactsPermissionNeededText, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    Text(
                        text = if (viewModel.isPremiumUser) "Premium" else "Free",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (viewModel.isPremiumUser) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.secondaryContainer
                    }
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (viewModel.isPremiumUser) stringResource(id = R.string.premium_version_title) else stringResource(id = R.string.free_version_title),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "🛡️",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    Text(
                        text = if (viewModel.isPremiumUser) {
                            stringResource(id = R.string.premium_version_description)
                        } else {
                            stringResource(id = R.string.free_version_description)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(
                        onClick = if (viewModel.isPremiumUser) { { } } else { { viewModel.activatePremium(true) } },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !viewModel.isPremiumUser
                    ) {
                        Text(text = if (viewModel.isPremiumUser) "Premium ativo" else stringResource(id = R.string.premium_cta))
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryMetricCard(
                    modifier = Modifier.weight(1f),
                    emoji = "📵",
                    title = "Bloqueios",
                    value = "${viewModel.blocklistCount()}"
                )
                SummaryMetricCard(
                    modifier = Modifier.weight(1f),
                    emoji = "✅",
                    title = "Liberados",
                    value = "${viewModel.whitelistCount()}"
                )
                SummaryMetricCard(
                    modifier = Modifier.weight(1f),
                    emoji = "🚨",
                    title = "Status",
                    value = if (viewModel.isFocusModeOn) "Ativo" else "Off"
                )
            }

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> OverviewTab(
                    onRequestRole = onRequestRole,
                    premiumActive = viewModel.isPremiumUser,
                    focusModeEnabled = viewModel.isFocusModeOn,
                    rulesEnabled = listOf(
                        viewModel.blockUnknownNumbers,
                        viewModel.blockPrivateNumbers,
                        viewModel.blockNoCallerId,
                        viewModel.blockInternationalNumbers,
                        viewModel.blockTelemarketing,
                        viewModel.blockRobocalls,
                        viewModel.blockSpam
                    ).count { it }
                )
                1 -> ProtectionTab(
                    viewModel = viewModel,
                    context = context,
                    contactsPermissionLauncher = contactsPermissionLauncher,
                    requestPremiumUpgrade = requestPremiumUpgrade
                )
                2 -> ListsTab(
                    viewModel = viewModel,
                    blacklistInput = blacklistInput,
                    whitelistInput = whitelistInput,
                    countryCodeInput = countryCodeInput,
                    dddInput = dddInput,
                    onBlacklistInputChange = { blacklistInput = it },
                    onWhitelistInputChange = { whitelistInput = it },
                    onCountryCodeInputChange = { countryCodeInput = it },
                    onDddInputChange = { dddInput = it },
                    onAddBlacklist = {
                        if (viewModel.addBlacklistNumber(blacklistInput)) {
                            blacklistInput = ""
                        } else {
                            Toast.makeText(context, invalidNumberText, Toast.LENGTH_SHORT).show()
                        }
                    },
                    onAddWhitelist = {
                        if (viewModel.addWhitelistNumber(whitelistInput)) {
                            whitelistInput = ""
                        } else {
                            Toast.makeText(context, invalidNumberText, Toast.LENGTH_SHORT).show()
                        }
                    },
                    onAddCountry = {
                        if (!viewModel.canUseFeature(AppFeature.BLOCK_COUNTRY)) {
                            requestPremiumUpgrade("Bloquear por DDI/país")
                            return@ListsTab
                        }
                        if (viewModel.addBlockedCountryCode(countryCodeInput)) {
                            countryCodeInput = ""
                        } else {
                            Toast.makeText(context, invalidCountryCodeText, Toast.LENGTH_SHORT).show()
                        }
                    },
                    onAddDdd = {
                        if (!viewModel.canUseFeature(AppFeature.BLOCK_DDD)) {
                            requestPremiumUpgrade("Bloquear por DDD")
                            return@ListsTab
                        }
                        if (viewModel.addBlockedDdd(dddInput)) {
                            dddInput = ""
                        } else {
                            Toast.makeText(context, invalidDddText, Toast.LENGTH_SHORT).show()
                        }
                    },
                    invalidNumberText = invalidNumberText,
                    invalidCountryCodeText = invalidCountryCodeText,
                    invalidDddText = invalidDddText
                )
                3 -> ActionsTab(
                    viewModel = viewModel,
                    context = context,
                    requestPremiumUpgrade = requestPremiumUpgrade
                )
            }

            val statusText = if (viewModel.isFocusModeOn) {
                stringResource(id = R.string.focus_mode_status_enabled)
            } else {
                stringResource(id = R.string.focus_mode_status_disabled)
            }
            Text(
                text = statusText,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Text(
                    text = stringResource(id = R.string.info_tip),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }

    if (showPremiumGate && premiumGateLabel.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { showPremiumGate = false },
            icon = { Text(text = "🚨") },
            title = { Text(text = "Proteção Premium") },
            text = {
                Text(
                    text = "A funcionalidade \"${premiumGateLabel}\" está disponível no plano Premium. " +
                        "Proteja seu celular com bloqueios avançados, filtros geográficos e inteligência anti-spam."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.activatePremium(true)
                        showPremiumGate = false
                        premiumGateLabel = ""
                    }
                ) {
                    Text(text = "Ativar Premium")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPremiumGate = false
                    premiumGateLabel = ""
                }) {
                    Text(text = "Agora não")
                }
            }
        )
    }
}
@Composable
private fun SummaryMetricCard(
    modifier: Modifier = Modifier,
    emoji: String,
    title: String,
    value: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, style = MaterialTheme.typography.titleLarge)
            Text(text = title, style = MaterialTheme.typography.labelMedium)
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun OverviewTab(
    onRequestRole: () -> Unit,
    premiumActive: Boolean,
    focusModeEnabled: Boolean,
    rulesEnabled: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ElevatedCard {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = stringResource(id = R.string.role_card_title), style = MaterialTheme.typography.titleMedium)
                Text(
                    text = stringResource(id = R.string.role_card_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(onClick = onRequestRole, modifier = Modifier.fillMaxWidth()) {
                    Text(text = stringResource(id = R.string.role_button_label))
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Proteção em destaque", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = if (premiumActive) {
                        "Premium ativo: foco rigoroso, bloqueios avançados e ações personalizadas habilitadas."
                    } else {
                        "Versão gratuita: mantenha o essencial do dia a dia e evolua para o plano Premium em poucos passos."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${rulesEnabled} regras de proteção ativas • Modo foco: ${if (focusModeEnabled) "ativo" else "desligado"}",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun ProtectionTab(
    viewModel: MainViewModel,
    context: Context,
    contactsPermissionLauncher: ActivityResultLauncher<String>,
    requestPremiumUpgrade: (String) -> Unit
) {
    ElevatedCard {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = stringResource(id = R.string.rules_card_title), style = MaterialTheme.typography.titleMedium)
            Text(
                text = stringResource(id = R.string.rules_card_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            SettingSwitchRow(
                title = stringResource(id = R.string.focus_mode_title),
                description = if (viewModel.canUseFeature(AppFeature.MODE_FOCUS)) {
                    stringResource(id = R.string.focus_mode_description)
                } else {
                    stringResource(id = R.string.premium_locked_focus)
                },
                enabled = true,
                checked = viewModel.isFocusModeOn
            ) { checked ->
                if (!viewModel.canUseFeature(AppFeature.MODE_FOCUS)) {
                    requestPremiumUpgrade("Modo foco rigoroso")
                    return@SettingSwitchRow
                }
                if (checked) {
                    val hasPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_CONTACTS
                    ) == PackageManager.PERMISSION_GRANTED
                    if (hasPermission) {
                        viewModel.toggleFocusMode(true)
                    } else {
                        contactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                    }
                } else {
                    viewModel.toggleFocusMode(false)
                }
            }
            SettingSwitchRow(
                title = stringResource(id = R.string.block_unknown_title),
                description = stringResource(id = R.string.block_unknown_description),
                checked = viewModel.blockUnknownNumbers,
                onCheckedChange = viewModel::toggleBlockUnknownNumbers
            )
            SettingSwitchRow(
                title = stringResource(id = R.string.block_private_title),
                description = stringResource(id = R.string.block_private_description),
                checked = viewModel.blockPrivateNumbers,
                onCheckedChange = viewModel::toggleBlockPrivateNumbers
            )
            SettingSwitchRow(
                title = stringResource(id = R.string.block_no_caller_id_title),
                description = stringResource(id = R.string.block_no_caller_id_description),
                checked = viewModel.blockNoCallerId,
                onCheckedChange = viewModel::toggleBlockNoCallerId
            )
            SettingSwitchRow(
                title = stringResource(id = R.string.block_international_title),
                description = if (viewModel.canUseFeature(AppFeature.BLOCK_INTERNATIONAL)) {
                    stringResource(id = R.string.block_international_description)
                } else {
                    stringResource(id = R.string.premium_locked_international)
                },
                enabled = true,
                checked = viewModel.blockInternationalNumbers,
                onCheckedChange = { enabled ->
                    if (!viewModel.canUseFeature(AppFeature.BLOCK_INTERNATIONAL)) {
                        requestPremiumUpgrade("Bloquear chamadas internacionais")
                        return@SettingSwitchRow
                    }
                    viewModel.toggleBlockInternationalNumbers(enabled)
                }
            )
            SettingSwitchRow(
                title = stringResource(id = R.string.block_telemarketing_title),
                description = if (viewModel.canUseFeature(AppFeature.BLOCK_TELEMARKETING)) {
                    stringResource(id = R.string.block_telemarketing_description)
                } else {
                    stringResource(id = R.string.premium_locked_telemarketing)
                },
                enabled = true,
                checked = viewModel.blockTelemarketing,
                onCheckedChange = { enabled ->
                    if (!viewModel.canUseFeature(AppFeature.BLOCK_TELEMARKETING)) {
                        requestPremiumUpgrade("Bloquear telemarketing")
                        return@SettingSwitchRow
                    }
                    viewModel.toggleBlockTelemarketing(enabled)
                }
            )
            SettingSwitchRow(
                title = stringResource(id = R.string.block_robocalls_title),
                description = if (viewModel.canUseFeature(AppFeature.BLOCK_ROBOCALLS)) {
                    stringResource(id = R.string.block_robocalls_description)
                } else {
                    stringResource(id = R.string.premium_locked_robocalls)
                },
                enabled = true,
                checked = viewModel.blockRobocalls,
                onCheckedChange = { enabled ->
                    if (!viewModel.canUseFeature(AppFeature.BLOCK_ROBOCALLS)) {
                        requestPremiumUpgrade("Bloquear robocalls")
                        return@SettingSwitchRow
                    }
                    viewModel.toggleBlockRobocalls(enabled)
                }
            )
            SettingSwitchRow(
                title = stringResource(id = R.string.block_spam_title),
                description = if (viewModel.canUseFeature(AppFeature.BLOCK_SPAM)) {
                    stringResource(id = R.string.block_spam_description)
                } else {
                    stringResource(id = R.string.premium_locked_spam)
                },
                enabled = true,
                checked = viewModel.blockSpam,
                onCheckedChange = { enabled ->
                    if (!viewModel.canUseFeature(AppFeature.BLOCK_SPAM)) {
                        requestPremiumUpgrade("Bloquear spam")
                        return@SettingSwitchRow
                    }
                    viewModel.toggleBlockSpam(enabled)
                }
            )
        }
    }
}

@Composable
private fun ListsTab(
    viewModel: MainViewModel,
    blacklistInput: String,
    whitelistInput: String,
    countryCodeInput: String,
    dddInput: String,
    onBlacklistInputChange: (String) -> Unit,
    onWhitelistInputChange: (String) -> Unit,
    onCountryCodeInputChange: (String) -> Unit,
    onDddInputChange: (String) -> Unit,
    onAddBlacklist: () -> Unit,
    onAddWhitelist: () -> Unit,
    onAddCountry: () -> Unit,
    onAddDdd: () -> Unit,
    invalidNumberText: String,
    invalidCountryCodeText: String,
    invalidDddText: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        EditableListCard(
            title = stringResource(id = R.string.blacklist_title),
            description = stringResource(id = R.string.blacklist_description),
            inputValue = blacklistInput,
            inputLabel = stringResource(id = R.string.blacklist_input_label),
            onInputChange = onBlacklistInputChange,
            onAdd = onAddBlacklist,
            items = viewModel.blacklistNumbers,
            onRemove = viewModel::removeBlacklistNumber
        )

        EditableListCard(
            title = stringResource(id = R.string.whitelist_title),
            description = stringResource(id = R.string.whitelist_description),
            inputValue = whitelistInput,
            inputLabel = stringResource(id = R.string.whitelist_input_label),
            onInputChange = onWhitelistInputChange,
            onAdd = onAddWhitelist,
            items = viewModel.whitelistNumbers,
            onRemove = viewModel::removeWhitelistNumber
        )

        EditableListCard(
            title = stringResource(id = R.string.country_block_title),
            description = if (viewModel.canUseFeature(AppFeature.BLOCK_COUNTRY)) {
                stringResource(id = R.string.country_block_description)
            } else {
                stringResource(id = R.string.premium_locked_country)
            },
            inputValue = countryCodeInput,
            inputLabel = stringResource(id = R.string.country_block_input_label),
            onInputChange = onCountryCodeInputChange,
            onAdd = onAddCountry,
            items = if (viewModel.canUseFeature(AppFeature.BLOCK_COUNTRY)) viewModel.blockedCountryCodes else emptyList(),
            onRemove = { code ->
                if (viewModel.canUseFeature(AppFeature.BLOCK_COUNTRY)) {
                    viewModel.removeBlockedCountryCode(code)
                }
            }
        )

        EditableListCard(
            title = stringResource(id = R.string.ddd_block_title),
            description = if (viewModel.canUseFeature(AppFeature.BLOCK_DDD)) {
                stringResource(id = R.string.ddd_block_description)
            } else {
                stringResource(id = R.string.premium_locked_ddd)
            },
            inputValue = dddInput,
            inputLabel = stringResource(id = R.string.ddd_block_input_label),
            onInputChange = onDddInputChange,
            onAdd = onAddDdd,
            items = if (viewModel.canUseFeature(AppFeature.BLOCK_DDD)) viewModel.blockedDdds else emptyList(),
            onRemove = { ddd ->
                if (viewModel.canUseFeature(AppFeature.BLOCK_DDD)) {
                    viewModel.removeBlockedDdd(ddd)
                }
            }
        )
    }
}

@Composable
private fun ActionsTab(
    viewModel: MainViewModel,
    context: Context,
    requestPremiumUpgrade: (String) -> Unit
) {
    ElevatedCard {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = stringResource(id = R.string.actions_card_title), style = MaterialTheme.typography.titleMedium)
            Text(
                text = stringResource(id = R.string.actions_card_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            SettingSwitchRow(
                title = stringResource(id = R.string.silent_block_title),
                description = if (viewModel.canUseFeature(AppFeature.SILENT_BLOCKING)) {
                    stringResource(id = R.string.silent_block_description)
                } else {
                    stringResource(id = R.string.premium_locked_silent)
                },
                enabled = true,
                checked = viewModel.silentBlocking,
                onCheckedChange = { enabled ->
                    if (!viewModel.canUseFeature(AppFeature.SILENT_BLOCKING)) {
                        requestPremiumUpgrade("Bloqueio silencioso")
                        return@SettingSwitchRow
                    }
                    viewModel.toggleSilentBlocking(enabled)
                }
            )
            SettingSwitchRow(
                title = stringResource(id = R.string.send_voicemail_title),
                description = if (viewModel.canUseFeature(AppFeature.SEND_TO_VOICEMAIL)) {
                    stringResource(id = R.string.send_voicemail_description)
                } else {
                    stringResource(id = R.string.premium_locked_voicemail)
                },
                enabled = true,
                checked = viewModel.sendToVoicemail,
                onCheckedChange = { enabled ->
                    if (!viewModel.canUseFeature(AppFeature.SEND_TO_VOICEMAIL)) {
                        requestPremiumUpgrade("Enviar para correio de voz")
                        return@SettingSwitchRow
                    }
                    viewModel.toggleSendToVoicemail(enabled)
                }
            )
            SettingSwitchRow(
                title = stringResource(id = R.string.auto_reject_title),
                description = stringResource(id = R.string.auto_reject_description),
                checked = viewModel.autoReject,
                onCheckedChange = viewModel::toggleAutoReject
            )
        }
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall)
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, enabled = true, onCheckedChange = onCheckedChange)
    }
    HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
}

@Composable
private fun EditableListCard(
    title: String,
    description: String,
    inputValue: String,
    inputLabel: String,
    onInputChange: (String) -> Unit,
    onAdd: () -> Unit,
    items: List<String>,
    onRemove: (String) -> Unit
) {
    ElevatedCard {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedTextField(
                value = inputValue,
                onValueChange = onInputChange,
                label = { Text(inputLabel) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Button(onClick = onAdd, modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(id = R.string.add_button))
            }

            if (items.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.empty_list_text),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = item, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                        TextButton(onClick = { onRemove(item) }, modifier = Modifier.width(92.dp)) {
                            Text(text = stringResource(id = R.string.remove_button))
                        }
                    }
                }
            }
        }
    }
}

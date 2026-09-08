package br.com.bloqfone.ui

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
            Text(text = stringResource(id = R.string.home_title), style = MaterialTheme.typography.headlineSmall)
            Text(
                text = stringResource(id = R.string.home_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

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
                        description = stringResource(id = R.string.focus_mode_description),
                        checked = viewModel.isFocusModeOn
                    ) { checked ->
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
                        description = stringResource(id = R.string.block_international_description),
                        checked = viewModel.blockInternationalNumbers,
                        onCheckedChange = viewModel::toggleBlockInternationalNumbers
                    )
                    SettingSwitchRow(
                        title = stringResource(id = R.string.block_telemarketing_title),
                        description = stringResource(id = R.string.block_telemarketing_description),
                        checked = viewModel.blockTelemarketing,
                        onCheckedChange = viewModel::toggleBlockTelemarketing
                    )
                    SettingSwitchRow(
                        title = stringResource(id = R.string.block_robocalls_title),
                        description = stringResource(id = R.string.block_robocalls_description),
                        checked = viewModel.blockRobocalls,
                        onCheckedChange = viewModel::toggleBlockRobocalls
                    )
                    SettingSwitchRow(
                        title = stringResource(id = R.string.block_spam_title),
                        description = stringResource(id = R.string.block_spam_description),
                        checked = viewModel.blockSpam,
                        onCheckedChange = viewModel::toggleBlockSpam
                    )
                }
            }

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
                        description = stringResource(id = R.string.silent_block_description),
                        checked = viewModel.silentBlocking,
                        onCheckedChange = viewModel::toggleSilentBlocking
                    )
                    SettingSwitchRow(
                        title = stringResource(id = R.string.send_voicemail_title),
                        description = stringResource(id = R.string.send_voicemail_description),
                        checked = viewModel.sendToVoicemail,
                        onCheckedChange = viewModel::toggleSendToVoicemail
                    )
                    SettingSwitchRow(
                        title = stringResource(id = R.string.auto_reject_title),
                        description = stringResource(id = R.string.auto_reject_description),
                        checked = viewModel.autoReject,
                        onCheckedChange = viewModel::toggleAutoReject
                    )
                }
            }

            EditableListCard(
                title = stringResource(id = R.string.blacklist_title),
                description = stringResource(id = R.string.blacklist_description),
                inputValue = blacklistInput,
                inputLabel = stringResource(id = R.string.blacklist_input_label),
                onInputChange = { blacklistInput = it },
                onAdd = {
                    if (viewModel.addBlacklistNumber(blacklistInput)) {
                        blacklistInput = ""
                    } else {
                        Toast.makeText(context, invalidNumberText, Toast.LENGTH_SHORT).show()
                    }
                },
                items = viewModel.blacklistNumbers,
                onRemove = viewModel::removeBlacklistNumber
            )

            EditableListCard(
                title = stringResource(id = R.string.whitelist_title),
                description = stringResource(id = R.string.whitelist_description),
                inputValue = whitelistInput,
                inputLabel = stringResource(id = R.string.whitelist_input_label),
                onInputChange = { whitelistInput = it },
                onAdd = {
                    if (viewModel.addWhitelistNumber(whitelistInput)) {
                        whitelistInput = ""
                    } else {
                        Toast.makeText(context, invalidNumberText, Toast.LENGTH_SHORT).show()
                    }
                },
                items = viewModel.whitelistNumbers,
                onRemove = viewModel::removeWhitelistNumber
            )

            EditableListCard(
                title = stringResource(id = R.string.country_block_title),
                description = stringResource(id = R.string.country_block_description),
                inputValue = countryCodeInput,
                inputLabel = stringResource(id = R.string.country_block_input_label),
                onInputChange = { countryCodeInput = it },
                onAdd = {
                    if (viewModel.addBlockedCountryCode(countryCodeInput)) {
                        countryCodeInput = ""
                    } else {
                        Toast.makeText(context, invalidCountryCodeText, Toast.LENGTH_SHORT).show()
                    }
                },
                items = viewModel.blockedCountryCodes,
                onRemove = viewModel::removeBlockedCountryCode
            )

            EditableListCard(
                title = stringResource(id = R.string.ddd_block_title),
                description = stringResource(id = R.string.ddd_block_description),
                inputValue = dddInput,
                inputLabel = stringResource(id = R.string.ddd_block_input_label),
                onInputChange = { dddInput = it },
                onAdd = {
                    if (viewModel.addBlockedDdd(dddInput)) {
                        dddInput = ""
                    } else {
                        Toast.makeText(context, invalidDddText, Toast.LENGTH_SHORT).show()
                    }
                },
                items = viewModel.blockedDdds,
                onRemove = viewModel::removeBlockedDdd
            )

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
}

@Composable
private fun SettingSwitchRow(
    title: String,
    description: String,
    checked: Boolean,
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
        Switch(checked = checked, onCheckedChange = onCheckedChange)
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

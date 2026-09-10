package br.com.bloqfone.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneDisabled
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import br.com.bloqfone.R
import br.com.bloqfone.data.BlockedCallRecord
import br.com.bloqfone.data.formatPhoneNumberForDisplay
import br.com.bloqfone.ui.theme.DangerContainerDark
import br.com.bloqfone.ui.theme.DangerContainerLight
import br.com.bloqfone.ui.theme.DangerRed
import br.com.bloqfone.ui.theme.InfoContainerDark
import br.com.bloqfone.ui.theme.InfoContainerLight
import br.com.bloqfone.ui.theme.OnDangerContainerDark
import br.com.bloqfone.ui.theme.OnDangerContainerLight
import br.com.bloqfone.ui.theme.OnInfoContainerDark
import br.com.bloqfone.ui.theme.OnInfoContainerLight
import br.com.bloqfone.ui.theme.OnSuccessContainerDark
import br.com.bloqfone.ui.theme.OnSuccessContainerLight
import br.com.bloqfone.ui.theme.OnWarningContainerDark
import br.com.bloqfone.ui.theme.OnWarningContainerLight
import br.com.bloqfone.ui.theme.ShieldBlueLight
import br.com.bloqfone.ui.theme.SuccessContainerDark
import br.com.bloqfone.ui.theme.SuccessContainerLight
import br.com.bloqfone.ui.theme.SuccessGreen
import br.com.bloqfone.ui.theme.WarningAmber
import br.com.bloqfone.ui.theme.WarningContainerDark
import br.com.bloqfone.ui.theme.WarningContainerLight
import kotlinx.coroutines.delay

private data class FeedbackStyle(
    val icon: ImageVector,
    val iconColor: Color,
    val containerColor: Color,
    val textColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(
    viewModel: MainViewModel,
    onRequestRole: () -> Unit
) {
    var showStatusInfoDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.bloqfone_logo),
                            contentDescription = "Logo BloqFone",
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                        Column {
                            Text(
                                text = stringResource(id = R.string.app_name),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = stringResource(id = R.string.app_tagline),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    StatusBadge(
                        isActive = viewModel.isCallScreeningRoleHeld,
                        onClick = { showStatusInfoDialog = true }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = viewModel.selectedNavIndex == 0,
                    onClick = { viewModel.selectNavTab(0) },
                    icon = {
                        Icon(
                            imageVector = if (viewModel.selectedNavIndex == 0) Icons.Filled.Shield else Icons.Outlined.Home,
                            contentDescription = stringResource(id = R.string.nav_home)
                        )
                    },
                    label = { Text(stringResource(id = R.string.nav_home)) }
                )
                NavigationBarItem(
                    selected = viewModel.selectedNavIndex == 1,
                    onClick = { viewModel.selectNavTab(1) },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Shield,
                            contentDescription = stringResource(id = R.string.nav_rules)
                        )
                    },
                    label = { Text(stringResource(id = R.string.nav_rules)) }
                )
                NavigationBarItem(
                    selected = viewModel.selectedNavIndex == 2,
                    onClick = { viewModel.selectNavTab(2) },
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.FormatListBulleted,
                            contentDescription = stringResource(id = R.string.nav_lists)
                        )
                    },
                    label = { Text(stringResource(id = R.string.nav_lists)) }
                )
                NavigationBarItem(
                    selected = viewModel.selectedNavIndex == 3,
                    onClick = { viewModel.selectNavTab(3) },
                    icon = {
                        Icon(
                            imageVector = if (viewModel.selectedNavIndex == 3) Icons.Filled.History else Icons.Outlined.History,
                            contentDescription = stringResource(id = R.string.nav_report)
                        )
                    },
                    label = { Text(stringResource(id = R.string.nav_report)) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Screen Content
            when (viewModel.selectedNavIndex) {
                0 -> HomeScreen(
                    viewModel = viewModel,
                    onRequestRole = onRequestRole,
                    onNavigateToReport = { viewModel.selectNavTab(3) }
                )
                1 -> RulesScreen(
                    viewModel = viewModel
                )
                2 -> ListsScreen(
                    viewModel = viewModel
                )
                3 -> ReportScreen(
                    viewModel = viewModel
                )
            }

            // In-app Animated Feedback Popup with High-Contrast Colors and Icons
            AnimatedVisibility(
                visible = viewModel.currentPopupMessage != null,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
                    .zIndex(10f)
            ) {
                viewModel.currentPopupMessage?.let { popup ->
                    FeedbackPopupCard(
                        popup = popup,
                        onDismiss = viewModel::dismissFeedback
                    )
                }
            }

            // Status Info Dialog Popup with High-Contrast Text and Icon
            if (showStatusInfoDialog) {
                StatusInfoDialog(
                    isActive = viewModel.isCallScreeningRoleHeld,
                    onDismiss = { showStatusInfoDialog = false },
                    onActivate = {
                        showStatusInfoDialog = false
                        onRequestRole()
                    }
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(
    isActive: Boolean,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isActive) {
        if (isDark) SuccessContainerDark else SuccessContainerLight
    } else {
        if (isDark) WarningContainerDark else WarningContainerLight
    }
    val contentColor = if (isActive) {
        if (isDark) OnSuccessContainerDark else OnSuccessContainerLight
    } else {
        if (isDark) OnWarningContainerDark else OnWarningContainerLight
    }
    val text = if (isActive) "✓ Ativo" else "! Inativo"

    Surface(
        modifier = Modifier
            .padding(end = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = if (isActive) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}

@Composable
private fun FeedbackPopupCard(
    popup: PopupMessage,
    onDismiss: () -> Unit
) {
    LaunchedEffect(popup.id) {
        delay(3200)
        onDismiss()
    }

    val isDark = isSystemInDarkTheme()
    val style = when (popup.type) {
        FeedbackType.SUCCESS -> if (isDark) {
            FeedbackStyle(Icons.Filled.CheckCircle, Color(0xFF34D399), SuccessContainerDark, OnSuccessContainerDark)
        } else {
            FeedbackStyle(Icons.Filled.CheckCircle, Color(0xFF059669), SuccessContainerLight, OnSuccessContainerLight)
        }
        FeedbackType.WARNING -> if (isDark) {
            FeedbackStyle(Icons.Filled.Warning, Color(0xFFFBBF24), WarningContainerDark, OnWarningContainerDark)
        } else {
            FeedbackStyle(Icons.Filled.Warning, Color(0xFFD97706), WarningContainerLight, OnWarningContainerLight)
        }
        FeedbackType.ERROR -> if (isDark) {
            FeedbackStyle(Icons.Filled.Error, Color(0xFFF87171), DangerContainerDark, OnDangerContainerDark)
        } else {
            FeedbackStyle(Icons.Filled.Error, Color(0xFFDC2626), DangerContainerLight, OnDangerContainerLight)
        }
        FeedbackType.INFO -> if (isDark) {
            FeedbackStyle(Icons.Filled.Info, Color(0xFF60A5FA), InfoContainerDark, OnInfoContainerDark)
        } else {
            FeedbackStyle(Icons.Filled.Info, Color(0xFF2563EB), InfoContainerLight, OnInfoContainerLight)
        }
    }

    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = style.containerColor
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = style.iconColor.copy(alpha = 0.2f),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = style.icon,
                        contentDescription = null,
                        tint = style.iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Text(
                text = popup.message,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = style.textColor,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = "Fechar",
                    tint = style.textColor.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun StatusInfoDialog(
    isActive: Boolean,
    onDismiss: () -> Unit,
    onActivate: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val iconContainerColor = if (isActive) {
        if (isDark) SuccessContainerDark else SuccessContainerLight
    } else {
        if (isDark) WarningContainerDark else WarningContainerLight
    }
    val iconColor = if (isActive) {
        if (isDark) OnSuccessContainerDark else SuccessGreen
    } else {
        if (isDark) OnWarningContainerDark else WarningAmber
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface,
        icon = {
            Surface(
                shape = CircleShape,
                color = iconContainerColor,
                modifier = Modifier.size(54.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isActive) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        },
        title = {
            Text(
                text = if (isActive) "Bloqueio de Chamadas Ativo" else "Ativação Pendente",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Text(
                text = if (isActive) {
                    "O BloqFone está definido como o app padrão de bloqueio de chamadas no Android. Suas listas negra/branca e regras automáticas estão ativas e monitorando ligações."
                } else {
                    "O Android exige que o BloqFone seja definido como aplicativo padrão de triagem de chamadas (Call Screening) para que possa bloquear chamadas indesejadas em segundo plano."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        confirmButton = {
            if (!isActive) {
                Button(
                    onClick = onActivate,
                    colors = ButtonDefaults.buttonColors(containerColor = WarningAmber)
                ) {
                    Icon(Icons.Filled.Shield, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Ativar Proteção", color = Color.White, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(onClick = onDismiss) {
                    Text("Entendido", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            if (!isActive) {
                OutlinedButton(onClick = onDismiss) {
                    Text("Depois")
                }
            }
        }
    )
}

@Composable
private fun HomeScreen(
    viewModel: MainViewModel,
    onRequestRole: () -> Unit,
    onNavigateToReport: () -> Unit
) {
    val isDark = isSystemInDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Hero Branding Card with Logo
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bloqfone_logo),
                    contentDescription = "Logo BloqFone",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(id = R.string.home_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(id = R.string.home_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Card de alerta exibido apenas se a proteção ainda não foi ativada pelo usuário.
        // Quando a proteção já está ativa, o card com a mensagem "Proteção Ativa" foi removido.
        if (!viewModel.isCallScreeningRoleHeld) {
            val statusContainerColor = if (isDark) WarningContainerDark else WarningContainerLight
            val statusTextColor = if (isDark) OnWarningContainerDark else OnWarningContainerLight

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = statusContainerColor
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = statusTextColor.copy(alpha = 0.15f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Warning,
                                    contentDescription = null,
                                    tint = statusTextColor,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(id = R.string.role_inactive_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = statusTextColor
                            )
                            Text(
                                text = stringResource(id = R.string.role_inactive_subtitle),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = statusTextColor.copy(alpha = 0.9f)
                            )
                        }
                    }

                    Button(
                        onClick = onRequestRole,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WarningAmber
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Shield,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.role_button_activate),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Metrics Summary Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToReport() },
                icon = Icons.Filled.PhoneDisabled,
                iconColor = DangerRed,
                title = stringResource(id = R.string.metric_blocked_calls),
                value = "${viewModel.blockedCalls.size}"
            )
            MetricCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.Block,
                iconColor = DangerRed.copy(alpha = 0.8f),
                title = stringResource(id = R.string.metric_blacklist),
                value = "${viewModel.blocklistCount()}"
            )
            MetricCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.Shield,
                iconColor = ShieldBlueLight,
                title = stringResource(id = R.string.metric_active_rules),
                value = "${viewModel.activeRulesCount()}"
            )
            MetricCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.CheckCircle,
                iconColor = SuccessGreen,
                title = stringResource(id = R.string.metric_whitelist),
                value = "${viewModel.whitelistCount()}"
            )
        }

        // Recent Blocked Call Banner (if any)
        if (viewModel.blockedCalls.isNotEmpty()) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToReport() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (isDark) DangerContainerDark else DangerContainerLight,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.PhoneDisabled,
                                contentDescription = null,
                                tint = if (isDark) OnDangerContainerDark else DangerRed,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Última chamada barrada",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        val latest = viewModel.blockedCalls.first()
                        Text(
                            text = "${latest.formattedDisplayNumber} • ${latest.reason} (${latest.formattedDate})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "Ver relatório →",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }



        // Privacy & Offline Badge Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            )
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = stringResource(id = R.string.privacy_card_title),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(id = R.string.privacy_card_text),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconColor: Color,
    title: String,
    value: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RulesScreen(
    viewModel: MainViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.rules_card_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(id = R.string.rules_card_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                SettingSwitchItem(
                    title = stringResource(id = R.string.block_focus_mode_title),
                    description = stringResource(id = R.string.block_focus_mode_description),
                    checked = viewModel.isFocusModeOn,
                    onCheckedChange = viewModel::toggleFocusMode
                )

                HorizontalDivider()

                SettingSwitchItem(
                    title = stringResource(id = R.string.block_telemarketing_title),
                    description = stringResource(id = R.string.block_telemarketing_description),
                    checked = viewModel.blockTelemarketing,
                    onCheckedChange = viewModel::toggleBlockTelemarketing
                )

                HorizontalDivider()

                SettingSwitchItem(
                    title = stringResource(id = R.string.block_robocalls_title),
                    description = stringResource(id = R.string.block_robocalls_description),
                    checked = viewModel.blockRobocalls,
                    onCheckedChange = viewModel::toggleBlockRobocalls
                )

                HorizontalDivider()

                SettingSwitchItem(
                    title = stringResource(id = R.string.block_spam_title),
                    description = stringResource(id = R.string.block_spam_description),
                    checked = viewModel.blockSpam,
                    onCheckedChange = viewModel::toggleBlockSpam
                )

                HorizontalDivider()

                SettingSwitchItem(
                    title = stringResource(id = R.string.block_international_title),
                    description = stringResource(id = R.string.block_international_description),
                    checked = viewModel.blockInternationalNumbers,
                    onCheckedChange = viewModel::toggleBlockInternationalNumbers
                )

                HorizontalDivider()

                SettingSwitchItem(
                    title = stringResource(id = R.string.block_unknown_title),
                    description = stringResource(id = R.string.block_unknown_description),
                    checked = viewModel.blockUnknownNumbers,
                    onCheckedChange = viewModel::toggleBlockUnknownNumbers
                )

                HorizontalDivider()

                SettingSwitchItem(
                    title = stringResource(id = R.string.block_private_title),
                    description = stringResource(id = R.string.block_private_description),
                    checked = viewModel.blockPrivateNumbers,
                    onCheckedChange = viewModel::toggleBlockPrivateNumbers
                )

                HorizontalDivider()

                SettingSwitchItem(
                    title = stringResource(id = R.string.block_no_caller_id_title),
                    description = stringResource(id = R.string.block_no_caller_id_description),
                    checked = viewModel.blockNoCallerId,
                    onCheckedChange = viewModel::toggleBlockNoCallerId
                )
            }
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.actions_card_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(id = R.string.actions_card_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                SettingSwitchItem(
                    title = stringResource(id = R.string.auto_reject_title),
                    description = stringResource(id = R.string.auto_reject_description),
                    checked = viewModel.autoReject,
                    onCheckedChange = viewModel::toggleAutoReject
                )
            }
        }
    }
}

@Composable
private fun SettingSwitchItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun ListsScreen(
    viewModel: MainViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var numberInput by remember { mutableStateOf("") }
    var numberToDelete by remember { mutableStateOf<Pair<String, Boolean>?>(null) }
    val focusManager = LocalFocusManager.current
    val isDark = isSystemInDarkTheme()

    val isBlacklistTab = selectedTab == 0
    val currentItems = if (isBlacklistTab) viewModel.filteredBlacklist else viewModel.filteredWhitelist
    val totalCount = if (isBlacklistTab) viewModel.blacklistNumbers.size else viewModel.whitelistNumbers.size
    val currentSearch = if (isBlacklistTab) viewModel.blacklistSearchQuery else viewModel.whitelistSearchQuery

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tab selector
        SecondaryTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.tab_blacklist),
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedTab == 0) DangerRed else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "${viewModel.blacklistNumbers.size}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (selectedTab == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.tab_whitelist),
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedTab == 1) SuccessGreen else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "${viewModel.whitelistNumbers.size}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (selectedTab == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            )
        }

        // Add Number Form
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isBlacklistTab) {
                        stringResource(id = R.string.blacklist_title)
                    } else {
                        stringResource(id = R.string.whitelist_title)
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isBlacklistTab) {
                        stringResource(id = R.string.blacklist_description)
                    } else {
                        stringResource(id = R.string.whitelist_description)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = numberInput,
                        onValueChange = { numberInput = it },
                        label = { Text(stringResource(id = R.string.add_number_label), maxLines = 1) },
                        placeholder = { Text(stringResource(id = R.string.add_number_placeholder), maxLines = 1) },
                        leadingIcon = {
                            Icon(Icons.Filled.Phone, contentDescription = null)
                        },
                        trailingIcon = {
                            if (numberInput.isNotEmpty()) {
                                IconButton(onClick = { numberInput = "" }) {
                                    Icon(Icons.Filled.Clear, contentDescription = "Limpar")
                                }
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                val success = if (isBlacklistTab) {
                                    viewModel.addBlacklistNumber(numberInput)
                                } else {
                                    viewModel.addWhitelistNumber(numberInput)
                                }
                                if (success) {
                                    numberInput = ""
                                    viewModel.showFeedback("Número adicionado com sucesso!", FeedbackType.SUCCESS)
                                } else {
                                    viewModel.showFeedback("Por favor, digite um número de telefone válido.", FeedbackType.WARNING)
                                }
                            }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            val success = if (isBlacklistTab) {
                                viewModel.addBlacklistNumber(numberInput)
                            } else {
                                viewModel.addWhitelistNumber(numberInput)
                            }
                            if (success) {
                                numberInput = ""
                                viewModel.showFeedback("Número adicionado com sucesso!", FeedbackType.SUCCESS)
                            } else {
                                viewModel.showFeedback("Por favor, digite um número de telefone válido.", FeedbackType.WARNING)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBlacklistTab) {
                                stringResource(id = R.string.add_to_blacklist)
                            } else {
                                stringResource(id = R.string.add_to_whitelist)
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Search Bar (if list is not empty)
        if (totalCount > 0) {
            OutlinedTextField(
                value = currentSearch,
                onValueChange = { query ->
                    if (isBlacklistTab) {
                        viewModel.blacklistSearchQuery = query
                    } else {
                        viewModel.whitelistSearchQuery = query
                    }
                },
                placeholder = { Text(stringResource(id = R.string.search_placeholder)) },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (currentSearch.isNotEmpty()) {
                        IconButton(onClick = {
                            if (isBlacklistTab) {
                                viewModel.blacklistSearchQuery = ""
                            } else {
                                viewModel.whitelistSearchQuery = ""
                            }
                        }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Limpar busca")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // List items or Empty state
        if (currentItems.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (isBlacklistTab) Icons.Filled.PhoneDisabled else Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = if (totalCount == 0) {
                            if (isBlacklistTab) {
                                stringResource(id = R.string.empty_blacklist)
                            } else {
                                stringResource(id = R.string.empty_whitelist)
                            }
                        } else {
                            stringResource(id = R.string.empty_search)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                currentItems.forEach { number ->
                    PhoneNumberRow(
                        number = number,
                        isBlacklist = isBlacklistTab,
                        onRemoveClick = {
                            numberToDelete = Pair(number, isBlacklistTab)
                        },
                        onMove = {
                            if (isBlacklistTab) {
                                viewModel.moveToWhitelist(number)
                                viewModel.showFeedback("Número movido para a Lista Branca!", FeedbackType.SUCCESS)
                            } else {
                                viewModel.moveToBlacklist(number)
                                viewModel.showFeedback("Número movido para a Lista Negra!", FeedbackType.SUCCESS)
                            }
                        }
                    )
                }
            }
        }

        // Delete Confirmation Popup Dialog with High-Contrast Colors and Icon
        numberToDelete?.let { (number, isBlacklist) ->
            val iconBg = if (isDark) DangerContainerDark else DangerContainerLight
            val iconTint = if (isDark) OnDangerContainerDark else DangerRed

            AlertDialog(
                onDismissRequest = { numberToDelete = null },
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                textContentColor = MaterialTheme.colorScheme.onSurface,
                icon = {
                    Surface(
                        shape = CircleShape,
                        color = iconBg,
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = null,
                                tint = iconTint,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                },
                title = {
                    Text(
                        text = "Remover da Lista?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                text = {
                    Text(
                        text = "Deseja remover o número ${formatPhoneNumberForDisplay(number)} da ${if (isBlacklist) "Lista Negra" else "Lista Branca"}?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (isBlacklist) {
                                viewModel.removeBlacklistNumber(number)
                            } else {
                                viewModel.removeWhitelistNumber(number)
                            }
                            numberToDelete = null
                            viewModel.showFeedback("Número removido com sucesso.", FeedbackType.INFO)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Remover", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { numberToDelete = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
private fun PhoneNumberRow(
    number: String,
    isBlacklist: Boolean,
    onRemoveClick: () -> Unit,
    onMove: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val badgeBg = if (isBlacklist) {
        if (isDark) DangerContainerDark else DangerContainerLight
    } else {
        if (isDark) SuccessContainerDark else SuccessContainerLight
    }
    val badgeTint = if (isBlacklist) {
        if (isDark) OnDangerContainerDark else DangerRed
    } else {
        if (isDark) OnSuccessContainerDark else SuccessGreen
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = badgeBg,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isBlacklist) Icons.Filled.PhoneDisabled else Icons.Filled.Phone,
                        contentDescription = null,
                        tint = badgeTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = formatPhoneNumberForDisplay(number),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (formatPhoneNumberForDisplay(number) != number) {
                    Text(
                        text = number,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Move button (transfer directly to the opposite list)
            IconButton(
                onClick = onMove
            ) {
                Icon(
                    imageVector = Icons.Filled.SwapHoriz,
                    contentDescription = if (isBlacklist) {
                        stringResource(id = R.string.move_to_whitelist)
                    } else {
                        stringResource(id = R.string.move_to_blacklist)
                    },
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Delete button (opens confirmation popup)
            IconButton(
                onClick = onRemoveClick
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(id = R.string.remove_button),
                    tint = DangerRed.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun ReportScreen(
    viewModel: MainViewModel
) {
    var callToDelete by remember { mutableStateOf<BlockedCallRecord?>(null) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }
    val isDark = isSystemInDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Card
        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.History,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = stringResource(id = R.string.report_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = stringResource(id = R.string.report_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (viewModel.blockedCalls.isNotEmpty()) {
                        OutlinedButton(
                            onClick = { showClearConfirmDialog = true },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = DangerRed
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(id = R.string.report_clear), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Search Bar (if list is not empty)
        if (viewModel.blockedCalls.isNotEmpty()) {
            OutlinedTextField(
                value = viewModel.reportSearchQuery,
                onValueChange = { viewModel.reportSearchQuery = it },
                placeholder = { Text(stringResource(id = R.string.report_search_placeholder)) },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (viewModel.reportSearchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.reportSearchQuery = "" }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Limpar busca")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Empty state or List of records
        if (viewModel.filteredBlockedCalls.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = if (viewModel.blockedCalls.isEmpty()) Icons.Filled.CheckCircle else Icons.Filled.PhoneDisabled,
                        contentDescription = null,
                        tint = if (viewModel.blockedCalls.isEmpty()) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = if (viewModel.blockedCalls.isEmpty()) {
                            stringResource(id = R.string.report_empty)
                        } else {
                            stringResource(id = R.string.report_empty_search)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                viewModel.filteredBlockedCalls.forEach { record ->
                    BlockedCallItemCard(
                        record = record,
                        isDark = isDark,
                        isInBlacklist = record.rawNumber != null && viewModel.blacklistNumbers.contains(record.rawNumber),
                        isInWhitelist = record.rawNumber != null && viewModel.whitelistNumbers.contains(record.rawNumber),
                        onAddToBlacklist = {
                            record.rawNumber?.let {
                                viewModel.addBlacklistNumber(it)
                                viewModel.showFeedback("Número adicionado à Lista Negra!", FeedbackType.SUCCESS)
                            }
                        },
                        onAddToWhitelist = {
                            record.rawNumber?.let {
                                viewModel.addWhitelistNumber(it)
                                viewModel.showFeedback("Número adicionado à Lista Branca!", FeedbackType.SUCCESS)
                            }
                        },
                        onDelete = {
                            callToDelete = record
                        }
                    )
                }
            }
        }
    }

    // Confirmation Dialog: Clear All Records
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            icon = {
                Surface(
                    shape = CircleShape,
                    color = if (isDark) DangerContainerDark else DangerContainerLight,
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = null,
                            tint = if (isDark) OnDangerContainerDark else DangerRed,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            },
            title = {
                Text(
                    text = stringResource(id = R.string.report_dialog_clear_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.report_dialog_clear_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearBlockedCalls()
                        showClearConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Limpar Tudo", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Confirmation Dialog: Delete Single Record
    callToDelete?.let { record ->
        AlertDialog(
            onDismissRequest = { callToDelete = null },
            containerColor = MaterialTheme.colorScheme.surface,
            icon = {
                Surface(
                    shape = CircleShape,
                    color = if (isDark) DangerContainerDark else DangerContainerLight,
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = null,
                            tint = if (isDark) OnDangerContainerDark else DangerRed,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            },
            title = {
                Text(
                    text = stringResource(id = R.string.report_dialog_delete_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = "${stringResource(id = R.string.report_dialog_delete_desc)}\n\n${record.formattedDisplayNumber}\n${record.formattedDate}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteBlockedCall(record.id)
                        callToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Excluir", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { callToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun BlockedCallItemCard(
    record: BlockedCallRecord,
    isDark: Boolean,
    isInBlacklist: Boolean,
    isInWhitelist: Boolean,
    onAddToBlacklist: () -> Unit,
    onAddToWhitelist: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isDark) DangerContainerDark else DangerContainerLight,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.PhoneDisabled,
                            contentDescription = null,
                            tint = if (isDark) OnDangerContainerDark else DangerRed,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = record.formattedDisplayNumber,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = record.formattedDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Excluir registro",
                        tint = DangerRed.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Badges Row (Reason and Action)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reason Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isDark) DangerContainerDark else DangerContainerLight
                ) {
                    Text(
                        text = "Motivo: ${record.reason}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) OnDangerContainerDark else DangerRed,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Action Badge
                val actionBg = if (record.wasAutoRejected) {
                    if (isDark) DangerContainerDark else DangerContainerLight
                } else {
                    if (isDark) WarningContainerDark else WarningContainerLight
                }
                val actionColor = if (record.wasAutoRejected) {
                    if (isDark) OnDangerContainerDark else DangerRed
                } else {
                    if (isDark) OnWarningContainerDark else WarningAmber
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = actionBg
                ) {
                    Text(
                        text = if (record.wasAutoRejected) {
                            stringResource(id = R.string.report_badge_rejected)
                        } else {
                            stringResource(id = R.string.report_badge_silenced)
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = actionColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Optional Quick Actions for Phone Numbers
            if (!record.rawNumber.isNullOrBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!isInBlacklist) {
                        OutlinedButton(
                            onClick = onAddToBlacklist,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.Block, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Lista Negra", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                    if (!isInWhitelist) {
                        OutlinedButton(
                            onClick = onAddToWhitelist,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp), tint = SuccessGreen)
                            Spacer(Modifier.width(4.dp))
                            Text("Lista Branca", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}


package com.example.myapplication.ui.feature.home.profile

import android.content.Context
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewModelScope
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.notification.NotificationPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

val LocalThemeViewModel = staticCompositionLocalOf<ThemeViewModel> {
    error("ThemeViewModel not provided")
}

val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_prefs")

private object ThemePreferencesKeys {
    val DARK_MODE = booleanPreferencesKey("dark_mode_enabled")
}

class ThemeRepository(private val dataStore: DataStore<Preferences>) {
    val isDark: kotlinx.coroutines.flow.Flow<Boolean> =
        dataStore.data.map { it[ThemePreferencesKeys.DARK_MODE] ?: false }

    suspend fun setDark(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[ThemePreferencesKeys.DARK_MODE] = enabled
        }
    }
}

class ThemeViewModel(private val repository: ThemeRepository) : ViewModel() {
    val isDark: StateFlow<Boolean> = repository.isDark.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    fun setDark(enabled: Boolean) {
        viewModelScope.launch { repository.setDark(enabled) }
    }
}

class ThemeViewModelFactory(
    private val repository: ThemeRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ThemeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class SettingsViewModel : ViewModel()

private const val PRIVACY_POLICY_URL = "https://sites.google.com/view/mybrokerr-privacy/home"
private const val SUPPORT_EMAIL = "mybrokerrofficial@gmail.com"

@Composable
fun SettingsScreen(
    themeViewModel: ThemeViewModel = LocalThemeViewModel.current,
    viewModel: SettingsViewModel = viewModel(),
    onLogoutClick: () -> Unit = {}
) {
    val isDarkTheme by themeViewModel.isDark.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val notificationsEnabled by NotificationPreferences
        .notificationsEnabledFlow(context.applicationContext)
        .collectAsState(initial = true)
    val selectedLanguageCode by LanguagePreferences
        .selectedLanguageFlow(context.applicationContext)
        .collectAsState(initial = LanguagePreferences.ENGLISH)
    val selectedLanguageLabel = when (selectedLanguageCode) {
        LanguagePreferences.HINDI -> stringResource(id = com.example.myapplication.R.string.language_hindi)
        else -> stringResource(id = com.example.myapplication.R.string.language_english)
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item { SectionHeader(text = stringResource(id = com.example.myapplication.R.string.settings_general_title)) }

            item {
                SettingsCard {
                    ToggleSetting(
                        icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                        title = stringResource(id = com.example.myapplication.R.string.settings_notifications),
                        subtitle = stringResource(id = com.example.myapplication.R.string.settings_notifications_subtitle),
                        checked = notificationsEnabled,
                        onCheckedChange = { enabled ->
                            scope.launch {
                                NotificationPreferences.setNotificationsEnabled(
                                    context = context.applicationContext,
                                    enabled = enabled
                                )
                            }
                        }
                    )
                    HorizontalDivider()
                    ToggleSetting(
                        icon = { Icon(Icons.Default.DarkMode, contentDescription = null) },
                        title = stringResource(id = com.example.myapplication.R.string.settings_dark_mode),
                        subtitle = stringResource(id = com.example.myapplication.R.string.settings_dark_mode_subtitle),
                        checked = isDarkTheme,
                        onCheckedChange = { themeViewModel.setDark(it) }
                    )
                    HorizontalDivider()
                    DropdownSetting(
                        icon = { Icon(Icons.Default.Language, contentDescription = null) },
                        title = stringResource(id = com.example.myapplication.R.string.settings_language),
                        currentValue = selectedLanguageLabel,
                        options = listOf(
                            LanguageOption(
                                code = LanguagePreferences.ENGLISH,
                                label = stringResource(id = com.example.myapplication.R.string.language_english)
                            ),
                            LanguageOption(
                                code = LanguagePreferences.HINDI,
                                label = stringResource(id = com.example.myapplication.R.string.language_hindi)
                            )
                        ),
                        onSelect = { option ->
                            scope.launch {
                                LanguagePreferences.setLanguage(
                                    context = context,
                                    languageCode = option.code
                                )
                                (context as? Activity)?.recreate()
                            }
                        }
                    )
                }
            }

            item { SectionHeader(text = stringResource(id = com.example.myapplication.R.string.settings_info_title)) }

            item {
                SettingsCard {
                    NavigationSetting(
                        icon = { Icon(Icons.Default.StarRate, contentDescription = null) },
                        title = stringResource(id = com.example.myapplication.R.string.settings_rate_app),
                        subtitle = stringResource(id = com.example.myapplication.R.string.settings_rate_app_subtitle),
                        onClick = { /* open play store */ }
                    )
                    HorizontalDivider()
                    NavigationSetting(
                        icon = { Icon(Icons.Default.Share, contentDescription = null) },
                        title = stringResource(id = com.example.myapplication.R.string.settings_share_app),
                        subtitle = stringResource(id = com.example.myapplication.R.string.settings_share_app_subtitle),
                        onClick = { /* share intent */ }
                    )
                    HorizontalDivider()
                    NavigationSetting(
                        icon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        title = stringResource(id = com.example.myapplication.R.string.settings_privacy_policy),
                        subtitle = stringResource(id = com.example.myapplication.R.string.settings_privacy_policy_subtitle),
                        onClick = { openExternalLink(context, PRIVACY_POLICY_URL) }
                    )
                    HorizontalDivider()
                    NavigationSetting(
                        icon = { Icon(Icons.Default.Description, contentDescription = null) },
                        title = stringResource(id = com.example.myapplication.R.string.settings_terms),
                        subtitle = stringResource(id = com.example.myapplication.R.string.settings_terms_subtitle),
                        onClick = { openExternalLink(context, PRIVACY_POLICY_URL) }
                    )
                    HorizontalDivider()
                    NavigationSetting(
                        icon = { Icon(Icons.Default.Email, contentDescription = null) },
                        title = stringResource(id = com.example.myapplication.R.string.settings_contact_us),
                        subtitle = stringResource(id = com.example.myapplication.R.string.settings_contact_us_subtitle),
                        onClick = {
                            openExternalLink(
                                context,
                                "mailto:$SUPPORT_EMAIL?subject=MyBrokerr%20Support"
                            )
                        }
                    )
                    HorizontalDivider()
                    NavigationSetting(
                        icon = { Icon(Icons.Default.Feedback, contentDescription = null) },
                        title = stringResource(id = com.example.myapplication.R.string.settings_feedback),
                        subtitle = stringResource(id = com.example.myapplication.R.string.settings_feedback_subtitle),
                        onClick = {
                            openExternalLink(
                                context,
                                "mailto:$SUPPORT_EMAIL?subject=MyBrokerr%20Feedback"
                            )
                        }
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextButton(onClick = onLogoutClick) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(id = com.example.myapplication.R.string.logout))
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth(), content = content)
    }
}

@Composable
private fun ToggleSetting(
    icon: @Composable (() -> Unit)? = null,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        leadingContent = {
            if (icon != null) {
                Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) { icon() }
            }
        },
        headlineContent = { Text(text = title) },
        supportingContent = {
            if (!subtitle.isNullOrEmpty()) Text(text = subtitle)
        },
        trailingContent = {
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    )
}

@Composable
private fun NavigationSetting(
    icon: @Composable (() -> Unit)? = null,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) { icon() }
            Spacer(modifier = Modifier.width(12.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            if (!subtitle.isNullOrEmpty()) Text(text = subtitle, style = MaterialTheme.typography.bodySmall)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
    }
}

@Composable
private fun DropdownSetting(
    icon: @Composable (() -> Unit)? = null,
    title: String,
    currentValue: String,
    options: List<LanguageOption>,
    onSelect: (LanguageOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    NavigationSetting(
        icon = icon,
        title = title,
        subtitle = currentValue,
        onClick = { expanded = true }
    )
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        options.forEach { opt ->
            DropdownMenuItem(text = { Text(opt.label) }, onClick = {
                onSelect(opt)
                expanded = false
            })
        }
    }
}

data class LanguageOption(
    val code: String,
    val label: String
)

private fun openExternalLink(context: Context, rawUrl: String) {
    val normalized = if (
        rawUrl.startsWith("http://", ignoreCase = true) ||
        rawUrl.startsWith("https://", ignoreCase = true) ||
        rawUrl.startsWith("tel:", ignoreCase = true) ||
        rawUrl.startsWith("mailto:", ignoreCase = true)
    ) {
        rawUrl
    } else {
        "https://$rawUrl"
    }

    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(normalized)).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}
//
//@Preview(showBackground = true)
//@Composable
//private fun SettingsScreenPreview() {
//    SettingsScreen()
//}


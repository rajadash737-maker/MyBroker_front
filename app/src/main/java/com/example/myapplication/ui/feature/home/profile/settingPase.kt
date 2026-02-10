package com.mybroker.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

class SettingsViewModel : ViewModel() {
    var notificationsEnabled by mutableStateOf(true)
    var darkThemeEnabled by mutableStateOf(false)
    var selectedLanguage by mutableStateOf("English")
}

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            item { SectionHeader(text = "General Settings") }

            item {
                ToggleSetting(
                    icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                    title = "Notifications",
                    subtitle = "Enable or disable app notifications",
                    checked = viewModel.notificationsEnabled,
                    onCheckedChange = { viewModel.notificationsEnabled = !viewModel.notificationsEnabled }
                )
            }

            item {
                ToggleSetting(
                    icon = { Icon(Icons.Default.DarkMode, contentDescription = null) },
                    title = "Dark Mode",
                    subtitle = "Switch between light and dark themes",
                    checked = viewModel.darkThemeEnabled,
                    onCheckedChange = { viewModel.darkThemeEnabled = !viewModel.darkThemeEnabled }
                )
            }

            item {
                DropdownSetting(
                    icon = { Icon(Icons.Default.Language, contentDescription = null) },
                    title = "Language",
                    currentValue = viewModel.selectedLanguage,
                    options = listOf("English", "Hindi", "Marathi", "Gujarati"),
                    onSelect = { viewModel.selectedLanguage = it }
                )
            }

            item { SectionHeader(text = "App Info & Feedback") }

            item {
                NavigationSetting(
                    icon = { Icon(Icons.Default.StarRate, contentDescription = null) },
                    title = "Rate App",
                    subtitle = "Give feedback on Play Store",
                    onClick = { /* open play store */ }
                )
            }

            item {
                NavigationSetting(
                    icon = { Icon(Icons.Default.Share, contentDescription = null) },
                    title = "Share App",
                    subtitle = "Invite friends to use MyBroker",
                    onClick = { /* share intent */ }
                )
            }

            item {
                NavigationSetting(
                    icon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    title = "Privacy Policy",
                    subtitle = "Read our privacy terms",
                    onClick = { /* open privacy page */ }
                )
            }

            item {
                NavigationSetting(
                    icon = { Icon(Icons.Default.Description, contentDescription = null) },
                    title = "Terms & Conditions",
                    subtitle = "View our terms of service",
                    onClick = { /* open terms page */ }
                )
            }

            item {
                NavigationSetting(
                    icon = { Icon(Icons.Default.Email, contentDescription = null) },
                    title = "Contact Us",
                    subtitle = "Get in touch with support",
                    onClick = { /* open contact form */ }
                )
            }

            item {
                NavigationSetting(
                    icon = { Icon(Icons.Default.Feedback, contentDescription = null) },
                    title = "Feedback",
                    subtitle = "Share your thoughts about MyBroker",
                    onClick = { /* open feedback form */ }
                )
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextButton(onClick = { /* logout */ }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Logout")
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 8.dp)
    )
}

@Composable
private fun ToggleSetting(
    icon: @Composable (() -> Unit)? = null,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
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
        Switch(checked = checked, onCheckedChange = { onCheckedChange() })
    }
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
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
    }
}

@Composable
private fun DropdownSetting(
    icon: @Composable (() -> Unit)? = null,
    title: String,
    currentValue: String,
    options: List<String>,
    onSelect: (String) -> Unit
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
            DropdownMenuItem(text = { Text(opt) }, onClick = {
                onSelect(opt)
                expanded = false
            })
        }
    }
}
//
//@Preview(showBackground = true)
//@Composable
//private fun SettingsScreenPreview() {
//    SettingsScreen()
//}

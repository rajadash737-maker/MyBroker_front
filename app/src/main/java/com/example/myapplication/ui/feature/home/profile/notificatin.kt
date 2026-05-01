package com.example.myapplication.ui.feature.home.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.notification.NotificationItem
import com.example.myapplication.notification.NotificationStore
import com.example.myapplication.notification.NotificationSync
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationScreen(
    onNotificationClick: (NotificationItem) -> Unit = {}
) {
    val context = LocalContext.current
    var items by remember { mutableStateOf<List<NotificationItem>>(emptyList()) }
    val version by NotificationStore.version.collectAsState()
    var showUnreadOnly by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        NotificationSync.refreshFromServer(context)
    }

    LaunchedEffect(version, showUnreadOnly) {
        items = NotificationStore.load(context).let { list ->
            if (showUnreadOnly) list.filter { !it.isRead } else list
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FilterChip(
                selected = showUnreadOnly,
                onClick = { showUnreadOnly = true },
                label = { Text(stringResource(id = R.string.notifications_unread)) }
            )
            FilterChip(
                selected = !showUnreadOnly,
                onClick = { showUnreadOnly = false },
                label = { Text(stringResource(id = R.string.notifications_all)) }
            )
            if (items.isNotEmpty() && showUnreadOnly) {
                TextButton(onClick = {
                    NotificationStore.markAllRead(context)
                    NotificationSync.markAllRead(context)
                }) {
                    Text(stringResource(id = R.string.notifications_mark_all_read))
                }
            }
        }

        if (items.isEmpty()) {
            Text(
                text = if (showUnreadOnly) stringResource(id = R.string.notifications_empty_unread) else stringResource(id = R.string.notifications_empty_all),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 24.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 12.dp),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items) { item ->
                    Card(
                        modifier = Modifier.clickable {
                            NotificationStore.markRead(context, item)
                            item.id?.let { NotificationSync.markRead(context, notificationIds = listOf(it)) }
                            item.referenceId?.let { NotificationSync.markRead(context, referenceIds = listOf(it)) }
                            onNotificationClick(item)
                        }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(item.title, style = MaterialTheme.typography.titleMedium)
                            Text(item.body, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                formatTimestamp(item.timestamp),
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatTimestamp(value: Long): String {
    val formatter = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    return formatter.format(Date(value))
}

package com.example.myapplication.ui.feature.sell_land.components



import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun DraftResumeDialog(
    onResume: () -> Unit,
    onStartNew: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Resume Draft?")
        },
        text = {
            Text("You have an unfinished Sell Land draft. What would you like to do?")
        },
        confirmButton = {
            TextButton(onClick = onResume) {
                Text("Resume")
            }
        },
        dismissButton = {
            TextButton(onClick = onStartNew) {
                Text("Start New")
            }
        }
    )
}

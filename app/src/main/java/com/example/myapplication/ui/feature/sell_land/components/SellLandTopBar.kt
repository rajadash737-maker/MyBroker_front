package com.example.myapplication.ui.feature.sell_land.components




import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellLandTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    onSaveDraft: (() -> Unit)? = null
) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            if (onSaveDraft != null) {
                TextButton(onClick = onSaveDraft) {
                    Text("Save Draft")
                }
            }
        }
    )
}

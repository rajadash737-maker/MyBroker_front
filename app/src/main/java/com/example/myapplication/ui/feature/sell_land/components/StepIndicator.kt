package com.example.myapplication.ui.feature.sell_land.components



import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.feature.sell_land.model.SellLandStep

@Composable
fun StepIndicator(
    currentStep: SellLandStep
) {
    val steps = SellLandStep.entries.toList()
    val progress =
        (steps.indexOf(currentStep) + 1).toFloat() / steps.size.toFloat()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Step ${steps.indexOf(currentStep) + 1} of ${steps.size}",
            style = MaterialTheme.typography.labelSmall
        )
    }
}

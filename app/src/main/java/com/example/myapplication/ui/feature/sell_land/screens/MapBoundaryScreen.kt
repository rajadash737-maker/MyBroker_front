package com.example.myapplication.ui.feature.sell_land.screens



import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.feature.sell_land.components.SellLandTopBar
import com.example.myapplication.ui.feature.sell_land.viewmodel.SellLandViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*


import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment





@Composable
fun MapBoundaryScreen(
    viewModel: SellLandViewModel,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    val draft by viewModel.draft.collectAsState()

    var points by remember { mutableStateOf(draft.polygon) }

    var isSatellite by remember { mutableStateOf(false) }

    val mapProperties = MapProperties(
        mapType = if (isSatellite) MapType.SATELLITE else MapType.NORMAL
    )


    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            points.firstOrNull() ?: LatLng(20.5937, 78.9629), // India center
            16f
        )
    }

    Scaffold(
        topBar = {
            SellLandTopBar(
                title = "Mark Land Boundary",
                onBack = onBack,
                onSaveDraft = {
                    viewModel.updatePolygon(points)
                    viewModel.saveBoundary()
                    viewModel.saveDraft(step = 4)
                }

            )
        },
        bottomBar = {
            BoundaryActionBar(
                isValid = points.size >= 3,
                onClear = { points = emptyList() },
                onConfirm = {
                    viewModel.updatePolygon(points)
                    viewModel.saveBoundary()
                    viewModel.saveDraft(step = 4)
                    onContinue()
                }

            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {

                GoogleMap(
                    modifier = Modifier.matchParentSize(),
                    cameraPositionState = cameraPositionState,
                    properties = mapProperties,
                    onMapClick = { latLng ->
                        if (!points.contains(latLng)) {
                            points = points + latLng
                        }
                    }
                ) {

                    // Markers
                    points.forEach { point ->
                        Marker(
                            state = MarkerState(position = point),
                            onClick = {
                                points = points - point
                                true
                            }
                        )
                    }

                    // Polygon
                    if (points.size >= 3) {
                        Polygon(
                            points = points,
                            fillColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                            strokeColor = MaterialTheme.colorScheme.primary,
                            strokeWidth = 4f
                        )
                    }
                }

                InstructionCard(
                    pointCount = points.size,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                )


                // 👇 Floating Button MUST be inside Box
                FloatingActionButton(
                    onClick = { isSatellite = !isSatellite },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = "Toggle Map"
                    )
                }
            }
        }
    }

}

@Composable
fun BoundaryActionBar(
    isValid: Boolean,
    onClear: () -> Unit,
    onConfirm: () -> Unit
) {
    Surface(shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onClear,
                modifier = Modifier.weight(1f)
            ) {
                Text("Clear")
            }

            Button(
                onClick = onConfirm,
                modifier = Modifier.weight(1f),
                enabled = isValid
            ) {
                Text("Confirm Boundary")
            }
        }
    }
}

@Composable
fun InstructionCard(
    pointCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "How to mark boundary",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text("• Tap on map to add points")
            Text("• Minimum 3 points required")
            Text("• Double tap to remove points")
            Text("• Points selected: $pointCount")
        }
    }
}

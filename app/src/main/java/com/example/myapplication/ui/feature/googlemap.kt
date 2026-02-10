package com.example.myapplication.ui.feature

//package com.example.myapplication.ui.theme

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
@Composable
fun BuyerMapScreen(
    navController: NavController,
    onLocationSelected: (Double, Double) -> Unit
) {
    val context = LocalContext.current

    // Default location
    val defaultLocation = LatLng(28.6139, 77.2090)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 17f)
    }

    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }

    // Move camera to current location
    LaunchedEffect(Unit) {
        moveToMyLocation(context, cameraPositionState)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            mapType = MapType.SATELLITE,
            isMyLocationEnabled = true
        ),
        onMapClick = { latLng ->
            selectedLatLng = latLng
            onLocationSelected(latLng.latitude, latLng.longitude)

            Toast.makeText(
                context,
                "Selected: ${latLng.latitude}, ${latLng.longitude}",
                Toast.LENGTH_SHORT
            ).show()
        }
    ) {
        selectedLatLng?.let { point ->
            Marker(
                state = MarkerState(position = point),
                title = "Selected Location"
            )
        }
    }
}

@SuppressLint("MissingPermission")
private fun moveToMyLocation(
    context: Context,
    cameraState: CameraPositionState
) {
    val fused = LocationServices.getFusedLocationProviderClient(context)

    fused.lastLocation.addOnSuccessListener { location: Location? ->
        location?.let {
            val current = LatLng(it.latitude, it.longitude)
            cameraState.position = CameraPosition.fromLatLngZoom(current, 18f)
        }
    }
}

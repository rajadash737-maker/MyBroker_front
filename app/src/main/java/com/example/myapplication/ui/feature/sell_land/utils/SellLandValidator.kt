package com.example.myapplication.ui.feature.sell_land.utils

import com.google.android.gms.maps.model.LatLng

object SellLandValidator {

    fun isPricingValid(price: Double) = price > 0

    fun isPolygonValid(points: List<LatLng>) = points.size >= 3
}

package com.example.myapplication.ui.feature.sell_land.screens



import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.example.myapplication.ui.feature.sell_land.model.OfferModel
import com.example.myapplication.ui.feature.sell_land.status.components.OfferCard

@Composable
fun OfferScreen() {

    val dummyOffers = listOf(
        OfferModel("1", "Rahul Sharma", 220000.0),
        OfferModel("2", "Amit Singh", 240000.0)
    )

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(dummyOffers) { offer ->
            OfferCard(
                offer = offer,
                onAccept = { },
                onReject = { },
                onCounter = { }
            )
        }
    }

}

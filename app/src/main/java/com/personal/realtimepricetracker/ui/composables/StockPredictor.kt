package com.personal.realtimepricetracker.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.personal.realtimepricetracker.data.model.StockPricePoint
import com.personal.realtimepricetracker.viewmodel.MainViewModel
import com.personal.realtimepricetracker.viewmodel.StockPredictionViewModel

@Composable
fun StockPredictor(
    stockPredictionViewModel: StockPredictionViewModel,
    mainViewModel: MainViewModel
    ){
    LaunchedEffect(Unit) {
        mainViewModel.getWatchListItemForUser()
    }

    val predictedPrice by stockPredictionViewModel.predictedPrice.collectAsState()
    val predictedDate by stockPredictionViewModel.predictedDate.collectAsState()
    val percentageChange by stockPredictionViewModel.percentageChange.collectAsState()
    val selectedTicker by stockPredictionViewModel.selectedTicker.collectAsState()
    val selectedTickerData by stockPredictionViewModel.selectedTickerData.collectAsState()
    val watchListItems by mainViewModel.watchList.collectAsState()
    var expanded by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (stockPredictionViewModel.isLoading.collectAsState().value) {
            CircularProgressIndicator()
        }

        Box {
            Button(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Select Stock: $selectedTicker")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                watchListItems.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item.companyName) },
                        onClick = {
                            stockPredictionViewModel.setSelectedTicker(item.ticker)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { stockPredictionViewModel.fetchAndPredictPrice("XT5PR5ABRCKXJJJW") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Predict Next Price")
        }

        Spacer(modifier = Modifier.height(24.dp))

        predictedPrice?.let { price ->
            predictedDate?.let { date ->
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Predicted Price: $${String.format("%.2f", price)}"
                    )
                    Text(
                        text = "Date: $date"
                    )
                    percentageChange?.let { change ->
                        val changeColor = if (change >= 0) Color.Green else Color.Red
                        Text(
                            text = "Percentage Change: ${String.format("%.2f", change)}%",
                            color = changeColor
                        )

                        Spacer(Modifier.height(32.dp))
                        selectedTickerData?.let {
                            StockLineGraph(
                                pricePoints = it.plus(StockPricePoint(date, price)).reversed(),
                                lineColor = changeColor,
                                dotColor = MaterialTheme.colorScheme.onBackground,
                                themeColor = MaterialTheme.colorScheme.onBackground,
                                predictedData = true
                            )
                        }
                    } ?: Text(
                        text = "Percentage Change: N/A"
                    )
                }
            }
        } ?: Text(
            text = "Select a stock and predict to see results"
        )
        Spacer(Modifier.weight(1f))
        // Disclaimer UI
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .background(color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),

        ) {
            Text(
                text = "Disclaimer: The stock price predictions provided by this app are for informational purposes only and are based on historical data and machine learning models. They do not constitute financial advice. Always consult with a qualified financial advisor before making investment decisions.",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}
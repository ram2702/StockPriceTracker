package com.personal.realtimepricetracker.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log
import com.personal.realtimepricetracker.data.api.StockApi
import com.personal.realtimepricetracker.data.model.PredictorFeedData
import com.personal.realtimepricetracker.data.model.StockPricePoint
import com.personal.realtimepricetracker.di.TFLiteStockPredictor
import com.personal.realtimepricetracker.utils.Utils.calculatePercentageChange
import com.personal.realtimepricetracker.utils.Utils.getNextDate
import kotlinx.coroutines.Dispatchers

@HiltViewModel
class StockPredictionViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val stockApi: StockApi,
    private val predictor: TFLiteStockPredictor
) : ViewModel() {
    private val _selectedTicker = MutableStateFlow("AAPL") // Default Ticker
    val selectedTicker: StateFlow<String> = _selectedTicker.asStateFlow()

    private val _selectedTickerData = MutableStateFlow<List<StockPricePoint>?>(null)
    val selectedTickerData: StateFlow<List<StockPricePoint>?> = _selectedTickerData.asStateFlow()

    private val _predictedPrice = MutableStateFlow<Float?>(null)
    val predictedPrice: StateFlow<Float?> = _predictedPrice.asStateFlow()

    private val _predictedDate = MutableStateFlow<String?>(null)
    val predictedDate: StateFlow<String?> = _predictedDate.asStateFlow()

    private val _percentageChange = MutableStateFlow<Float?>(null)
    val percentageChange: StateFlow<Float?> = _percentageChange.asStateFlow()

    fun setSelectedTicker(ticker: String) {
        _selectedTicker.value = ticker
    }

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()


    fun fetchAndPredictPrice(apiKey: String) {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = stockApi.getDailyPrices(_selectedTicker.value, apiKey)
                if(response.isSuccessful){
                    response.body()?.let { stockResponse ->
                        val prices = stockResponse.timeSeries.entries.map{
                            PredictorFeedData(
                                date = it.key,
                                open = it.value.open,
                                high = it.value.high,
                                low = it.value.low,
                                close = it.value.close,
                                volume = it.value.volume,
                            )
                        }
                        val listOfStockPricePoints = stockResponse.timeSeries.entries.map {
                            StockPricePoint(
                                date = it.key,
                                close = it.value.close
                            )
                        }
                        val latestPrice = stockResponse.timeSeries.values.last().close
                        val predicted = predictor.predictNextPrice(prices)
                        _selectedTickerData.value = listOfStockPricePoints
                        _predictedPrice.value = predicted
                        _predictedDate.value = getNextDate(prices.last().date)
                        _percentageChange.value = calculatePercentageChange(latestPrice, predicted ?: 0f)
                    }

                } else {
                    _selectedTickerData.value = null
                    _predictedPrice.value = null
                    _predictedDate.value = null
                    _percentageChange.value = null
                    Log.w("StockPredictionViewModel", "Not enough data for prediction")
                }
            } catch (e: Exception) {
                Log.w("StockPredictionViewModel", "Prediction failed", e)
                _selectedTickerData.value = null
                _predictedPrice.value = null
                _predictedDate.value = null
                _percentageChange.value = null
            } finally {
              _isLoading.value = false
            }
        }
    }
}


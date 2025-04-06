package com.personal.realtimepricetracker.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personal.realtimepricetracker.data.model.StockPricePoint
import com.personal.realtimepricetracker.data.api.StockApi
import com.personal.realtimepricetracker.data.db.WatchListEntity
import com.personal.realtimepricetracker.data.model.IndexSample
import com.personal.realtimepricetracker.data.model.StockResponse
import com.personal.realtimepricetracker.data.model.WatchlistItem
import com.personal.realtimepricetracker.data.model.majorGlobalIndices
import com.personal.realtimepricetracker.data.repository.WatchListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PriceTrackerViewModel @Inject constructor(
    private val stockApi: StockApi,
    private val repository: WatchListRepository
) : ViewModel() {
    val TAG = PriceTrackerViewModel::class.java.simpleName
    val api_key = "Q8NZMY9D2MKR1KUX"

    private val _indexData = MutableStateFlow<List<IndexSample>>(emptyList())
    val indexData: StateFlow<List<IndexSample>> = _indexData
    private val _watchList = MutableStateFlow<List<WatchlistItem>>(emptyList())
    val watchList: StateFlow<List<WatchlistItem>> = _watchList
    private val _searchResults = MutableStateFlow<Map<String, String>>(emptyMap())
    val searchResults: StateFlow<Map<String, String>> = _searchResults


    init {
        viewModelScope.launch(Dispatchers.IO) {
            majorGlobalIndices.forEach { index ->
                getIndexSample(index)
            }
            repository.watchlistSymbols.collect { watchListEntities ->
                watchListEntities.forEach { watchListEntity ->
                    fetchWatchlistItem(watchListEntity)
                }
            }
        }
    }

    fun searchForTicker(updatedString: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val response =
                        stockApi.getTickerFromCompanyName(updatedString, "NFB8GY61TW30BGGH")
                    if (response.isSuccessful) {
                        val searchResultMap =
                            response.body()?.bestMatches?.associate { searchData ->
                                searchData.symbol to searchData.name
                            } ?: emptyMap()
                        _searchResults.value = searchResultMap
                        Log.d(
                            TAG,
                            ("API Raw $updatedString " + response.body()?.toString())
                                ?: "no error"
                        )
                        Log.d(TAG, "Success: ${searchResultMap.size} searchResults")
                    } else {
                        Log.e(TAG, "Error: ${response.code()} - ${response.message()}")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Network Error: ${e.message}", e)
                }
            }
        }
    }

    fun insertToWatchList(symbol: String, name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertToWatchList(symbol, name)
        }
    }

    private fun fetchWatchlistItem(watchlistEntity: WatchListEntity) {
        viewModelScope.launch {
            try {
                val response = stockApi.getDailyPrices(watchlistEntity.ticker, api_key)
                if (response.isSuccessful) {
                    response.body()?.let { stockResponse ->
                        val item = convertToWatchlistItem(watchlistEntity.ticker,watchlistEntity.companyName, stockResponse)
                        // Do something with item (store in state, etc.)
                        Log.d("WatchlistItem", "Got: $item")
                        val currentList = _watchList.value.orEmpty()
                            .filterNot { it.symbol == item.symbol } + item

                        _watchList.value = currentList
                    }
                } else {
                    Log.e("API", "Error: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("API", "Exception: ${e.message}")
            }
        }
    }

    private suspend fun getIndexSample(symbol: String) {
        try {
            val response = stockApi.getDailyPrices(symbol, api_key)
            if (response.isSuccessful) {
                val stockResponse = response.body()
                val timeSeries = stockResponse?.timeSeries ?: emptyMap()

                val stockPrices = timeSeries.entries
                    .map { (date, data) -> StockPricePoint(date, data.close) }
                    .sortedBy { it.date }

                val updated = _indexData.value + IndexSample(
                    indexName = symbol,
                    companyName = stockResponse?.metadata?.get("2. Symbol") ?: symbol,
                    stockPrices = stockPrices,
                )
                _indexData.value = updated

            } else {
                Log.e("StockAPI", "Error: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("StockAPI", "Exception: ${e.message}", e)
        }
    }



    private fun convertToWatchlistItem(symbol: String,companyName:String, stockResponse: StockResponse): WatchlistItem {
        val timeSeries = stockResponse.timeSeries

        // Convert map to sorted list of StockPricePoint
        val stockPrices = timeSeries.map { (date, data) ->
            StockPricePoint(date, data.close)
        }.sortedByDescending { it.date } // Most recent first

        // Calculate percent change using last 2 close prices
        val percentChange : Float = if (stockPrices.size >= 2) {
            val latest = stockPrices[0].close
            val previous = stockPrices[1].close
            ((latest - previous) / previous) * 100
        } else {
            0f
        }

        return WatchlistItem(
            symbol = symbol,
            stockPrices = stockPrices,
            percentChange = String.format("%.2f", percentChange).toFloat(),
            logoUrl = "https://img.logo.dev/ticker/$symbol?token=pk_czwzG--yTyqlZnf3x1hvLw&retina=true",
            companyName = companyName
        )
    }
}
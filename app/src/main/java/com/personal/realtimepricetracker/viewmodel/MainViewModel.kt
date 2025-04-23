package com.personal.realtimepricetracker.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseAuth
import com.personal.realtimepricetracker.data.api.StockApi
import com.personal.realtimepricetracker.data.db.PriceAlertEntity
import com.personal.realtimepricetracker.data.db.WatchListEntity
import com.personal.realtimepricetracker.data.db.toWorkData
import com.personal.realtimepricetracker.data.model.DailyData
import com.personal.realtimepricetracker.data.model.StockData
import com.personal.realtimepricetracker.data.model.StockResponse
import com.personal.realtimepricetracker.data.model.majorGlobalIndices
import com.personal.realtimepricetracker.data.repository.PriceAlertRepository
import com.personal.realtimepricetracker.data.repository.WatchListRepository
import com.personal.realtimepricetracker.utils.ApiEndPoints
import com.personal.realtimepricetracker.utils.Utils.getLogoUrlFromTicker
import com.personal.realtimepricetracker.worker.PriceAlertWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val stockApi: StockApi,
    private val watchListRepository: WatchListRepository,
    private val priceAlertRepository: PriceAlertRepository,
    @ApplicationContext private val applicationContext: Context
) : ViewModel() {
    private val tag = MainViewModel::class.java.simpleName
    private val apiKey = "XT5PR5ABRCKXJJJW"

    private val _indexData = MutableStateFlow<List<StockData>>(emptyList())
    val indexData: StateFlow<List<StockData>> = _indexData

    private val _watchList = MutableStateFlow<List<StockData>>(emptyList())
    val watchList: StateFlow<List<StockData>> = _watchList

    private val _searchResults = MutableStateFlow<List<List<String>>>(emptyList())
    val searchResults: StateFlow<List<List<String>>> = _searchResults

    private val _detailScreenStock = MutableStateFlow<StockData>(StockData("","", emptyMap(),0f,""))
    var detailScreenStock: StateFlow<StockData> = _detailScreenStock

    private val _fancyDetails = MutableStateFlow<List<String>>(emptyList())
    val fancyDetails: StateFlow<List<String>> = _fancyDetails

    private val _alertPrices = MutableStateFlow<List<Float>>(emptyList())
    val alertPrices: StateFlow<List<Float>> = _alertPrices


    init {
        viewModelScope.launch(Dispatchers.IO) {
            majorGlobalIndices.forEach { (index,companyName) ->
                getIndexSample(index, companyName )
            }
        }
    }

    fun getWatchListItemForUser() {
        _watchList.value = emptyList()
        viewModelScope.launch(Dispatchers.IO) {
            val queryList: List<WatchListEntity> =
                watchListRepository.watchlistSymbols.first().filter { watchListEntity ->
                    Log.d(
                        tag,
                        "getWatchListItemForUser: name ${watchListEntity.companyName} uid: ${watchListEntity.userID} firebaseUID: ${FirebaseAuth.getInstance().uid} "
                    )
                    watchListEntity.userID == FirebaseAuth.getInstance().uid
                }
            queryList.forEach {
                Log.d(tag, "getWatchListItemForUser: ${it.companyName}")
            }
            fetchWatchlistItem(queryList)
        }
    }

    fun searchForTicker(updatedString: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val response =
                        stockApi.getTickerFromCompanyName(updatedString, "NFB8GY61TW30BGGH")
                    if (response.isSuccessful) {
                        val searchResultMap: List<List<String>> =
                            response.body()?.bestMatches?.map { searchData ->
                                listOf(
                                            searchData.ticker,
                                            searchData.name,
                                            searchData.type,
                                            searchData.region,
                                            searchData.marketOpen,
                                            searchData.marketClose,
                                            searchData.timezone,
                                            searchData.currency
                                )
                            } ?: emptyList()
                        _searchResults.value = searchResultMap
                        Log.d(tag,("API Raw $updatedString " + response.body()?.toString()))
                        Log.d(tag, "Success: ${searchResultMap.size} searchResults")
                    } else {
                        Log.e(tag, "Error: ${response.code()} - ${response.message()}")
                    }
                } catch (e: Exception) {
                    Log.e(tag, "Network Error: ${e.message}", e)
                }
            }
        }
    }

    fun insertToWatchList(ticker: String, name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            var stockPricePoint : Map<String,DailyData> = emptyMap()
            val response = stockApi.getDailyPrices(ticker, apiKey)
            if(response.isSuccessful){
                response.body()?.let { stockResponse ->
                    stockPricePoint = stockResponse.timeSeries
                }
            }
            watchListRepository.insertToWatchList(ticker, name, stockPricePoint)
        }
    }

    private fun fetchWatchlistItem(watchlistEntityItems: List<WatchListEntity>) {
        viewModelScope.launch {
            watchlistEntityItems.forEach { watchlistEntity ->
                try{
                    val response = stockApi.getDailyPrices(watchlistEntity.ticker, apiKey)
                    if (response.isSuccessful) {
                        _watchList.value = response.body()?.let { stockResponse ->
                            val item = convertToWatchlistItem(
                                watchlistEntity.ticker,
                                watchlistEntity.companyName,
                                stockResponse
                            )
                            Log.d("WatchlistItem", "Got: $item")
                            val currentList = _watchList.value
                                .filterNot { it.ticker == item.ticker } + item

                            currentList

                        } ?: watchlistEntityItems.map {
                                StockData(
                                    ticker = it.ticker,
                                    companyName = it.companyName,
                                    stockPrices = it.stockPricePoints,
                                    percentChange = it.percentageChange,
                                    logoUrl = getLogoUrlFromTicker(it.ticker)
                                )
                        }
                    } else {
                        Log.e("API", "Error: ${response.code()} ${response.message()}")
                        throw RuntimeException("Error: ${response.code()} ${response.message()}")
                    }
                } catch (e: Exception) {
                    Log.e("API", "Exception: ${e.message}")
                }
            }
        }
    }

    private suspend fun getIndexSample(ticker: String, companyName: String) {
        try {
            val response = stockApi.getDailyPrices(ticker, apiKey)
            if (response.isSuccessful) {
                val stockResponse = response.body()
                val timeSeries = stockResponse?.timeSeries ?: emptyMap()

                val updated = _indexData.value + StockData(
                    ticker = ticker,
                    companyName = companyName,
                    stockPrices = timeSeries,
                    percentChange = getPercentageChange(timeSeries),
                    logoUrl = getLogoUrlFromTicker(ticker)
                )
                _indexData.value = updated

            } else {
                Log.e("StockAPI", "Error: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("StockAPI", "Exception: ${e.message}", e)
        }
    }

    private fun getPercentageChange(timeSeries: Map<String, DailyData>) =
           (timeSeries.values.last().close - timeSeries.values.first().close) / timeSeries.values.first().close * 100


    @SuppressLint("DefaultLocale")
    private fun convertToWatchlistItem(
        ticker: String,
        companyName: String,
        stockResponse: StockResponse
    ): StockData {
        val timeSeries = stockResponse.timeSeries

        // Convert map to sorted list of StockPricePoint
        val stockPrices = timeSeries

        // Calculate percent change using last 2 close prices
        val percentChange: Float = if (stockPrices.size >= 2) {
            val latest = stockPrices.values.first().close
            val previous = stockPrices.values.last().close
            ((latest - previous) / previous) * 100
        } else {
            0f
        }

        return StockData(
            ticker = ticker,
            stockPrices = stockPrices,
            percentChange = String.format("%.2f", percentChange).toFloat(),
            logoUrl = getLogoUrlFromTicker(ticker),
            companyName = companyName
        )
    }

    fun fetchStockPrices(ticker: String, companyName: String, functionString: ApiEndPoints) {
        viewModelScope.launch {
            try {
                val response = when(functionString){
                    ApiEndPoints.TIME_SERIES_INTRADAY -> {
                        stockApi.getIntraDayPrices(symbol = ticker, apiKey= apiKey)
                    }
                    ApiEndPoints.TIME_SERIES_DAILY -> {
                        stockApi.getDailyPrices(ticker, apiKey)
                    }
                    ApiEndPoints.TIME_SERIES_WEEKLY -> {
                        stockApi.getWeeklyPrices(ticker,apiKey)
                    }
                    ApiEndPoints.TIME_SERIES_MONTHLY -> {
                        stockApi.getMonthlyPrices(ticker,apiKey)
                    }
                }
                if(response.isSuccessful){
                    val stockData = response.body()?.let { stockResponse ->
                        Log.d(tag, "fetchStockPrices: ${stockResponse.timeSeries}")
                        StockData(
                            ticker = ticker,
                            companyName = companyName,
                            stockPrices = stockResponse.timeSeries,
                            percentChange = getPercentageChange(stockResponse.timeSeries),
                            logoUrl = getLogoUrlFromTicker(ticker)
                        )
                    }
                    if (stockData != null) {
                        _detailScreenStock.value= stockData
                    }
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }


    fun setStockData(stockData: StockData) {
        _detailScreenStock.value = stockData
    }

    fun getStockFancyDetails(ticker: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val response =
                        stockApi.getTickerFromCompanyName(ticker, "NFB8GY61TW30BGGH")
                    if (response.isSuccessful) {
                        val bestMatch = response.body()?.bestMatches?.first()
                        Log.d("MainViewModel", "getStockFancyDetails: ${bestMatch?.name}")
                        _fancyDetails.value = bestMatch?.let {
                            listOf(
                                it.ticker,
                                it.name,
                                it.type,
                                it.region,
                                it.marketOpen,
                                it.marketClose,
                                it.timezone,
                                it.currency
                            )
                        } ?: emptyList()
                    }
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
    }

    fun insertToPriceAlert(priceAlertEntity: PriceAlertEntity, context: Context) {
        Log.d(tag,"PriceAlertWorker ${priceAlertEntity.alertType}")
        val workManager = WorkManager.getInstance(applicationContext)
        val constraints: Constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val request = PeriodicWorkRequestBuilder<PriceAlertWorker>(15,TimeUnit.MINUTES)
            .setInputData(priceAlertEntity.toWorkData())
            .setConstraints(constraints)
            .build()
        val uuid = request.id
        CoroutineScope(Dispatchers.IO).launch {
            priceAlertRepository.insertPriceAlert(PriceAlertEntity(
                workId = uuid,
                ticker = priceAlertEntity.ticker,
                companyName = priceAlertEntity.companyName,
                userID = priceAlertEntity.userID,
                currentPrice = priceAlertEntity.currentPrice,
                alertPrice = priceAlertEntity.alertPrice,
                alertType = priceAlertEntity.alertType,
                alertStatus = priceAlertEntity.alertStatus
            )
            )
        }
        val result = workManager.enqueue(request)
        Log.d(tag, "Work enqueue attempted with result: ${result.state.value.toString()}")
    }

    fun fetchAlertPrices(ticker: String) {
        viewModelScope.launch {
            val prices = priceAlertRepository.getAlertPricesForTicker(ticker).first()
            Log.d("StockDetail", "from Viewmodel: $prices and ticker: $ticker")
            _alertPrices.value = prices
        }
    }

    fun deleteItemFromWatchList(item: StockData) {
        viewModelScope.launch(Dispatchers.IO) {
            watchListRepository.deleteItemFromWatchList(item)
        }
        getWatchListItemForUser()
    }
}


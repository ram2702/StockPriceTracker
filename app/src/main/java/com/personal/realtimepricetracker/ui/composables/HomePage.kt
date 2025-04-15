package com.personal.realtimepricetracker.ui.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.IconButton

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.personal.realtimepricetracker.R
import com.personal.realtimepricetracker.data.model.StockData
import com.personal.realtimepricetracker.data.model.StockPricePoint
import com.personal.realtimepricetracker.data.model.majorGlobalIndices
import com.personal.realtimepricetracker.data.model.sampleIndexData
import com.personal.realtimepricetracker.data.model.sampleWatchlistItems
import com.personal.realtimepricetracker.utils.Utils
import com.personal.realtimepricetracker.utils.Utils.getStockPricePoints
import com.personal.realtimepricetracker.viewmodel.MainViewModel

@Composable
fun HomePage(
    viewModel: MainViewModel,
    navHost: NavHostController
) {
    val onDetailClick: (StockData) -> Unit = { stockData->
        viewModel.setStockData(stockData)
        navHost.navigate("details")
    }

    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        TitleCard()
        IndexGraphList(viewModel, onDetailClick)
        WatchList(viewModel, onDetailClick)
    }
}

@Composable
fun TitleCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 8.dp)
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(R.drawable.arrow_trending),
            contentDescription = "home"
        )
        Text(
            "StockTracker",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.poppins))
        )
    }
}

@Composable
fun IndexGraphList(viewModel: MainViewModel, onDetailClick: (StockData) -> Unit) {
    val indicesData by viewModel.indexData.collectAsState()

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp)
    ) {
        items(indicesData.ifEmpty { sampleIndexData }) { item ->
            val stockPricePoints : List<StockPricePoint> = getStockPricePoints(item.stockPrices)
            IndexGraphCard(
                item,
                onDetailClick
            )
        }
    }
}

@Composable
fun IndexGraphCard(
    index: StockData,
    onDetailClick: (StockData) -> Unit
) {
    val indexName = index.ticker
    val companyName = majorGlobalIndices.find { it.first == index.ticker}?.second ?: "Unidentified Stock"
    // Calculate percent change
    val percentChange = remember(index.stockPrices) {
        if (index.stockPrices.size >= 2) {
            val first = index.stockPrices.values.first().close
            val last = index.stockPrices.values.last().close
            ((last - first) / first) * 100f
        } else 0f
    }

    val trendColor =
        if (percentChange > 0f) Color(0xFF4CAF50) else Color(0xFFFF5252) // Green or Red

    Card(
        modifier = Modifier
            .width(250.dp)
            .padding(8.dp)
            .clickable { onDetailClick(index) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = Utils.getLogoUrlFromTicker(indexName),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    placeholder = painterResource(R.drawable.arrow_trending),
                    error = painterResource(R.drawable.data_icon),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(companyName, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis   )
                    Text(indexName, style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = String.format("%.2f%%", percentChange),
                color = trendColor,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Canvas Graph
            if (index.stockPrices.size >= 2) {
                StockGraphCanvas(getStockPricePoints(index.stockPrices), trendColor, 200.dp, 50.dp)
            } else {
                Text("No data", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun StockGraphCanvas(
    stockPrices: List<StockPricePoint>,
    trendColor: Color,
    width: Dp,
    height: Dp
) {
    Canvas(
        modifier = Modifier
            .size(width, height)
    ) {
        val width = size.width
        val height = size.height
        val max = stockPrices.maxOf { it.close }
        val min = stockPrices.minOf { it.close }
        val range = if (max == min) 1f else max - min
        val xStep = width / (stockPrices.size - 1)

        val path = Path()
        stockPrices.forEachIndexed { i, point ->
            val x = i * xStep
            val y = height - ((point.close - min) / range) * height
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = trendColor,
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

@Composable
fun WatchList(viewModel: MainViewModel, onDetailClick: (StockData) -> Unit) {
    val watchlistItems by viewModel.watchList.collectAsState()
    var selectedFilter by remember { mutableIntStateOf(1) } // 1 for Gainers Asc, 2 for Gainers Desc, 3 for Losers Asc, 4 for Losers Desc
    val sortedWatchlistItems = Utils.sortWatchlistItems(watchlistItems.ifEmpty { sampleWatchlistItems }, selectedFilter)
    Column(modifier = Modifier.fillMaxSize()) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically ,modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "WatchList",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            Row(
                modifier = Modifier
                    .padding(0.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(modifier = Modifier
                    .wrapContentSize()
                    .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp), colors = ButtonDefaults.buttonColors(
                    containerColor = if(selectedFilter<=2) Color(0xFF3B4CD5) else Color.Transparent,
                    disabledContentColor = Color.Transparent
                ),onClick = {
                    if(selectedFilter>=2){
                        selectedFilter-=2
                    }
                }, contentPadding = PaddingValues(vertical = 4.dp, horizontal = 4.dp)) {
                    Text(
                        modifier = Modifier, text = "Gainers",color = if(selectedFilter<=2) Color.White else MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(Modifier.width(8.dp))
                Button(modifier = Modifier
                    .wrapContentSize()
                    .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp), colors = ButtonDefaults.buttonColors(
                    containerColor = if(selectedFilter>2) Color(0xFF3B4CD5) else Color.Transparent,
                    disabledContentColor = Color.Transparent
                ) ,onClick = { if(selectedFilter<=2){
                    selectedFilter+=2
                } }, contentPadding = PaddingValues(vertical = 4.dp, horizontal = 4.dp)) {
                    Text(
                        modifier = Modifier.padding(0.dp), text = "Price", color = if(selectedFilter>2) Color.White else MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = { if(selectedFilter%2==0){
                    selectedFilter--
                } }, modifier = Modifier
                    .size(24.dp)
                    .padding(0.dp)) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Sort by Ascending",
                        tint = if(selectedFilter%2!=0) Color.White else MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .background(if(selectedFilter%2!=0) Color(0xFF3B4CD5) else Color.Transparent, CircleShape)
                    )
                }
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = {
                    if(selectedFilter%2!=0){
                        selectedFilter++
                    }
                }, modifier = Modifier
                    .size(24.dp)
                    .padding(0.dp)) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Sort by Descending",
                        tint = if(selectedFilter%2==0) Color.White else MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .background(if(selectedFilter%2==0) Color(0xFF3B4CD5) else Color.Transparent, CircleShape)
                    )
                }
                Spacer(Modifier.width(8.dp))
            }
        }
        LazyColumn {
            items(sortedWatchlistItems) { item ->
                WatchlistItemCard(item, onDetailClick)
            }
        }
    }
}

@Composable
fun WatchlistItemCard(item: StockData, onDetailClick: (StockData) -> Unit) {
    Row(

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable{onDetailClick(item)},
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = item.logoUrl,
                contentDescription = "${item.ticker} logo",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = item.ticker,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    modifier = Modifier.width(100.dp),
                    text = item.companyName,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Gray
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            val stockPricePoints: List<StockPricePoint> = getStockPricePoints(item.stockPrices)
            val graphColor = if (item.percentChange > 0) Color.Green else Color.Red
            StockGraphCanvas(stockPricePoints, trendColor = graphColor, 60.dp, 30.dp)

            Spacer(modifier = Modifier.width(12.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${item.stockPrices.values.last().close}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "${if (item.percentChange >= 0) "+" else ""}${item.percentChange}%",
                    fontSize = 13.sp,
                    color = if (item.percentChange >= 0) Color(0xFF4CAF50) else Color.Red
                )
            }
        }
    }
}

package com.personal.realtimepricetracker.ui.composables

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.personal.realtimepricetracker.data.model.StockPricePoint
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.personal.realtimepricetracker.R
import com.personal.realtimepricetracker.data.db.PriceAlertEntity
import com.personal.realtimepricetracker.data.model.StockData
import com.personal.realtimepricetracker.utils.ApiEndPoints
import com.personal.realtimepricetracker.utils.SearchDataIndex
import com.personal.realtimepricetracker.utils.Utils
import com.personal.realtimepricetracker.utils.filterOptions
import com.personal.realtimepricetracker.viewmodel.MainViewModel
import java.util.UUID


@Composable
fun StockDetail(
    navController: NavController,
    mainViewModel: MainViewModel,
    context: Context
) {
    val stockData by mainViewModel.detailScreenStock.collectAsState()
    var showPopup by remember { mutableStateOf(false) }

    //Fetch the Stock's metaData(NOT the StockPrices) using ticker
    LaunchedEffect(Unit) {
        mainViewModel.getStockFancyDetails(stockData.ticker)
    }

    var selectedButton by remember { mutableStateOf("1W") }
    val stockFancyDetails by mainViewModel.fancyDetails.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (showPopup) {
            ShowPopup(context,stockData, mainViewModel) {
                showPopup = !it
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically

        ) {
            Icon(
                modifier = Modifier
                    .size(30.dp)

                    .clickable {
                        navController.popBackStack()
                    }, imageVector = Icons.Default.ArrowBack, contentDescription = "Back Button"
            )
            Spacer(Modifier.width(8.dp))
            AsyncImage(
                model = Utils.getLogoUrlFromTicker(stockData.ticker),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                placeholder = painterResource(R.drawable.arrow_trending),
                error = painterResource(R.drawable.data_icon),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(8.dp))
            Column(verticalArrangement = Arrangement.Center, modifier = Modifier.width(150.dp)) {
                Text(
                    stockData.companyName,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 20.sp,
                    lineHeight = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(stockData.ticker, color = Color.Gray, fontSize = 10.sp, lineHeight = 10.sp)
            }
            Spacer(modifier = Modifier.weight(1f)) // to push the next item to the end
            IconButton(onClick = { showPopup = true }) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(R.drawable.alarm),
                    tint = Color.Unspecified,
                    contentDescription = "Set Price Alert"
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        StockLineGraph(
            pricePoints = Utils.getStockPricePoints(stockData.stockPrices),
            lineColor = if (stockData.percentChange >= 0) Color(0xFF029302) else Color.Red,
            dotColor = MaterialTheme.colorScheme.onBackground,
            themeColor = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            filterOptions.forEach { timeRange ->
                Button(
                    modifier = Modifier
                        .wrapContentSize()
                        .border(
                            1.dp,
                            if (selectedButton == timeRange.timeSeries) MaterialTheme.colorScheme.onBackground else Color.Gray
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContentColor = Color.Transparent
                    ), onClick = {
                        mainViewModel.fetchStockPrices(
                            stockData.ticker,
                            stockData.companyName,
                            timeRange.functionString
                        )
                        selectedButton = timeRange.timeSeries
                    }) {
                    Text(
                        modifier = Modifier.padding(2.dp),
                        text = timeRange.timeSeries,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        SearchDataCard(fancyDetails = stockFancyDetails)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShowPopup(context: Context,stockData: StockData,mainViewModel: MainViewModel, dismissPopup: (Boolean) -> Unit) {
    var priceAlert by remember { mutableStateOf("") }
    var frequencyType by remember { mutableStateOf("Once") }
    val stockCurrentPrice = stockData.stockPrices.values.last().close

    LaunchedEffect(Unit) {
        mainViewModel.fetchAlertPrices(stockData.ticker)
    }
    val alertPrices by mainViewModel.alertPrices.collectAsState()
    Log.d("StockDetail", "from Popup $alertPrices")
    BasicAlertDialog(onDismissRequest = { dismissPopup(true) }
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(8.dp))
                .border(2.dp, Color.Black, shape = RoundedCornerShape(8.dp))
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.4f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stockData.companyName,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$${stockCurrentPrice}",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Thin,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(
                    modifier = Modifier
                        .height(24.dp)
                        .wrapContentWidth()
                )
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Target Price:", fontSize = 16.sp)
                    BasicTextField(
                        modifier = Modifier
                            .width(100.dp)
                            .height(30.dp)
                            .align(Alignment.CenterVertically)
                            .background(
                                color = priceAlert.toFloatOrNull()?.let {
                                    if (alertPrices.contains(it)) Color.Red else Color(0xFF029302)
                                } ?: Color(0xFF029302),
                                shape = RectangleShape
                            )
                            .padding(start = 4.dp, top = 2.dp, end = 4.dp),
                        value = priceAlert,
                        onValueChange = { if(!it.contains(",")||!it.contains("-")) priceAlert = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 16.sp
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val frequencyButtonColors: ButtonColors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                    )
                    Text("Notify Me:", fontSize = 16.sp)
                    Row {
                        Button(
                            modifier = Modifier.defaultMinSize(1.dp,1.dp),
                            onClick = { frequencyType = "Once"},
                            contentPadding = PaddingValues(6.dp,2.dp),
                            colors = frequencyButtonColors,
                            border = BorderStroke(1.dp, if(frequencyType == "Once") MaterialTheme.colorScheme.onBackground else Color.Gray)
                        ) { Text(text = "Once",fontWeight = FontWeight.Thin, fontSize = 14.sp) }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            modifier = Modifier.defaultMinSize(1.dp,1.dp),
                            onClick = {frequencyType = "Every Time"},
                            contentPadding = PaddingValues(6.dp,2.dp),
                            colors = frequencyButtonColors,
                            border = BorderStroke(1.dp, if(frequencyType == "Every Time") MaterialTheme.colorScheme.onBackground else Color.Gray)
                        ) { Text(text = "Every Time", fontWeight = FontWeight.Thin, fontSize = 14.sp) }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Spacer(modifier = Modifier.weight(1f))
                Button(modifier = Modifier.align(Alignment.End), onClick = {
                    if (priceAlert.toFloatOrNull()?.let { !alertPrices.contains(it) } == true && priceAlert.isNotEmpty()) {
                        val priceAlertEntity = PriceAlertEntity(
                            ticker = stockData.ticker,
                            companyName = stockData.companyName,
                            userID = FirebaseAuth.getInstance().uid.toString(),
                            currentPrice = stockData.stockPrices.values.last().close,
                            alertPrice = priceAlert.toFloat(),
                            alertType = frequencyType,
                            alertStatus = false,
                            workId = UUID.randomUUID()
                        )
                        mainViewModel.insertToPriceAlert(priceAlertEntity, context)
                        dismissPopup(true)
                    } else if (alertPrices.contains(priceAlert.toFloatOrNull())) {
                        Toast.makeText(context, "Price Alert already exists for ${stockData.companyName}!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Enter a valid Price Alert!", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Create Alert")
                }
            }
        }
    }
}

@Composable
fun StockLineGraph(
    pricePoints: List<StockPricePoint>,
    lineColor: Color = Color.Red,
    dotColor: Color = Color.Red,
    dottedLineColor: Color = Color.LightGray,
    themeColor: Color
) {
    if (pricePoints.isEmpty()) return
    val maxPrice = pricePoints.maxOf { it.close }
    val minPrice = pricePoints.minOf { it.close }
    val priceRange = maxPrice - minPrice
    var xPos by remember { mutableFloatStateOf(0.0f) }
    var yPos by remember { mutableFloatStateOf(0.0f) }
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val position = event.changes.first().position
                        xPos = position.x
                        yPos = position.y
                    }
                }
            },
    ) {
        val width = size.width
        val height = size.height
        val spacing = width / (pricePoints.size - 1)
        val pointCount = pricePoints.size
        val points = pricePoints.mapIndexed { index, data ->
            val x = index * spacing
            val y = height - ((data.close - minPrice) / priceRange) * height
            Offset(x, y)
        }
        val index = (xPos / spacing).toInt().coerceIn(0, pointCount - 1)
        val snappedPoint = points[index]
        // Measure text widths
        val priceText = "\$${pricePoints[index].close}"
        val dateText = pricePoints[index].date
        val paint = Paint().apply {
            color = themeColor.toArgb()
            textSize = 42f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }
        val priceTextWidth = paint.measureText(priceText)
        val dateTextWidth = paint.measureText(dateText)
        // Clamp X within canvas width
        val canvasWidth = size.width
        val xOffset = 10f // Padding from the edge
        val clampedX = snappedPoint.x.coerceIn(
            xOffset,
            canvasWidth - maxOf(priceTextWidth, dateTextWidth) - xOffset
        )

        // Line path
        val path = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                lineTo(points[i].x, points[i].y)
            }
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 8f)
        )

        // Current price dot
        val lastPoint = points.last()
        drawCircle(
            color = dotColor,
            radius = 12f,
            center = snappedPoint
        )



        drawContext.canvas.nativeCanvas.apply {
            drawText(
                "${pricePoints[index].date}",
                clampedX,
                snappedPoint.y - 80,
                paint
            )
            drawText(
                "\$${pricePoints[index].close}",
                clampedX,
                snappedPoint.y - 30,
                paint
            )
        }

        // Dotted horizontal line from left to current price point
        drawLine(
            color = dottedLineColor,
            start = Offset(0f, lastPoint.y),
            end = Offset(lastPoint.x, lastPoint.y),
            strokeWidth = 3f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f))
        )
    }
}

@Composable
fun SearchDataCard(fancyDetails: List<String>) {
    if (fancyDetails.size < 6) {
        Text(
            text = "Loading stock details...",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        return
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = "Stock Information",
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            textAlign = TextAlign.Center,
            fontSize = 24.sp
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(SearchDataIndex.entries.size) { index ->
                if (index in 1..fancyDetails.size) {
                    Log.d("StockDetail", "SearchDataCard: ${fancyDetails[index]}")
                }
                Spacer(Modifier.height(4.dp))
                Card(
                    modifier = Modifier
                        .fillMaxHeight(),
                    border = BorderStroke(2.dp,MaterialTheme.colorScheme.onBackground),
                    colors = CardColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        disabledContainerColor = Color.Unspecified,
                        disabledContentColor = Color.Unspecified
                    ),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val text2 = when (index) {
                            1 -> "Company"
                            2 -> "Stock Type"
                            3 -> "Region"
                            4 -> "Market Opens At"
                            5 -> "Market Closes At"
                            6 -> "Timezone"
                            7 -> "Currency"
                            else -> {
                                "Ticker"
                            }
                        }
                        Text(
                            text = text2
                        )
                        Text(
                            text = fancyDetails[index],
                            Modifier.width(150.dp),
                            textAlign = TextAlign.End,
                            overflow = TextOverflow.Clip
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}


sealed class TimeRange(val timeSeries: String, val functionString: ApiEndPoints) {
    object IntraDay : TimeRange("1D", ApiEndPoints.TIME_SERIES_INTRADAY)
    object Daily : TimeRange("1W", ApiEndPoints.TIME_SERIES_DAILY)
    object Weekly : TimeRange("1M", ApiEndPoints.TIME_SERIES_WEEKLY)
    object Monthly : TimeRange("1Y", ApiEndPoints.TIME_SERIES_MONTHLY)
}
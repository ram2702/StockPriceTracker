package com.personal.realtimepricetracker.ui.composables

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.personal.realtimepricetracker.R
import com.personal.realtimepricetracker.data.db.PriceAlertEntity
import com.personal.realtimepricetracker.utils.DeleteScenario
import com.personal.realtimepricetracker.utils.Utils
import com.personal.realtimepricetracker.viewmodel.MainViewModel

@Composable
fun PriceAlertsScreen(
    context: Context,
    navController: NavHostController,
    mainViewModel: MainViewModel,
    userId: String
) {
    val priceAlerts by mainViewModel.alertPriceList.collectAsState()
    val deletePriceAlert : (PriceAlertEntity?) -> Unit = { priceAlert ->
        priceAlert?.let{ mainViewModel.deletePriceAlert(priceAlert, context) } ?:
        mainViewModel.deletePriceAlert(null,context)
    }

    var showPopup by remember { mutableStateOf(false) }
    if(showPopup) DeletePopup(DeleteScenario.DeleteAllPriceAlerts, { deletePriceAlert(null) }) { showPopup = !it }

    LaunchedEffect(priceAlerts) {
        mainViewModel.fetchAllPriceAlerts(userId)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 4.dp),
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

            Text(
                "Price Alerts",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 24.sp,
                lineHeight = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.weight(1f))
            Icon(modifier = Modifier
                .padding(end = 8.dp)
                .clickable {
                    if (priceAlerts.isNotEmpty()) {
                        showPopup = true
                    }
                },
                imageVector = Icons.Default.Delete,
                tint = if (priceAlerts.isNotEmpty()) Color.Red else Color.Gray,
                contentDescription = "Delete notifications"
            )

        }
        Spacer(Modifier.height(16.dp))

        if(priceAlerts.isNotEmpty()){
            LazyColumn {
                items(priceAlerts.size) { priceAlert ->
                    PriceAlertItem(priceAlerts[priceAlert], deletePriceAlert)
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        thickness = 1.dp
                    )
                }
            }
        }else{
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clickable { },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_trending),
                    contentDescription = "No Price Alerts Set!",
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF1F1F1F), shape = CircleShape)
                        .padding(6.dp),
                    tint = Color(0xFF4C84FF)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "No Price Alerts Set!",
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    modifier = Modifier.fillMaxWidth(0.7f),
                    text = "Add Price Alerts, don't miss updates on the stocks you're watching!",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun PriceAlertItem(
    priceAlertEntity: PriceAlertEntity,
    deletePriceAlert: (PriceAlertEntity)->Unit
) {
    var showPopup by remember { mutableStateOf(false) }
    if(showPopup) DeletePopup(DeleteScenario.DeletePriceAlert(priceAlertEntity.companyName,priceAlertEntity.ticker,priceAlertEntity.alertPrice),
        { deletePriceAlert(priceAlertEntity) }) { showPopup = !it }
    Row(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { showPopup = true }
                )
            }
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = Utils.getLogoUrlFromTicker(priceAlertEntity.ticker),
            contentDescription = "${priceAlertEntity.companyName} logo",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            placeholder = painterResource(R.drawable.arrow_trending),
            error = painterResource(R.drawable.data_icon)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = priceAlertEntity.ticker,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                modifier = Modifier.width(100.dp),
                text = priceAlertEntity.companyName,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(text = "$${priceAlertEntity.alertPrice}")
    }
}
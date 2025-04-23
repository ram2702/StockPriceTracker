package com.personal.realtimepricetracker.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.personal.realtimepricetracker.R
import com.personal.realtimepricetracker.data.db.NotificationEntity
import com.personal.realtimepricetracker.utils.DeleteScenario
import com.personal.realtimepricetracker.utils.Utils
import com.personal.realtimepricetracker.viewmodel.MainViewModel

@Composable
fun NotificationListScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    userId: String
) {
    val notifications by mainViewModel.notificationHistory.collectAsState()

    LaunchedEffect(notifications) {
        mainViewModel.fetchAllNotifications(userId)
    }
    var showDeletePopup by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize()
    ) {
        HeaderRow(navController, notifications, { showDeletePopup = true }) {
            if (showDeletePopup) DeletePopup(DeleteScenario.DeleteAllNotificationHistory,
                { mainViewModel.deleteNotificationHistory() }) { showPopup ->
                showDeletePopup = !showPopup
            }
        }
        Spacer(Modifier.height(16.dp))

        if(notifications.isNotEmpty()){
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                items(notifications.size) { notification ->
                    NotificationItem(
                        notification = notifications.get(notification),
                        modifier = Modifier.fillMaxWidth()
                    )
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
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = "No notifications missed",
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF1F1F1F), shape = CircleShape)
                        .padding(6.dp),
                    tint = Color(0xFF4C84FF)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "No unread Notifications!",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    modifier = Modifier.fillMaxWidth(0.7f),
                    text = "Good Job! Keeping track of alerts puts you ahead in the race!",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun HeaderRow(
    navController: NavHostController,
    notifications: List<NotificationEntity>,
    showPopup:()->Unit,
    DeletePopup: @Composable () -> Unit
) {
    DeletePopup()
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
            "Notification History",
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
                if (notifications.isNotEmpty()) {
                    showPopup()
                }
            },
            imageVector = Icons.Default.Delete,
            tint = if (notifications.isNotEmpty()) Color.Red else Color.Gray,
            contentDescription = "Delete notifications"
        )

    }
}

@Composable
fun NotificationItem(
    notification: NotificationEntity,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = Utils.getLogoUrlFromTicker(notification.ticker),
            contentDescription = "${notification.companyName} logo",
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
                text = notification.companyName,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = Utils.extractLastWord(notification.description),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(text = Utils.formatRelativeTime(notification.time))
    }
}
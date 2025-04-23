package com.personal.realtimepricetracker.utils

sealed class AuthResult {
    data object Idle : AuthResult()
    data class Success(val userId: String) : AuthResult()
    data class Failure(val message: String) : AuthResult()
    data object Loading : AuthResult()
}


sealed class DeleteScenario(val title: String, val descriptionString: String) {
    data class DeletePriceAlert(
        val companyName: String,
        val ticker: String,
        val alertPrice: Float
    ) : DeleteScenario(
        companyName, "Delete Price Alert for $ticker at $alertPrice?"
    )

    data object DeleteAllPriceAlerts :
        DeleteScenario("Delete Price Alerts?", "Delete All Price Alerts?")

    data object DeleteAllNotificationHistory :
        DeleteScenario("Delete Notifications?", "Clear all notification history?")

    data class DeleteStockFromWatchList(
        val companyName: String,
        val ticker: String
    ) : DeleteScenario(companyName, "Remove stock from your Watchlist?")
}

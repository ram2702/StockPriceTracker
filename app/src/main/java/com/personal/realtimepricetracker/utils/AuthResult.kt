package com.personal.realtimepricetracker.utils

sealed class AuthResult {
    data object Idle : AuthResult()
    data class Success(val userId: String) : AuthResult()
    data class Failure(val message:String) : AuthResult()
    data object Loading : AuthResult()
}

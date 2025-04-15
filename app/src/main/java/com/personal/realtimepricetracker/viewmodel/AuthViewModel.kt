package com.personal.realtimepricetracker.viewmodel

import android.app.Application
import android.service.autofill.UserData
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import com.personal.realtimepricetracker.R
import com.personal.realtimepricetracker.data.model.StockVisionUser
import com.personal.realtimepricetracker.utils.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class AuthViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {
    private val TAG = AuthViewModel::class.java.simpleName
    private val auth = FirebaseAuth.getInstance()

    //For userId
    private var _userId = MutableStateFlow("")
    val userId: StateFlow<String> = _userId

    //For Setting Loading State and Navigation
    private var _authResult = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val authResult: StateFlow<AuthResult> = _authResult

    //For fetching User Data from Firestone DB
    private var _userData = MutableStateFlow(StockVisionUser("", ""))
    val userData: StateFlow<StockVisionUser> = _userData

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(getApplication<Application>().applicationContext.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    init {
        auth.currentUser?.let {
            Log.d(TAG, "Succcess ${it.uid}")
            setSuccess()
        } ?: AuthResult.Idle
    }

    fun getGoogleSignInClient(): GoogleSignInClient {
        val client = GoogleSignIn.getClient(getApplication<Application>().applicationContext, gso)
        return client
    }

    // Called from Activity/Composable once we get idToken
    fun signInWithGoogleCredential(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        _authResult.value = AuthResult.Loading

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    setSuccess()
                } else {
                    _authResult.value =
                        AuthResult.Failure(task.exception?.message ?: "Unknown error")
                }
            }
    }

    fun createUserAtSignUp(email: String, password: String, userName: String) {
        _authResult.value = AuthResult.Loading
        viewModelScope.launch(Dispatchers.IO) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser?.uid ?: "UserID"
                    val userMap = hashMapOf(
                        "uid" to user,
                        "email" to email,
                        "username" to userName
                    )
                    Firebase.firestore.collection("users").document(user)
                        .set(userMap)
                        .addOnSuccessListener {
                            _authResult.value = AuthResult.Success(user)
                        }
                        .addOnFailureListener { e: Exception ->
                            _authResult.value =
                                AuthResult.Failure(e.message ?: "Firestore write failed")
                        }
                } else {
                    _authResult.value =
                        AuthResult.Failure(task.exception?.message ?: "Unknown Exception")
                }
            }
        }
    }

    fun signInWithCredentials(email: String, password: String) {
        _authResult.value = AuthResult.Loading
        viewModelScope.launch(Dispatchers.IO) {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    setSuccess()
                } else {
                    _authResult.value =
                        AuthResult.Failure(task.exception?.message ?: "Unknown Exception")
                }
            }
        }
    }

    fun fetchUsername() {
        val isGoogleLogin = auth.currentUser?.providerData?.any { it.providerId == "google.com" } == true

        try {
            viewModelScope.launch {
                val documentSnapshot =
                    auth.uid?.let { uid ->
                        Firebase.firestore
                            .collection("users")
                            .document(uid)
                            .get()
                            .await()
                    }
                if(isGoogleLogin){
                    val firebaseUser = FirebaseAuth.getInstance().currentUser
                    val stockVisionUserData = firebaseUser?.let { user ->
                        StockVisionUser(
                            userName = user.displayName.toString(),
                            email = user.email.toString(),
                            imageUrl = user.photoUrl.toString()
                        )
                    }
                    stockVisionUserData?.let {
                        _userData.value = it
                    }
                }
                else if(documentSnapshot?.exists() == true) {
                    documentSnapshot.toObject(StockVisionUser::class.java)?.let {
                        _userData.value = it
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "fetchUsername: $e")
        }
    }

    override fun onCleared() {
        super.onCleared()
        logOut()
    }

    fun logOut() {
        Log.d(TAG, "Signing out: ${auth.currentUser?.email}")
        auth.signOut()
        val googleSignInClient =
            GoogleSignIn.getClient(getApplication<Application>().applicationContext, gso)
        googleSignInClient.signOut().addOnCompleteListener {
            Log.d(
                TAG,
                if (GoogleSignIn.getLastSignedInAccount(getApplication<Application>().applicationContext) != null)
                    "Google User Signed in"
                else
                    "Google User Signed out"
            )
        }

        _authResult.value = AuthResult.Idle
    }

    fun setLoading() {
        _authResult.value = AuthResult.Loading
    }

    fun setSuccess() {
        _authResult.value = AuthResult.Success(auth.uid.toString())
        _userId.value = auth.uid.toString()
    }
}
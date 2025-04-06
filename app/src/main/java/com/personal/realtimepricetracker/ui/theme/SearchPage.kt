package com.personal.realtimepricetracker.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.personal.realtimepricetracker.R
import com.personal.realtimepricetracker.viewmodel.PriceTrackerViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@Composable
fun SearchPage(
    viewModel: PriceTrackerViewModel,
    innerPadding: PaddingValues
) {
    var searchQuery by remember { mutableStateOf("") }
    var queriedStringAfterDebounce by remember { mutableStateOf("") }
    val searchedResults by viewModel.searchResults.collectAsState()
    //Debounce Mechanism
    LaunchedEffect(searchQuery) {
        snapshotFlow { searchQuery }
            .debounce(1000)
            .collectLatest { updatedString ->
                if(updatedString != "") {
                    viewModel.searchForTicker(updatedString)
                }
                queriedStringAfterDebounce = updatedString
            }

    }

    Column(
        Modifier
            .padding(top = innerPadding.calculateTopPadding(), start = 16.dp, end = 16.dp)
            .fillMaxSize()
    ) {
        SearchBar(
            query = searchQuery,
            { searchQuery = it })
        Text("You searched for: $queriedStringAfterDebounce", color = Color.Gray, fontSize = 8.sp)
        if(queriedStringAfterDebounce!=""){
            LazyColumn {
                items(searchedResults.entries.toList()) { (symbol, name) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AsyncImage(
                            model = "https://img.logo.dev/ticker/$symbol?token=pk_czwzG--yTyqlZnf3x1hvLw&retina=true",
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            placeholder = painterResource(R.drawable.arrow_trending),
                            error = painterResource(R.drawable.data_icon),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        // Symbol + Name
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = symbol,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily(Font(R.font.poppins))
                            )
                            Text(
                                text = name,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                fontFamily = FontFamily(Font(R.font.poppins))
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                viewModel.insertToWatchList(symbol, name)
                            }
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Add to Watchlist")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        onValueChange = onQueryChanged,
        value = query,
        label = { Text("Search by Stock Name/Ticker", fontSize = 14.sp) },
        maxLines = 1,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        })
}
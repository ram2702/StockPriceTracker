package com.personal.realtimepricetracker.ui.composables

import androidx.compose.foundation.border
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.personal.realtimepricetracker.R
import com.personal.realtimepricetracker.utils.SearchDataIndex
import com.personal.realtimepricetracker.viewmodel.AuthViewModel
import com.personal.realtimepricetracker.viewmodel.MainViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@OptIn(FlowPreview::class)
@Composable
fun SearchPage(
    viewModel: MainViewModel,
    authViewModel: AuthViewModel,
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
            .padding()
            .fillMaxSize()
    ) {
        SearchBar(
            query = searchQuery,
            { searchQuery = it })
        Text("You searched for: $queriedStringAfterDebounce", color = Color.Gray, fontSize = 8.sp)
        if(queriedStringAfterDebounce!=""){
            LazyColumn(
                modifier = Modifier.padding(start = 4.dp, end = 4.dp)
            ) {
                items(searchedResults) { result ->
                    val symbol = result[SearchDataIndex.SYMBOL.index]
                    val name = result[SearchDataIndex.NAME.index]
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

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily(Font(R.font.poppins)),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis

                            )
                            Text(
                                text = symbol,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit
) {
    TextField(
        modifier = Modifier.fillMaxWidth(),
        onValueChange = onQueryChanged,
        value = query,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Gray,
            focusedIndicatorColor = Color.Gray,
            disabledIndicatorColor = Color.Gray,
            cursorColor = MaterialTheme.colorScheme.onBackground
        ),
        placeholder = { Text("Search by Stock Name/Ticker", fontSize = 14.sp) },
        maxLines = 1,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        })
}
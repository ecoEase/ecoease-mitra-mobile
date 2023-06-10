package com.bangkit.ecoeasemitra.ui.screen.order

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bangkit.ecoeasemitra.R
import com.bangkit.ecoeasemitra.data.Screen
import com.bangkit.ecoeasemitra.data.room.model.OrderWithDetailTransaction
import com.bangkit.ecoeasemitra.helper.toCurrency
import com.bangkit.ecoeasemitra.ui.common.UiState
import com.bangkit.ecoeasemitra.ui.component.ErrorHandler
import com.bangkit.ecoeasemitra.ui.component.ItemHistory
import com.bangkit.ecoeasemitra.ui.theme.DarkGrey
import com.bangkit.ecoeasemitra.utils.WindowInfo
import com.bangkit.ecoeasemitra.utils.rememberWindowInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OrderHistoryScreen(
    orderHistoryState: StateFlow<UiState<List<OrderWithDetailTransaction>>>,
    loadOrderHistory: () -> Unit,
    reloadOrderHistory: () -> Unit,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
){

    val refreshState = rememberCoroutineScope()
    var refreshing: Boolean by remember{ mutableStateOf(false) }
    fun refresh() = refreshState.launch {
        refreshing = true
        Log.d("TAG", "refresh: ")
        delay(200)
        reloadOrderHistory()
        refreshing = false
    }
    val pullRefreshState = rememberPullRefreshState(refreshing, ::refresh)
    val windowInfo = rememberWindowInfo()

    Box(modifier = modifier
        .fillMaxSize()
        .pullRefresh(pullRefreshState)
    ) {

        orderHistoryState.collectAsState(initial = UiState.Loading).value.let {uiState ->
            when(uiState){
                is UiState.Loading -> {
                    Loader()
                    loadOrderHistory()
                }
                is UiState.Success -> {

                    if(uiState.data.isEmpty()){
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)) {
                            Image(painter =  painterResource(R.drawable.empty_illustration), contentDescription = "empty image", modifier = Modifier
                                .size(224.dp)
                                .align(
                                    Alignment.CenterHorizontally
                                ))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = stringResource(R.string.no_order_history), style = MaterialTheme.typography.subtitle1.copy(
                                color = DarkGrey,
                                textAlign = TextAlign.Center
                            ), modifier = Modifier.align(
                                Alignment.CenterHorizontally))
                        }
                    }

                    LazyVerticalGrid(
                        columns =  GridCells.Fixed( if(windowInfo.screenWidthInfo == WindowInfo.WindowType.Compact) 1 else 2),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 32.dp)
                        ,
                        contentPadding = PaddingValues(top = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ){
                        items(uiState.data){ item ->
                            ItemHistory(
                                items = item.items.map { it.garbage.type},
                                date = item.order.created,
                                totalPrice = item.order.totalTransaction.toCurrency(),
                                statusItemHistory = item.order.status,
                                onClickDetail = { navHostController.navigate(Screen.DetailOrder.createRoute(item.order.id))
                                }
                            )
                        }
                    }

                    PullRefreshIndicator(refreshing = refreshing, state = pullRefreshState, modifier = Modifier.align(Alignment.TopCenter))

                }
                is UiState.Error -> ErrorHandler(
                    errorText = uiState.errorMessage,
                    onReload = { reloadOrderHistory() })
            }
        }
    }
}

@Composable
private fun Loader(modifier: Modifier = Modifier){
    Column(modifier = modifier.fillMaxWidth()) {
        CircularProgressIndicator(modifier = modifier.align(Alignment.CenterHorizontally))
    }
}

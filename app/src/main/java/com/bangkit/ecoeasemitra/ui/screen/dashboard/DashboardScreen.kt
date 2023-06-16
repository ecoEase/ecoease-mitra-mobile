package com.bangkit.ecoeasemitra.ui.screen.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bangkit.ecoeasemitra.R
import com.bangkit.ecoeasemitra.data.Screen
import com.bangkit.ecoeasemitra.data.room.model.Garbage
import com.bangkit.ecoeasemitra.helper.greeting
import com.bangkit.ecoeasemitra.helper.toCurrency
import com.bangkit.ecoeasemitra.ui.common.UiState
import com.bangkit.ecoeasemitra.ui.component.Banner
import com.bangkit.ecoeasemitra.ui.component.CardPrice
import com.bangkit.ecoeasemitra.ui.component.ErrorHandler
import com.bangkit.ecoeasemitra.utils.WindowInfo
import com.bangkit.ecoeasemitra.utils.rememberWindowInfo
import kotlinx.coroutines.flow.StateFlow

@Composable
fun DashboardScreen(
    garbageStateFlow: StateFlow<UiState<List<Garbage>>>,
    onLoadGarbage: () -> Unit,
    onReloadGarbage: () -> Unit,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    val windowInfo = rememberWindowInfo()
    Column {

        if (windowInfo.screenWidthInfo == WindowInfo.WindowType.Compact) {
            DashboardScreenPotraitContent(
                garbageStateFlow = garbageStateFlow,
                onLoadGarbage = onLoadGarbage,
                onReloadGarbage = onReloadGarbage,
                navHostController = navHostController,
                modifier = modifier
            )
        } else {
            DashboardScreenLandscapeContent(
                garbageStateFlow = garbageStateFlow,
                onLoadGarbage = onLoadGarbage,
                onReloadGarbage = onReloadGarbage,
                navHostController = navHostController,
                modifier = modifier
            )
        }
    }
}

@Composable
fun DashboardScreenPotraitContent(
    garbageStateFlow: StateFlow<UiState<List<Garbage>>>,
    onLoadGarbage: () -> Unit,
    onReloadGarbage: () -> Unit,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .padding(top = 32.dp)
    ) {
        Header(navHostController)
        DashboardScreenContent(
            garbageStateFlow = garbageStateFlow,
            onLoadGarbage = onLoadGarbage,
            onReloadGarbage = onReloadGarbage,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun DashboardScreenLandscapeContent(
    garbageStateFlow: StateFlow<UiState<List<Garbage>>>,
    onLoadGarbage: () -> Unit,
    onReloadGarbage: () -> Unit,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Header(navHostController = navHostController, modifier = Modifier.weight(1f))
        DashboardScreenContent(
            garbageStateFlow = garbageStateFlow,
            onLoadGarbage = onLoadGarbage,
            onReloadGarbage = onReloadGarbage,
            modifier = Modifier.weight(1f)
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Header(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(text = greeting(), style = MaterialTheme.typography.h4)
        Spacer(modifier = Modifier.height(42.dp))
        Banner(bannerAction = { navHostController.navigate(Screen.Map.route) })
        Spacer(modifier = Modifier.height(42.dp))
    }
}

@Composable
fun DashboardScreenContent(
    garbageStateFlow: StateFlow<UiState<List<Garbage>>>,
    onLoadGarbage: () -> Unit,
    onReloadGarbage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = stringResource(R.string.garbage_price), style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))
        garbageStateFlow.collectAsState(initial = UiState.Loading).value.let { uiState ->
            when (uiState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    onLoadGarbage()
                }
                is UiState.Success -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 64.dp)
                    ) {
                        items(uiState.data) { item ->
                            CardPrice(
                                imageUrl = item.urlPhoto,
                                name = item.type,
                                price = "Rp${item.price.toCurrency()}"
                            )
                        }
                    }
                }
                is UiState.Error -> ErrorHandler(
                    errorText = uiState.errorMessage,
                    onReload = { onReloadGarbage() })
            }
        }
    }
}
package com.bangkit.ecoeasemitra.ui.screen

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Replay
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bangkit.ecoeasemitra.R
import com.bangkit.ecoeasemitra.data.Screen
import com.bangkit.ecoeasemitra.data.room.model.OrderWithDetailTransaction
import com.bangkit.ecoeasemitra.helper.getLastLocation
import com.bangkit.ecoeasemitra.ui.common.UiState
import com.bangkit.ecoeasemitra.ui.component.RoundedButton
import com.bangkit.ecoeasemitra.ui.screen.order.locationPermissions
import com.bangkit.ecoeasemitra.ui.screen.order.locationPermissions28Above
import com.bangkit.ecoeasemitra.ui.theme.DarkGrey
import com.bangkit.ecoeasemitra.ui.theme.EcoEaseTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterialApi::class)
@Composable
fun MapScreen(
    navHostController: NavHostController,
    availableOrderStateFlow: StateFlow<UiState<List<OrderWithDetailTransaction>>>,
    loadAvailableOrders: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val cameraPositionState = rememberCameraPositionState()

    val permissionsState =
        rememberMultiplePermissionsState(permissions = if (Build.VERSION.SDK_INT > 28) locationPermissions28Above else locationPermissions)
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    var userName by rememberSaveable { mutableStateOf("") }
    var id: String by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf("") }
    var detailAddress by rememberSaveable { mutableStateOf("") }
    var district by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    var garbageNames by remember { mutableStateOf("") }

    fun handlerRequestPermissionAndCurrentLocation() {
        permissionsState.launchMultiplePermissionRequest()
        fusedLocationClient.getLastLocation(context,
            onSuccess = {
                cameraPositionState.position =
                    CameraPosition.fromLatLngZoom(LatLng(it.latitude, it.longitude), 15f)
            },
            onError = {
                Toast.makeText(context, "$it", Toast.LENGTH_SHORT).show()
            }
        )
    }

    LaunchedEffect(Unit) {
        handlerRequestPermissionAndCurrentLocation()
    }

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )

    PermissionsRequired(
        multiplePermissionsState = permissionsState,
        permissionsNotGrantedContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Text(
                    text = "To run this feature, this app require those permission.",
                    style = MaterialTheme.typography.caption.copy(
                        color = DarkGrey
                    )
                )
                RoundedButton(
                    text = "re-request permission",
                    trailIcon = Icons.Default.Replay,
                    onClick = { handlerRequestPermissionAndCurrentLocation() })
            }
        },
        permissionsNotAvailableContent = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Required permission is not available in your device 😢")
            }
        }) {

        fun poiClickHandler(data: OrderWithDetailTransaction) {
            data.let {
                id = it.order.id
                userName = it.user.firstName
                detailAddress = it.address.detail
                district = it.address.district
                city = it.address.city
                date = it.order.created
                garbageNames = it.items.map { it.garbage.type }.joinToString(", ")
            }
            bottomSheetScaffoldState.bottomSheetState.isExpanded
        }
        BottomSheetScaffold(
            scaffoldState = bottomSheetScaffoldState,
            sheetContent = {
                DetailOrder(
                    id = id,
                    userName = userName,
                    date = date,
                    detailAddress = detailAddress,
                    district = district,
                    city = city,
                    garbageNames = garbageNames,
                    collapseBottomSheet = {
                        coroutineScope.launch {
                            bottomSheetScaffoldState.bottomSheetState.collapse()
                        }
                    },
                    openDetailOrder = { id ->
                        navHostController.navigate(
                            Screen.DetailOrder.createRoute(
                                id
                            )
                        )
                    }
                )
            },
            sheetPeekHeight = if (id.isEmpty()) 0.dp else 64.dp,
            sheetShape = RoundedCornerShape(topEnd = 30.dp, topStart = 30.dp),
        ) {
            Box(
                modifier = modifier
                    .fillMaxSize()
            ) {
                GoogleMap(
                    properties = MapProperties(isMyLocationEnabled = true),
                    uiSettings = MapUiSettings(
                        myLocationButtonEnabled = true,
                        compassEnabled = true
                    ),
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    availableOrderStateFlow.collectAsState(initial = UiState.Loading).value.let { uiState ->
                        when (uiState) {
                            is UiState.Loading -> loadAvailableOrders()
                            is UiState.Success -> {
                                uiState.data.forEach {
                                    it.location?.let { location ->
                                        Marker(
                                            state = MarkerState(
                                                position = LatLng(
                                                    location.latitude,
                                                    location.longitude
                                                )
                                            ),
                                            onClick = { _ ->
                                                if (bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
                                                    coroutineScope.launch { bottomSheetScaffoldState.bottomSheetState.expand() }
                                                }
                                                poiClickHandler(it)
                                                false
                                            },
                                            title = "${it.address.district}, ${it.address.city}",
                                            snippet = it.address.detail
                                        )
                                    }
                                }
                            }
                            is UiState.Error -> Log.d("TAG", "MapScreen: ${uiState.errorMessage}")
                        }
                    }
                }
            }
        }
    }

}

@Composable
private fun DetailOrder(
    id: String,
    userName: String,
    date: String,
    detailAddress: String,
    district: String,
    city: String,
    garbageNames: String,
    collapseBottomSheet: () -> Unit,
    openDetailOrder: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 64.dp, top = 16.dp)
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(DarkGrey)
                    .width(64.dp)
                    .height(4.dp)
                    .align(Alignment.Center)
            )

        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Detail Order", style = MaterialTheme.typography.body2.copy(
                    color = DarkGrey
                )
            )
            IconButton(
                onClick = { collapseBottomSheet() }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "close icon",
                    tint = DarkGrey
                )
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = garbageNames,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.weight(1f),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
            Text(
                text = date, style = MaterialTheme.typography.caption.copy(
                    DarkGrey
                )
            )
        }
        Column{
            Text(
                text = userName,
                style = MaterialTheme.typography.body1.copy(color = DarkGrey)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = detailAddress, style = MaterialTheme.typography.subtitle2)
            Text(text = "$district, $city.", style = MaterialTheme.typography.caption)
        }
        RoundedButton(
            text = stringResource(id = R.string.detail),
            modifier = Modifier.fillMaxWidth(),
            onClick = { openDetailOrder(id) })
    }
}

@Preview(showBackground = true)
@Composable
fun MapScreenPreview() {
    EcoEaseTheme {
//        MapScreen()
    }
}
package com.bangkit.ecoeasemitra.ui.screen.order

import android.location.Location
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bangkit.ecoeasemitra.R
import com.bangkit.ecoeasemitra.data.Screen
import com.bangkit.ecoeasemitra.data.event.MyEvent
import com.bangkit.ecoeasemitra.data.model.GarbageAdded
import com.bangkit.ecoeasemitra.data.model.Order
import com.bangkit.ecoeasemitra.data.room.model.Garbage
import com.bangkit.ecoeasemitra.helper.generateUUID
import com.bangkit.ecoeasemitra.helper.toCurrency
import com.bangkit.ecoeasemitra.ui.common.UiState
import com.bangkit.ecoeasemitra.ui.component.*
import com.bangkit.ecoeasemitra.ui.theme.LightGrey
import com.bangkit.ecoeasemitra.utils.WindowInfo
import com.bangkit.ecoeasemitra.utils.rememberWindowInfo
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

//    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
val locationPermissions28Above = listOf(
    android.Manifest.permission.ACCESS_COARSE_LOCATION,
    android.Manifest.permission.ACCESS_FINE_LOCATION,
)

val locationPermissions = listOf(
    android.Manifest.permission.ACCESS_COARSE_LOCATION,
    android.Manifest.permission.ACCESS_FINE_LOCATION,
)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun OrderScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    orderStateFlow: StateFlow<Order>,
    listGarbageFlow: StateFlow<UiState<List<Garbage>>>,
    lastLocationStateFlow: StateFlow<UiState<Location>>,
    loadLastLocation: () -> Unit,
    loadListGarbage: () -> Unit,
    reloadListGarbage: () -> Unit,
    addGarbageOrderSlot: () -> Unit = {},
    deleteGarbageSlotAt: (Int) -> Unit = {},
    onAcceptResetOrder: () -> Unit = {},
    onLoadSelectedAddress: () -> Unit,
    onReloadSelectedAddress: () -> Unit,
    onMakeOrder: (List<GarbageAdded>, Long, Location?) -> Unit,
    selectedAddressStateFlow: StateFlow<UiState<com.bangkit.ecoeasemitra.data.room.model.Address>>,
    eventFlow: Flow<MyEvent>,
    updateGarbageAtIndex: (Int, GarbageAdded) -> Unit,
) {
    val context = LocalContext.current
    val orderState by orderStateFlow.collectAsState()
    val lazyListState = rememberLazyListState()
    val windowInfo = rememberWindowInfo()
    val permissionState =
        rememberMultiplePermissionsState(permissions = if (Build.VERSION.SDK_INT > 28) locationPermissions28Above else locationPermissions)

    var garbageSlot: MutableList<String> = remember { mutableStateListOf<String>() }
    var location: Location? by remember { mutableStateOf(null) }
    var addedForm by rememberSaveable { mutableStateOf(0) }
    var openDialog by remember { mutableStateOf(false) }
    var openDialogResetOrder by remember { mutableStateOf(false) }
    var openDialogLocationPermission by remember { mutableStateOf(false) }
    var isAddressNull by remember { mutableStateOf(true) }

    LaunchedEffect(garbageSlot.toList()) { lazyListState.animateScrollToItem(garbageSlot.size) }
    BackHandler(orderState.total > 0) {//handle physical back button
        openDialogResetOrder = true
    }

    fun deleteGarbageSlotAtHandler(index: Int) {
        garbageSlot.removeAt(index)
        deleteGarbageSlotAt(index)
    }

    fun onMakeOrderHandler() {
        if (isAddressNull) {
            Toast.makeText(
                context,
                "Anda masih belum memilih alamat, pilih alamat terlebih dahulu",
                Toast.LENGTH_SHORT
            ).show()
        } else if (orderState.garbageList.contains(null)) {//prevent null value when user make new order
            Toast.makeText(
                context,
                "Masih ada kolom sampah yg kosong, isi atau hapus kolom terlebih dahulu",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            openDialog = true
        }
    }

    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
        eventFlow.collect { event ->
            when(event) {
                is MyEvent.MessageEvent -> Toast.makeText( context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    PermissionsRequired(
        multiplePermissionsState = permissionState,
        permissionsNotGrantedContent = { },
        permissionsNotAvailableContent = {
            Column { Text(text = "Maaf perangkat anda tidak dapat mengakses fitur ini") }
        }) {

        lastLocationStateFlow.collectAsState(initial = UiState.Loading).value.let { uiState ->
            when (uiState) {
                is UiState.Success -> location = uiState.data
                is UiState.Loading -> loadLastLocation()
                is UiState.Error -> Log.d("TAG", "error: ${uiState.errorMessage}")
//                is UiState.Error -> Toast.makeText(context, "error: ${uiState.errorMessage}", Toast.LENGTH_SHORT).show()
            }
        }

        ScreenModeContainer(
            modifier = modifier,
            windowType = windowInfo.screenWidthInfo,
            content = {
            OrderScreenContent(
                navHostController = navHostController,
                isPotrait = true,
                lazyListState = lazyListState,
                orderStateFlow = orderStateFlow,
                listGarbageFlow = listGarbageFlow,
                loadListGarbage = loadListGarbage,
                reloadListGarbage = reloadListGarbage,
                onAddGarbageOrderSlot = {
                    addGarbageOrderSlot()//add new slot garbage in viewmodel
                    addedForm += 1
                    garbageSlot.add(generateUUID())
                },
                deleteGarbageSlotAt = ::deleteGarbageSlotAtHandler,
                onLoadSelectedAddress = onLoadSelectedAddress,
                onSuccessLoadSelectedAddress = { isAddressNull = false },
                onReloadSelectedAddress = onReloadSelectedAddress,
                garbageSlot = garbageSlot,
                selectedAddressStateFlow = selectedAddressStateFlow,
                updateGarbageAtIndex = { index, newUpdateGarbageData ->
                    updateGarbageAtIndex(index, newUpdateGarbageData)
                }
            )
        },
            bottomSheet = {
                BottomSheet(
                    label = stringResource(R.string.total),
                    actionName = stringResource(R.string.make_order),
                    onActionButtonClicked = ::onMakeOrderHandler,
                    information = "Rp${orderState.total.toCurrency()}",
                    isActive = orderState.total > 0 && !isAddressNull,
                    modifier = Modifier.fillMaxSize()
                )
            }
        )
    }
    DialogBox(
        text = if (location == null) "Sepertinya order anda tidak dapat di pin point oleh sistem, apakah anda yakin ingin melanjutkan?" else "Apakah anda sudah yakin?",
        isOpen = openDialog,
        onDissmiss = { openDialog = false },
        onAccept = {
            onAcceptResetOrder()
            onMakeOrder(
                orderState.garbageList.map { it!! },
                orderState.total,
                location)
        })
    DialogBox(
        text = "Apakah anda yakin ingin membatalkan order anda",
        onDissmiss = { openDialogResetOrder = false },
        onAccept = { onAcceptResetOrder() },
        isOpen = openDialogResetOrder
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OrderScreenContent(
    navHostController: NavHostController,
    isPotrait: Boolean,
    lazyListState: LazyListState,
    orderStateFlow: StateFlow<Order>,
    listGarbageFlow: StateFlow<UiState<List<Garbage>>>,
    loadListGarbage: () -> Unit,
    reloadListGarbage: () -> Unit,
    onAddGarbageOrderSlot: () -> Unit,
    deleteGarbageSlotAt: (Int) -> Unit,
    onLoadSelectedAddress: () -> Unit,
    onSuccessLoadSelectedAddress: () -> Unit,
    onReloadSelectedAddress: () -> Unit,
    garbageSlot: MutableList<String>,
    selectedAddressStateFlow: StateFlow<UiState<com.bangkit.ecoeasemitra.data.room.model.Address>>,
    updateGarbageAtIndex: (Int, GarbageAdded) -> Unit,
    modifier: Modifier = Modifier,
) {
    val orderState by orderStateFlow.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .padding(top = if (isPotrait) 52.dp else 0.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LazyColumn(
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = if (isPotrait) 136.dp else 0.dp)
        ) {
            item {
                Text(text = stringResource(R.string.address))
                selectedAddressStateFlow.collectAsState(initial = UiState.Loading).value.let { uiState ->
                    when (uiState) {
                        is UiState.Loading -> {
                            CircularProgressIndicator()
                            onLoadSelectedAddress()
                        }
                        is UiState.Success -> {
                            if (!uiState.data.selected) {
                                Text(stringResource(R.string.empty_address))
                                RoundedButton(
                                    text = stringResource(R.string.choose_address),
                                    onClick = { navHostController.navigate(Screen.ChangeAddress.route) })
                            } else {
                                onSuccessLoadSelectedAddress()//this will used to update the state to check if user already select address or not
                                AddressCard(
                                    name = uiState.data.name,
                                    detail = uiState.data.detail,
                                    district = uiState.data.district,
                                    city = uiState.data.city,
                                    onClickChange = { navHostController.navigate(Screen.ChangeAddress.route) }
                                )
                            }
                        }
                        is UiState.Error -> ErrorHandler(
                            errorText = uiState.errorMessage,
                            onReload = { onReloadSelectedAddress() })
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            val borderSize = 1.dp.toPx()
                            drawLine(
                                color = LightGrey,
                                start = Offset(0f, size.height),
                                end = Offset(size.width, size.height),
                                strokeWidth = borderSize
                            )
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.garbage))
                    RoundedButton(
                        enabled = listGarbageFlow.collectAsState().value.let { uiState ->
                            when(uiState){
                                is UiState.Success -> uiState.data.isNotEmpty()
                                else -> false
                            }
                        },
                        text = stringResource(R.string.add),
                        onClick = { onAddGarbageOrderSlot() })
                }
            }
            items(garbageSlot.toList(), key = { it }) { name ->
                val index = garbageSlot.toList().indexOf(name)
                //populate the addGarbageForm with stateflow order when there is data from it
                val addedGarbage =
                    if (orderState.garbageList.isNotEmpty()) orderState.garbageList[index] else null
                val initialGarbageName = addedGarbage?.garbage?.type
                val initialGarbageAmount = addedGarbage?.amount
                val initialGarbagePrice = addedGarbage?.garbage?.price
                val initialGarbageTotalPrice = addedGarbage?.totalPrice

//                val listGarbageSelected = orderState.garbageList.filterNotNull().map { it.garbage }

                listGarbageFlow.collectAsState(initial = UiState.Loading).value.let { uiState ->
                    when (uiState) {
                        is UiState.Loading -> loadListGarbage()
                        is UiState.Success -> {
                            AddGarbageForm(
                                initSelected = initialGarbageName,
                                initAmount = initialGarbageAmount,
                                initPrice = initialGarbagePrice,
                                initTotalPrice = initialGarbageTotalPrice,
                                listGarbage = uiState.data,
                                onDelete = {
                                    deleteGarbageSlotAt(index)
                                },
                                onUpdate = { newUpdateGarbageData ->
                                    updateGarbageAtIndex(index, newUpdateGarbageData)
                                },
                                modifier = Modifier
                                    .animateItemPlacement(tween(durationMillis = 100))
                            )
                        }
                        is UiState.Error -> ErrorHandler(
                            errorText = uiState.errorMessage,
                            onReload = { reloadListGarbage() })
                    }
                }
            }
        }
    }
}

@Composable
private fun ScreenModeContainer(
    windowType: WindowInfo.WindowType,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
    bottomSheet: @Composable () -> Unit,
) {
    when (windowType) {
        is WindowInfo.WindowType.Compact -> Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column {
                content()
            }
            Box(
                modifier = Modifier
                    .height(120.dp)
                    .align(Alignment.BottomCenter)
            ) {
                bottomSheet()
            }
        }
        else -> Row(
            modifier = modifier
                .fillMaxSize()
        ) {
            Column(modifier = Modifier.weight(1.4f)) {
                content()
            }
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()
            ) {
                bottomSheet()
            }
        }
    }
}
package com.bangkit.ecoeasemitra.ui.screen.order

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bangkit.ecoeasemitra.R
import com.bangkit.ecoeasemitra.data.Screen
import com.bangkit.ecoeasemitra.data.event.MyEvent
import com.bangkit.ecoeasemitra.data.room.model.*
import com.bangkit.ecoeasemitra.ui.common.UiState
import com.bangkit.ecoeasemitra.ui.component.*
import com.bangkit.ecoeasemitra.ui.theme.DarkGrey
import com.bangkit.ecoeasemitra.ui.theme.OrangeAccent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun DetailOrderScreen(
    orderId: String,
    navHostController: NavHostController,
    userStateFlow: StateFlow<User?>,
    onLoadDetailOrder: (String) -> Unit,
    orderDetailStateFlow: StateFlow<UiState<OrderWithDetailTransaction>>,
    onReloadDetailOrder: () -> Unit,
    onUpdateOrderStatus: (Order, StatusOrderItem, () -> Unit) -> Unit,
    onCreateNewChatroom: (String) -> Unit,
    sendNotification: (token: String, message: String) -> Unit,
    eventFlow: Flow<MyEvent>,
    createChatroomEventFlow: Flow<MyEvent>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var userId by rememberSaveable {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        eventFlow.collect { event ->
            when (event) {
                is MyEvent.MessageEvent -> Toast.makeText(
                    context,
                    event.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        createChatroomEventFlow.collect { event ->
            when (event) {
                is MyEvent.MessageEvent -> Toast.makeText(
                    context,
                    event.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    orderDetailStateFlow.collectAsState(initial = UiState.Loading).value.let { uiState ->
        when (uiState) {
            is UiState.Loading -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                onLoadDetailOrder(orderId)
            }
            is UiState.Success -> {

                OrderDetailContent(
                    navHostController = navHostController,
                    onCreateNewChatroom = onCreateNewChatroom,
                    listGarbage = uiState.data.items,
                    onUpdateOrderStatus = onUpdateOrderStatus,
                    order = uiState.data.order,
                    address = uiState.data.address,
                    user = uiState.data.user,
                    mitra = uiState.data.mitra,
                    sendNotification = sendNotification,
                    modifier = modifier,
                    myId = userStateFlow.collectAsState().value?.id ?: ""
                )

            }
            is UiState.Error -> {
                ErrorHandler(errorText = uiState.errorMessage, onReload = {
                    onReloadDetailOrder()
                })
            }
        }
    }
}

@Composable
fun OrderDetailContent(
    navHostController: NavHostController,
    onCreateNewChatroom: (String) -> Unit,
    order: Order,
    address: Address,
    user: User,
    mitra: Mitra?,
    myId: String,
    sendNotification: (token: String, message: String) -> Unit,
    onUpdateOrderStatus: (Order, StatusOrderItem, () -> Unit) -> Unit,
    listGarbage: List<GarbageTransactionWithDetail>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var openDialog by remember {
        mutableStateOf(false)
    }
    var openCancelOrderDialog by remember {
        mutableStateOf(false)
    }
    val buttonText = when(order.status){
        StatusOrderItem.NOT_TAKEN -> "ambil order"
        StatusOrderItem.TAKEN -> "ubah status (on process)"
        StatusOrderItem.ON_PROCESS-> "selesaikan order"
        else -> ""
    }
    val dialogText = when(order.status){
        StatusOrderItem.NOT_TAKEN -> "apakah anda yakin ingin ambil pesanan ini?"
        StatusOrderItem.TAKEN -> "apakah anda yakin ingin mengubah status pesanan ini menjadi ON PROCESS?"
        StatusOrderItem.ON_PROCESS -> "apakah anda yakin ingin menyelesaikan pesanan ini"
        else -> ""
    }

    fun onUpdateOrderStatusHandler(isCancelOrder: Boolean = false) {
        when {
            order.status == StatusOrderItem.NOT_TAKEN && !isCancelOrder -> {
                onUpdateOrderStatus(
                    order,
                    StatusOrderItem.TAKEN
                ) { navHostController.navigate(Screen.Success.createRoute("Berhasil pickup order🎉")) }
                onCreateNewChatroom(order.userId)
                sendNotification(user.fcmToken ?: "", "Pesananmu sudah diambil oleh mitra kami.")
            }
            order.status == StatusOrderItem.TAKEN && !isCancelOrder -> onUpdateOrderStatus(
                order,
                StatusOrderItem.ON_PROCESS
            ) {
                navHostController.navigate(Screen.Success.createRoute("Berhasil mengubah status order (on process)"))
                sendNotification(user.fcmToken ?: "", "Pesananmu sudah diproses oleh mitra kami, harap tunggu ya.")
            }
            order.status == StatusOrderItem.ON_PROCESS && !isCancelOrder -> onUpdateOrderStatus(
                order,
                StatusOrderItem.FINISHED,
            ) {
                navHostController.navigate(Screen.Success.createRoute("Berhasil menyelesaikan order 🎉"))
                sendNotification(user.fcmToken ?: "", "Hore! Pesananmu sudah selesai.")
            }
            isCancelOrder -> onUpdateOrderStatus(order, StatusOrderItem.CANCELED) {
                navHostController.navigate(Screen.History.route)
                sendNotification(user.fcmToken ?: "", "Maaf, pesananmu dibatalkan oleh mitra kami 😢")
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .padding(vertical = 32.dp)
    ) {
        Text(
            text = stringResource(R.string.status), style = MaterialTheme.typography.body1.copy(
                color = DarkGrey
            )
        )
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            StatusOrder(statusItemHistory = order.status)
            if (order.status == StatusOrderItem.TAKEN || order.status == StatusOrderItem.ON_PROCESS) {
                PillWidget(
                    color = OrangeAccent,
                    textColor = Color.White,
                    text = "batalkan pesanan",
                    modifier = Modifier.clickable { openCancelOrderDialog = true })
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.address_info),
            style = MaterialTheme.typography.body1.copy(
                color = DarkGrey
            )
        )
        DetailAddressCard(
            name = address.name,
            detail = address.detail,
            district = address.district,
            city = address.city
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.detail), style = MaterialTheme.typography.body1.copy(
                color = DarkGrey
            )
        )
        mitra?.let {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.pick_by),
                    style = MaterialTheme.typography.body1.copy(color = DarkGrey)
                )
                Text(text = it.firstName, style = MaterialTheme.typography.body1)
            }
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(listGarbage) {
                DetailCardGarbage(
                    garbageName = it.garbage.type,
                    amount = it.orderInfo.qty,
                    price = it.garbage.price,
                    total = it.orderInfo.total
                )
            }
        }
        if (order.status != StatusOrderItem.CANCELED && order.status != StatusOrderItem.FINISHED) {
            RoundedButton(
                text = buttonText,
                type = RoundedButtonType.PRIMARY,
                enabled = true,
                onClick = { openDialog = true },
                modifier = Modifier.fillMaxWidth()
            )
        }

        DialogBox(
            text = dialogText,
            onDissmiss = { openDialog = false },
            isOpen = openDialog,
            onAccept = { onUpdateOrderStatusHandler() }
        )

        DialogBox(
            text = "apakah anda yakin ingin membatalkan pesanan ini?",
            onDissmiss = { openCancelOrderDialog = false },
            isOpen = openCancelOrderDialog,
            onAccept = { onUpdateOrderStatusHandler(isCancelOrder = true) }
        )
    }
}
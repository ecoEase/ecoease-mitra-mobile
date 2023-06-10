package com.bangkit.ecoeasemitra.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ecoeasemitra.data.event.MyEvent
import com.bangkit.ecoeasemitra.data.model.GarbageAdded
import com.bangkit.ecoeasemitra.data.model.Order
import com.bangkit.ecoeasemitra.data.repository.MainRepository
import com.bangkit.ecoeasemitra.data.room.model.*
import com.bangkit.ecoeasemitra.ui.common.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderViewModel(private val repository: MainRepository) : ViewModel() {
    private val eventChannel = Channel<MyEvent>()
    val eventFlow = eventChannel.receiveAsFlow()

    private val garbage = MutableStateFlow<MutableList<GarbageAdded?>>(mutableListOf())
    private val _orderState = MutableStateFlow(Order(garbageList = listOf(), total = 0))
    private val _orderHistoryState =
        MutableStateFlow<UiState<List<OrderWithDetailTransaction>>>(UiState.Loading)
    private val _detailOrderState =
        MutableStateFlow<UiState<OrderWithDetailTransaction>>(UiState.Loading)
    private val _availableOrders =
        MutableStateFlow<UiState<List<OrderWithDetailTransaction>>>(UiState.Loading)
    private val _myUserData =
        MutableStateFlow<User?>(null)

    val orderState: StateFlow<Order> = _orderState
    val orderHistoryState: StateFlow<UiState<List<OrderWithDetailTransaction>>> = _orderHistoryState
    val detailOrderState: StateFlow<UiState<OrderWithDetailTransaction>> = _detailOrderState
    val availableOrders: StateFlow<UiState<List<OrderWithDetailTransaction>>> = _availableOrders
    val myUserData: StateFlow<User?> = _myUserData

    init {
        getMyId()
    }
    private fun getMyId() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getUser().catch {
                    eventChannel.send(MyEvent.MessageEvent("error: ${it.message}"))
                    _myUserData.value = null
                }.collect {
                    _myUserData.value = it
                }
            } catch (e: Exception) {
                eventChannel.send(MyEvent.MessageEvent("error: ${e.message}"))
                _myUserData.value = null
            }
        }
    }

    private fun calculateCurrentOrder() {
        val currentTotal = garbage.value.map { garbage ->
            garbage?.let {
                it.garbage.price * it.amount
            } ?: 0
        }.reduce { acc, i -> acc + i }
        _orderState.value = Order(garbageList = garbage.value, total = currentTotal)
    }

    fun resetCurrentOrder() {
        garbage.value = mutableListOf()
        _orderState.value = Order(garbageList = listOf(), total = 0)
    }

    // TODO: FIX the  duplicate garbage order
    fun addGarbageSlot() {//responsible for adding new garbage form
        garbage.value.add(null)
        calculateCurrentOrder()
    }
    fun deleteGarbageAt(index: Int) {
        if (garbage.value.size <= 1) {
            garbage.value = mutableListOf()
            resetCurrentOrder()
        } else {
            garbage.value.removeAt(index)
            calculateCurrentOrder()
        }
    }
    fun updateGarbage(index: Int, garbage: GarbageAdded) {//responsible for updating state in add garbage form
        this.garbage.value[index] = garbage
        calculateCurrentOrder()
    }
    suspend fun updateOrderStatus(orderId: String, statusOrderItem: StatusOrderItem, onSuccess: () -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main){
                    onSuccess()
                }
            }catch (e: Exception){
                eventChannel.send(MyEvent.MessageEvent("error: ${e.message}"))
            }
        }
    }

    //HISTORY
    fun loadOrderHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = repository.getUser().first().id
            Log.d("TAG", "loadOrderHistory: $userId")
            try {
                _orderHistoryState.value = UiState.Loading
                repository.getAllOrderHistories(userId).catch { error ->
                    _orderHistoryState.value = UiState.Error("error: ${error.message}")
                }.collect { result ->
                    _orderHistoryState.value = UiState.Success(result)
                }
            } catch (e: Exception) {
                Log.d("TAG", "loadOrderHistory: $e")
                _orderHistoryState.value =
                    if (e.message.toString().contains("HTTP 404")) UiState.Success(
                        listOf()
                    ) else UiState.Error("error: ${e.message}")
            }
        }
    }

    fun reloadOrderHistory() {
        _orderHistoryState.value = UiState.Loading
    }

    fun makeOrder(
        listGarbage: List<GarbageAdded>,
        totalTransaction: Long,
        location: android.location.Location?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                //get user data
                val user = repository.getUser()
                //get address
                val selectedAddress = repository.getSelectedAddress()
                //add new order
                selectedAddress.first()?.let { address ->
                    repository.addNewOrder(
                        listGarbage,
                        user.first(),
                        address,
                        totalTransaction,
                        location
                    )
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                }
            } catch (e: Exception) {
                eventChannel.send(MyEvent.MessageEvent("error: ${e.message}"))
            }
        }
    }

    fun loadDetailOrder(orderId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _detailOrderState.value = UiState.Loading
            try {
                repository.getOrderDetail(orderId).catch {
                    _detailOrderState.value = UiState.Error(it.message.toString())
                }.collect { data ->
                    _detailOrderState.value = UiState.Success(data)
                }
            } catch (e: Exception) {
                _detailOrderState.value = UiState.Error(e.message.toString())
            }
        }
    }

    fun reloadDetailOrder() {
        _detailOrderState.value = UiState.Loading
    }

    fun updateOrder(
        order: com.bangkit.ecoeasemitra.data.room.model.Order,
        statusOrderItem: StatusOrderItem,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.cancelOrderStatus(order = order, statusOrderItem = statusOrderItem)
                eventChannel.send(MyEvent.MessageEvent("success update order"))
                withContext(Dispatchers.Main){
                    onSuccess()
                }
            } catch (e: Exception) {
                eventChannel.send(MyEvent.MessageEvent("error: ${e.message}"))
            }
        }
    }

    fun loadAvailableOrder() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getAvailableOrder().catch {
                    _availableOrders.value = UiState.Error("error: ${it.message}")
                    eventChannel.send(MyEvent.MessageEvent("error: ${it.message}"))
                }.collect {
                    _availableOrders.value = UiState.Success(it)
                }
            } catch (e: Exception) {
                _availableOrders.value = UiState.Error("error: ${e.message}")
                eventChannel.send(MyEvent.MessageEvent("error: ${e.message}"))
            }
        }
    }
}
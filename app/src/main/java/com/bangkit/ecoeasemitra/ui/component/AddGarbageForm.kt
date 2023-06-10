package com.bangkit.ecoeasemitra.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bangkit.ecoeasemitra.R
import com.bangkit.ecoeasemitra.data.model.GarbageAdded
import com.bangkit.ecoeasemitra.data.room.model.Garbage
import com.bangkit.ecoeasemitra.helper.toCurrency
import com.bangkit.ecoeasemitra.ui.theme.BluePrimary
import com.bangkit.ecoeasemitra.ui.theme.DarkGrey
import com.bangkit.ecoeasemitra.ui.theme.OrangeAccent

@Composable
fun AddGarbageForm(
    listGarbage: List<Garbage>,
    onDelete: () -> Unit,
    onUpdate: (GarbageAdded) -> Unit,
    initSelected: String? = null,
    initAmount: Int? = null,
    initPrice: Long? = null,
    initTotalPrice: Long? = null,
    modifier: Modifier = Modifier
){
    var selectedGarbageIndex: Int by rememberSaveable{ mutableStateOf(-1) }
    var selectedGarbageAmount: Int by rememberSaveable{ mutableStateOf(0) }
    val listGarbageName = listGarbage.map { it.type }
    var totalPrice by rememberSaveable { mutableStateOf(initTotalPrice ?: 0) }
    var price by rememberSaveable{ mutableStateOf(initPrice ?: 0) }


    LaunchedEffect(selectedGarbageAmount, selectedGarbageIndex){
        if(selectedGarbageIndex != -1){
            price = listGarbage[selectedGarbageIndex].price
            totalPrice = selectedGarbageAmount * listGarbage[selectedGarbageIndex].price
            onUpdate(
                GarbageAdded(
                    garbage = listGarbage[selectedGarbageIndex],
                    amount = selectedGarbageAmount,
                    totalPrice = listGarbage[selectedGarbageIndex].price * selectedGarbageAmount
                )
            )
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
        ,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, color = BluePrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = stringResource(R.string.garbage_type), style = MaterialTheme.typography.body2.copy(
                    color = DarkGrey
                ))
                Icon(Icons.Default.Delete, contentDescription = "delete icon", tint = OrangeAccent, modifier = Modifier.clickable { onDelete() })
            }
            DropDown(
                initValue = initSelected,
                listItem = listGarbageName,
                onSelected = { selected ->
                    selectedGarbageIndex = listGarbageName.indexOf(selected)
                },
                label = "Pilih sampah"
            )
            AnimatedVisibility(visible = selectedGarbageIndex != -1 || initSelected != null) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Harga satuan Rp${price.toCurrency()}", style = MaterialTheme.typography.body2.copy(
                        color = DarkGrey
                    ))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                        ,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Rp${totalPrice.toCurrency()}")
                        Counter(
                            initValue = initAmount,
                            onValueChange = {
                                selectedGarbageAmount = it
//                                totalPrice = selectedGarbageAmount * listGarbage[selectedGarbageIndex].price
                            }
                        )
                    }
                }
            }
        }
    }
}
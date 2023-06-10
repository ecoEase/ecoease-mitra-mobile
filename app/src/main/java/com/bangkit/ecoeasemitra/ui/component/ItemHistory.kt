package com.bangkit.ecoeasemitra.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bangkit.ecoeasemitra.R
import com.bangkit.ecoeasemitra.ui.theme.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.bangkit.ecoeasemitra.data.room.model.StatusOrderItem

@Composable
fun ItemHistory(
    items: List<String>,
    date: String,
    totalPrice: String,
    statusItemHistory: StatusOrderItem,
    onClickDetail: () -> Unit,
    modifier: Modifier = Modifier
){
    Card(
        border = BorderStroke(
            width = 1.dp,
            color = when(statusItemHistory){
                StatusOrderItem.NOT_TAKEN -> BlueSecondary
                StatusOrderItem.TAKEN -> DarkTosca
                StatusOrderItem.ON_PROCESS -> GreenSecondary
                StatusOrderItem.FINISHED-> GreenPrimary
                StatusOrderItem.CANCELED -> OrangeAccent
            }
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
            ,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = items.joinToString(", "), modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Box(modifier = Modifier.width(16.dp))
                Text(
                    text = date,
                    style = MaterialTheme.typography.caption.copy(
                        color = DarkGrey
                    )
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "total",
                    style = MaterialTheme.typography.caption.copy(
                        color = DarkGrey
                    ),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(text = "Rp$totalPrice")
            }
            Row {
                Text(
                    text = "status",
                    style = MaterialTheme.typography.caption.copy(
                        color = DarkGrey
                    ),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Box(modifier = Modifier.width(16.dp))
                StatusOrder(statusItemHistory = statusItemHistory)
                Box(modifier = Modifier.weight(1f))
                PillWidget(color = MaterialTheme.colors.primary, text = stringResource(R.string.detail), textColor = MaterialTheme.colors.onBackground, modifier = Modifier.clickable { onClickDetail() })
            }
        }
    }
}

@Composable
fun StatusOrder(
    statusItemHistory: StatusOrderItem,
    modifier: Modifier = Modifier
){
    val animatedColor by animateColorAsState(
        targetValue = when(statusItemHistory){
            StatusOrderItem.NOT_TAKEN -> BlueSecondary
            StatusOrderItem.TAKEN -> DarkTosca
            StatusOrderItem.ON_PROCESS -> GreenSecondary
            StatusOrderItem.FINISHED -> GreenPrimary
            StatusOrderItem.CANCELED -> OrangeAccent
        },
        animationSpec = tween(200)
    )
    PillWidget(
        color = animatedColor,
        text =
            when(statusItemHistory){
                StatusOrderItem.NOT_TAKEN -> {
                    stringResource(R.string.order_not_taken)
                }
                StatusOrderItem.TAKEN -> {
                    stringResource(R.string.order_taken)
                }
                StatusOrderItem.ON_PROCESS -> {
                    stringResource(R.string.order_on_process)
                }
                StatusOrderItem.FINISHED -> {
                    stringResource(R.string.finished)
                }
                StatusOrderItem.CANCELED -> {
                    stringResource(R.string.order_canceled)
                }
            },
        textColor = Color.White,
        modifier = modifier
    )
}

@Composable
fun PillWidget(
    color: Color,
    textColor: Color,
    text: String,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color)
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ){
        Text(text = text, color = textColor, style = MaterialTheme.typography.caption)
    }
}

@Preview(showBackground = true)
@Composable
fun ItemHistoryPreview(){
    EcoEaseTheme() {
        Column(Modifier.padding(16.dp)) {
            ItemHistory(items = listOf("test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test",), statusItemHistory = StatusOrderItem.NOT_TAKEN, date = "20-02-2020", totalPrice = "12000", onClickDetail = { })
        }
    }
}

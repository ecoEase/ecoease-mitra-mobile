package com.bangkit.ecoeasemitra.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun BottomSheet(
    onActionButtonClicked: () -> Unit = {},
    label: String,
    actionName: String,
    information: String,
    isActive: Boolean,
    modifier: Modifier = Modifier
){
    val animatedBackgroundColor by animateColorAsState(
        targetValue = if(isActive) MaterialTheme.colors.primary else MaterialTheme.colors.secondary,
        animationSpec = tween(durationMillis = 200)
    )

    Surface(modifier = modifier
        .fillMaxWidth()
        .height(120.dp)
    ){
        Column(modifier = Modifier
            .fillMaxSize()
            .background(animatedBackgroundColor)
            .padding(horizontal = 32.dp, vertical = 16.dp)
        ,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = label, style = MaterialTheme.typography.h5.copy(
                    color = MaterialTheme.colors.background
                ))
                Text(text = information, style = MaterialTheme.typography.h5.copy(
                    color = MaterialTheme.colors.background,
                    fontWeight = FontWeight.Bold
                ))
            }
            Spacer(modifier = Modifier.weight(1f))
            RoundedButton(text = actionName, onClick = onActionButtonClicked, type = RoundedButtonType.SECONDARY, modifier = Modifier.align(Alignment.End))
        }
    }
}
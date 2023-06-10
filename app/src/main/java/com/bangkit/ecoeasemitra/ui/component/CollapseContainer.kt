package com.bangkit.ecoeasemitra.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bangkit.ecoeasemitra.ui.theme.EcoEaseTheme
import com.bangkit.ecoeasemitra.ui.theme.LightTosca

@Composable
fun CollapseContainer(
    label: String,
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit = {},
){

    val animateBotPadding by animateDpAsState(
        targetValue = if (expanded) 16.dp else 0.dp,
        animationSpec = tween(200)
    )
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(width = 1.dp, color = LightTosca)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 32.dp)
                .padding(bottom = animateBotPadding)
        ) {
            Row {
                Text(
                    text = label,
                    modifier = Modifier
                        .weight(1f)
                    ,
                )
                Icon(
                    if(expanded) Icons.Filled.ChevronLeft else Icons.Filled.ChevronRight,
                    contentDescription = "expand icon",
                    modifier = Modifier
                        .rotate(90f)
                        .clickable{ onToggle() }
                )
            }
            AnimatedVisibility(visible = expanded) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CollapseContainerPreview(){
    EcoEaseTheme() {
        CollapseContainer(label = "Tambah alamat", expanded = true, onToggle = {}){
            TextInput(label = "lorem", value = "", onValueChange = {})
            TextInput(label = "lorem", value = "", onValueChange = {})
        }
    }
}
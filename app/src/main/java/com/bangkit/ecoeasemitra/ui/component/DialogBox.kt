package com.bangkit.ecoeasemitra.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bangkit.ecoeasemitra.ui.theme.EcoEaseTheme

@Composable
fun DialogBox(
    text: String,
    onDissmiss: () -> Unit,
    onAccept: () -> Unit = {},
    isOpen: Boolean = false,
    modifier: Modifier = Modifier
){
    AnimatedVisibility(visible = isOpen) {
        Dialog(
            onDismissRequest = { onDissmiss() },
        ) {
            Card(
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = modifier
                        .height(162.dp)
                        .width(276.dp)
                        .padding(horizontal = 32.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = text,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                        ,
                        style = MaterialTheme.typography.subtitle1.copy(
                            textAlign = TextAlign.Center
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        RoundedButton(text = "Tidak", type = RoundedButtonType.SECONDARY, onClick = {
                            onDissmiss()
                        })
                        RoundedButton(text = "Ya", type = RoundedButtonType.PRIMARY, onClick = {
                            onDissmiss()
                            onAccept()
                        })
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DialogBoxPreview(){
    EcoEaseTheme() {
        DialogBox(text = "Testing?", isOpen = true, onDissmiss = {})
    }
}
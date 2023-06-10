package com.bangkit.ecoeasemitra.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bangkit.ecoeasemitra.ui.theme.EcoEaseTheme

@Composable
fun Counter(
    initValue: Int? = null,
    modifier: Modifier = Modifier,
    onValueChange: (Int) -> Unit = {}
){
    var counter: Int by rememberSaveable{
        mutableStateOf(initValue ?: 1)
    }
    
    LaunchedEffect(counter){
        onValueChange(counter)
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CounterButton(
            icon = Icons.Default.Remove,
            description = "minus",
            onClick = { if(counter > 1) counter -= 1 }
        )
        Text(text = counter.toString(), modifier = Modifier.width(42.dp), textAlign = TextAlign.Center)
        CounterButton(
            icon = Icons.Default.Add,
            description = "plus",
            onClick = { counter += 1 }
        )
    }
}

@Composable
fun CounterButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    description: String,
    onClick: () -> Unit = {}
){
    Box(
        modifier = modifier
            .size(24.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colors.primary)
            .clickable { onClick() }
    ){
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CounterPreview(){
    EcoEaseTheme {
        Counter()
    }
}
@Preview(showBackground = true)
@Composable
fun CounterButtonPreview(){
    EcoEaseTheme {
        CounterButton(
            icon = Icons.Default.Add,
            description = ""
        )
    }
}
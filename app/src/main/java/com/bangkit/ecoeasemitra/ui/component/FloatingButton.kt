package com.bangkit.ecoeasemitra.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bangkit.ecoeasemitra.ui.theme.EcoEaseTheme

@Composable
fun FloatingButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    description: String,
    backgroundColor: Color = MaterialTheme.colors.primary,
    iconColor: Color = Color.White,
    icon: ImageVector
){
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            modifier = Modifier
                .align(Alignment.Center),
            tint = iconColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FloatingButtonPreview(){
    EcoEaseTheme {
        FloatingButton(
            icon = Icons.Default.CameraAlt,
            description = ""
        )
    }
}
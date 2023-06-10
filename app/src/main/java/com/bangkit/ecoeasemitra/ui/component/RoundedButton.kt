package com.bangkit.ecoeasemitra.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bangkit.ecoeasemitra.ui.theme.BlueSecondary
import com.bangkit.ecoeasemitra.ui.theme.EcoEaseTheme

enum class RoundedButtonType{
    PRIMARY,
    SECONDARY
}
@Composable
fun RoundedButton(
    text: String,
    type: RoundedButtonType = RoundedButtonType.PRIMARY,
    onClick: () -> Unit = {},
    trailIcon: ImageVector? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
){
    Button(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(32.dp))
            .widthIn(min = 100.dp)
        ,
        enabled = enabled,
        shape = RoundedCornerShape(32.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (type == RoundedButtonType.PRIMARY) MaterialTheme.colors.primary else MaterialTheme.colors.secondary,
            contentColor = if (type == RoundedButtonType.PRIMARY) Color.White else BlueSecondary
        )
    ) {
        Text(text = text)
        trailIcon?.let {
            Box(modifier = Modifier.width(4.dp))
            Icon( it, contentDescription = "reload icon")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoundedButtonPreview(){
    EcoEaseTheme {
        RoundedButton("Primary", trailIcon = Icons.Default.Refresh)
    }
}


@Preview(showBackground = true)
@Composable
fun RoundedButtonSecPreview(){
    EcoEaseTheme {
        RoundedButton("Secondary", RoundedButtonType.SECONDARY)
    }
}
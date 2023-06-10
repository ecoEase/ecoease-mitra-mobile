package com.bangkit.ecoeasemitra.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bangkit.ecoeasemitra.ui.theme.BlueSecondary
import com.bangkit.ecoeasemitra.ui.theme.EcoEaseTheme

@Composable
fun ChatBubble(
    message: String,
    sender: String,
    isOwner: Boolean,
    date: String,
    modifier: Modifier = Modifier
){
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = if(isOwner) Arrangement.End else Arrangement.Start
    ) {
        Column(modifier = modifier
            .widthIn(max = screenWidthDp * 3/5)
            .clip(
                RoundedCornerShape(
                    topStart = if (isOwner) 16.dp else 0.dp,
                    topEnd = if (isOwner) 0.dp else 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                )
            )
            .background(if (isOwner) MaterialTheme.colors.primary else MaterialTheme.colors.secondary)
            .padding(vertical = 2.dp, horizontal = 16.dp)
            ,
            horizontalAlignment = if(isOwner) Alignment.Start else Alignment.End
        ) {
            Text(text = sender, style = MaterialTheme.typography.caption.copy(
                    fontWeight = FontWeight.Bold,
                    color = if(isOwner) Color.White.copy(
                        alpha = 0.5f
                    ) else BlueSecondary.copy(
                        alpha = 0.5f
                    )
                ),
            modifier = Modifier.align(Alignment.Start)
            )
            Text(text = message.trim(), style = MaterialTheme.typography.body2.copy(
                if(isOwner) Color.White
                else BlueSecondary
            ))
            Text(text = date, style = MaterialTheme.typography.caption.copy(
                if(isOwner) Color.White.copy(
                    alpha = 0.5f
                ) else BlueSecondary.copy(
                    alpha = 0.5f
                ),
                fontSize = 8.sp
            ),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatBubblePreview(){
    EcoEaseTheme() {
        Column(modifier = Modifier.fillMaxSize()) {
            ChatBubble(message = "lorem", sender = "udin", isOwner = true, date = "Now")
            ChatBubble(message = " lorem lorem lorem udin lorem lorem loremudin lorem lorem loremudin lorem lorem loremudin lorem lorem lorem", sender = "udin", isOwner = false, date = "Now")
        }
    }
}
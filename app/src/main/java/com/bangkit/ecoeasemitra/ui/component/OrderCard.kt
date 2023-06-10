package com.bangkit.ecoeasemitra.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bangkit.ecoeasemitra.ui.theme.DarkGrey
import com.bangkit.ecoeasemitra.ui.theme.EcoEaseTheme

@Composable
fun OrderCard(
    userName: String,
    date: String,
    detailAddress: String,
    district: String,
    city: String,
    garbageNames: List<String>,
    modifier: Modifier = Modifier
){
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = garbageNames.joinToString(", "),
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.weight(1f),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
                Text(text = date, style = MaterialTheme.typography.caption.copy(
                    DarkGrey
                ))
            }
            Row {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = userName, style = MaterialTheme.typography.caption.copy( color = DarkGrey ))
                    Text(text = detailAddress, style = MaterialTheme.typography.subtitle2)
                    Text(text = "$district, $city.", style = MaterialTheme.typography.caption)
                }
                RoundedButton(text = "detail order", modifier = Modifier.align(Alignment.Bottom))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOrderCard(){
    EcoEaseTheme() {
        OrderCard(userName = "Septa", date = "Now", detailAddress = "ambarawa", district = "candi", city = "malang", garbageNames = listOf("kaleng", "karton"))
    }
}
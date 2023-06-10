package com.bangkit.ecoeasemitra.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bangkit.ecoeasemitra.R
import com.bangkit.ecoeasemitra.ui.theme.BluePrimary
import com.bangkit.ecoeasemitra.ui.theme.DarkGrey
import com.bangkit.ecoeasemitra.ui.theme.LightGrey

@Composable
fun DetailCardGarbage(
    garbageName: String,
    amount: Int,
    price: Long,
    total: Long,
    modifier: Modifier = Modifier
){
   CardContainer(modifier = modifier) {
       Column(
           modifier = Modifier
               .fillMaxWidth()
               .padding(vertical = 16.dp, horizontal = 32.dp),
           verticalArrangement = Arrangement.spacedBy(8.dp)
       ) {
           Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
               Text(text = garbageName, style = MaterialTheme.typography.body2)
               Text(text = "$amount x Rp$price", style = MaterialTheme.typography.body2.copy(
                   color = LightGrey
               ))
           }
           Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.align(Alignment.End)) {
               Text(text = stringResource(R.string.total), style = MaterialTheme.typography.body2.copy(
                   color = LightGrey
               ))
               Text(text = "Rp$total", style = MaterialTheme.typography.body2)
           }
       }
   }
}

@Composable
fun DetailAddressCard(
    name: String,
    detail: String,
    district: String,
    city: String,
    modifier: Modifier = Modifier
){
    CardContainer(modifier = modifier) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = name)
            Text(text = "detail: $detail", style = MaterialTheme.typography.body2)
            Text(text = "Kecamatan: $district", style = MaterialTheme.typography.body2)
            Text(text = "Kab/Kota: $city", style = MaterialTheme.typography.body2)
        }
    }
}

@Composable
fun CardContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
){
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, DarkGrey)
    ) {
        content()
    }
}
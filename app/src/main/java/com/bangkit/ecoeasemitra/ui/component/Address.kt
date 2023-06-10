package com.bangkit.ecoeasemitra.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bangkit.ecoeasemitra.R
import com.bangkit.ecoeasemitra.ui.theme.DarkGrey
import com.bangkit.ecoeasemitra.ui.theme.EcoEaseTheme

@Composable
fun AddressCard(
    name: String,
    detail: String,
    district: String,
    city: String,
    onClickChange: () -> Unit,
    modifier: Modifier = Modifier
){
        var expandedContainer by remember{ mutableStateOf(false) }
        CollapseContainer(label = name, modifier = modifier, expanded = expandedContainer, onToggle = { expandedContainer = !expandedContainer }){
            Box(modifier = Modifier.height(8.dp))
            Text(text = detail, style = MaterialTheme.typography.body2)
            Box(modifier = Modifier.height(4.dp))
            Text(text = "$district, $city", style = MaterialTheme.typography.body2.copy(
                color = DarkGrey
            ))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                RoundedButton(
                    text = stringResource(R.string.change),
                    onClick = onClickChange,
                    type = RoundedButtonType.SECONDARY
                )
            }
        }
}


@Preview(showBackground = true)
@Composable
fun AddressPreview(){
    EcoEaseTheme() {
        AddressCard(name = "Alamat 1", detail = "Jalan yang lurus lorem ipsum blabal", district = "Candi 2", city = "Malang", onClickChange = {})
    }
}
package com.bangkit.ecoeasemitra.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bangkit.ecoeasemitra.R
import com.bangkit.ecoeasemitra.ui.theme.*

@Composable
fun AddressChoice(
    name: String,
    detail: String,
    district: String,
    city: String,
    checked: Boolean = false,
    onDelete: () -> Unit,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
){
    var openDialog by remember{ mutableStateOf(false) }

    val animateColorRadio by animateColorAsState(
        targetValue = if(checked) MaterialTheme.colors.primary else Color.Transparent,
        animationSpec = tween(200)
    )
    val animateColorBorder by animateColorAsState(
        targetValue = if(checked) MaterialTheme.colors.primary else GreenSecondary,
        animationSpec = tween(200)
    )

    Card(
        border = BorderStroke(width = if(checked) 4.dp else 1.dp, color = animateColorBorder),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
        ,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp)
            ,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(animateColorRadio)
                    .border(
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colors.primary
                        ), shape = CircleShape
                    )
                    .clickable { onSelected() }
            )
            Box(modifier = Modifier.width(27.dp))
            Column(Modifier.weight(1f)) {
                Text(text = name, overflow = TextOverflow.Ellipsis, maxLines = 1)
                Box(modifier = Modifier.height(8.dp))
                Text(text = detail, style = MaterialTheme.typography.body2, overflow = TextOverflow.Ellipsis, maxLines = 2)
                Box(modifier = Modifier.height(4.dp))
                Text(text = "$district, $city", style = MaterialTheme.typography.body2.copy(
                    color = DarkGrey
                ), overflow = TextOverflow.Ellipsis, maxLines = 1)
            }
            Box(modifier = Modifier.width(27.dp))
            Icon(
                imageVector = Icons.Default.Delete,
                tint = OrangeAccent,
                contentDescription = "delete icon",
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.Top)
                    .clickable { openDialog = true }
            )
        }
        DialogBox(text = stringResource(R.string.delete_address_confirm), onDissmiss = { openDialog = false }, isOpen = openDialog, onAccept = {
            onDelete()
        })
    }
}


@Preview(showBackground = true)
@Composable
fun AddressChoicePreview(){
    EcoEaseTheme() {
        AddressChoice(name = "Alamat 1", detail = "Jalan yang lurus lorem ipsum blabalasdasdasd", district = "Candi", city = "Malang", onDelete = {}, onSelected = {})
    }
}
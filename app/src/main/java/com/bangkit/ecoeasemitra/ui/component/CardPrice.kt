package com.bangkit.ecoeasemitra.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bangkit.ecoeasemitra.R
import com.bangkit.ecoeasemitra.ui.theme.DarkGrey
import com.bangkit.ecoeasemitra.ui.theme.EcoEaseTheme

@Composable
fun CardPrice(
    imageUrl: String,
    name: String,
    price: String,
    modifier: Modifier = Modifier
){
    var isLoadingImage by rememberSaveable{
        mutableStateOf(false)
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
        ,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row {
//            if (isLoadingImage) CircularProgressIndicator(modifier = Modifier.size(64.dp).align(Alignment.CenterVertically).offset(x = 16.dp))
//            else
            AsyncImage(
                model = imageUrl,
                contentDescription = "$name image",
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop,
                onLoading = { isLoadingImage = true },
                onError = { isLoadingImage = false },
                onSuccess = { isLoadingImage = false },
                placeholder = painterResource(id = R.drawable.baseline_image_24),
                error = painterResource(id = R.drawable.baseline_image_24),
            )
            Box(modifier = Modifier.width(60.dp))
            Column(
                modifier = Modifier
                    .padding(vertical = 16.dp)
            ) {
                Text(text = name)
                Box(modifier = Modifier.height(8.dp))
                Text(text = stringResource(R.string.average_price), style = MaterialTheme.typography.body2.copy(color = DarkGrey))
                Box(modifier = Modifier.height(4.dp))
                Text(text = "$price/Kg")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardPricePreview(){
    EcoEaseTheme {
        CardPrice(
            imageUrl = "https://images.unsplash.com/photo-1683659635689-3df761eddb70?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=877&q=80",
            name = "Trash 1",
            price = "500"
        )
    }
}
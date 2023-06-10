package com.bangkit.ecoeasemitra.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bangkit.ecoeasemitra.R
import com.bangkit.ecoeasemitra.ui.theme.DarkGrey
import com.bangkit.ecoeasemitra.ui.theme.EcoEaseTheme

@Composable
fun ErrorHandler(
    errorText: String,
    onReload: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(errorText, textAlign = TextAlign.Center, style = MaterialTheme.typography.caption.copy(
            color = DarkGrey
        ))
        RoundedButton(text = stringResource(R.string.reload), trailIcon = Icons.Default.Refresh, onClick = { onReload() })
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorHandlerPreview(){
    EcoEaseTheme() {
        ErrorHandler(errorText = "error", onReload = {  })
    }
}
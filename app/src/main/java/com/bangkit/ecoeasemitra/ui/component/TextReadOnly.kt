package com.bangkit.ecoeasemitra.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bangkit.ecoeasemitra.ui.theme.DarkGrey

@Composable
fun TextReadOnly(
    label: String,
    text: String,
    modifier: Modifier = Modifier
){
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(text = label, style = MaterialTheme.typography.body2.copy(
            color = DarkGrey
        ))
        Text(text = text, style = MaterialTheme.typography.body2)
    }
}
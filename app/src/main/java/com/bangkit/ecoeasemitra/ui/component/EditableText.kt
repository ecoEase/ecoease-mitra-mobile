package com.bangkit.ecoeasemitra.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bangkit.ecoeasemitra.ui.theme.BluePrimary
import com.bangkit.ecoeasemitra.ui.theme.EcoEaseTheme
import com.bangkit.ecoeasemitra.ui.theme.LightGreyVariant

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditableText(
    label: String,
    text: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier
){
    var value: String by rememberSaveable{
        mutableStateOf(text)
    }
    var onEdit: Boolean by rememberSaveable{
        mutableStateOf(false)
    }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
        ,
        value = value,
        label = {
            Text(label)
        },
        onValueChange = {
            value = it
            onChange(it)
        },
        shape = RoundedCornerShape(32.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            backgroundColor = LightGreyVariant,
            textColor = MaterialTheme.colors.onBackground,
            focusedLabelColor = MaterialTheme.colors.onBackground,

            focusedBorderColor = MaterialTheme.colors.primary,
            unfocusedBorderColor = BluePrimary
        ),
        trailingIcon = {
            if(!onEdit){
                Icon(Icons.Default.Edit, contentDescription = "edit icon", modifier = Modifier
                    .clickable {
                        onEdit = true
                        focusRequester.requestFocus()
                    }
                )
            }
            if(onEdit && value.isNotEmpty()){
                Icon(Icons.Default.Close, contentDescription = "edit icon", modifier = Modifier
                    .clickable { value = "" }
                )
            }
        },
        isError = value.isEmpty(),
    )
}

@Preview(showBackground = true)
@Composable
fun EditableTextPreview(){
    EcoEaseTheme() {
        EditableText(onChange = {}, text = "Lorem ipsum", label = "Name")
    }
}
package com.bangkit.ecoeasemitra.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bangkit.ecoeasemitra.ui.theme.BluePrimary
import com.bangkit.ecoeasemitra.ui.theme.EcoEaseTheme
import com.bangkit.ecoeasemitra.ui.theme.LightTosca

@Composable
fun TextInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String? = null,
    isTextArea: Boolean = false,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    isError: Boolean = false,
    errorMessage: String = "Error!",
    placeHolder: String = "",
    validate: () -> Unit = {},
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Done
){
    val focusRequester = remember{ FocusRequester() }
    val animatedBackgroundColor by animateColorAsState(
        targetValue = if(value.isNotEmpty()) LightTosca.copy(alpha = 0.3f) else MaterialTheme.colors.background,
        animationSpec = tween(200)
    )
    var peekPassword by remember { mutableStateOf(false) }
    var isFocused by remember{ mutableStateOf(false) }
    var isInputted by remember{ mutableStateOf(false) }

    Column {
        OutlinedTextField(
            modifier =
            modifier
                .fillMaxWidth()
                .apply {
                    if (isTextArea) height(124.dp)
                }
                .focusRequester(focusRequester)
                .onFocusChanged {
                    isFocused = it.isFocused
                }
            ,
            singleLine = !isTextArea,
            value = value,
            label = { if(label != null) Text(label) },
            placeholder = { Text(text = placeHolder) },
            onValueChange = {
                onValueChange(it)
                validate()
            },
            shape = RoundedCornerShape(32.dp),
            visualTransformation = if(isPassword && !peekPassword) PasswordVisualTransformation() else VisualTransformation.None,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = animatedBackgroundColor,
                textColor = MaterialTheme.colors.onBackground,
                focusedLabelColor = MaterialTheme.colors.onBackground,
                focusedBorderColor = MaterialTheme.colors.primary,
                unfocusedBorderColor = BluePrimary,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            trailingIcon = {
                if(isPassword){
                    IconButton(onClick = { peekPassword = !peekPassword }) {
                        Icon(imageVector = if(peekPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = "password peek icon")
                    }
                }
            },
            isError = isError
        )
        AnimatedVisibility(visible = isError) {
            Text(errorMessage, style = MaterialTheme.typography.caption.copy(
                color = Color.Red
            ), modifier = Modifier.padding(start = 16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TextInputPreview(){
    EcoEaseTheme {
        Column() {
            TextInput(label = "Label", value = "", onValueChange = {})
            TextInput(label = "Label", value = "", onValueChange = {}, isError = true)
        }
    }
}
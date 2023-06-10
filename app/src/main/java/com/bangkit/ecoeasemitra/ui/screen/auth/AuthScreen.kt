package com.bangkit.ecoeasemitra.ui.screen.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bangkit.ecoeasemitra.R
import com.bangkit.ecoeasemitra.data.Screen
import com.bangkit.ecoeasemitra.helper.InputValidation
import com.bangkit.ecoeasemitra.ui.common.UiState
import com.bangkit.ecoeasemitra.ui.component.RoundedButton
import com.bangkit.ecoeasemitra.ui.component.TextInput
import com.bangkit.ecoeasemitra.ui.theme.GreenPrimary
import kotlinx.coroutines.flow.StateFlow


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AuthScreen(
    emailValidation: InputValidation,
    passwordValidation: InputValidation,
    validateEmail: () -> Unit,
    validatePassword: () -> Unit,
    loginAction: () -> Unit,
    isLoginValid: StateFlow<UiState<Boolean>>,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var enableLogin by remember { mutableStateOf(true) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 96.dp)
            .padding(horizontal = 32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {//Logo
            Image(
                painter = painterResource(id = R.drawable.ecoease_mitra_logo),
                contentDescription = "ecoase icon",
                modifier = Modifier
                    .size(42.dp),
            )
            Text(text = stringResource(id = R.string.app_name), style = MaterialTheme.typography.h5)
        }
        isLoginValid.collectAsState().value.let { uiState ->
            when (uiState) {
                is UiState.Loading -> enableLogin = false
                is UiState.Success -> {
                    enableLogin = true
                }
                is UiState.Error -> {
                    enableLogin = true
                    Text(text = uiState.errorMessage)
                }
            }
        }
        Text("Login")
        TextInput(
            label = "Email",
            value = emailValidation.inputValue.collectAsState().value,
            onValueChange = { emailValidation.updateInputValue(it) },
            isError = emailValidation.isErrorState.collectAsState().value,
            errorMessage = emailValidation.getErrorMessage(),
            validate = validateEmail,
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        )
        TextInput(
            label = "Password",
            value = passwordValidation.inputValue.collectAsState().value,
            onValueChange = { passwordValidation.updateInputValue(it) },
            isError = passwordValidation.isErrorState.collectAsState().value,
            errorMessage = passwordValidation.getErrorMessage(),
            validate = validatePassword,
            isPassword = true,
            imeAction = ImeAction.Done
        )

        RoundedButton(
            text = "login",
            enabled = enableLogin,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                loginAction()
                keyboardController?.hide()
            })
        Row {
            Text("atau ")
            Text(
                "buat akun baru",
                modifier = Modifier.clickable { navHostController.navigate(Screen.Register.route) },
                style = MaterialTheme.typography.body1.copy(
                    color = GreenPrimary
                )
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun AuthScreenPreview(){
//    EcoEaseTheme {
////        AuthScreen(navHostController = rememberNavController(), loginAction = {})
//    }
//}
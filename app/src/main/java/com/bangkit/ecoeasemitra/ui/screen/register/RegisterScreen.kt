package com.bangkit.ecoeasemitra.ui.screen.register

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bangkit.ecoeasemitra.R
import com.bangkit.ecoeasemitra.data.Screen
import com.bangkit.ecoeasemitra.data.event.MyEvent
import com.bangkit.ecoeasemitra.data.model.ImageCaptured
import com.bangkit.ecoeasemitra.helper.InputValidation
import com.bangkit.ecoeasemitra.helper.toFile
import com.bangkit.ecoeasemitra.ui.common.UiState
import com.bangkit.ecoeasemitra.ui.component.PillWidget
import com.bangkit.ecoeasemitra.ui.component.RoundedButton
import com.bangkit.ecoeasemitra.ui.component.TextInput
import com.bangkit.ecoeasemitra.ui.theme.DarkGrey
import com.bangkit.ecoeasemitra.ui.theme.GreenPrimary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

@Composable
fun RegisterScreen(
    firstnameValidation: InputValidation,
    lastnameValidation: InputValidation,
    emailValidation: InputValidation,
    phoneNumberValidation: InputValidation,
    passwordValidation: InputValidation,
    imageProfile: StateFlow<UiState<ImageCaptured>>,
    validateFirstnameInput: () -> Unit,
    validateLastnameInput: () -> Unit,
    validateEmailInput: () -> Unit,
    validatePhoneNumberInput: () -> Unit,
    validatePasswordInput: () -> Unit,
    loadImageProfile: () -> Unit,
    navHostController: NavHostController,
    errorEvent: Flow<MyEvent>,
    onRegister: (photoFile: File, onSuccess: () -> Unit) -> Unit,
    openGallery: () -> Unit,
    isButtonEnabled: StateFlow<UiState<Boolean>>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var imageUri: Uri? by remember { mutableStateOf(null) }

    imageProfile.collectAsState().value.let { uiState ->
        when (uiState) {
            is UiState.Success -> imageUri = uiState.data.uri
            is UiState.Error -> Log.d(
                "TAG",
                "RegisterScreen image profile: ${uiState.errorMessage}"
            )
            is UiState.Loading -> loadImageProfile()
        }
    }

    LaunchedEffect(Unit) {
        errorEvent.collect { event ->
            when (event) {
                is MyEvent.MessageEvent -> Toast.makeText(
                    context,
                    event.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .padding(top = 32.dp)
            .padding(horizontal = 32.dp),
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
        Text("Registrasi")
        TextInput(
            label = "Nama Depan",
            onValueChange = { firstnameValidation.updateInputValue(it) },
            value = firstnameValidation.inputValue.collectAsState().value,
            isError = firstnameValidation.isErrorState.collectAsState().value,
            errorMessage = firstnameValidation.getErrorMessage(),
            validate = validateFirstnameInput,
            imeAction = ImeAction.Next
        )
        TextInput(
            label = "Nama Belakang",
            onValueChange = { lastnameValidation.updateInputValue(it) },
            value = lastnameValidation.inputValue.collectAsState().value,
            isError = lastnameValidation.isErrorState.collectAsState().value,
            errorMessage = lastnameValidation.getErrorMessage(),
            validate = validateLastnameInput,
            imeAction = ImeAction.Next
        )
        PickPhotoProfileImage(openGallery = openGallery, uiStateProfileImage = imageProfile)
        TextInput(
            label = "Email",
            onValueChange = { emailValidation.updateInputValue(it) },
            value = emailValidation.inputValue.collectAsState().value,
            isError = emailValidation.isErrorState.collectAsState().value,
            errorMessage = emailValidation.getErrorMessage(),
            keyboardType = KeyboardType.Email,
            validate = validateEmailInput,
            imeAction = ImeAction.Next
        )
        TextInput(
            label = "Nomor Telepon",
            onValueChange = { phoneNumberValidation.updateInputValue(it) },
            value = phoneNumberValidation.inputValue.collectAsState().value,
            isError = phoneNumberValidation.isErrorState.collectAsState().value,
            errorMessage = phoneNumberValidation.getErrorMessage(),
            keyboardType = KeyboardType.Number,
            validate = validatePhoneNumberInput,
            imeAction = ImeAction.Next
        )
        TextInput(
            label = "Password",
            onValueChange = { passwordValidation.updateInputValue(it) },
            value = passwordValidation.inputValue.collectAsState().value,
            isError = passwordValidation.isErrorState.collectAsState().value,
            errorMessage = passwordValidation.getErrorMessage(),
            isPassword = true,
            validate = validatePasswordInput,
            imeAction = ImeAction.Next
        )
        RoundedButton(text = stringResource(R.string.register),
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onRegister(imageUri!!.toFile(context)) {
                    navHostController.navigate(Screen.Auth.route)
                }
            },
            enabled =
            isButtonEnabled.collectAsState().value.let { uiState ->
                when (uiState) {
                    is UiState.Loading -> false
                    else -> true
                }
            })
        Row {
            Text("atau ")
            Text(
                "login akun",
                modifier = Modifier.clickable { navHostController.navigate(Screen.Auth.route) },
                style = MaterialTheme.typography.body1.copy(
                    color = GreenPrimary
                )
            )
        }
    }
}

@Composable
private fun PickPhotoProfileImage(
    openGallery: () -> Unit,
    uiStateProfileImage: StateFlow<UiState<ImageCaptured>>,
    modifier: Modifier = Modifier,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = modifier) {
        Text(text = "Foto profil")
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            PillWidget(
                color = GreenPrimary,
                textColor = Color.White,
                text = "pilih gambar",
                modifier = Modifier.clickable { openGallery() })
            uiStateProfileImage.collectAsState(initial = UiState.Loading).value.let { uiState ->
                when {
                    uiState is UiState.Success -> Text(
                        uiState.data.uri.toString(),
                        style = MaterialTheme.typography.caption
                    )
                    else -> Text(
                        "klik tombol disamping untuk pilih foto dari gallery",
                        style = MaterialTheme.typography.caption.copy(
                            color = DarkGrey
                        )
                    )
                }
            }
        }
    }
}

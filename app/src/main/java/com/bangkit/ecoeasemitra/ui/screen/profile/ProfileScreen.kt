package com.bangkit.ecoeasemitra.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bangkit.ecoeasemitra.R
import com.bangkit.ecoeasemitra.data.room.model.User
import com.bangkit.ecoeasemitra.ui.common.UiState
import com.bangkit.ecoeasemitra.ui.component.*
import com.bangkit.ecoeasemitra.ui.theme.BluePrimary
import com.bangkit.ecoeasemitra.utils.WindowInfo
import com.bangkit.ecoeasemitra.utils.rememberWindowInfo
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ProfileScreen(
    userStateFlow: StateFlow<UiState<User>>,
    logoutAction: () -> Unit,
    onLoadUser: () -> Unit,
    onReloadUser: () -> Unit,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
){
    var openDialog by remember{ mutableStateOf(false) }
    val windowInfo = rememberWindowInfo()
    userStateFlow.collectAsState(initial = UiState.Loading).value.let { uiState ->
        when(uiState){
            is UiState.Loading -> {
                CircularProgressIndicator()
                onLoadUser()
            }
            is UiState.Success -> {
                when(windowInfo.screenWidthInfo){
                    is WindowInfo.WindowType.Compact -> ProfileScreenPortraitContent(
                        email = uiState.data.email,
                        firstName = uiState.data.firstName,
                        lastName = uiState.data.lastName,
                        photoUrl = uiState.data.urlPhotoProfile,
                        phoneNum = uiState.data.phoneNumber,
                        openLogoutDialog = { openDialog = true },
                    )
                    else -> ProfileScreenLandscapeContent(
                        openLogoutDialog = { openDialog = true },
                    )
                }
            }
            is UiState.Error -> {
                ErrorHandler(errorText = uiState.errorMessage, onReload = { onReloadUser() })
            }
        }
    }
    DialogBox(text = stringResource(R.string.logout_confirm), onDissmiss = { openDialog = false }, isOpen = openDialog, onAccept = {
        logoutAction()
    })
}

@Composable
fun ProfileScreenPortraitContent(
    photoUrl: String,
    firstName: String,
    lastName: String,
    email: String,
    phoneNum: String,
    openLogoutDialog: () -> Unit,
    modifier: Modifier = Modifier,
){
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Avatar(
            imageUrl = photoUrl,
            size = AvatarSize.LARGE
        )
        Text(text = firstName, style = MaterialTheme.typography.h4)
        Text(text = email, style = MaterialTheme.typography.caption.copy(
            color = BluePrimary
        ))
        TextReadOnly(label = "Nama", text = "$firstName $lastName")
        TextReadOnly(label = "No Telp.", text = phoneNum)
        LogoutButton(action = {
            openLogoutDialog()
        })
    }
}


@Composable
fun ProfileScreenLandscapeContent(
    openLogoutDialog: () -> Unit,
    modifier: Modifier = Modifier,
){
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
    ) {
        Column(
            modifier = modifier
                .weight(1f)
                .padding(horizontal = 32.dp)
                .padding(top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Avatar(
                imageUrl = "https://images.unsplash.com/photo-1528190336454-13cd56b45b5a?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=870&q=80",
                size = AvatarSize.LARGE
            )
            Text(text = "Maya", style = MaterialTheme.typography.h4)
            Text(text = "maya@gmail.com", style = MaterialTheme.typography.caption.copy(
                color = BluePrimary
            ))
        }
        Column(
            modifier = modifier
                .weight(1f)
                .padding(horizontal = 32.dp)
                .padding(top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextReadOnly(label = "Nama", text = "Maya")
            TextReadOnly(label = "No Telp.", text = "082132351498")
            TextReadOnly(label = "Password", text = "082132351498")
            LogoutButton(action = {
                openLogoutDialog()
            })
        }
    }
}

@Composable
fun LogoutButton(
    action: () -> Unit,
    modifier: Modifier = Modifier
){
    Row(modifier = modifier
        .fillMaxWidth()
        .clickable { action() }
    ,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(imageVector = Icons.Default.Logout, contentDescription = "logout")
        Text(text = "Logout")
    }
}
package com.bangkit.ecoeasemitra

import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bangkit.ecoeasemitra.config.ViewModelFactory
import com.bangkit.ecoeasemitra.data.Screen
import com.bangkit.ecoeasemitra.data.model.ImageCaptured
import com.bangkit.ecoeasemitra.data.viewmodel.*
import com.bangkit.ecoeasemitra.di.Injection
import com.bangkit.ecoeasemitra.ui.component.*
import com.bangkit.ecoeasemitra.ui.screen.*
import com.bangkit.ecoeasemitra.ui.screen.auth.AuthScreen
import com.bangkit.ecoeasemitra.ui.screen.chat.ChatRoomScreen
import com.bangkit.ecoeasemitra.ui.screen.chat.UsersChatsScreen
import com.bangkit.ecoeasemitra.ui.screen.dashboard.DashboardScreen
import com.bangkit.ecoeasemitra.ui.screen.onboard.OnBoardingScreen
import com.bangkit.ecoeasemitra.ui.screen.order.DetailOrderScreen
import com.bangkit.ecoeasemitra.ui.screen.order.OrderHistoryScreen
import com.bangkit.ecoeasemitra.ui.screen.order.OrderSuccessScreen
import com.bangkit.ecoeasemitra.ui.screen.register.RegisterScreen
import com.bangkit.ecoeasemitra.ui.theme.EcoEaseTheme

val listMainRoute = listOf(
    Screen.Home,
    Screen.History,
    Screen.Map,
    Screen.UsersChats
)
val listNoTopBar = listOf(
    Screen.Onboard,
    Screen.Auth,
    Screen.Register,
    Screen.PickupOrderSuccess
)
class MainActivity : ComponentActivity() {
    private lateinit var registerViewModel: RegisterViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        val splashViewModel = ViewModelFactory(Injection.provideInjection(this)).create(SplashViewModel::class.java)
        super.onCreate(savedInstanceState)
        registerViewModel = ViewModelFactory(Injection.provideInjection(this)).create(RegisterViewModel::class.java)
        val authViewModel = ViewModelFactory(Injection.provideInjection(this)).create(AuthViewModel::class.java)
        val orderViewModel = ViewModelFactory(Injection.provideInjection(this)).create(OrderViewModel::class.java)
        val garbageViewModel = ViewModelFactory(Injection.provideInjection(this)).create(GarbageViewModel::class.java)
        val userViewModel = ViewModelFactory(Injection.provideInjection(this)).create(UserViewModel::class.java)
        val messageViewModel = ViewModelFactory(Injection.provideInjection(this)).create(MessageViewModel::class.java)

        installSplashScreen().setKeepOnScreenCondition{
            splashViewModel.isLoading.value
        }
        setContent {
            EcoEaseTheme {
                val navController: NavHostController = rememberNavController()
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                var openDialog by remember{ mutableStateOf(false) }
                val isReadOnboardNew by splashViewModel.isReadOnboard.collectAsState()
                val isLogged by splashViewModel.isLogged.collectAsState()

                fun resetOrder(){
                    orderViewModel.resetCurrentOrder()
                    navController.popBackStack()
                }

                val isTopBarShown = !listNoTopBar.map { it.route }.contains(currentRoute)
                val isMainRoute = listMainRoute.map { it.route }.contains(currentRoute)

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                    ,
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold(
                        topBar = { if(isTopBarShown){
                                TopBar(
                                    currentRoute = currentRoute,
                                    navController = navController,
                                    isUseNavButton = !isMainRoute,
                                    isUseAvatar = currentRoute == Screen.Home.route,
                                    userStateFlow = userViewModel.user,
                                    loadUser = { userViewModel.getUser() },
                                    onTapNavButton = {
                                        if(orderViewModel.orderState.value.total > 0 && currentRoute == Screen.Order.route){
                                            openDialog = true
                                        }else{
                                            navController.popBackStack()
                                        }
                                    },
                                    onTapAvatar = {
                                        navController.navigate(Screen.Profile.route)
                                    }
                                )
                            }
                        },
                        bottomBar = {
                            if(isMainRoute) BottomNavBar(navController = navController, items = listMainRoute)
                        },
                        floatingActionButtonPosition = FabPosition.Center,
                        isFloatingActionButtonDocked = true,
                    ) {paddingValues ->
                        DialogBox(text = "Apakah anda yakin ingin membatalkan order anda", onDissmiss = { openDialog = false }, onAccept = { resetOrder() }, isOpen = openDialog)
                        NavHost(
                            navController = navController,
                            startDestination =  if(isReadOnboardNew){
                                                    if(isLogged) Screen.Home.route else Screen.Auth.route
                                                } else Screen.Onboard.route, //Screen.OnBoard.route,
                            modifier = Modifier.padding(paddingValues)
                        ){
                            composable(Screen.Onboard.route){
                                OnBoardingScreen(navController = navController, onFinish = { splashViewModel.finishedOnBoard() })
                            }
                            composable(Screen.Home.route){
                                DashboardScreen(
                                    navHostController = navController,
                                    garbageStateFlow = garbageViewModel.garbageState,
                                    onLoadGarbage = { garbageViewModel.getAllGarbage() },
                                    onReloadGarbage = { garbageViewModel.reloadGarbage() },
                                )
                            }

                            composable(Screen.History.route){
                                OrderHistoryScreen(
                                    orderHistoryState = orderViewModel.orderHistoryState,
                                    loadOrderHistory = { orderViewModel.loadOrderHistory() },
                                    reloadOrderHistory = { orderViewModel.reloadOrderHistory() },
                                    navHostController = navController
                                )
                            }
                            composable(Screen.Profile.route){
                                ProfileScreen(
                                    navHostController = navController,
                                    userStateFlow = userViewModel.user,
                                    onLoadUser = { userViewModel.getUser() },
                                    onReloadUser = { userViewModel.reloadUser() },
                                    logoutAction = { authViewModel.logout(onSuccess = {
                                        navController.navigate(Screen.Auth.route){
                                            popUpTo(Screen.Home.route){
                                                inclusive = true
                                            }
                                        }
                                    }) }
                                )
                            }
                            composable(Screen.Map.route){
                                MapScreen(
                                    navHostController = navController,
                                    availableOrderStateFlow = orderViewModel.availableOrders,
                                    loadAvailableOrders = { orderViewModel.loadAvailableOrder() }
                                )
                            }
                            composable(Screen.Auth.route){
                                AuthScreen(
                                    navHostController = navController,
                                    emailValidation = authViewModel.emailValidation,
                                    passwordValidation = authViewModel.passwordValidation,
                                    validateEmail = { authViewModel.validateEmailInput() },
                                    validatePassword = { authViewModel.validatePasswordInput() },
                                    loginAction = { authViewModel.login(onSuccess = {
                                        navController.navigate(Screen.Home.route) {
                                            popUpTo(Screen.Auth.route) {
                                                inclusive = true
                                            }
                                        }
                                    }) },
                                    isLoginValid = authViewModel.isLoginValid
                                )
                            }
                            composable(Screen.Register.route){
                                RegisterScreen(
                                    navHostController = navController,
                                    firstnameValidation = registerViewModel.firstnameValidation,
                                    lastnameValidation = registerViewModel.lastnameValidation,
                                    emailValidation = registerViewModel.emailValidation,
                                    phoneNumberValidation = registerViewModel.phoneNumberValidation,
                                    passwordValidation = registerViewModel.passwordValidation,
                                    imageProfile = registerViewModel.uiStateProfileImage,
                                    loadImageProfile = { registerViewModel.getProfileImageUri() },
                                    validateFirstnameInput = { registerViewModel.validateFirstnameInput() },
                                    validateLastnameInput = { registerViewModel.validateLastnameInput() },
                                    validateEmailInput = { registerViewModel.validateEmailInput() },
                                    validatePhoneNumberInput = { registerViewModel.validatePhoneNumberInput() },
                                    validatePasswordInput = { registerViewModel.validatePasswordInput() },
                                    errorEvent = registerViewModel.eventFlow,
                                    onRegister = { photoFile, onSuccess -> registerViewModel.register(photoFile, onSuccess) },
                                    openGallery = {
                                        val intent = Intent().apply {
                                            action = ACTION_GET_CONTENT
                                            type = "image/*"
                                        }
                                        val chooser = Intent.createChooser(intent, "Choose a picture")
                                        launcherIntentGalleryRegister.launch(chooser)
                                    }
                                )
                            }
                            composable(Screen.PickupOrderSuccess.route){
                                OrderSuccessScreen(navHostController = navController)
                            }
                            composable(
                                route = Screen.DetailOrder.route,
                                arguments = listOf(navArgument("orderId"){type = NavType.StringType})
                            ){
                                val orderId = it.arguments?.getString("orderId") ?: ""
                                DetailOrderScreen(
                                    orderId = orderId,
                                    userStateFlow = orderViewModel.myUserData,
                                    orderDetailStateFlow = orderViewModel.detailOrderState,
                                    onLoadDetailOrder = { id -> orderViewModel.loadDetailOrder(id) },
                                    onReloadDetailOrder = { orderViewModel.reloadDetailOrder() },
                                    onUpdateOrderStatus = { order, status -> orderViewModel.updateOrder(order, status, onSuccess = {
                                        navController.navigate(Screen.History.route){
                                            popUpTo(Screen.DetailOrder.createRoute(orderId)){
                                                inclusive = true
                                            }
                                        }
                                    }) },
                                    eventFlow = orderViewModel.eventFlow,
                                    createChatroomEventFlow = messageViewModel.eventFlow,
                                    onCreateNewChatroom = { userId -> messageViewModel.createChatroom(userId) },
                                )
                            }
                            composable(Screen.UsersChats.route){
                                UsersChatsScreen(
                                    navHostController = navController,
                                    onLoadChatRooms = {},
                                    eventFlow = messageViewModel.eventFlow,
                                    onDeleteRoom = { roomKey, roomId -> messageViewModel.deleteChatroom(roomKey, roomId, onSuccess = {
                                    // TODO: add onsuccess
                                    }) },
                                )
                            }
                            composable(
                                route = Screen.ChatRoom.route,
                                arguments = listOf(navArgument("roomId"){type = NavType.StringType})
                            ){
                                val roomId = it.arguments?.getString("roomId") ?: "ref"
                                ChatRoomScreen(
                                    getCurrentUser = {messageViewModel.getCurrentUser()},
                                    reloadGetCurrentUser = {messageViewModel.reloadCurrentUser()},
                                    userUiState = messageViewModel.user,
                                    sendNotification = { body -> messageViewModel.sendNotification(body) },
                                    roomId = roomId,
                                )
                            }
                        }
                    }
                }

            }
        }
    }
    private val launcherIntentGalleryRegister = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if(result.resultCode == RESULT_OK){
            val selectedImage = result.data?.data as Uri
            selectedImage?.let { uri ->
                registerViewModel.setProfileImage(
                    ImageCaptured(uri = uri, isBackCam = true)
                )
            }
        }
    }
}
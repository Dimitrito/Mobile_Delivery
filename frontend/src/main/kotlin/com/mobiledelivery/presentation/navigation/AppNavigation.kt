package com.mobiledelivery.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.mobiledelivery.di.AppModule
import com.mobiledelivery.di.UseCaseModule
import com.mobiledelivery.data.shared.PreferencesManager
import com.mobiledelivery.data.shared.TokenManager
import com.mobiledelivery.presentation.screens.cart.CartScreen
import com.mobiledelivery.presentation.screens.courier.CourierOrderDetailScreen
import com.mobiledelivery.presentation.screens.courier.CourierOrdersScreen
import com.mobiledelivery.presentation.screens.dishdetail.DishDetailScreen
import com.mobiledelivery.presentation.screens.feedback.FeedbackScreen
import com.mobiledelivery.presentation.screens.home.HomeScreen
import com.mobiledelivery.presentation.screens.login.LoginScreen
import com.mobiledelivery.presentation.screens.profile.ProfileScreen
import com.mobiledelivery.presentation.screens.register.RegisterScreen
import com.mobiledelivery.presentation.states.UiState
import com.mobiledelivery.presentation.viewmodels.AuthViewModel
import com.mobiledelivery.presentation.viewmodels.CartViewModel
import com.mobiledelivery.presentation.viewmodels.CourierViewModel
import com.mobiledelivery.presentation.viewmodels.CategoriesViewModel
import com.mobiledelivery.presentation.viewmodels.FeedbackViewModel

/**
 * Маршрути навігації
 */
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Cart : Screen("cart")
    object Profile : Screen("profile")
    object CourierOrders : Screen("courier_orders")
    object Feedback : Screen("feedback")
    
    object DishDetail : Screen("dish_detail/{dishId}") {
        fun createRoute(dishId: Int) = "dish_detail/$dishId"
    }
    
    object CourierOrderDetail : Screen("courier_order_detail/{orderId}") {
        fun createRoute(orderId: Int) = "courier_order_detail/$orderId"
    }
}

/**
 * Навігація додатку
 */
@Composable
fun AppNavigation(navController: NavHostController) {
    val tokenManager = TokenManager(PreferencesManager(AppModule.applicationContext))
    
    // Створюємо ViewModels
    val authViewModel = remember {
        AuthViewModel(
            loginUseCase = UseCaseModule.createLoginUseCase(tokenManager),
            registerUseCase = UseCaseModule.createRegisterUseCase(tokenManager),
            logoutUseCase = UseCaseModule.createLogoutUseCase(tokenManager),
            getCurrentUserUseCase = UseCaseModule.createGetCurrentUserUseCase(tokenManager),
            isAuthenticatedUseCase = UseCaseModule.createIsAuthenticatedUseCase(tokenManager),
            forgotPasswordUseCase = UseCaseModule.createForgotPasswordUseCase(tokenManager),
            updateProfileUseCase = UseCaseModule.createUpdateProfileUseCase(tokenManager),
            getCustomerUseCase = UseCaseModule.createGetCustomerUseCase(tokenManager),
            getOrdersUseCase = UseCaseModule.createGetOrdersUseCase(tokenManager),
            getCourierByUserIdUseCase = UseCaseModule.createGetCourierByUserIdUseCase(tokenManager)
        )
    }
    
    val categoriesViewModel = remember {
        CategoriesViewModel(
            getCategoriesUseCase = UseCaseModule.createGetCategoriesUseCase(tokenManager),
            getDishesByCategoryUseCase = UseCaseModule.createGetDishesByCategoryUseCase(tokenManager)
        )
    }
    
    val cartViewModel = remember {
        CartViewModel(
            placeOrderUseCase = UseCaseModule.createPlaceOrderUseCase(tokenManager),
            // Используем реальный PayPal Sandbox API (тестовый режим, но реальный API)
            createPayPalPaymentUseCase = UseCaseModule.createPayPalPaymentUseCase(useTestMode = false),
            capturePayPalPaymentUseCase = UseCaseModule.createCapturePayPalPaymentUseCase(useTestMode = false)
        )
    }
    
    val courierViewModel = remember {
        CourierViewModel(
            getCourierDeliveriesUseCase = UseCaseModule.createGetCourierDeliveriesUseCase(tokenManager),
            markDeliveryAsDeliveredUseCase = UseCaseModule.createMarkDeliveryAsDeliveredUseCase(tokenManager),
            getCourierByUserIdUseCase = UseCaseModule.createGetCourierByUserIdUseCase(tokenManager)
        )
    }

    val feedbackViewModel = remember {
        FeedbackViewModel(
            getFeedbacksUseCase = UseCaseModule.createGetFeedbacksUseCase(tokenManager),
            createFeedbackUseCase = UseCaseModule.createCreateFeedbackUseCase(tokenManager)
        )
    }
    
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToHome = {
                    // Повертаємось назад або на Home
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    } else {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                }
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Home.route) {
            // Отримуємо актуальний маршрут для HomeScreen
            val currentRouteState by navController.currentBackStackEntryAsState()
            val currentRouteForHome = currentRouteState?.destination?.route ?: Screen.Home.route
            
            HomeScreen(
                authViewModel = authViewModel,
                categoriesViewModel = categoriesViewModel,
                cartViewModel = cartViewModel,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToCart = {
                    navController.navigate(Screen.Cart.route)
                },
                onNavigateToProfile = {
                    if (authViewModel.isAuthenticated) {
                        navController.navigate(Screen.Profile.route)
                    } else {
                        navController.navigate(Screen.Login.route)
                    }
                },
                onNavigateToCourierOrders = {
                    navController.navigate(Screen.CourierOrders.route)
                },
                onNavigateToDishDetail = { dishId ->
                    navController.navigate(Screen.DishDetail.createRoute(dishId))
                },
                currentRoute = currentRouteForHome
            )
        }
        
        composable(Screen.Cart.route) {
            CartScreen(
                cartViewModel = cartViewModel,
                authViewModel = authViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onOrderPlaced = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen(
                authViewModel = authViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToCourierOrders = {
                    navController.navigate(Screen.CourierOrders.route)
                },
                onNavigateToFeedback = {
                    navController.navigate(Screen.Feedback.route)
                }
            )
        }

        composable(Screen.Feedback.route) {
            val customerState by authViewModel.customerState.collectAsStateWithLifecycle()
            val customerId = (customerState as? UiState.Success)?.data?.id
            FeedbackScreen(
                viewModel = feedbackViewModel,
                customerId = customerId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.DishDetail.route,
            arguments = listOf(navArgument("dishId") { type = NavType.IntType })
        ) { backStackEntry ->
            val dishId = backStackEntry.arguments?.getInt("dishId") ?: return@composable
            
            // Знаходимо страву в поточному списку
            val dishesState by categoriesViewModel.dishesState.collectAsStateWithLifecycle()
            val dish = when (val state = dishesState) {
                is UiState.Success -> state.data.find { it.id == dishId }
                else -> null
            }
            
            if (dish != null) {
                DishDetailScreen(
                    dish = dish,
                    cartViewModel = cartViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onAddToCart = {
                        navController.popBackStack()
                    }
                )
            } else {
                // Якщо страву не знайдено, показуємо помилку або повертаємось назад
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Страву не знайдено")
                }
            }
        }
        
        composable(Screen.CourierOrders.route) {
            val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
            
            LaunchedEffect(currentUser) {
                currentUser?.let { user ->
                    courierViewModel.loadCourierId(user.id)
                }
            }
            
            CourierOrdersScreen(
                courierViewModel = courierViewModel,
                onNavigateToOrderDetail = { delivery ->
                    // Зберігаємо delivery в ViewModel перед навігацією
                    courierViewModel.setSelectedDelivery(delivery)
                    navController.navigate(Screen.CourierOrderDetail.createRoute(delivery.orderId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.CourierOrderDetail.route,
            arguments = listOf(navArgument("orderId") { type = NavType.IntType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getInt("orderId") ?: return@composable
            
            // Отримуємо вибрану доставку з ViewModel
            val selectedDelivery by courierViewModel.selectedDelivery.collectAsStateWithLifecycle()
            
            // Якщо доставка не знайдена в ViewModel, намагаємось знайти її в списку
            val delivery = selectedDelivery ?: run {
                val deliveriesState by courierViewModel.deliveriesState.collectAsStateWithLifecycle()
                when (val state = deliveriesState) {
                    is UiState.Success -> state.data.find { it.orderId == orderId }
                    else -> null
                }
            }
            
            if (delivery != null) {
                CourierOrderDetailScreen(
                    delivery = delivery,
                    courierViewModel = courierViewModel,
                    onNavigateBack = {
                        courierViewModel.clearSelectedDelivery()
                        navController.popBackStack()
                    }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Доставку не знайдено",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                courierViewModel.clearSelectedDelivery()
                                navController.popBackStack()
                            }
                        ) {
                            Text("Повернутися назад")
                        }
                    }
                }
            }
        }
    }
}


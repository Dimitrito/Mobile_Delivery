package com.mobiledelivery.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mobiledelivery.di.AppModule
import com.mobiledelivery.di.UseCaseModule
import com.mobiledelivery.data.shared.PreferencesManager
import com.mobiledelivery.data.shared.TokenManager
import com.mobiledelivery.presentation.screens.cart.CartScreen
import com.mobiledelivery.presentation.screens.dishdetail.DishDetailScreen
import com.mobiledelivery.presentation.screens.home.HomeScreen
import com.mobiledelivery.presentation.screens.login.LoginScreen
import com.mobiledelivery.presentation.screens.profile.ProfileScreen
import com.mobiledelivery.presentation.screens.register.RegisterScreen
import com.mobiledelivery.presentation.states.UiState
import com.mobiledelivery.presentation.viewmodels.AuthViewModel
import com.mobiledelivery.presentation.viewmodels.CartViewModel
import com.mobiledelivery.presentation.viewmodels.CategoriesViewModel

/**
 * Маршрути навігації
 */
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Cart : Screen("cart")
    object Profile : Screen("profile")
    
    object DishDetail : Screen("dish_detail/{dishId}") {
        fun createRoute(dishId: Int) = "dish_detail/$dishId"
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
            forgotPasswordUseCase = UseCaseModule.createForgotPasswordUseCase(tokenManager)
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
            placeOrderUseCase = UseCaseModule.createPlaceOrderUseCase(tokenManager)
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
                onNavigateToDishDetail = { dishId ->
                    navController.navigate(Screen.DishDetail.createRoute(dishId))
                }
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
                }
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
    }
}


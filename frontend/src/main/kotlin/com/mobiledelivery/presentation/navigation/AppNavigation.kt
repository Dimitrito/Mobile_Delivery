package com.mobiledelivery.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mobiledelivery.di.AppModule
import com.mobiledelivery.di.UseCaseModule
import com.mobiledelivery.data.shared.PreferencesManager
import com.mobiledelivery.data.shared.TokenManager
import com.mobiledelivery.presentation.screens.cart.CartScreen
import com.mobiledelivery.presentation.screens.home.HomeScreen
import com.mobiledelivery.presentation.screens.login.LoginScreen
import com.mobiledelivery.presentation.screens.register.RegisterScreen
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
            isAuthenticatedUseCase = UseCaseModule.createIsAuthenticatedUseCase(tokenManager)
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
        startDestination = if (authViewModel.isAuthenticated) Screen.Home.route else Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
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
    }
}


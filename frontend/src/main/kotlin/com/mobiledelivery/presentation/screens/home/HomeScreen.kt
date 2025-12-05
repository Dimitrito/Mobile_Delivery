package com.mobiledelivery.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import coil.transform.RoundedCornersTransformation
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import android.util.Log
import com.mobiledelivery.data.api.models.CategoryResponse
import com.mobiledelivery.domain.models.MenuItem
import com.mobiledelivery.presentation.states.UiState
import com.mobiledelivery.presentation.viewmodels.AuthViewModel
import com.mobiledelivery.presentation.viewmodels.CartViewModel
import com.mobiledelivery.presentation.viewmodels.CategoriesViewModel

// Кольори
private val OrangeAccent = Color(0xFFFF6B35)
private val OrangeLight = Color(0xFFFFE0B2)
private val BackgroundColor = Color(0xFFFAF8F5)
private val CardBackground = Color.White
private val DarkText = Color(0xFF1A1A1A)
private val GrayText = Color(0xFF9E9E9E)
private val LightBorder = Color(0xFFE8E6E3)

// Base URL для зображень (без /api/)
private const val IMAGE_BASE_URL = "http://10.0.2.2:8000"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    categoriesViewModel: CategoriesViewModel,
    cartViewModel: CartViewModel,
    onLogout: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCourierOrders: () -> Unit = {},
    onNavigateToDishDetail: (Int) -> Unit = {},
    currentRoute: String = "home"
) {
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val categoriesState by categoriesViewModel.categoriesState.collectAsStateWithLifecycle()
    val dishesState by categoriesViewModel.dishesState.collectAsStateWithLifecycle()
    val selectedCategoryId by categoriesViewModel.selectedCategoryId.collectAsStateWithLifecycle()
    val cartState by cartViewModel.cart.collectAsStateWithLifecycle()
    
    var searchQuery by remember { mutableStateOf("") }
    
    // Перевіряємо чи користувач є кур'єром (перевірка через таблицю Courier)
    val isCourier = remember(currentUser) {
        val result = currentUser?.isCourier == true
        // Відладочний лог
        Log.d("HomeScreen", "User: ${currentUser?.email}, isCourier: ${currentUser?.isCourier}, result: $result")
        result
    }
    
    LaunchedEffect(Unit) {
        categoriesViewModel.initialize()
    }
    
    Scaffold(
        containerColor = BackgroundColor,
        bottomBar = {
            BottomNavigationBar(
                cartItemCount = cartState.itemCount,
                isCourier = isCourier,
                currentRoute = currentRoute,
                onMenuClick = { },
                onCartClick = onNavigateToCart,
                onProfileClick = onNavigateToProfile,
                onOrdersClick = onNavigateToCourierOrders
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardBackground)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Mobile Delivery",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
            }
            
            // Показуємо меню тільки для не-кур'єрів
            if (!isCourier) {
                // Search Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardBackground)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search", color = GrayText) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = GrayText
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BackgroundColor,
                            unfocusedContainerColor = BackgroundColor,
                            focusedBorderColor = LightBorder,
                            unfocusedBorderColor = LightBorder
                        ),
                        singleLine = true
                    )
                }
                
                // Categories
                when (val state = categoriesState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = OrangeAccent)
                    }
                }
                is UiState.Success -> {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CardBackground)
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(state.data) { category ->
                            CategoryChip(
                                category = category,
                                isSelected = selectedCategoryId == category.id,
                                onClick = { categoriesViewModel.selectCategory(category.id) }
                            )
                        }
                    }
                }
                is UiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                else -> {}
            }
            
                // Popular Section Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Popular",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                }
                
                // Dishes Grid
                when (val state = dishesState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = OrangeAccent)
                    }
                }
                is UiState.Success -> {
                    // Фільтруємо страви за пошуковим запитом
                    val filteredDishes = remember(state.data, searchQuery) {
                        val query = searchQuery.trim()
                        if (query.isEmpty()) {
                            state.data
                        } else {
                            state.data.filter { dish ->
                                dish.name.contains(query, ignoreCase = true) ||
                                        (dish.description?.contains(query, ignoreCase = true) ?: false)
                            }
                        }
                    }
                    
                    if (filteredDishes.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (state.data.isEmpty())
                                    "No dishes in this category"
                                else
                                    "No dishes found for \"$searchQuery\"",
                                color = GrayText
                            )
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(filteredDishes) { dish ->
                                DishCard(
                                    dish = dish,
                                    cartViewModel = cartViewModel,
                                    onClick = { onNavigateToDishDetail(dish.id) }
                                )
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { categoriesViewModel.refreshDishes() },
                            colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent)
                        ) {
                            Text("Try again")
                        }
                    }
                }
                else -> {}
                }
            } else {
                // Для кур'єрів показуємо повідомлення
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocalShipping,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = OrangeAccent
                        )
                        Text(
                            text = "Courier Mode",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                        Text(
                            text = "Use the Orders tab to view your deliveries",
                            fontSize = 16.sp,
                            color = GrayText,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(
    category: CategoryResponse,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) OrangeAccent else CardBackground)
            .border(
                width = 1.dp,
                color = if (isSelected) OrangeAccent else LightBorder,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = category.category_name,
            color = if (isSelected) Color.White else GrayText,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun DishCard(
    dish: MenuItem,
    cartViewModel: CartViewModel,
    onClick: () -> Unit = {}
) {
    val cartState by cartViewModel.cart.collectAsStateWithLifecycle()
    val quantity = cartState.items.find { it.menuItem.id == dish.id }?.quantity ?: 0
    
    val context = LocalContext.current
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Dish Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                OrangeLight.copy(alpha = 0.3f),
                                OrangeLight.copy(alpha = 0.1f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Додаємо base URL якщо URL відносний
                val fullImageUrl = dish.imageUrl?.let { url ->
                    if (url.startsWith("http")) url else "$IMAGE_BASE_URL$url"
                }
                
                if (!fullImageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(fullImageUrl)
                            .crossfade(true)
                            .size(Size(300, 240)) // Зменшуємо розмір зображення з бази
                            .memoryCacheKey("${dish.id}_small")
                            .diskCacheKey("${dish.id}_small")
                            .build(),
                        contentDescription = dish.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Placeholder іконка коли немає зображення
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = OrangeAccent.copy(alpha = 0.5f),
                        modifier = Modifier.size(36.dp)
                    )
                }
                
                // Discount badge
                if (dish.discount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Red)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "-${dish.discount.toInt()}%",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = dish.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = "${String.format("%.0f", dish.price)} ₴",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                if (dish.available) {
                    if (quantity > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(LightBorder)
                                        .clickable { cartViewModel.updateItemQuantity(dish.id, quantity - 1) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Remove,
                                        contentDescription = "Decrease",
                                        tint = DarkText,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                
                                Text(
                                    text = "$quantity",
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText,
                                    fontSize = 13.sp
                                )
                                
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(OrangeAccent)
                                        .clickable { cartViewModel.addItem(dish) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Increase",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        Button(
                            onClick = { cartViewModel.addItem(dish) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = OrangeAccent
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            Text(
                                text = "Add",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Unavailable",
                        color = GrayText,
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomNavigationBar(
    cartItemCount: Int,
    isCourier: Boolean,
    currentRoute: String,
    onMenuClick: () -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onOrdersClick: () -> Unit
) {
    // Визначаємо яка вкладка має бути виділена
    val isMenuSelected = !isCourier && (currentRoute == "home" || currentRoute == "cart" || currentRoute.startsWith("dish_detail"))
    val isCartSelected = !isCourier && currentRoute == "cart"
    val isOrdersSelected = isCourier && (currentRoute == "home" || currentRoute == "courier_orders" || currentRoute.startsWith("courier_order_detail"))
    val isProfileSelected = currentRoute == "profile"
    
    NavigationBar(
        containerColor = CardBackground,
        tonalElevation = 8.dp
    ) {
        // Показуємо Menu тільки для не-кур'єрів
        if (!isCourier) {
            NavigationBarItem(
                selected = isMenuSelected,
                onClick = onMenuClick,
                icon = {
                    Icon(
                        imageVector = Icons.Default.RestaurantMenu,
                        contentDescription = "Menu"
                    )
                },
                label = { Text("Menu") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = OrangeAccent,
                    selectedTextColor = OrangeAccent,
                    indicatorColor = Color.Transparent
                )
            )
            
            // Показуємо Cart тільки для не-кур'єрів
            NavigationBarItem(
                selected = isCartSelected,
                onClick = onCartClick,
            icon = {
                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge(containerColor = OrangeAccent) {
                                Text("$cartItemCount")
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = "Cart"
                    )
                }
            },
            label = { Text("Cart") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = OrangeAccent,
                selectedTextColor = OrangeAccent
            )
        )
        
        }
        
        // Для кур'єрів показуємо вкладку Orders
        if (isCourier) {
            NavigationBarItem(
                selected = isOrdersSelected,
                onClick = onOrdersClick,
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.ListAlt,
                        contentDescription = "Orders"
                    )
                },
                label = { Text("Orders") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = OrangeAccent,
                    selectedTextColor = OrangeAccent
                )
            )
        }
        
        NavigationBarItem(
            selected = isProfileSelected,
            onClick = onProfileClick,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Profile"
                )
            },
            label = { Text("Profile") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = OrangeAccent,
                selectedTextColor = OrangeAccent
            )
        )
    }
}

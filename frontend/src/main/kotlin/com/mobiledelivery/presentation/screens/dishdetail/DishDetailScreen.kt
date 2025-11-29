package com.mobiledelivery.presentation.screens.dishdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.mobiledelivery.domain.models.MenuItem
import com.mobiledelivery.presentation.viewmodels.CartViewModel

// Кольори
private val OrangeAccent = Color(0xFFFF6B35)
private val OrangeLight = Color(0xFFFFE0B2)
private val BackgroundColor = Color(0xFFFAF8F5)
private val CardBackground = Color.White
private val DarkText = Color(0xFF1A1A1A)
private val GrayText = Color(0xFF9E9E9E)
private val LightBorder = Color(0xFFE8E6E3)

// Base URL для зображень
private const val IMAGE_BASE_URL = "http://10.0.2.2:8000"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DishDetailScreen(
    dish: MenuItem,
    cartViewModel: CartViewModel,
    onNavigateBack: () -> Unit,
    onAddToCart: () -> Unit = {}
) {
    val context = LocalContext.current
    val cartState by cartViewModel.cart.collectAsStateWithLifecycle()
    val quantity = cartState.items.find { it.menuItem.id == dish.id }?.quantity ?: 0
    
    var selectedQuantity by remember { mutableStateOf(if (quantity > 0) quantity else 1) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = DarkText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Зображення страви
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    OrangeLight.copy(alpha = 0.3f),
                                    OrangeLight.copy(alpha = 0.1f)
                                )
                            )
                        )
                ) {
                    val fullImageUrl = dish.imageUrl?.let { url ->
                        if (url.startsWith("http")) url else "$IMAGE_BASE_URL$url"
                    }
                    
                    if (!fullImageUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(fullImageUrl)
                                .crossfade(true)
                                .size(Size(800, 600))
                                .build(),
                            contentDescription = dish.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = null,
                            tint = OrangeAccent.copy(alpha = 0.5f),
                            modifier = Modifier
                                .size(80.dp)
                                .align(Alignment.Center)
                        )
                    }
                    
                    // Discount badge
                    if (dish.discount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Red)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "-${dish.discount.toInt()}%",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                // Картка з деталями
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Назва страви
                        Text(
                            text = dish.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                        
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        // Опис
                        if (!dish.description.isNullOrEmpty()) {
                            Text(
                                text = dish.description,
                                fontSize = 15.sp,
                                color = GrayText,
                                lineHeight = 22.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        
                        // Харчова цінність (якщо є)
                        if (dish.calories != null || dish.mass != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                dish.mass?.let { mass ->
                                    InfoChip(
                                        icon = Icons.Default.Scale,
                                        label = "Маса",
                                        value = "${mass.toInt()}г",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                dish.calories?.let { calories ->
                                    InfoChip(
                                        icon = Icons.Default.LocalFireDepartment,
                                        label = "Калорії",
                                        value = "${calories.toInt()}",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        
                        // БЖУ (якщо є)
                        if (dish.protein != null || dish.fat != null || dish.carbohydrate != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                dish.protein?.let { protein ->
                                    InfoChip(
                                        icon = Icons.Default.FitnessCenter,
                                        label = "Білки",
                                        value = "${protein.toInt()}г",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                dish.fat?.let { fat ->
                                    InfoChip(
                                        icon = Icons.Default.OilBarrel,
                                        label = "Жири",
                                        value = "${fat.toInt()}г",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                dish.carbohydrate?.let { carb ->
                                    InfoChip(
                                        icon = Icons.Default.Grain,
                                        label = "Вуглеводи",
                                        value = "${carb.toInt()}г",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        
                        Divider(color = LightBorder)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Кількість та ціна
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Кількість",
                                    fontSize = 14.sp,
                                    color = GrayText
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Кнопки кількості
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(LightBorder)
                                            .clickable { 
                                                if (selectedQuantity > 1) {
                                                    selectedQuantity--
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Remove,
                                            contentDescription = "Decrease",
                                            tint = DarkText,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    
                                    Text(
                                        text = "$selectedQuantity",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkText,
                                        modifier = Modifier.width(40.dp),
                                        textAlign = TextAlign.Center
                                    )
                                    
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(OrangeAccent)
                                            .clickable { 
                                                selectedQuantity++
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Increase",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                            
                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "Ціна",
                                    fontSize = 14.sp,
                                    color = GrayText
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${String.format("%.0f", dish.price * selectedQuantity)} ₴",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OrangeAccent
                                )
                                if (selectedQuantity > 1) {
                                    Text(
                                        text = "${String.format("%.0f", dish.price)} ₴ за шт.",
                                        fontSize = 12.sp,
                                        color = GrayText
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        // Кнопка додавання до кошика
                        Button(
                            onClick = {
                                // Додаємо вибрану кількість до кошика
                                repeat(selectedQuantity) {
                                    cartViewModel.addItem(dish)
                                }
                                onAddToCart()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = OrangeAccent,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(28.dp),
                            enabled = dish.available
                        ) {
                            if (quantity > 0) {
                                Text(
                                    text = "Оновити кошик ($quantity)",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            } else {
                                Text(
                                    text = "Додати до кошика",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        
                        if (!dish.available) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Страва недоступна",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = BackgroundColor),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = OrangeAccent,
                modifier = Modifier.size(16.dp)
            )
            Column {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = GrayText
                )
                Text(
                    text = value,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
            }
        }
    }
}


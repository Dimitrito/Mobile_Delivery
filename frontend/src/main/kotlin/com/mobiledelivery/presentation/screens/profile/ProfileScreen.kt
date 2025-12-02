package com.mobiledelivery.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mobiledelivery.domain.models.Order
import com.mobiledelivery.domain.models.User
import com.mobiledelivery.presentation.states.UiState
import com.mobiledelivery.presentation.viewmodels.AuthViewModel

// Кольори
private val OrangeAccent = Color(0xFFFF6B35)
private val BackgroundColor = Color(0xFFF8F8F8)
private val CardBackground = Color.White
private val DarkText = Color(0xFF1A1A1A)
private val GrayText = Color(0xFF9E9E9E)
private val IconOrange = Color(0xFFFF6B35)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val customerState by authViewModel.customerState.collectAsStateWithLifecycle()
    val orderHistoryState by authViewModel.orderHistoryState.collectAsStateWithLifecycle()
    val updateProfileState by authViewModel.updateProfileState.collectAsStateWithLifecycle()
    var orderHistoryExpanded by remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }
    
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    
    val isSaving = updateProfileState is UiState.Loading
    
    LaunchedEffect(currentUser?.id) {
        currentUser?.let { user ->
            firstName = user.firstName.orEmpty()
            lastName = user.lastName.orEmpty()
            phoneNumber = user.phoneNumber.orEmpty()
            email = user.email
            authViewModel.refreshProfileData()
        }
    }
    
    LaunchedEffect(customerState) {
        val state = customerState
        if (state is UiState.Success) {
            address = state.data.delivery_address
        }
    }
    
    LaunchedEffect(updateProfileState) {
        when (val state = updateProfileState) {
            is UiState.Success -> {
                snackbarHostState.showSnackbar("Профіль успішно оновлено")
                password = ""
                authViewModel.resetUpdateProfileState()
            }
            is UiState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                authViewModel.resetUpdateProfileState()
            }
            else -> Unit
        }
    }
    
    Scaffold(
        containerColor = BackgroundColor,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            ProfileHeader(onNavigateBack = onNavigateBack)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            ProfileSummaryCard(currentUser = currentUser)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            PersonalInfoCard(
                firstName = firstName,
                lastName = lastName,
                phoneNumber = phoneNumber,
                email = email,
                address = address,
                password = password,
                customerState = customerState,
                isSaving = isSaving,
                onFirstNameChange = { firstName = it },
                onLastNameChange = { lastName = it },
                onPhoneChange = { phoneNumber = it },
                onAddressChange = { address = it },
                onPasswordChange = { password = it },
                onSaveClick = {
                    authViewModel.updateProfile(
                        firstName = firstName,
                        lastName = lastName,
                        phoneNumber = phoneNumber,
                        deliveryAddress = address,
                        password = password
                    )
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OrderHistoryCard(
                orderHistoryExpanded = orderHistoryExpanded,
                orderHistoryState = orderHistoryState,
                onToggle = { orderHistoryExpanded = !orderHistoryExpanded },
                onRetry = { authViewModel.refreshOrderHistory() }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            LogoutButton(
                onLogoutClick = {
                    authViewModel.logout()
                    onLogout()
                }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ProfileHeader(onNavigateBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Назад",
                    tint = DarkText
                )
            }

            Text(
                text = "Profile",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )

            IconButton(onClick = { /* Notifications */ }) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Сповіщення",
                    tint = OrangeAccent
                )
            }
        }
    }
}

@Composable
private fun ProfileSummaryCard(currentUser: User?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                color = OrangeAccent.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = currentUser?.firstName?.firstOrNull()?.uppercase()
                            ?: currentUser?.email?.firstOrNull()?.uppercase()
                            ?: "?",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangeAccent
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                val fullName = currentUser?.let { user ->
                    listOfNotNull(user.firstName, user.lastName)
                        .filter { it.isNotBlank() && it != "Default first name" && it != "Default last name" }
                        .joinToString(" ")
                        .ifBlank { user.email.substringBefore("@") }
                } ?: "User"

                Text(
                    text = fullName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Phone,
                        contentDescription = null,
                        tint = GrayText,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = currentUser?.phoneNumber?.ifBlank { "Phone" } ?: "Phone",
                        fontSize = 12.sp,
                        color = GrayText
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = null,
                        tint = GrayText,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = currentUser?.email ?: "Email",
                        fontSize = 12.sp,
                        color = GrayText
                    )
                }
            }
        }
    }
}

@Composable
private fun PersonalInfoCard(
    firstName: String,
    lastName: String,
    phoneNumber: String,
    email: String,
    address: String,
    password: String,
    customerState: UiState<*>,
    isSaving: Boolean,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Personal information",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkText
            )

            OutlinedTextField(
                value = firstName,
                onValueChange = onFirstNameChange,
                label = { Text("First name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = onLastNameChange,
                label = { Text("Last name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = onPhoneChange,
                label = { Text("Phone number") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = {},
                label = { Text("Email") },
                singleLine = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = address,
                onValueChange = onAddressChange,
                label = { Text("Delivery address") },
                singleLine = false,
                modifier = Modifier.fillMaxWidth()
            )

            if (customerState is UiState.Error) {
                Text(
                    text = customerState.message,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }

            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("New password (optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = onSaveClick,
                enabled = !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangeAccent,
                    contentColor = Color.White,
                    disabledContainerColor = OrangeAccent.copy(alpha = 0.5f)
                )
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Save changes")
                }
            }
        }
    }
}

@Composable
private fun OrderHistoryCard(
    orderHistoryExpanded: Boolean,
    orderHistoryState: UiState<List<Order>>,
    onToggle: () -> Unit,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Receipt,
                        contentDescription = null,
                        tint = IconOrange,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Order history",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkText
                    )
                }
                Icon(
                    imageVector = if (orderHistoryExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = GrayText
                )
            }

            if (orderHistoryExpanded) {
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                when (val state = orderHistoryState) {
                    is UiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = OrangeAccent)
                        }
                    }
                    is UiState.Success -> {
                        if (state.data.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Історія замовлень порожня",
                                    color = GrayText
                                )
                            }
                        } else {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                state.data.take(10).forEach { order ->
                                    OrderHistoryItem(order = order)
                                }
                            }
                        }
                    }
                    is UiState.Error -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            TextButton(onClick = onRetry) {
                                Text("Спробувати знову")
                            }
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}

@Composable
private fun OrderHistoryItem(order: Order) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundColor, shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Order #${order.id}",
            fontWeight = FontWeight.Bold,
            color = DarkText
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Status: ${order.status.name.lowercase().replaceFirstChar { it.uppercase() }}",
            color = GrayText,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Total: ${String.format("%.2f ₴", order.totalPrice)}",
            color = DarkText,
            fontWeight = FontWeight.SemiBold
        )
        order.createdAt?.let {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Created: ${it.replace('T', ' ')}",
                color = GrayText,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun LogoutButton(onLogoutClick: () -> Unit) {
    Button(
        onClick = onLogoutClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = OrangeAccent,
            contentColor = Color.White
        )
    ) {
        Icon(
            imageVector = Icons.Default.ExitToApp,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Log out",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}



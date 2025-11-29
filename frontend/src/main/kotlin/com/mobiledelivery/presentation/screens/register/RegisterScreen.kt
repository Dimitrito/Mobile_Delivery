package com.mobiledelivery.presentation.screens.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mobiledelivery.presentation.states.UiEvent
import com.mobiledelivery.presentation.states.UiState
import com.mobiledelivery.presentation.viewmodels.AuthViewModel

// Кольори для дизайну
private val OrangeAccent = Color(0xFFFF6B35)
private val InputBackground = Color(0xFFFAF8F5)
private val InputBorderColor = Color(0xFFE8E6E3)
private val DarkText = Color(0xFF1A1A1A)
private val GrayText = Color(0xFF9E9E9E)

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit = {}
) {
    val registerState by viewModel.registerState.collectAsStateWithLifecycle()
    val uiEvent by viewModel.uiEvent.collectAsStateWithLifecycle()
    
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var password2 by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var password2Visible by remember { mutableStateOf(false) }
    
    // Обробка подій UI
    LaunchedEffect(uiEvent) {
        uiEvent?.let { event ->
            when (event) {
                is UiEvent.Navigate -> {
                    if (event.route == "login") {
                        onNavigateToHome() // Після реєстрації переходимо на Home
                    }
                }
                is UiEvent.ShowMessage -> {
                    // Можна показати Snackbar
                }
                else -> {}
            }
            viewModel.clearEvent()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            
            // Логотип / Назва
            Text(
                text = "Mobile",
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = DarkText,
                lineHeight = 38.sp
            )
            Text(
                text = "Delivery",
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = DarkText,
                lineHeight = 38.sp
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Email
            Text(
                text = "Email",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = DarkText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("your@email.com", color = GrayText) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = InputBackground,
                    unfocusedContainerColor = InputBackground,
                    focusedBorderColor = OrangeAccent,
                    unfocusedBorderColor = InputBorderColor,
                    cursorColor = OrangeAccent
                ),
                singleLine = true,
                enabled = registerState !is UiState.Loading
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Name (First + Last in row)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Ім'я",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkText,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        placeholder = { Text("Ім'я", color = GrayText) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = InputBackground,
                            unfocusedContainerColor = InputBackground,
                            focusedBorderColor = OrangeAccent,
                            unfocusedBorderColor = InputBorderColor,
                            cursorColor = OrangeAccent
                        ),
                        singleLine = true,
                        enabled = registerState !is UiState.Loading
                    )
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Прізвище",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkText,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        placeholder = { Text("Прізвище", color = GrayText) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = InputBackground,
                            unfocusedContainerColor = InputBackground,
                            focusedBorderColor = OrangeAccent,
                            unfocusedBorderColor = InputBorderColor,
                            cursorColor = OrangeAccent
                        ),
                        singleLine = true,
                        enabled = registerState !is UiState.Loading
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Phone
            Text(
                text = "Телефон",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = DarkText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                placeholder = { Text("+380XXXXXXXXX", color = GrayText) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = InputBackground,
                    unfocusedContainerColor = InputBackground,
                    focusedBorderColor = OrangeAccent,
                    unfocusedBorderColor = InputBorderColor,
                    cursorColor = OrangeAccent
                ),
                singleLine = true,
                enabled = registerState !is UiState.Loading
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Password
            Text(
                text = "Пароль",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = DarkText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Введіть пароль", color = GrayText) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = InputBackground,
                    unfocusedContainerColor = InputBackground,
                    focusedBorderColor = OrangeAccent,
                    unfocusedBorderColor = InputBorderColor,
                    cursorColor = OrangeAccent
                ),
                singleLine = true,
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = GrayText
                        )
                    }
                },
                enabled = registerState !is UiState.Loading
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Confirm Password
            Text(
                text = "Підтвердження паролю",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = DarkText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = password2,
                onValueChange = { password2 = it },
                placeholder = { Text("Повторіть пароль", color = GrayText) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = InputBackground,
                    unfocusedContainerColor = InputBackground,
                    focusedBorderColor = OrangeAccent,
                    unfocusedBorderColor = InputBorderColor,
                    cursorColor = OrangeAccent
                ),
                singleLine = true,
                visualTransformation = if (password2Visible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    IconButton(onClick = { password2Visible = !password2Visible }) {
                        Icon(
                            imageVector = if (password2Visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = GrayText
                        )
                    }
                },
                enabled = registerState !is UiState.Loading
            )
            
            // Error message
            when (val state = registerState) {
                is UiState.Error -> {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                else -> {}
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Create account button
            Button(
                onClick = {
                    viewModel.register(
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        phoneNumber = phoneNumber,
                        password = password,
                        password2 = password2
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangeAccent,
                    contentColor = Color.White,
                    disabledContainerColor = OrangeAccent.copy(alpha = 0.5f)
                ),
                enabled = registerState !is UiState.Loading
            ) {
                if (registerState is UiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Create account",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Already have account
            TextButton(onClick = onNavigateToLogin) {
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(color = DarkText)) {
                            append("I already have an ")
                        }
                        withStyle(style = SpanStyle(color = OrangeAccent, fontWeight = FontWeight.SemiBold)) {
                            append("account")
                        }
                    },
                    fontSize = 16.sp
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

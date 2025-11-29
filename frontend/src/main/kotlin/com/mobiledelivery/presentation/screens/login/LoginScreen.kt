package com.mobiledelivery.presentation.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mobiledelivery.presentation.states.UiEvent
import com.mobiledelivery.presentation.states.UiState
import com.mobiledelivery.presentation.viewmodels.AuthViewModel

// Кольори для дизайну
private val OrangeAccent = Color(0xFFFF6B35)
private val InputBackground = Color(0xFFEEEEEE)
private val InputBorderColor = Color(0xFFBDBDBD)
private val DarkText = Color(0xFF1A1A1A)

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()
    val forgotPasswordState by viewModel.forgotPasswordState.collectAsStateWithLifecycle()
    val uiEvent by viewModel.uiEvent.collectAsStateWithLifecycle()
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    
    // Обробка подій UI
    LaunchedEffect(uiEvent) {
        uiEvent?.let { event ->
            when (event) {
                is UiEvent.Navigate -> {
                    if (event.route == "home") {
                        onNavigateToHome()
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
    
    // Діалог відновлення паролю
    if (showForgotPasswordDialog) {
        ForgotPasswordDialog(
            viewModel = viewModel,
            forgotPasswordState = forgotPasswordState,
            onDismiss = {
                showForgotPasswordDialog = false
                viewModel.resetForgotPasswordState()
            }
        )
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(120.dp))
            
            // Логотип / Назва
            Text(
                text = "Mobile",
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                color = DarkText,
                lineHeight = 44.sp
            )
            Text(
                text = "Delivery",
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                color = DarkText,
                lineHeight = 44.sp
            )
            
            Spacer(modifier = Modifier.height(60.dp))
            
            // Email поле
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { 
                    Text(
                        "Email",
                        color = Color.Gray
                    ) 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = InputBackground,
                    unfocusedContainerColor = InputBackground,
                    disabledContainerColor = InputBackground,
                    focusedBorderColor = OrangeAccent,
                    unfocusedBorderColor = InputBorderColor,
                    cursorColor = OrangeAccent
                ),
                singleLine = true,
                enabled = loginState !is UiState.Loading
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Password поле
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { 
                    Text(
                        "Password",
                        color = Color.Gray
                    ) 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = InputBackground,
                    unfocusedContainerColor = InputBackground,
                    disabledContainerColor = InputBackground,
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
                            imageVector = if (passwordVisible) {
                                Icons.Default.Visibility
                            } else {
                                Icons.Default.VisibilityOff
                            },
                            contentDescription = if (passwordVisible) "Приховати пароль" else "Показати пароль",
                            tint = Color.Gray
                        )
                    }
                },
                enabled = loginState !is UiState.Loading
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Forgot password
            TextButton(
                onClick = { showForgotPasswordDialog = true },
                modifier = Modifier.align(Alignment.Start),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Forgot password?",
                    color = DarkText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // Помилка
            when (val state = loginState) {
                is UiState.Error -> {
                    Spacer(modifier = Modifier.height(8.dp))
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
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Sign in button
            Button(
                onClick = { viewModel.login(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangeAccent,
                    contentColor = Color.White,
                    disabledContainerColor = OrangeAccent.copy(alpha = 0.5f)
                ),
                enabled = loginState !is UiState.Loading
            ) {
                if (loginState is UiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Sign in",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Create account
            TextButton(
                onClick = onNavigateToRegister,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Create account",
                    color = DarkText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ForgotPasswordDialog(
    viewModel: AuthViewModel,
    forgotPasswordState: UiState<String>,
    onDismiss: () -> Unit
) {
    var forgotEmail by remember { mutableStateOf("") }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = OrangeAccent,
                    modifier = Modifier.size(48.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Відновлення паролю",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Введіть email, на який буде надіслано новий пароль",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Success message
                if (forgotPasswordState is UiState.Success) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE8F5E9)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = forgotPasswordState.data,
                            color = Color(0xFF2E7D32),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent)
                    ) {
                        Text("OK")
                    }
                } else {
                    // Email input
                    OutlinedTextField(
                        value = forgotEmail,
                        onValueChange = { forgotEmail = it },
                        placeholder = { Text("Email", color = Color.Gray) },
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
                        enabled = forgotPasswordState !is UiState.Loading
                    )
                    
                    // Error message
                    if (forgotPasswordState is UiState.Error) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = forgotPasswordState.message,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Скасувати", color = DarkText)
                        }
                        
                        Button(
                            onClick = { viewModel.forgotPassword(forgotEmail) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                            enabled = forgotPasswordState !is UiState.Loading && forgotEmail.isNotBlank()
                        ) {
                            if (forgotPasswordState is UiState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Надіслати")
                            }
                        }
                    }
                }
            }
        }
    }
}

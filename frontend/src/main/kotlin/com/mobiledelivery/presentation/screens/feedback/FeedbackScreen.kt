package com.mobiledelivery.presentation.screens.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mobiledelivery.presentation.states.UiState
import com.mobiledelivery.presentation.viewmodels.FeedbackViewModel

private val OrangeAccent = androidx.compose.ui.graphics.Color(0xFFFF6B35)

@Composable
fun FeedbackScreen(
    viewModel: FeedbackViewModel,
    customerId: Int?,
    onNavigateBack: () -> Unit
) {
    val feedbacksState by viewModel.feedbacksState.collectAsStateWithLifecycle()
    val createState by viewModel.createState.collectAsStateWithLifecycle()
    var reviewText by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(5f) }

    LaunchedEffect(Unit) {
        viewModel.loadFeedbacks()
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = OrangeAccent
                    )
                }
                Text(
                    text = "Відгуки",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = OrangeAccent
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Останні відгуки",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = OrangeAccent
            )

            when (val state = feedbacksState) {
                is UiState.Loading -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = OrangeAccent)
                    }
                }
                is UiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is UiState.Success -> {
                    state.data.forEach { feedback ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = feedback.user_first_name ?: "User ${feedback.user}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = OrangeAccent
                                )
                                Text(
                                    text = "Оцінка: ${"%.1f".format(feedback.rating)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OrangeAccent
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = feedback.review_text,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
                else -> {}
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Залишити відгук",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = OrangeAccent
            )

            OutlinedTextField(
                value = reviewText,
                onValueChange = { reviewText = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Ваш відгук") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OrangeAccent,
                    unfocusedBorderColor = OrangeAccent.copy(alpha = 0.4f),
                    cursorColor = OrangeAccent,
                    focusedLabelColor = OrangeAccent
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Оцінка: ${rating.toInt()}", color = OrangeAccent)
                Spacer(modifier = Modifier.width(12.dp))
                Slider(
                    value = rating,
                    onValueChange = { rating = it },
                    valueRange = 1f..5f,
                    steps = 3,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        activeTrackColor = OrangeAccent,
                        inactiveTrackColor = OrangeAccent.copy(alpha = 0.3f),
                        thumbColor = OrangeAccent
                    )
                )
            }

            Button(
                onClick = {
                    customerId?.let {
                        viewModel.submitFeedback(it, reviewText, rating.toDouble())
                    }
                },
                enabled = customerId != null && reviewText.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangeAccent,
                    contentColor = androidx.compose.ui.graphics.Color.White
                )
            ) {
                if (createState is UiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(16.dp),
                        strokeWidth = 2.dp,
                        color = OrangeAccent
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Надіслати")
                }
            }

            if (createState is UiState.Error) {
                val state = createState as UiState.Error
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error
                )
            }
            if (createState is UiState.Success) {
                Text(
                    text = "Відгук надіслано",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}


package com.koloos.app.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AuthScreen(
    state: AuthUiState,
    onRequestOtp: (String) -> Unit,
    onVerifyOtp: (String) -> Unit
) {
    val phone = remember { mutableStateOf("08012345678") }
    val name = remember { mutableStateOf("Amina Store") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(PaddingValues(24.dp)),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Text(text = "KoloOS")
        OutlinedTextField(
            value = phone.value,
            onValueChange = { phone.value = it },
            label = { Text("Phone") }
        )
        OutlinedTextField(
            value = name.value,
            onValueChange = { name.value = it },
            label = { Text("Merchant name") }
        )
        Button(onClick = { onRequestOtp(phone.value) }, enabled = !state.isLoading) {
            Text("Request OTP")
        }
        TextButton(onClick = { onVerifyOtp(name.value) }, enabled = state.otp != null && !state.isLoading) {
            Text("Verify OTP")
        }
        state.error?.let { Text(text = it) }
        state.merchantName?.let { Text(text = "Welcome $it") }
    }
}

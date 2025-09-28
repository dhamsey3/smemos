package com.koloos.app.presentation.sales

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.koloos.app.domain.models.Product

@Composable
fun SaleScreen(
    state: SaleUiState,
    onBack: () -> Unit,
    onRecordSale: (Product, Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Record Sale")
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(state.products) { product ->
                ProductSaleRow(product = product, onRecordSale = onRecordSale)
            }
        }
        state.successMessage?.let { Text(text = it) }
        state.error?.let { Text(text = it) }
        TextButton(onClick = onBack) { Text("Back") }
    }
}

@Composable
private fun ProductSaleRow(product: Product, onRecordSale: (Product, Int) -> Unit) {
    val quantity = remember { mutableStateOf("1") }
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = product.name)
        Text(text = product.priceDisplay)
        OutlinedTextField(value = quantity.value, onValueChange = { quantity.value = it }, label = { Text("Qty") })
        Spacer(Modifier.height(8.dp))
        Button(onClick = { onRecordSale(product, quantity.value.toIntOrNull() ?: 1) }) { Text("Record Sale") }
    }
}

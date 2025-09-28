package com.koloos.app.presentation.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.koloos.app.domain.models.Product

@Composable
fun ProductListScreen(
    state: ProductUiState,
    onBack: () -> Unit,
    onCreate: (String, Double, Int) -> Unit,
    onUpdate: (String, String?, Double?, Int?) -> Unit,
    onDelete: (String) -> Unit
) {
    val name = remember { mutableStateOf("") }
    val price = remember { mutableStateOf("0.0") }
    val quantity = remember { mutableStateOf("0") }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Products") })
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = name.value, onValueChange = { name.value = it }, label = { Text("Name") })
            OutlinedTextField(value = price.value, onValueChange = { price.value = it }, label = { Text("Price") })
            OutlinedTextField(value = quantity.value, onValueChange = { quantity.value = it }, label = { Text("Qty") })
            Button(onClick = {
                onCreate(name.value, price.value.toDoubleOrNull() ?: 0.0, quantity.value.toIntOrNull() ?: 0)
                name.value = ""
                price.value = "0.0"
                quantity.value = "0"
            }) { Text("Add Product") }
            state.error?.let { Text(text = it) }
        }
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(state.products) { product ->
                ProductRow(product = product, onDelete = onDelete, onUpdate = onUpdate)
            }
        }
        TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
    }
}

@Composable
private fun ProductRow(product: Product, onDelete: (String) -> Unit, onUpdate: (String, String?, Double?, Int?) -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = product.name)
            Text(text = product.priceDisplay)
            Text(text = "Stock: ${product.quantity}")
            TextButton(onClick = { onUpdate(product.id, null, null, product.quantity + 1) }) {
                Text("Restock")
            }
        }
        IconButton(onClick = { onDelete(product.id) }) {
            androidx.compose.material3.Icon(imageVector = Icons.Default.Delete, contentDescription = null)
        }
    }
}

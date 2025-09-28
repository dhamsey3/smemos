package com.koloos.app.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koloos.app.data.product.ProductRepository
import com.koloos.app.domain.models.Product
import com.koloos.app.domain.models.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState

    init {
        viewModelScope.launch {
            repository.refresh()
            repository.observeProducts().collectLatest { entities ->
                _uiState.value = _uiState.value.copy(products = entities.map { it.toDomain() })
            }
        }
    }

    fun createProduct(name: String, price: Double, quantity: Int) {
        viewModelScope.launch {
            runCatching { repository.createProduct(name, price, quantity) }
                .onFailure { _uiState.value = _uiState.value.copy(error = it.message) }
        }
    }

    fun updateProduct(id: String, name: String?, price: Double?, quantity: Int?) {
        viewModelScope.launch {
            runCatching { repository.updateProduct(id, name, price, quantity) }
                .onFailure { _uiState.value = _uiState.value.copy(error = it.message) }
        }
    }

    fun deleteProduct(id: String) {
        viewModelScope.launch {
            runCatching { repository.deleteProduct(id) }
                .onFailure { _uiState.value = _uiState.value.copy(error = it.message) }
        }
    }
}

data class ProductUiState(
    val products: List<Product> = emptyList(),
    val error: String? = null
)

package com.koloos.app.presentation.sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koloos.app.data.product.ProductRepository
import com.koloos.app.data.sales.SaleItemRequest
import com.koloos.app.data.sales.SalesRepository
import com.koloos.app.domain.models.Product
import com.koloos.app.domain.models.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaleViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val salesRepository: SalesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SaleUiState())
    val uiState: StateFlow<SaleUiState> = _uiState

    init {
        viewModelScope.launch {
            productRepository.observeProducts().collectLatest { products ->
                _uiState.value = _uiState.value.copy(products = products.map { it.toDomain() })
            }
        }
    }

    fun recordSale(product: Product, quantity: Int) {
        viewModelScope.launch {
            runCatching { salesRepository.recordSaleOffline(listOf(SaleItemRequest(product.id, quantity))) }
                .onSuccess {
                    _uiState.value = _uiState.value.copy(successMessage = "Sale queued")
                }
                .onFailure { _uiState.value = _uiState.value.copy(error = it.message) }
        }
    }
}

data class SaleUiState(
    val products: List<Product> = emptyList(),
    val successMessage: String? = null,
    val error: String? = null
)

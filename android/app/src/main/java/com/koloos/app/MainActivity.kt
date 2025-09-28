package com.koloos.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.koloos.app.presentation.auth.AuthScreen
import com.koloos.app.presentation.dashboard.DashboardScreen
import com.koloos.app.presentation.products.ProductListScreen
import com.koloos.app.presentation.sales.SaleScreen
import com.koloos.app.presentation.theme.KoloOSTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KoloOSTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    KoloNavHost()
                }
            }
        }
    }
}

@Composable
private fun KoloNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "auth") {
        composable("auth") {
            val viewModel = hiltViewModel<com.koloos.app.presentation.auth.AuthViewModel>()
            val state by viewModel.uiState.collectAsState()
            AuthScreen(
                state = state,
                onRequestOtp = viewModel::requestOtp,
                onVerifyOtp = {
                    viewModel.verifyOtp(it)
                    navController.navigate("dashboard") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }
        composable("dashboard") {
            DashboardScreen(
                onAddProduct = { navController.navigate("products") },
                onRecordSale = { navController.navigate("sale") }
            )
        }
        composable("products") {
            val viewModel = hiltViewModel<com.koloos.app.presentation.products.ProductViewModel>()
            val state by viewModel.uiState.collectAsState()
            ProductListScreen(
                state = state,
                onBack = { navController.popBackStack() },
                onCreate = viewModel::createProduct,
                onUpdate = viewModel::updateProduct,
                onDelete = viewModel::deleteProduct
            )
        }
        composable("sale") {
            val viewModel = hiltViewModel<com.koloos.app.presentation.sales.SaleViewModel>()
            val state by viewModel.uiState.collectAsState()
            SaleScreen(
                state = state,
                onBack = { navController.popBackStack() },
                onRecordSale = viewModel::recordSale
            )
        }
    }
}

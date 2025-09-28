package com.koloos.app.domain.models

data class Product(
    val id: String,
    val name: String,
    val priceCents: Int,
    val quantity: Int
) {
    val priceDisplay: String get() = "₦${priceCents / 100.0}".replace(".0", "")
}

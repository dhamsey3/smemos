package com.koloos.app.domain.models

data class Sale(
    val id: String,
    val totalAmountCents: Int,
    val status: String,
    val createdAt: Long
) {
    val totalDisplay: String get() = "₦${totalAmountCents / 100.0}".replace(".0", "")
}

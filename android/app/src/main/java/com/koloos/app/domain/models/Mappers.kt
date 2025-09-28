package com.koloos.app.domain.models

import com.koloos.app.core.database.entity.ProductEntity
import com.koloos.app.core.database.entity.SaleEntity

fun ProductEntity.toDomain() = Product(
    id = id,
    name = name,
    priceCents = priceCents,
    quantity = quantity
)

fun SaleEntity.toDomain() = Sale(
    id = id,
    totalAmountCents = totalAmountCents,
    status = status,
    createdAt = createdAt
)

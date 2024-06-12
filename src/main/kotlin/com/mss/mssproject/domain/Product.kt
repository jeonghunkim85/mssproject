package com.mss.mssproject.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "products")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    val category: Category,
    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    val brand: Brand,
    val price: BigDecimal,
    @CreatedDate
    val createdAt: LocalDateTime? = LocalDateTime.now(),
    @LastModifiedDate
    val updatedAt: LocalDateTime? = LocalDateTime.now(),
)

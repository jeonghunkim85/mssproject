package com.mss.mssproject.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity
@Table(name = "categories")
data class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    // unique
    val name: String,
    @CreatedDate
    val createdAt: LocalDateTime? = LocalDateTime.now(),
    @LastModifiedDate
    val updatedAt: LocalDateTime? = LocalDateTime.now(),
)

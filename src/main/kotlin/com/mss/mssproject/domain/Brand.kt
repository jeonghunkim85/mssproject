package com.mss.mssproject.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity
@Table(name = "brands")
data class Brand(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val name: String,

    @CreatedDate
    val createdAt: LocalDateTime?,

    @LastModifiedDate
    val updatedAt: LocalDateTime?,
)
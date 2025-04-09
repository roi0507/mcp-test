package com.example.api.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class ItemDetail(
    val id: Int = 0,
    val name: String,
    val warehouse: String,
    val amount: Int,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) 
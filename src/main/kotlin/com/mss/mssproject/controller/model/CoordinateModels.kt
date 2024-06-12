package com.mss.mssproject.controller.model

import com.fasterxml.jackson.annotation.JsonProperty

object CoordinateModels {
    data class CoordinateView(
        @JsonProperty("카테고리")
        val products: List<ProductWithBrandView>,
        @JsonProperty("총액")
        val totalPrice: String,
    )

    data class CoordinateWithBrandView(
        @JsonProperty("브랜드")
        val brandName: String,
        @JsonProperty("카테고리")
        val products: List<ProductView>,
        @JsonProperty("총액")
        val totalPrice: String,
    )

    data class ProductView(
        @JsonProperty("카테고리")
        val categoryName: String,
        @JsonProperty("가격")
        val price: String,
    )

    data class ProductWithBrandView(
        @JsonProperty("카테고리")
        val categoryName: String,
        @JsonProperty("브랜드")
        val brandName: String,
        @JsonProperty("가격")
        val price: String,
    )
}

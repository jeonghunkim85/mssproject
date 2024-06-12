package com.mss.mssproject.controller

import com.fasterxml.jackson.annotation.JsonProperty
import com.mss.mssproject.application.ReadProductUseCase
import com.mss.mssproject.application.WriteProductUseCases
import com.mss.mssproject.controller.mapper.toDtoWithBrand
import com.mss.mssproject.controller.model.CoordinateModels.ProductWithBrandView
import com.mss.mssproject.controller.model.ProductModels.ProductView
import com.mss.mssproject.controller.model.ProductModels.WriteProductDto
import com.mss.mssproject.domain.ProductsByCategory
import com.mss.mssproject.exception.BadRequestException
import com.mss.mssproject.exception.NotFoundException
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RequestMapping("/products")
@RestController
class ProductController(
    private val readProductUseCase: ReadProductUseCase,
    private val writeProductUseCase: WriteProductUseCases,
) {
    @Operation(summary = "상품 조회")
    @GetMapping("/{id}")
    fun getProduct(@PathVariable id: Long): ProductView {
        val product = try {
            readProductUseCase.readProduct(id)
        } catch (nse: NoSuchElementException) {
            throw NotFoundException(nse.message)
        }
        return ProductView(product)
    }

    @Operation(summary = "상품 등록")
    @PostMapping
    fun postProduct(
        @RequestBody
        @Valid
        writeProductDto: WriteProductDto,
    ): ProductView {
        val product = try {
            writeProductUseCase.postProduct(writeProductDto.toWriteModel())
        } catch (ise: IllegalStateException) {
            throw BadRequestException(ise.message)
        }
        return ProductView(product)
    }

    @Operation(summary = "상품 수정")
    @PutMapping("/{id}")
    fun putProduct(
        @PathVariable
        id: Long,
        @RequestBody
        @Valid
        writeProductDto: WriteProductDto,
    ): ProductView {
        val product = try {
            writeProductUseCase.putProduct(id, writeProductDto.toWriteModel())
        } catch (ise: IllegalStateException) {
            throw BadRequestException(ise.message)
        }
        return ProductView(product)
    }

    @Operation(summary = "상품 삭제")
    @DeleteMapping("/{id}")
    fun deleteProduct(@PathVariable id: Long) {
        try {
            writeProductUseCase.deleteProduct(id)
        }catch (nse: NoSuchElementException) {
            throw NotFoundException(nse.message)
        }
    }
}
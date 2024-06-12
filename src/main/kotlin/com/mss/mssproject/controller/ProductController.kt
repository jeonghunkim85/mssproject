package com.mss.mssproject.controller

import com.mss.mssproject.application.ReadProductUseCase
import com.mss.mssproject.application.WriteProductUseCases
import com.mss.mssproject.controller.model.ProductModels.ProductView
import com.mss.mssproject.controller.model.ProductModels.WriteProductDto
import com.mss.mssproject.exception.BadRequestException
import com.mss.mssproject.exception.NotFoundException
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/products")
@RestController
class ProductController(
    private val readProductUseCase: ReadProductUseCase,
    private val writeProductUseCase: WriteProductUseCases,
) {
    @Operation(summary = "상품 조회")
    @GetMapping("/{id}")
    fun getProduct(
        @PathVariable id: Long,
    ): ProductView {
        val product =
            try {
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
        val product =
            try {
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
        val product =
            try {
                writeProductUseCase.putProduct(id, writeProductDto.toWriteModel())
            } catch (ise: IllegalStateException) {
                throw BadRequestException(ise.message)
            }
        return ProductView(product)
    }

    @Operation(summary = "상품 삭제")
    @DeleteMapping("/{id}")
    fun deleteProduct(
        @PathVariable id: Long,
    ) {
        try {
            writeProductUseCase.deleteProduct(id)
        } catch (nse: NoSuchElementException) {
            throw NotFoundException(nse.message)
        }
    }
}

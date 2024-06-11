package com.mss.mssproject.controller

import com.mss.mssproject.application.ReadBrandUseCases
import com.mss.mssproject.application.WriteBrandUseCases
import com.mss.mssproject.controller.model.BrandModels.BrandView
import com.mss.mssproject.controller.model.BrandModels.WriteBrandDto
import com.mss.mssproject.exception.BadRequestException
import com.mss.mssproject.exception.ConflictException
import com.mss.mssproject.exception.NotFoundException
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/brands")
class BrandController(
    private val readBrandUseCases: ReadBrandUseCases,
    private val writeBrandUseCases: WriteBrandUseCases,
) {
    @Operation(summary = "브랜드 조회")
    @GetMapping("/{id}")
    fun getBrand(@PathVariable id: Long): BrandView {
        val brand = try {
            readBrandUseCases.getBrand(id)
        } catch (nse: NoSuchElementException) {
            throw NotFoundException(nse.message)
        }
        return BrandView(brand)
    }

    @Operation(summary = "브랜드명으로 브랜드 조회")
    @GetMapping
    fun findBrands(
        @RequestParam(name = "name", required = false)
        name: String?
    ): List<BrandView> {
        if(name.isNullOrBlank()) {
            throw BadRequestException("requestParam 'name' should not be null or blank")
        }
        val brand = try {
            readBrandUseCases.getBrandByName(name.trim())
        } catch (nse: NoSuchElementException) {
            throw NotFoundException(nse)
        }
        return listOf(BrandView(brand))
    }

    @Operation(summary = "브랜드 등록")
    @PostMapping
    fun postBrand(
        @Valid
        @RequestBody
        writeBrandDto: WriteBrandDto,
    ): BrandView {
        val registeredBrand = writeBrandUseCases.postBrand(writeBrandDto.toWriteModel())
        return BrandView(registeredBrand)
    }

    /**
     * 등록이나 수정에 필요한 필드가 하나만 존재하므로 patch 도 put 과 동일하게 작동합니다
     */
    @Operation(summary = "브랜드 수정")
    @PutMapping("/{id}")
    fun putBrand(
        @PathVariable
        id: Long,
        @Valid
        @RequestBody
        writeBrandDto: WriteBrandDto,
    ): BrandView {
        val modifiedBrand = writeBrandUseCases.putBrand(id, writeBrandDto.toWriteModel())
        return BrandView(modifiedBrand)
    }

    @Operation(summary = "브랜드 삭제")
    @DeleteMapping("/{id}")
    fun deleteBrand(@PathVariable id: Long) {
        try {
            writeBrandUseCases.deleteBrand(id)
        } catch (ise: IllegalStateException) {
            throw ConflictException(ise.message)
        } catch (nse: NoSuchElementException) {
            throw NotFoundException(nse.message)
        }
    }
}
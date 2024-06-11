package com.mss.mssproject.application

import com.mss.mssproject.application.WriteProductUseCases.WriteProductModel
import com.mss.mssproject.domain.Brand
import com.mss.mssproject.domain.Category
import com.mss.mssproject.domain.Product
import com.mss.mssproject.repository.*
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.springframework.context.annotation.Import
import org.springframework.data.repository.findByIdOrNull
import java.math.BigDecimal

@Import(
    CategoryRepositoryFakeImpl::class,
    ProductRepositoryFakeImpl::class,
    BrandRepositoryFakeImpl::class,
    ProductService::class,
)
@DisplayName("ProductService Test")
class ProductServiceTest(
    private val categoryRepository: CategoryRepository,
    private val brandRepository: BrandRepository,
    private val productRepository: ProductRepository,
    private val productService: ProductService,
) : BehaviorSpec({

    afterContainer {
        categoryRepository.deleteAll()
        brandRepository.deleteAll()
        productRepository.deleteAll()
    }

    Given("productRepository 에 id 1 인 상품이 저장돼있다") {

        beforeContainer {
            val category = categoryRepository.save(
                Category(id = 1, name = "상의")
            )
            val brand = brandRepository.save(
                Brand(id = 1, name = "A")
            )
            productRepository.save(
                Product(id = 1, category = category, brand = brand, price = BigDecimal.valueOf(1000))
            )
        }

        When("id 1 로 productService.readProduct 가 실행되면") {
            val result = productService.readProduct(1L)
            Then("product 가 조회된다") {
                result shouldNotBe null
                result.id shouldBe 1L
                result.price shouldBe BigDecimal.valueOf(1000)
                result.brand.id shouldBe 1L
                result.category.id shouldBe 1L
            }
        }

        When("id 1 로 productService.putProduct 가 실행되면") {
            val writeModel = WriteProductModel(
                categoryId = 1,
                brandId = 1,
                price = BigDecimal.valueOf(2000)
            )
            val result = productService.putProduct(2L, writeModel)
            Then("product 가 저장되고, 저장된 product 를 return 한다") {
                result.id shouldBe 2L
                with(productRepository.findByIdOrNull(2)) {
                    this shouldNotBe null
                    this?.category?.id shouldBe 1L
                    this?.brand?.id shouldBe 1L
                    this?.price shouldBe BigDecimal.valueOf(2000)
                }
            }
        }

        When("id 1 로 productService.deleteProduct 가 실행되면") {
            productService.deleteProduct(1)
            Then("product 가 repository 에서 삭제된다") {
                productRepository.findByIdOrNull(1) shouldBe null
            }
        }
    }

    Given("productRepository 에 아무것도 저장돼있지 않다") {
        beforeContainer {
            productRepository.findAll() shouldHaveSize 0
        }

        And("brand 와 category 는 저장돼있다") {

            beforeContainer {
                categoryRepository.save(
                    Category(id = 1, name = "상의")
                )
                brandRepository.save(
                    Brand(id = 1, name = "A")
                )
            }

            When("postProduct 가 실행되면") {
                val writeModel = WriteProductModel(
                    categoryId = 1,
                    brandId = 1,
                    price = BigDecimal.valueOf(1000)
                )
                val result = productService.postProduct(writeModel)
                Then("product 가 저장되고, 저장된 product 를 return 한다") {
                    result.id shouldNotBe 0L
                    val savedId = result.id

                    with(productRepository.findByIdOrNull(savedId)) {
                        this shouldNotBe null
                        this?.category?.id shouldBe 1L
                        this?.brand?.id shouldBe 1L
                    }
                }
            }

            When("id 2 로 putProduct 가 실행되면") {
                val writeModel = WriteProductModel(
                    categoryId = 1,
                    brandId = 1,
                    price = BigDecimal.valueOf(1000)
                )
                val result = productService.putProduct(2L, writeModel)
                Then("product 가 저장되고, 저장된 product 를 return 한다") {
                    result.id shouldBe 2L
                    with(productRepository.findByIdOrNull(2)) {
                        this shouldNotBe null
                        this?.category?.id shouldBe 1L
                        this?.brand?.id shouldBe 1L
                    }
                }
            }
        }

        And("brand 가 저장돼 있고 category 는 저장돼있지 않다") {
            beforeContainer {
                brandRepository.save(
                    Brand(id = 1, name = "A")
                )
                categoryRepository.findAll() shouldHaveSize 0
            }

            When("postProduct 가 실행되면") {
                val writeProductModel = WriteProductModel(
                    brandId = 1L,
                    categoryId = 1L,
                    price = BigDecimal.valueOf(100)
                )
                val result = kotlin.runCatching {
                    productService.postProduct(writeProductModel)
                }
                Then("IllegalArgumentException 가 난다") {
                    val exception = result.exceptionOrNull()
                    exception shouldNotBe null
                    exception.shouldBeInstanceOf<IllegalArgumentException>()
                }
            }
            When("putProduct 가 실행되면") {
                val writeProductModel = WriteProductModel(
                    brandId = 1L,
                    categoryId = 1L,
                    price = BigDecimal.valueOf(100)
                )
                val result = kotlin.runCatching {
                    productService.putProduct(1L, writeProductModel)
                }
                Then("IllegalArgumentException 가 난다") {
                    val exception = result.exceptionOrNull()
                    exception shouldNotBe null
                    exception.shouldBeInstanceOf<IllegalArgumentException>()
                }
            }
        }
        And("brand 는 저장돼있지 않고 category 는 저장돼있다") {
            beforeContainer {
                brandRepository.findAll() shouldHaveSize 0
                categoryRepository.save(
                    Category(id = 1, name = "상의")
                )
            }
            When("postProduct 가 실행되면") {
                val writeProductModel = WriteProductModel(
                    brandId = 1L,
                    categoryId = 1L,
                    price = BigDecimal.valueOf(100)
                )
                val result = kotlin.runCatching {
                    productService.postProduct(writeProductModel)
                }
                Then("IllegalArgumentException 가 난다") {
                    val exception = result.exceptionOrNull()
                    exception shouldNotBe null
                    exception.shouldBeInstanceOf<IllegalArgumentException>()
                }
            }
            When("putProduct 가 실행되면") {
                val writeProductModel = WriteProductModel(
                    brandId = 1L,
                    categoryId = 1L,
                    price = BigDecimal.valueOf(100)
                )
                val result = kotlin.runCatching {
                    productService.putProduct(1L, writeProductModel)
                }
                Then("IllegalArgumentException 가 난다") {
                    val exception = result.exceptionOrNull()
                    exception shouldNotBe null
                    exception.shouldBeInstanceOf<IllegalArgumentException>()
                }
            }
        }

        When("id=3 으로 deleteProduct 가 실행되면") {
            val result = kotlin.runCatching {
                productService.deleteProduct(3)
            }
            Then("NoSuchElementException 에러가 난다") {
                val exception = result.exceptionOrNull()
                exception shouldNotBe null
                exception.shouldBeInstanceOf<NoSuchElementException>()
            }
        }
    }
})

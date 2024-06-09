package com.mss.mssproject.application

import com.mss.mssproject.application.WriteBrandUseCases.WriteBrandModel
import com.mss.mssproject.domain.Brand
import com.mss.mssproject.domain.Category
import com.mss.mssproject.domain.Product
import com.mss.mssproject.repository.BrandRepository
import com.mss.mssproject.repository.BrandRepositoryFakeImpl
import com.mss.mssproject.repository.ProductRepository
import com.mss.mssproject.repository.ProductRepositoryFakeImpl
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.springframework.context.annotation.Import
import org.springframework.data.repository.findByIdOrNull
import java.math.BigDecimal

@DisplayName("BrandService Test")
@Import(
    BrandRepositoryFakeImpl::class,
    ProductRepositoryFakeImpl::class,
    BrandService::class,
)
class BrandServiceTest(
    private val brandService: BrandService,
    private val brandRepository: BrandRepository,
    private val productRepository: ProductRepository,
) : BehaviorSpec({

    afterContainer {
        brandRepository.deleteAll()
        productRepository.deleteAll()
    }

    Given("BrandRepository 에 name 이 'C' 인 brand 가 저장돼 있다") {
        brandRepository.save(Brand(name = "C"))
        When("brandService.getBrandByName('C') 가 호출되면") {
            val result = brandService.getBrandByName("C")
            Then("name이 C 인 Brand 가 return 된다") {
                result.name shouldBe "C"
            }
        }
    }

    Given("BrandRepository 에 name 이 'C' 인 brand 가 저장돼 있지 않다") {
        brandRepository.findByName("C") shouldBe null
        When("brandService.getBrandByName('C') 가 호출되면") {
            val result = kotlin.runCatching {
                brandService.getBrandByName("C")
            }
            Then("NoSuchElementException 이 발생한다") {
                val exception = result.exceptionOrNull() shouldNotBe null
                exception.shouldBeInstanceOf<NoSuchElementException>()
            }
        }
    }

    Given("BrandRepository 에 id 가 1 인 brand 가 저장돼있다") {
        brandRepository.save(Brand(id=1, name="A"))
        When("brandService.getBrand(1) 가 호출되면") {
            val result = brandService.getBrand(1)
            Then("id가 1인 Brand 가 return 된다") {
                result shouldNotBe null
                result.id shouldBe 1L
                result.name shouldBe "A"
            }
        }
    }

    Given("BrandRepository 에 id 가 1인 brand 가 저장돼있지 않다") {
        brandRepository.findByIdOrNull(1L) shouldBe null
        When("brandService.getBrand(1) 가 호출되면") {
            val result = kotlin.runCatching {
                brandService.getBrand(1)
            }
            Then("NoSuchElementException 이 발생한다") {
                val exception = result.exceptionOrNull() shouldNotBe null
                exception.shouldBeInstanceOf<NoSuchElementException>()
            }
        }
    }

    Given("BrandRepository 에 아무것도 저장돼있지 않다") {
        brandRepository.findAll() shouldHaveSize 0
        When("name 이 A 인 writeBrandModel 로 postBrand 가 호출되면") {
            val writeBrandModel = WriteBrandModel(name = "A")
            val postResult = brandService.postBrand(writeBrandModel)
            Then("name 이 'A' 인 brand 를 return 한다") {
                postResult.name shouldBe "A"
            }
            Then("brandRepository 에는 name 이 A 인 브랜드가 저장돼 있다") {
                brandRepository.findByName("A") shouldNotBe null
            }
        }

        When("name 이 A 인 writeBrandModel 과 id 2 로 putBrand 가 호출되면") {
            val writeBrandModel = WriteBrandModel(name = "A")
            val putResult = brandService.putBrand(2L, writeBrandModel)
            Then("name 이 'A', id가 2인 인 brand 를 return 한다.") {
                putResult.name shouldBe "A"
                putResult.id shouldBe 2L
            }
            Then("repository 에는 id=2 인 brand 의 이름은 'A' 가 저장돼있다") {
                val brand = brandRepository.findByIdOrNull(2)
                brand shouldNotBe null
                brand?.id shouldBe 2L
                brand?.name shouldBe "A"
            }
        }

        When("deleteBrand(1) 가 호출되면") {
            val result = kotlin.runCatching {
                brandService.deleteBrand(1)
            }
            Then("NoSuchElementException 이 발생한다") {
                val exception = result.exceptionOrNull()
                exception shouldNotBe null
                exception.shouldBeInstanceOf<NoSuchElementException>()
            }
        }
    }

    Given("BrandRepository 에 name=A, id=1 인 brand 가 저장돼있다") {
        val brand = Brand(id=1, name="A")
        brandRepository.save(brand)
        When("name 이 B 인 writeBrandModel 과 id 1 로 putBrand 가 호출되면") {
            val writeBrandModel = WriteBrandModel("B")
            val putResult = brandService.putBrand(1, writeBrandModel)
            Then("저장결과의 name 은 B 이다") {
                putResult.name shouldBe "B"
                putResult.id shouldBe 1L
            }
            Then("저장된 brand 의 name 이 B 로 업데이트 된다") {
                val savedBrand = brandRepository.findByIdOrNull(1L)
                checkNotNull(savedBrand)
                savedBrand.name shouldBe "B"
            }
        }

        And("productRepository 에 brand id 가 1인 상품이 1개 저장돼있다") {
            brandRepository.save(brand)
            productRepository.save(Product(
                brand = brand,
                category = Category(id=1, name = "A"),
                price = BigDecimal.valueOf(100)
            ))
            When("deleteBrand(1) 가 호출되면") {
                val result = kotlin.runCatching {
                    brandService.deleteBrand(1)
                }
                Then("IllegalStateException 이 발생한다") {
                    val exception = result.exceptionOrNull()
                    exception shouldNotBe null
                    exception.shouldBeInstanceOf<IllegalStateException>()
                }
            }
        }

        And("productRepository 에 brand id 가 1 인 상품이 저장돼 있지 않다") {
            brandRepository.save(brand)
            productRepository.findAll().all { it.brand.id != 1L } shouldBe true
            When("deleteBrand(1)가 호출되면") {
                brandService.deleteBrand(1L)
                Then("brandRepository 에는 brandId=1 인 brand가 삭제된다") {
                    brandRepository.findByIdOrNull(1L) shouldBe null
                }
            }
        }
    }
})

package com.mss.mssproject.repository

import com.mss.mssproject.domain.Product
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.repository.findByIdOrNull
import java.math.BigDecimal

@DisplayName("ProductRepositoryTest Query Test")
@DataJpaTest
class ProductRepositoryTest(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val brandRepository: BrandRepository,
) : DescribeSpec({

        describe("findCheapestProductsByCategory 가 올바르게 쿼리 하는지 확인합니다") {
            context("함수가 실행되면") {
                val result = productRepository.findCheapestProductsByAllCategory()
                print(result)

                it("결과물은 8개의 값을 가지고 있어야 한다") {
                    result shouldHaveSize 8
                }
                it("상의의 브랜드는 C 이고, 가격은 10000 이어야 한다") {
                    val categoryName = "상의"
                    result.count { it.category.name == categoryName } shouldBe 1
                    val product = result.find { it.category.name == categoryName }
                    checkNotNull(product)
                    with(product) {
                        brand.name shouldBe "C"
                        price shouldBe BigDecimal.valueOf(10000)
                    }
                }
                it("아우터의 브랜드는 E 이고, 가격은 5000 이어야 한다") {
                    val categoryName = "아우터"
                    result.count { it.category.name == categoryName } shouldBe 1
                    val product = result.find { it.category.name == categoryName }
                    checkNotNull(product)
                    with(product) {
                        brand.name shouldBe "E"
                        price shouldBe BigDecimal.valueOf(5000)
                    }
                }
                it("바지의 브랜드는 D 이고, 가격은 3000 이어야 한다") {
                    val categoryName = "바지"
                    result.count { it.category.name == categoryName } shouldBe 1
                    val product = result.find { it.category.name == categoryName }
                    checkNotNull(product)
                    with(product) {
                        brand.name shouldBe "D"
                        price shouldBe BigDecimal.valueOf(3000)
                    }
                }
                it("스니커즈의 브랜드는 G 이고, 가격은 9000 이어야 한다") {
                    val categoryName = "스니커즈"
                    result.count { it.category.name == categoryName } shouldBe 1
                    val product = result.find { it.category.name == categoryName }
                    checkNotNull(product)
                    with(product) {
                        brand.name shouldBe "G"
                        price shouldBe BigDecimal.valueOf(9000)
                    }
                }
                it("가방의 브랜드는 A 이고, 가격은 2000 이어야 한다") {
                    val categoryName = "가방"
                    result.count { it.category.name == categoryName } shouldBe 1
                    val product = result.find { it.category.name == categoryName }
                    checkNotNull(product)
                    with(product) {
                        brand.name shouldBe "A"
                        price shouldBe BigDecimal.valueOf(2000)
                    }
                }
                it("모자의 브랜드는 D 이고, 가격은 1500 이어야 한다") {
                    val categoryName = "모자"
                    result.count { it.category.name == categoryName } shouldBe 1
                    val product = result.find { it.category.name == categoryName }
                    checkNotNull(product)
                    with(product) {
                        brand.name shouldBe "D"
                        price shouldBe BigDecimal.valueOf(1500)
                    }
                }
                it("양말의 브랜드는 I 이고, 가격은 1700 이어야 한다") {
                    val categoryName = "양말"
                    result.count { it.category.name == categoryName } shouldBe 1
                    val product = result.find { it.category.name == categoryName }
                    checkNotNull(product)
                    with(product) {
                        brand.name shouldBe "I"
                        price shouldBe BigDecimal.valueOf(1700)
                    }
                }
                it("액세서리의 브랜드는 F 이고, 가격은 1900 이어야 한다") {
                    val categoryName = "액세서리"
                    result.count { it.category.name == categoryName } shouldBe 1
                    val product = result.find { it.category.name == categoryName }
                    checkNotNull(product)
                    with(product) {
                        brand.name shouldBe "F"
                        price shouldBe BigDecimal.valueOf(1900)
                    }
                }
            }
        }

        describe("단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격 쿼리 테스트") {
            val brandDId = 4L
            val brand =
                brandRepository.findByIdOrNull(brandDId)
                    ?: error("cannot find brand(id=4) from db")

            context("findCheapestProductsByBrand(brand(id=4)) 함수가 실행되면") {

                val result = productRepository.findCheapestProductsByBrand(brand)

                it("결과는 8개의 사이즈를 갖고 있어야 한다") {
                    result shouldHaveSize 8
                }
                it("결과 상품 모두의 브랜드는 D 여야 한다") {
                    result.all { it.brand.name == "D" } shouldBe true
                }
                it("결과 상품의 모든 가격을 합하면 36,100 이어야 한다 ") {
                    result.sumOf { it.price } shouldBe BigDecimal.valueOf(36_100)
                }
                it("결과 목록의 카테고리별 상품 count 는 1이다") {
                    result.groupBy { it.category.id }.all { it.value.size == 1 } shouldBe true
                }
            }
            context("상의 카테고리의 브랜드 'D' 에 가격이 10100보다 큰 상품이 추가되더라도") {
                val category = categoryRepository.findByIdOrNull(1) ?: error("cannot find category 1")
                productRepository.save(
                    Product(
                        category = category,
                        brand = brand,
                        price = BigDecimal.valueOf(10200),
                    ),
                )
                context("함수가 실행되면") {
                    val result = productRepository.findCheapestProductsByBrand(brand)
                    it("결과는 8개의 사이즈를 갖고 있어야 한다") {
                        result shouldHaveSize 8
                    }
                    it("결과 상품의 모든 가격을 합하면 36,100 이어야 한다 ") {
                        result.sumOf { it.price } shouldBe BigDecimal.valueOf(36_100)
                    }
                    it("결과 목록의 카테고리별 상품 count 는 1이다") {
                        result.groupBy { it.category.id }.all { it.value.size == 1 } shouldBe true
                    }
                }
            }
        }

        describe("카테고리명으로 최저가, 최고가 상품 쿼리 테스트") {
            val category = categoryRepository.findByName("상의") ?: error("cannot find category(name=상의)")
            context("상의로 검색시") {

                val result = productRepository.findCheapestAndMostExpensiveProductsByCategory(category)

                it("result 는 2개의 item 을 갖고있다") {
                    result shouldHaveSize 2
                }

                it("result 중 MIN 인 Product 결과는 C 브랜드의 가격은 10000 이다") {
                    val cheapestProduct = result.find { it.first == "MIN" }
                    cheapestProduct shouldNotBe null
                    checkNotNull(cheapestProduct)
                    cheapestProduct.second.brand.name shouldBe "C"
                    cheapestProduct.second.price shouldBe BigDecimal.valueOf(10_000)
                }
                it("result 중 MAX 인 Product 결과는 I 브랜드의 가격은 11400 이다") {
                    val mostExpensiveProduct = result.find { it.first == "MAX" }
                    mostExpensiveProduct shouldNotBe null
                    checkNotNull(mostExpensiveProduct)
                    mostExpensiveProduct.second.brand.name shouldBe "I"
                    mostExpensiveProduct.second.price shouldBe BigDecimal.valueOf(11_400)
                }
            }

            context("상의에 최저가 상품 (brand=D, 가격 10_000)이 하나 더 추가된다") {
                val brand = brandRepository.findByIdOrNull(4) ?: error("cannot find brand(id=4)")

                Product(
                    brand = brand,
                    category = category,
                    price = BigDecimal.valueOf(10_000),
                ).also(productRepository::save)

                context("상의로 검색시") {
                    val result = productRepository.findCheapestAndMostExpensiveProductsByCategory(category)

                    it("result 는 3개의 아이템을 갖고 있다") {
                        result shouldHaveSize 3
                    }

                    it("result 중 MIN 인 Product 는 2개이고 브랜드는 C,D 가격은 10000 이다") {
                        val cheapestProducts = result.filter { it.first == "MIN" }
                        cheapestProducts shouldHaveSize 2
                        val products = cheapestProducts.map { it.second }
                        products.map { it.brand.name } shouldBe listOf("C", "D")
                        products.all { it.price == BigDecimal.valueOf(10_000) } shouldBe true
                    }

                    it("result 중 MAX 인 Product 결과는 I 브랜드의 가격은 11400 이다") {
                        val mostExpensiveProduct = result.find { it.first == "MAX" }
                        mostExpensiveProduct shouldNotBe null
                        checkNotNull(mostExpensiveProduct)
                        mostExpensiveProduct.second.brand.name shouldBe "I"
                        mostExpensiveProduct.second.price shouldBe BigDecimal.valueOf(11_400)
                    }
                }
            }
        }
    })

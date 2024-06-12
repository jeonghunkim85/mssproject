package com.mss.mssproject.application

import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import java.math.BigDecimal

/*
 * 본래 service 의 경우 fakeImpl 을 통한 unit test 를 주로 작성하나,
 * 적절한 쿼리 결과를 위한 데이터 셋팅의 효율성을 위해 JpaTest 를 활용합니다.
 * repository 의 각 함수가 적절한 결과를 내놓는지에 대해서는 repositoryTest 에서 별도로 검증합니다.
 */
@Import(CoordinateService::class)
@DisplayName("CoordinateService Test")
@DataJpaTest
class CoordinateServiceTest(
    private val coordinateService: CoordinateService,
) : DescribeSpec({
        describe("findCheapestCoordinate 테스트") {
            context("DB 에 값이 셋팅 돼있고, 쿼리 시 적절한 결과를 return 해줍니다.") {
                context("coordinateService.findCheapestCoordinate 함수가 실행되면") {
                    val result = coordinateService.findCheapestCoordinate()
                    it("결과물은 8 개의 아이템을 가지고 있습니다") {
                        result.products shouldHaveSize 8
                    }
                    it("결과물 가격의 총합은 34100 입니다.") {
                        result.totalPrice shouldBe BigDecimal.valueOf(34100)
                    }
                    it("카테고리는 상의,아우터,바지,스니커즈,가방,모자,양말,액세서리 순으로 반환됩니다") {
                        result.products.map { it.category.name }.joinToString(",") shouldBe
                            "상의,아우터,바지,스니커즈,가방,모자,양말,액세서리"
                    }
                    it("브랜드는 C,E,D,G,A,D,I,F 순으로 반환됩니다") {
                        result.products.map { it.brand.name }.joinToString(",") shouldBe
                            "C,E,D,G,A,D,I,F"
                    }
                    it("가격은 10000,5000,3000,9000,2000,1500,1700,1900 순으로 반환됩니다") {
                        result.products.map { it.price.toString() }.joinToString(",") shouldBe
                            "10000,5000,3000,9000,2000,1500,1700,1900"
                    }
                }
            }
        }

        describe("findCoordinateByCheapestBrand 테스트") {
            context("DB 에 값이 셋팅 돼있고, 쿼리 시 적절한 결과를 return 해줍니다.") {
                context("findCoordinateByCheapestBrand 함수가 실행되면") {
                    val result = coordinateService.findCoordinateByCheapestBrand()
                    it("가장 저렴한 브랜드는 D 입니다.") {
                        result.brand.name shouldBe "D"
                    }
                    it("products 의 size 는 8 입니다.") {
                        result.products shouldHaveSize 8
                    }
                    it("가겨 총합은 36100 입니다") {
                        result.totalPrice shouldBe BigDecimal.valueOf(36100)
                    }
                    it("카테고리는 상의,아우터,바지,스니커즈,가방,모자,양말,액세서리 순으로 반환됩니다") {
                        result.products.map { it.category.name }.joinToString(",") shouldBe
                            "상의,아우터,바지,스니커즈,가방,모자,양말,액세서리"
                    }
                    it("가격은 10100,5100,3000,9500,2500,1500,2400,2000 순으로 반환됩니다") {
                        result.products.map { it.price.toString() }.joinToString(",") shouldBe
                            "10100,5100,3000,9500,2500,1500,2400,2000"
                    }
                }
            }
        }

        describe("findCheapestAndMostExpensiveProductByCategoryName 테스트") {
            /**
             * 쿼리 결과의 적절성 여부는 ProductRepositoryTest.kt 를 통해 확인
             * "상의" 를 조회하는 경우
             *      최저가는 C 브랜드의 10000 원짜리 상품이
             *      최고가는 I 브랜드의 11400 원짜리 상품이 return 됩니다.
             */
            context("DB 에 값이 셋팅 돼있고, 쿼리 시 적절한 결과를 return 해 줍니다") {
                context("coordinateService.findCheapestAndMostExpensiveProductByCategoryName('상의') 가 실행되면") {
                    val result = coordinateService.findCheapestAndMostExpensiveProductByCategoryName("상의")
                    it("result 의 category 는 id=1, name='상의' 입니다") {
                        result.category.id shouldBe 1L
                        result.category.name shouldBe "상의"
                    }
                    it("result 의 cheapestProduct 는 C 브랜드의 10000 원짜리 상품 1개를 가진 list 입니다") {
                        result.cheapestProduct shouldHaveSize 1
                        with(result.cheapestProduct.first()) {
                            brand.name shouldBe "C"
                            category.name shouldBe "상의"
                            price shouldBe BigDecimal.valueOf(10000)
                        }
                    }
                    it("result 의 mostExpensiveProduct 는 I 브랜드의 11400 원짜리 상품 1개를 가진 list 입니다") {
                        result.mostExpensiveProduct shouldHaveSize 1
                        with(result.mostExpensiveProduct.first()) {
                            brand.name shouldBe "I"
                            category.name shouldBe "상의"
                            price shouldBe BigDecimal.valueOf(11400)
                        }
                    }
                }
            }
        }
    })

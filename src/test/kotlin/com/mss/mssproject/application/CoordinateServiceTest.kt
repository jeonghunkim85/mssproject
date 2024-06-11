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
 */
@Import(CoordinateService::class)
@DisplayName("CoordinateService Test")
@DataJpaTest
class CoordinateServiceTest(
    private val readCoordinateService: CoordinateService,
) : DescribeSpec({
    describe("findCheapestAndMostExpensiveProductByCategoryName 테스트") {
        /**
         * 쿼리 결과의 적절성 여부는 ProductRepositoryTest.kt 를 통해 확인
         * "상의" 를 조회하는 경우
         *      최저가는 C 브랜드의 10000 원짜리 상품이
         *      최고가는 I 브랜드의 11400 원짜리 상품이 return 됩니다.
         */
        context("DB 에 값이 셋팅 돼있고, 쿼리 시 적절한 결과를 return 해 줍니다") {
            context("readProductUseCase.findCheapestAndMostExpensiveProductByCategoryName('상의') 가 실행되면") {
                val result = readCoordinateService.findCheapestAndMostExpensiveProductByCategoryName("상의")
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
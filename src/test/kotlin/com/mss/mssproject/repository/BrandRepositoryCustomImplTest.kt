package com.mss.mssproject.repository

import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DisplayName("BrandRepositoryCustomImpl Test")
@DataJpaTest
class BrandRepositoryCustomImplTest(
    private val brandRepository: BrandRepository,
) : DescribeSpec({

    describe("단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격인 브랜드를 쿼리") {
        context("각 카테고리별 1개씩의 최저가 상품의 합이 제일 적은 브랜드는 D 로 DB 에 값이 저장돼 있다") {
            context("쿼리가 실행되면") {
                val result = brandRepository.findCheapestBrand()
                it("결과는 D 브랜드 여야 한다") {
                    checkNotNull(result)
                    result.name shouldBe "D"
                }
            }
        }
    }
})

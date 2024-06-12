package com.mss.mssproject.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mss.mssproject.application.CoordinateService
import com.mss.mssproject.domain.Brand
import com.mss.mssproject.domain.Category
import com.mss.mssproject.domain.Coordinate
import com.mss.mssproject.domain.CoordinateWithBrand
import com.mss.mssproject.domain.Product
import com.mss.mssproject.domain.ProductsByCategory
import com.mss.mssproject.extension.contentAsTree
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.clearAllMocks
import io.mockk.every
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import java.math.BigDecimal

@AutoConfigureMockMvc
@WebMvcTest(CoordinateController::class)
@DisplayName("CoordinateService test")
class CoordinateControllerTest(
    private var mockMvc: MockMvc,
    @MockkBean
    private val coordinateService: CoordinateService,
) : DescribeSpec({

        val objectMapper = jacksonObjectMapper()

        afterContainer {
            clearAllMocks()
        }

        describe("getCheapest test") {
            context("coordinateUseCases.findCheapestCoordinate 가 호출되면 응답을 준다") {
                beforeContainer {
                    every {
                        coordinateService.findCheapestCoordinate()
                    } returns
                        Coordinate(
                            products =
                                listOf(
                                    Product(
                                        id = 1,
                                        category =
                                            Category(
                                                id = 1,
                                                name = "상의",
                                            ),
                                        brand =
                                            Brand(
                                                id = 1,
                                                name = "C",
                                            ),
                                        price = BigDecimal.valueOf(10_000),
                                    ),
                                    Product(
                                        id = 2,
                                        category =
                                            Category(
                                                id = 2,
                                                name = "아우터",
                                            ),
                                        brand =
                                            Brand(
                                                id = 2,
                                                name = "E",
                                            ),
                                        price = BigDecimal.valueOf(5_000),
                                    ),
                                ),
                        )
                }

                context("/coordinates/cheapest 가 호출되면") {
                    val response =
                        mockMvc
                            .perform(
                                get("/coordinates/cheapest")
                                    .contentType(MediaType.APPLICATION_JSON),
                            ).andReturn()
                            .response

                    it("response.status 는 200 이다") {
                        response.status shouldBe 200
                    }

                    it("결과 contents 는 아래와 같다") {
                        val expected =
                            objectMapper.readTree(
                                """
                        {
                          "최저가": {
                            "카테고리": [
                              {
                                "카테고리": "상의",
                                "브랜드": "C",
                                "가격": "10,000"
                              },
                              {
                                "카테고리": "아우터",
                                "브랜드": "E",
                                "가격": "5,000"
                              }
                            ],
                            "총액": "15,000"
                          }
                        }
                    """,
                            )
                        response.contentAsTree shouldBe expected
                    }
                }
            }
        }

        describe("/coordinates/cheapest-by-brand 테스트") {
            context("coordinateUseCases.findCoordinateByCheapestBrand 가 호출되면 응답을 준다") {
                beforeContainer {
                    val brand = Brand(id = 1, name = "D")
                    every {
                        coordinateService.findCoordinateByCheapestBrand()
                    } returns
                        CoordinateWithBrand(
                            brand = brand,
                            products =
                                listOf(
                                    Product(
                                        id = 1,
                                        brand = brand,
                                        category = Category(id = 1, name = "상의"),
                                        price = BigDecimal.valueOf(10_100),
                                    ),
                                    Product(
                                        id = 2,
                                        brand = brand,
                                        category = Category(id = 2, name = "아우터"),
                                        price = BigDecimal.valueOf(5_100),
                                    ),
                                ),
                        )
                }
                context("/coordinates/cheapest-by-brand 가 호출되면") {
                    val response =
                        mockMvc
                            .perform(
                                get("/coordinates/cheapest-by-brand")
                                    .contentType(MediaType.APPLICATION_JSON),
                            ).andReturn()
                            .response

                    it("response.status 는 200 이다") {
                        response.status shouldBe 200
                    }

                    it("결과 contents 는 아래와 같다") {
                        val expected =
                            objectMapper.readTree(
                                """
                        {
                          "최저가": {
                            "브랜드": "D",
                            "카테고리": [
                              {
                                "카테고리": "상의",
                                "가격": "10,100"
                              },
                              {
                                "카테고리": "아우터",
                                "가격": "5,100"
                              }
                            ],
                            "총액": "15,200"
                          }
                        }
                    """,
                            )
                        response.contentAsTree shouldBe expected
                    }
                }
            }

            context("coordinateService.findCoordinateByCheapestBrand 가 호출되면 IllegalStateException 이 난다") {
                beforeContainer {
                    every {
                        coordinateService.findCoordinateByCheapestBrand()
                    } throws IllegalStateException("테스트 메세지")
                }
                context("/coordinates/cheapest-by-brand 가 호출되면") {

                    val response =
                        mockMvc
                            .perform(
                                get("/coordinates/cheapest-by-brand")
                                    .contentType(MediaType.APPLICATION_JSON),
                            ).andReturn()
                            .response

                    it("response status code 는 500 이다") {
                        response.status shouldBe 500
                    }

                    it("response content 는 아래와 같다") {
                        val result: Map<String, Any> = objectMapper.readValue(response.getContentAsString(Charsets.UTF_8))
                        result["code"] shouldBe "INTERNAL_SERVER_ERROR"
                        result["message"] shouldBe "테스트 메세지"
                    }
                }
            }
        }

        describe("/coordinates/cheapest-and-most-expensive 테스트") {
            context(
                "coordinateUseCases.findCheapestAndMostExpensiveProductByCategoryName(상의) 가 호출되면 응답을 준다",
            ) {
                beforeContainer {
                    val category = Category(id = 1, name = "상의")
                    every {
                        coordinateService.findCheapestAndMostExpensiveProductByCategoryName("상의")
                    } returns
                        ProductsByCategory(
                            category = category,
                            cheapestProduct =
                                listOf(
                                    Product(
                                        id = 1, category = category, brand = Brand(id = 1, name = "C"),
                                        price = BigDecimal.valueOf(10_000),
                                    ),
                                ),
                            mostExpensiveProduct =
                                listOf(
                                    Product(
                                        id = 2, category = category, brand = Brand(id = 2, name = "I"),
                                        price = BigDecimal.valueOf(11_400),
                                    ),
                                ),
                        )
                }

                context("/coordinates/cheapest-by-brand?category_name=상의 가 호출되면") {
                    val response =
                        mockMvc
                            .perform(
                                get("/coordinates/cheapest-and-most-expensive")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .param("category_name", "상의"),
                            ).andReturn()
                            .response

                    it("response.status 는 200 이다") {
                        response.status shouldBe 200
                    }

                    it("response.content 는 아래와 같다") {
                        val expected =
                            objectMapper.readTree(
                                """
                        {
                          "카테고리": "상의",
                          "최저가": [
                            {
                              "카테고리": "상의",
                              "브랜드": "C",
                              "가격": "10,000"
                            }
                          ],
                          "최고가": [
                            {
                              "카테고리": "상의",
                              "브랜드": "I",
                              "가격": "11,400"
                            }
                          ]
                        }
                    """,
                            )
                        response.contentAsTree shouldBe expected
                    }
                }
            }

            context(
                """coordinateUseCases.findCheapestAndMostExpensiveProductByCategoryName(not_valid_category_name)
            가 호출되면 IllegalStateException 을 준다""",
            ) {
                beforeContainer {
                    every {
                        coordinateService.findCheapestAndMostExpensiveProductByCategoryName("not_valid_category_name")
                    } throws IllegalStateException("cannot find category not_valid_category_name")
                }
                context("/coordinates/cheapest-by-brand?category_name=상의 가 호출되면") {
                    val response =
                        mockMvc
                            .perform(
                                get("/coordinates/cheapest-and-most-expensive")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .param("category_name", "not_valid_category_name"),
                            ).andReturn()
                            .response

                    it("response status 는 400 이다") {
                        response.status shouldBe 400
                    }
                    it("response content 는 아래와 같다") {
                        val result: Map<String, Any> = objectMapper.readValue(response.getContentAsString(Charsets.UTF_8))
                        result["code"] shouldBe "BAD_REQUEST"
                        result["message"] shouldBe "cannot find category not_valid_category_name"
                    }
                }
            }

            context("/coordinates/cheapest-by-brand 가 category_name param 없이 호출되면") {
                val response =
                    mockMvc
                        .perform(
                            get("/coordinates/cheapest-and-most-expensive")
                                .contentType(MediaType.APPLICATION_JSON),
                        ).andReturn()
                        .response

                it("response status 는 400 이다") {
                    response.status shouldBe 400
                }
                it("response content 는 아래와 같다") {
                    val result: Map<String, Any> = objectMapper.readValue(response.getContentAsString(Charsets.UTF_8))
                    result["code"] shouldBe "BAD_REQUEST"
                    result["message"]?.toString() shouldContain "category_name must not be blank"
                }
            }
        }
    })

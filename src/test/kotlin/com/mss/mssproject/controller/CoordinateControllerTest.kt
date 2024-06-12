package com.mss.mssproject.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mss.mssproject.application.CoordinateUseCases
import com.mss.mssproject.extension.contentAsTree
import com.ninjasquad.springmockk.SpykBean
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.clearAllMocks
import io.mockk.every
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

@ComponentScan(
    basePackages = [
        "com.mss.mssproject.application",
        "com.mss.mssproject.controller",
        "com.mss.mssproject.repository",
    ],
)
@AutoConfigureDataJpa
@AutoConfigureMockMvc
@WebMvcTest(CoordinateController::class)
@DisplayName("CoordinateService test")
class CoordinateControllerTest(
    private var mockMvc: MockMvc,
    @SpykBean
    private val coordinateUseCases: CoordinateUseCases,
) : DescribeSpec({

        val objectMapper = jacksonObjectMapper()

        afterContainer {
            clearAllMocks()
        }

        describe("getCheapest test") {
            context("coordinateUseCases.findCheapestCoordinate 가 호출되면 응답을 준다") {
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
                              },
                              {
                                "카테고리": "바지",
                                "브랜드": "D",
                                "가격": "3,000"
                              },
                              {
                                "카테고리": "스니커즈",
                                "브랜드": "G",
                                "가격": "9,000"
                              },
                              {
                                "카테고리": "가방",
                                "브랜드": "A",
                                "가격": "2,000"
                              },
                              {
                                "카테고리": "모자",
                                "브랜드": "D",
                                "가격": "1,500"
                              },
                              {
                                "카테고리": "양말",
                                "브랜드": "I",
                                "가격": "1,700"
                              },
                              {
                                "카테고리": "액세서리",
                                "브랜드": "F",
                                "가격": "1,900"
                              }
                            ],
                            "총액": "34,100"
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
                              },
                              {
                                "카테고리": "바지",
                                "가격": "3,000"
                              },
                              {
                                "카테고리": "스니커즈",
                                "가격": "9,500"
                              },
                              {
                                "카테고리": "가방",
                                "가격": "2,500"
                              },
                              {
                                "카테고리": "모자",
                                "가격": "1,500"
                              },
                              {
                                "카테고리": "양말",
                                "가격": "2,400"
                              },
                              {
                                "카테고리": "액세서리",
                                "가격": "2,000"
                              }
                            ],
                            "총액": "36,100"
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
                        coordinateUseCases.findCoordinateByCheapestBrand()
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
            context("coordinateUseCases.findCheapestAndMostExpensiveProductByCategoryName(상의) 가 호출되면 응답을 준다") {
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
                        coordinateUseCases.findCheapestAndMostExpensiveProductByCategoryName("not_valid_category_name")
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

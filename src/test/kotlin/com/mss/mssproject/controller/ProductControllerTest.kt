package com.mss.mssproject.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mss.mssproject.application.ProductService
import com.mss.mssproject.application.WriteProductUseCases.WriteProductModel
import com.mss.mssproject.domain.Brand
import com.mss.mssproject.domain.Category
import com.mss.mssproject.domain.Product
import com.mss.mssproject.extension.contentAsTree
import com.mss.mssproject.extension.contentAsUTF8String
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import java.math.BigDecimal

@WebMvcTest(ProductController::class)
@AutoConfigureMockMvc
class ProductControllerTest(
    private val mockMvc: MockMvc,
    @MockkBean
    private val productService: ProductService,
) : DescribeSpec({

        val objectMapper = jacksonObjectMapper()

        afterContainer {
            clearAllMocks()
        }

        describe("상품조회 테스트") {
            describe("정상조회 케이스") {
                context("productService.readProduct(1) 가 호출되면 product 를 return 한다") {
                    every {
                        productService.readProduct(1L)
                    } returns
                        Product(
                            id = 1,
                            brand = Brand(id = 2, name = "A"),
                            category = Category(id = 3, name = "상의"),
                            price = BigDecimal.valueOf(1000),
                        )

                    context("GET /products/1 이 호출되면") {
                        val response =
                            mockMvc
                                .perform(
                                    get("/products/{id}", 1)
                                        .contentType(MediaType.APPLICATION_JSON),
                                ).andReturn()
                                .response

                        it("status 200 을 return 한다") {
                            response.status shouldBe 200
                        }
                        it("content 는 아래와 같다") {
                            val expected =
                                objectMapper.readTree(
                                    """
                                    {
                                      "id": 1,
                                      "brandId": 2,
                                      "categoryId": 3,
                                      "price": 1000
                                    }
                                    """.trimIndent(),
                                )
                            response.contentAsTree shouldBe expected
                        }
                    }
                }
            }

            describe("에러케이스") {
                context("productService.readProduct(2) 가 호출되면 NoSuchElementException 을 던진다") {
                    every {
                        productService.readProduct(any())
                    } throws NoSuchElementException("not found product")

                    context("GET /products/1 이 호출되면") {
                        val response =
                            mockMvc
                                .perform(
                                    get("/products/{id}", 1)
                                        .contentType(MediaType.APPLICATION_JSON),
                                ).andReturn()
                                .response

                        it("status 404 을 return 한다") {
                            response.status shouldBe 404
                        }
                        it("content 는 아래와 같다") {
                            val expected =
                                objectMapper.readTree(
                                    """
                                    {"code":"NOT_FOUND", "message":"not found product"}
                                    """.trimIndent(),
                                )
                            response.contentAsTree shouldBe expected
                        }
                    }
                }
            }
        }

        describe("상품 등록 테스트") {
            beforeContainer {
                every {
                    productService.postProduct(any())
                } answers {
                    val arg0 = (it.invocation.args[0] as WriteProductModel)
                    Product(
                        id = 1,
                        brand = Brand(id = arg0.brandId, name = "brand1"),
                        category = Category(id = arg0.categoryId, name = "category1"),
                        price = arg0.price,
                    )
                }
            }

            describe("입력값 정상 케이스") {
                val requestBody = """{"brandId": 1, "categoryId": 1, "price": 1000}"""
                context("POST /products 가 호출되면") {
                    val response =
                        mockMvc
                            .perform(
                                post("/products")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestBody),
                            ).andReturn()
                            .response

                    it("productService.postProduct 가 호출되고, 결과는 정상이다") {
                        response.status shouldBe 200
                        response.contentAsTree shouldBe
                            """{"id":1,"brandId":1,"categoryId":1,"price":1000}""".let(objectMapper::readTree)

                        verify {
                            productService.postProduct(
                                WriteProductModel(
                                    brandId = 1,
                                    categoryId = 1,
                                    price = BigDecimal.valueOf(1000),
                                ),
                            )
                        }
                    }
                }

                describe("IllegalStateException 에러 케이스") {
                    context("writeProductUseCase.postProduct 호출 시 IllegalStateException 이 발생한다") {
                        beforeContainer {
                            every {
                                productService.postProduct(any())
                            } throws IllegalStateException("cannot find brand")
                        }

                        context("POST /products 가 호출되면") {
                            val response =
                                mockMvc
                                    .perform(
                                        post("/products")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(requestBody),
                                    ).andReturn()
                                    .response

                            it("status 400 과 에러메세지를 출력한다") {
                                response.status shouldBe 400
                                response.contentAsTree shouldBe
                                    """{"code":"BAD_REQUEST","message":"cannot find brand"}""".let(objectMapper::readTree)
                            }
                        }
                    }
                }
            }
            describe("입력값 오류 케이스") {
                context("brandId 가 null 이다") {
                    val requestBody = """{"brandId": null, "categoryId": 1, "price": 1000}"""
                    context("POST /products 가 호출되면") {
                        val response =
                            mockMvc
                                .perform(
                                    post("/products")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBody),
                                ).andReturn()
                                .response

                        it("status 400 과 에러메세지를 출력하고 productService.postProduct 가 호출되지 않는다") {
                            response.status shouldBe 400

                            response.contentAsTree shouldBe
                                """
                            {"code":"BAD_REQUEST","messages":["brandId 는 필수입니다."]}
                        """.let(objectMapper::readTree)

                            verify(inverse = true) {
                                productService.postProduct(any())
                            }
                        }
                    }
                }
                context("brandId 가 0 이다") {
                    val requestBody = """{"brandId": 0, "categoryId": 1, "price": 1000}"""
                    context("POST /products 가 호출되면") {
                        val response =
                            mockMvc
                                .perform(
                                    post("/products")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBody),
                                ).andReturn()
                                .response

                        it("status 400 과 에러메세지를 출력하고 productService.postProduct 가 호출되지 않는다") {
                            response.status shouldBe 400

                            response.contentAsTree shouldBe
                                """
                            {"code":"BAD_REQUEST","messages":["brandId 는 양수만 입력 가능합니다."]}
                        """.let(objectMapper::readTree)

                            verify(inverse = true) {
                                productService.postProduct(any())
                            }
                        }
                    }
                }
                context("categoryId 가 null 이다") {
                    val requestBody = """{"brandId": 1, "categoryId": null, "price": 1000}"""
                    context("POST /products 가 호출되면") {
                        val response =
                            mockMvc
                                .perform(
                                    post("/products")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBody),
                                ).andReturn()
                                .response

                        it("status 400과 에러메세지를 출력하고 productService.postProduct 가 호출되지 않는다") {
                            response.status shouldBe 400

                            response.contentAsTree shouldBe
                                """
                            {"code":"BAD_REQUEST","messages":["categoryId 는 필수입니다."]}
                        """.let(objectMapper::readTree)

                            verify(inverse = true) {
                                productService.postProduct(any())
                            }
                        }
                    }
                }
                context("categoryId 가 0 이다") {
                    val requestBody = """{"brandId": 1, "categoryId": 0, "price": 1000}"""
                    context("POST /products 가 호출되면") {
                        val response =
                            mockMvc
                                .perform(
                                    post("/products")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBody),
                                ).andReturn()
                                .response

                        it("status 400과 에러메세지를 출력하고 productService.postProduct 가 호출되지 않는다") {
                            response.status shouldBe 400

                            response.contentAsTree shouldBe
                                """
                            {"code":"BAD_REQUEST","messages":["categoryId 는 양수만 입력 가능합니다."]}
                        """.let(objectMapper::readTree)

                            verify(inverse = true) {
                                productService.postProduct(any())
                            }
                        }
                    }
                }
                context("price 가 null 이다") {
                    val requestBody = """{"brandId": 1, "categoryId": 1, "price": null}"""
                    context("POST /products 가 호출되면") {
                        val response =
                            mockMvc
                                .perform(
                                    post("/products")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBody),
                                ).andReturn()
                                .response

                        it("status 400과 에러메세지를 출력하고 productService.postProduct 가 호출되지 않는다") {
                            response.status shouldBe 400

                            response.contentAsTree shouldBe
                                """
                            {"code":"BAD_REQUEST","messages":["price 는 필수입니다."]}
                        """.let(objectMapper::readTree)

                            verify(inverse = true) {
                                productService.postProduct(any())
                            }
                        }
                    }
                }
                context("price 가 음수이다 이다") {
                    val requestBody = """{"brandId": 1, "categoryId": 1, "price": -1}"""
                    context("POST /products 가 호출되면") {
                        val response =
                            mockMvc
                                .perform(
                                    post("/products")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBody),
                                ).andReturn()
                                .response

                        it("status 400과 에러메세지를 출력하고 productService.postProduct 가 호출되지 않는다") {
                            response.status shouldBe 400

                            response.contentAsTree shouldBe
                                """
                            {"code":"BAD_REQUEST","messages":["price 는 0 이상만 입력 가능합니다."]}
                        """.let(objectMapper::readTree)

                            verify(inverse = true) {
                                productService.postProduct(any())
                            }
                        }
                    }
                }
            }
        }

        describe("상품 수정 테스트") {
            // 입력값 validation 의 경우, @Valid 와 입력 모델을 등록과 동일하게 사용하므로 중복해서 검증하지 않습니다
            describe("정상 케이스") {
                beforeContainer {
                    every {
                        productService.putProduct(any(), any())
                    } answers {
                        val arg0 = (it.invocation.args[0] as Long)
                        val arg1 = (it.invocation.args[1] as WriteProductModel)
                        Product(
                            id = arg0,
                            brand = Brand(id = arg1.brandId, name = "brand1"),
                            category = Category(id = arg1.categoryId, name = "category1"),
                            price = arg1.price,
                        )
                    }
                }

                val id = 1
                val requestBody = """{"brandId": 1, "categoryId": 1, "price": 1000}"""

                context("PUT /products/1 가 호출되면") {
                    val response =
                        mockMvc
                            .perform(
                                put("/products/{id}", id)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestBody),
                            ).andReturn()
                            .response

                    it("productService.putProduct 가 호출되고 200 status 와 등록된 컨텐츠를 출력한다 ") {
                        verify {
                            productService.putProduct(1, any())
                        }

                        response.status shouldBe 200
                        response.contentAsTree shouldBe
                            """{"id":1,"brandId":1,"categoryId":1,"price":1000}""".let(objectMapper::readTree)
                    }
                }
            }
            describe("에러 케이스") {
                context("putProduct 가 호출되면 IllegalStateException 이 발생한다") {
                    beforeContainer {
                        every {
                            productService.putProduct(any(), any())
                        } throws IllegalStateException("cannot find category")
                    }
                    val id = 1
                    val requestBody = """{"brandId": 1, "categoryId": 1, "price": 1000}"""

                    context("PUT /products/1 가 호출되면") {
                        val response =
                            mockMvc
                                .perform(
                                    put("/products/{id}", id)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBody),
                                ).andReturn()
                                .response
                        it("400 status 가 return 되고 에러메세지를 출력한다") {
                            response.status shouldBe 400
                            response.contentAsTree shouldBe
                                """{"code":"BAD_REQUEST","message":"cannot find category"}""".let(objectMapper::readTree)
                        }
                    }
                }
            }
        }

        describe("상품 삭제 테스트") {
            describe("정상 케이스") {
                beforeContainer {
                    every {
                        productService.deleteProduct(1)
                    } just runs
                }
                context("delete /products/1 이 호출되면") {
                    val response =
                        mockMvc
                            .perform(
                                delete("/products/{id}", 1)
                                    .contentType(MediaType.APPLICATION_JSON),
                            ).andReturn()
                            .response

                    it("writeProductUseCase.deleteProduct 가 호출되고 200 status 를 return 하며 content 는 empty 이다") {
                        verify {
                            productService.deleteProduct(1)
                        }
                        response.status shouldBe 200
                        response.contentLength shouldBe 0
                    }
                }
            }

            describe("비정상 케이스") {
                context("productService.deleteProduct 가 호출되면 NoSuchElementException 을 던진다") {
                    beforeContainer {
                        every {
                            productService.deleteProduct(any())
                        } throws NoSuchElementException("cannot find product")
                    }
                    context("delete /products/1 이 호출되면") {
                        val response =
                            mockMvc
                                .perform(
                                    delete("/products/{id}", 1)
                                        .contentType(MediaType.APPLICATION_JSON),
                                ).andReturn()
                                .response

                        it("404 status 를 return 하며 에러 메세지를 출력한다") {
                            response.status shouldBe 404
                            response.contentAsUTF8String shouldContain "cannot find product"
                        }
                    }
                }
            }
        }
    })

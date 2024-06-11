package com.mss.mssproject.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mss.mssproject.domain.Brand
import com.mss.mssproject.repository.BrandRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@ComponentScan(
    basePackages = [
        "com.mss.mssproject.application",
        "com.mss.mssproject.controller",
        "com.mss.mssproject.repository",
    ]
)
@AutoConfigureDataJpa
@WebMvcTest(BrandController::class)
@AutoConfigureMockMvc
class BrandControllerTest(
    private var mockMvc: MockMvc,
    private val brandRepository: BrandRepository,
) : DescribeSpec({

    val objectMapper = jacksonObjectMapper()

    describe("브랜드 조회 by id api 테스트 ") {
        describe("정상 조회 case") {
            context("brand 1 인 brand 가 저장돼있다") {
                context("/brands/1 이 호출되면") {
                    val response = mockMvc.perform(
                        get("/brands/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                    ).andReturn()
                        .response

                    it("""결과 status=200 / content={"id":1,"name":"A"} 이다 """) {
                        response.status shouldBe 200
                        val resultObject: Map<String, String> = objectMapper.readValue(response.contentAsString)
                        resultObject shouldBe mapOf(
                            "id" to "1",
                            "name" to "A",
                        )
                    }
                }
            }
            context("db 에 brand 999 는 저장돼있지 않다") {
                context("/brands/999 이 호출되면") {
                    val response = mockMvc.perform(
                        get("/brands/{id}", 999)
                            .contentType(MediaType.APPLICATION_JSON)
                    ).andReturn()
                        .response

                    it("""결과 status=404 / content.message= cannot find brand(id=999) 이다""") {
                        response.status shouldBe 404

                        val resultObject: Map<String, String> = objectMapper.readValue(response.contentAsString)
                        resultObject.get("message") shouldContain "cannot find brand"
                    }
                }
            }
        }
    }

    describe("브랜드 조회 by name api 테스트") {
        describe("정상조회 case") {
            context("brand name 'A' 인 brand 가 저장돼있다") {
                context("/brands?name=A 가 호출되면") {
                    val response = mockMvc.perform(
                        get("/brands")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("name", "A")
                    ).andReturn()
                        .response

                    it("""결과 status=200 / content=[{"id":1,"name":"A"}] 이다 """) {
                        response.status shouldBe 200
                        val resultObject: List<Map<String, String>> = objectMapper.readValue(response.contentAsString)
                        resultObject shouldBe listOf(
                            mapOf(
                                "id" to "1",
                                "name" to "A",
                            )
                        )
                    }
                }
            }
            context("brand name 'Z' 인 brand 가 저장돼있지 않다") {
                context("/brands?name=Z 가 호출되면") {
                    val response = mockMvc.perform(
                        get("/brands")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("name", "Z")
                    ).andReturn()
                        .response

                    it("""결과 status=404 / content.message= cannot find brand(id=999) 이다""") {
                        response.status shouldBe 404

                        val resultObject: Map<String, String> = objectMapper.readValue(response.contentAsString)
                        resultObject["message"] shouldContain "cannot find brand"
                    }
                }
            }
            context("brand name 을 빈 string 으로 호출하면") {
                val response = mockMvc.perform(
                    get("/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("name", "")
                ).andReturn()
                    .response

                it("""결과 status=400 / content.message= requestParam 'name' should not be null or blank 이다""") {
                    response.status shouldBe 400

                    val resultObject: Map<String, String> = objectMapper.readValue(response.contentAsString)
                    resultObject["message"] shouldContain "'name' should not be null or blank"
                }
            }
        }
    }

    describe("브랜드 수정 테스트") {
        describe("정상 수정 case") {
            context("""id=1 / {"name": "AA"} 로 브랜드 등록 api 가 호출되면""") {
                val response = mockMvc.perform(
                    put("/brands/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""{"name": "AA"}""")
                ).andReturn()
                    .response
                it("결과 status=200 / content id 는 1이고 name 은 AA 어야 한다 ") {
                    response.status shouldBeIn listOf(200, 201)
                    val resultObject: Map<String, String> = objectMapper.readValue(response.contentAsString)
                    resultObject["id"] shouldNotBe null
                    resultObject["id"]?.toLong() shouldBe 1L
                    resultObject["name"] shouldBe "AA"
                }
            }
        }

        describe("비정상 case") {
            context("""id=1 / {"name": null} 로 브랜드 수정 api 가 호출되면""") {
                val response = mockMvc.perform(
                    put("/brands/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""{"name": null}""")
                ).andReturn()
                    .response

                it("결과 status=400 / content") {
                    response.status shouldBe 400
                    response.contentAsString shouldContain "name must not be blank"
                }
            }
            context("""{"name": ""} 로 브랜드 수정 api 가 호출되면""") {
                val response = mockMvc.perform(
                    put("/brands/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""{"name": ""}""")
                ).andReturn()
                    .response

                it("결과 status=400 / content") {
                    response.status shouldBe 400
                    response.contentAsString shouldContain "name must not be blank"
                }
            }
        }
    }

    describe("브랜드 등록 테스트") {
        describe("정상 등록 case") {
            context("""{"name": "Y"} 로 브랜드 등록 api 가 호출되면""") {
                val response = mockMvc.perform(
                    post("/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""{"name": "Y"}""")
                ).andReturn()
                    .response
                it("결과 status=200 / content id 는 0보다 크고 name 은 Y 어야 한다 ") {
                    response.status shouldBeIn listOf(200, 201)
                    val resultObject: Map<String, String> = objectMapper.readValue(response.contentAsString)
                    resultObject["id"] shouldNotBe null
                    resultObject["id"]?.toLong()!! shouldBeGreaterThan 0L
                    resultObject["name"] shouldBe "Y"
                }
            }
        }

        describe("비정상 case") {
            context("""{"name": null} 로 브랜드 등록 api 가 호출되면""") {
                val response = mockMvc.perform(
                    post("/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""{"name": null}""")
                ).andReturn()
                    .response

                it("결과 status=400 / content") {
                    response.status shouldBe 400
                    response.contentAsString shouldContain "name must not be blank"
                }
            }
            context("""{"name": ""} 로 브랜드 등록 api 가 호출되면""") {
                val response = mockMvc.perform(
                    post("/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""{"name": ""}""")
                ).andReturn()
                    .response

                it("결과 status=400 / content") {
                    response.status shouldBe 400
                    response.contentAsString shouldContain "name must not be blank"
                }
            }
        }
    }

    describe("브랜드 삭제 테스트") {
        describe("정상 case") {
            context("DB 에 상품을 갖고있지 않은 brand 가 있다") {
                val savedBrand = brandRepository.save(Brand(name="brand with no products"))

                context("brand 삭제가 호출되면") {
                    val response = mockMvc.perform(
                        delete("/brands/{id}", savedBrand.id)
                    ).andReturn().response

                    it("결과 status=200") {
                        response.status shouldBe 200
                    }
                    it("삭제된 id 로 조회 시 404") {
                        mockMvc.perform(
                            get("/brands/{id}", savedBrand.id)
                        ).andExpect(status().isNotFound)
                    }
                }
            }
        }

        describe("비정상 case") {
            context("brand 1은 DB 에 상품을 갖고 있다") {
                context("brand 삭제가 호출되면") {
                    val response = mockMvc.perform(
                        delete("/brands/{id}", 1)
                    ).andReturn().response

                    it("결과 status=409") {
                        response.status shouldBe 409
                        response.contentAsString shouldContain "products persisted. cannot delete."
                    }
                }
            }

            context("brand 100 은 저장돼있지 않다") {
                context("brand 삭제가 호출되면") {
                    val response = mockMvc.perform(
                        delete("/brands/{id}", 100)
                    ).andReturn().response

                    it("결과 status=404") {
                        response.status shouldBe 404
                        response.contentAsString shouldContain "cannot find brand"
                    }
                }
            }
        }
    }
})

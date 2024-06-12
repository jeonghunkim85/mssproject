package com.mss.mssproject.extension

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.mock.web.MockHttpServletResponse

private val objectMapper = jacksonObjectMapper()

val MockHttpServletResponse.contentAsUTF8String: String get() = this.getContentAsString(Charsets.UTF_8)

val MockHttpServletResponse.contentAsTree: JsonNode get() = objectMapper.readTree(this.contentAsUTF8String)
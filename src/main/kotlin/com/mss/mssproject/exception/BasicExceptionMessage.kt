package com.mss.mssproject.exception

import org.springframework.http.HttpStatusCode

open class BasicExceptionMessage(
    open val code: HttpStatusCode,
    open val message: String?
)

class ValidationExceptionMessage(
    open val code: HttpStatusCode,
    open val messages: List<String>,
)
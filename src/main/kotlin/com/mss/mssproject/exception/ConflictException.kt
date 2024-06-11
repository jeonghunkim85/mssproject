package com.mss.mssproject.exception

import org.springframework.http.HttpStatus

class ConflictException (
    override val message: String? = null
): RuntimeException(message) {
    constructor(exception: Exception): this(exception.message)

    fun toBasicExceptionMessage() = BasicExceptionMessage(
        code = HttpStatus.CONFLICT,
        message = message,
    )
}
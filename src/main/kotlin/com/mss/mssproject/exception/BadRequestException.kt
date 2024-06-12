package com.mss.mssproject.exception

import org.springframework.http.HttpStatus

class BadRequestException(
    override val message: String? = null,
) : RuntimeException(message) {
    constructor(exception: Exception) : this(exception.message)

    fun toBasicExceptionMessage() =
        BasicExceptionMessage(
            code = HttpStatus.BAD_REQUEST,
            message = message,
        )
}

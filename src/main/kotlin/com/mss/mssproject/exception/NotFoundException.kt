package com.mss.mssproject.exception

import org.springframework.http.HttpStatus

class NotFoundException(
    override val message: String? = null,
) : RuntimeException(
        message,
    ) {
    constructor(exception: Exception) : this(exception.message)

    fun toExceptionMessage() =
        BasicExceptionMessage(
            code = HttpStatus.NOT_FOUND,
            message = message,
        )
}

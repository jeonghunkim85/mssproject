package com.mss.mssproject.config

import com.mss.mssproject.exception.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionConfig {

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    fun notFoundExceptionHandler(error: NotFoundException): BasicExceptionMessage = error.toExceptionMessage()

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    fun badRequestExceptionHandler(error: BadRequestException): BasicExceptionMessage = error.toBasicExceptionMessage()

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    fun methodArgumentNotValidException(error: MethodArgumentNotValidException): ValidationExceptionMessage {
        val message = error.bindingResult.allErrors.mapNotNull { it.defaultMessage }
        return ValidationExceptionMessage(
            code = HttpStatus.BAD_REQUEST,
            messages = message,
        )
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    fun conflictExceptionHandler(error: ConflictException): BasicExceptionMessage = error.toBasicExceptionMessage()
}

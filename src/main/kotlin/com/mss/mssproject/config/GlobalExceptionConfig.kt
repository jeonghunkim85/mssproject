package com.mss.mssproject.config

import com.mss.mssproject.exception.BadRequestException
import com.mss.mssproject.exception.BasicExceptionMessage
import com.mss.mssproject.exception.ConflictException
import com.mss.mssproject.exception.NotFoundException
import com.mss.mssproject.exception.ValidationExceptionMessage
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

    /**
     * 대부분 controller 에서 변환해서 내리나,
     * catch 를 누락한 경우 아래와 같은 형태로 변환해서 내리도록 handling 합니다
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @ExceptionHandler
    fun rootErrorHandler(error: Exception): BasicExceptionMessage =
        BasicExceptionMessage(
            code = HttpStatus.INTERNAL_SERVER_ERROR,
            message = error.message,
        )
}

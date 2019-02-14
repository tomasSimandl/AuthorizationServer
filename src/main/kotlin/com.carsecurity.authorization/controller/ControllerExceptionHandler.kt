package com.carsecurity.authorization.controller

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class ControllerExceptionHandler {

    private val logger = LoggerFactory.getLogger(javaClass)

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception) {
        logger.error("Controller throw exception. Message: " + e.message)
        logger.debug("Exception:", e)
    }
}
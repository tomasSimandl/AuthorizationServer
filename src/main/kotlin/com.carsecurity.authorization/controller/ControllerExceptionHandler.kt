package com.carsecurity.authorization.controller

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * This class is used for catching all exceptions from controllers
 */
@ControllerAdvice
class ControllerExceptionHandler {

    /** Logger of this class */
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Method catch all exceptions of all controllers and log them to logger.
     * @param e is exception which was thrown by controller.
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception) {
        logger.error("Controller throw exception. Message: " + e.message)
        logger.debug("Exception:", e)
    }
}
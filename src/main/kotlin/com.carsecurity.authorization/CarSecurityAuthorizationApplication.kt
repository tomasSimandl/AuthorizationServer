package com.carsecurity.authorization

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity

/**
 * Class which start up whole application.
 */
@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true)
class CarSecurityAuthorizationApplication

/**
 * Method starts application. Application entry point.
 * @param args additional spring arguments.
 */
fun main(args: Array<String>) {
    runApplication<CarSecurityAuthorizationApplication>(*args)
}
package com.carsecurity.authorization.controller

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.provider.NoSuchClientException
import org.springframework.security.oauth2.provider.client.BaseClientDetails
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService
import org.springframework.web.bind.annotation.*
import javax.sql.DataSource
import javax.servlet.http.HttpServletResponse


@RestController
class ClientRegistrationController(
        private val dataSource: DataSource
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val service = JdbcClientDetailsService(dataSource)


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception) {
        logger.error("Controller throw exception", e)
    }


    @PostMapping("/client")
    fun index(@RequestBody clientDetails: BaseClientDetails, response: HttpServletResponse) {
        try {
            service.loadClientByClientId(clientDetails.clientId)
            response.status = HttpServletResponse.SC_CONFLICT

            logger.warn("Client with id ${clientDetails.clientId} already exists.")

        } catch (e: NoSuchClientException) {
            service.addClientDetails(clientDetails)
            response.status = HttpServletResponse.SC_CREATED

            logger.debug("Client with id ${clientDetails.clientId} created.")
        }
    }

//    @Transactional
//    fun onApplicationEvent() {
//        if (alreadySetup) return
//        alreadySetup = true
//
//        createClientIfNotExist(
//                client = "my-client",
//                secret = "secret",
//                scopes = hashSetOf("read", "write"),
//                authorizedGrantedType = hashSetOf("password", "refresh_token"),
//                resourceIds = hashSetOf(ResourceServerConfig.MILKYWAY_RESOURCE_ID),
//                authorities = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_CLIENT, ROLE_TRUSTED_CLIENT").toSet(),
//                accessTokenValiditySeconds = 60 * 24 * 7,
//                refreshTokenValiditySeconds = 60 * 24 * 30
//        )
//    }
}
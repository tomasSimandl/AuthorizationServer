package com.carsecurity.authorization.controller

import com.carsecurity.authorization.domain.dto.ClientDetailsDTO
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.provider.NoSuchClientException
import org.springframework.security.oauth2.provider.client.BaseClientDetails
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService
import org.springframework.web.bind.annotation.*
import javax.sql.DataSource


@RestController
@RequestMapping("client")
class ClientController(
        dataSource: DataSource,
        passwordEncoder: PasswordEncoder

) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val clientService = JdbcClientDetailsService(dataSource)

    init {
        clientService.setPasswordEncoder(passwordEncoder)
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    fun getClients(): List<ClientDetailsDTO> = clientService.listClientDetails().map { clientDetail -> ClientDetailsDTO(clientDetail) }


    @PostMapping
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    fun createClient(@RequestBody clientCreate: ClientDetailsDTO): ResponseEntity<ClientDetailsDTO> {

        try {
            clientService.loadClientByClientId(clientCreate.clientId)

            logger.warn("Client with id ${clientCreate.clientId} already exists.")
            return ResponseEntity(HttpStatus.CONFLICT)

        } catch (e: NoSuchClientException) {

            val clientDetails = BaseClientDetails()
            clientDetails.clientId = clientCreate.clientId
            clientDetails.clientSecret = clientCreate.clientSecret
            clientDetails.setScope(clientCreate.scope)
            clientDetails.setResourceIds(clientCreate.resourceIds)
            clientDetails.setAuthorizedGrantTypes(clientCreate.authorizedGrantTypes)
            clientDetails.registeredRedirectUri = clientCreate.webServerRedirectUri
            clientDetails.authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(clientCreate.authorities.joinToString(","))
            clientDetails.accessTokenValiditySeconds = clientCreate.accessTokenValidity
            clientDetails.refreshTokenValiditySeconds = clientCreate.refreshTokenValidity
            clientDetails.additionalInformation = clientCreate.additionalInformation


            clientService.addClientDetails(clientDetails)
            logger.debug("Client with id ${clientDetails.clientId} created.")

            return ResponseEntity(ClientDetailsDTO(clientDetails), HttpStatus.CREATED)
        }
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    fun deleteClient(@RequestParam(name = "client_id") clientId: String) {
        clientService.removeClientDetails(clientId)

        // TODO (after delete user logout user (remove token))
    }
}
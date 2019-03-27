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

/**
 * Controller for client managing. Only user with SUPER_ADMIN role have access to this controller.
 *
 * @param dataSource is source where will be all auth data.
 * @param passwordEncoder Encoder for password hashing.
 */
@RestController
@RequestMapping("client")
class ClientController(
        dataSource: DataSource,
        passwordEncoder: PasswordEncoder

) {
    /** Logger for this class. */
    private val logger = LoggerFactory.getLogger(javaClass)
    /** Service for accessing clients data. */
    private val clientService = JdbcClientDetailsService(dataSource)

    /**
     * Initialization of clientService. Correct passwordEncoder is set to it.
     */
    init {
        clientService.setPasswordEncoder(passwordEncoder)
    }

    /**
     * Method return list of cf found clients.
     * @return list of [ClientDetailsDTO] which was found in database.
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    fun getClients(): List<ClientDetailsDTO> = clientService.listClientDetails().map { clientDetail -> ClientDetailsDTO(clientDetail) }


    /**
     * Method creates input client in database and return created client.
     * @param clientCreate is client which will be created in database.
     * @return client which was created in database.
     */
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

    /**
     * Remove client form database.
     *
     * @param clientId is identification of client which will be deleted in database.
     */
    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    fun deleteClient(@RequestParam(name = "client_id") clientId: String) {
        clientService.removeClientDetails(clientId)

        // TODO (after delete user logout user (remove token))
    }
}
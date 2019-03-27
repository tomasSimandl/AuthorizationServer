package com.carsecurity.authorization.domain.dto

import org.springframework.security.oauth2.provider.ClientDetails

/**
 * Data class used for transfer client data over https requests.
 */
data class ClientDetailsDTO(

        /** Identification of client */
        val clientId: String = "",
        /** Resource ids which can be requested by client. */
        val resourceIds: Set<String> = HashSet(),
        /** Secret of client */
        val clientSecret: String = "",
        /** Scope which is requested by client */
        val scope: Set<String> = HashSet(),
        /** Grant types which are supported by this client */
        val authorizedGrantTypes: Set<String> = HashSet(),
        /** Redirect url to this client. */
        val webServerRedirectUri: Set<String> = HashSet(),
        /** List of clients authorities */
        val authorities: Collection<String> = HashSet(),
        /** Seconds how long will be token available. */
        val accessTokenValidity: Int = 0,
        /** Seconds how long will be refresh token available. */
        val refreshTokenValidity: Int = 0,
        /** Additional informations about client. */
        val additionalInformation: Map<String, Any> = HashMap()
) {
    /**
     * Constructor which can initialize this class with [ClientDetails] object.
     *
     * @param clientDetails is client of which will be initialized this class.
     */
    constructor(clientDetails: ClientDetails) : this(
            clientId = clientDetails.clientId,
            resourceIds = clientDetails.resourceIds ?: HashSet(),
            clientSecret = clientDetails.clientSecret,
            scope = clientDetails.scope ?: HashSet(),
            authorizedGrantTypes = clientDetails.authorizedGrantTypes ?: HashSet(),
            webServerRedirectUri = clientDetails.registeredRedirectUri ?: HashSet(),
            authorities = clientDetails.authorities.map { authority -> authority.authority },
            accessTokenValidity = clientDetails.accessTokenValiditySeconds ?: 0,
            refreshTokenValidity = clientDetails.refreshTokenValiditySeconds ?: 0,
            additionalInformation = clientDetails.additionalInformation ?: HashMap()
    )
}
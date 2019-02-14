package com.carsecurity.authorization.domain.dto

import org.springframework.security.oauth2.provider.ClientDetails

data class ClientDetailsDTO (

        val clientId: String = "",
        val resourceIds: Set<String> = HashSet(),
        val clientSecret: String = "",
        val scope: Set<String> = HashSet(),
        val authorizedGrantTypes: Set<String> = HashSet(),
        val webServerRedirectUri: Set<String> = HashSet(),
        val authorities: Collection<String> = HashSet(),
        val accessTokenValidity: Int = 0,
        val refreshTokenValidity: Int = 0,
        val additionalInformation: Map<String, Any> = HashMap()
) {
    constructor(clientDetails: ClientDetails): this(
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
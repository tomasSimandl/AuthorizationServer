package com.carsecurity.authorization.domain.dto

import javax.persistence.Column

data class ClientDetailsCreate (

        @Column(name="client_id")
        val clientId: String,

        @Column(name = "resource_ids")
        val resourceIds: String,

        val clientSecret: String,
        val scope: String,
        val authorizedGrantTypes: String,
        val webServerRedirectUri: String,
        val authorities: String,
        val accessTokenValidity: Long,
        val refreshTokenValidity: Long,
        val additionalInformation: String,
        val autoapprove: String
)
package com.carsecurity.authorization.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.TokenStore

@Configuration
@EnableResourceServer
class ResourceServerConfig (

        @Value("\${oauth2.resource-id}")
        private val resourceId: String,

        private val tokenStore: TokenStore

) : ResourceServerConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http
                .authorizeRequests()
                .anyRequest()
                .authenticated()
    }

    override fun configure(resources: ResourceServerSecurityConfigurer) {
        resources
                .tokenStore(tokenStore)
                .resourceId(resourceId)
    }
}
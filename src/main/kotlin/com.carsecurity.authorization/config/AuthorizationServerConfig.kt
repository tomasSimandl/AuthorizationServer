package com.carsecurity.authorization.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices
import org.springframework.security.oauth2.provider.token.TokenStore
import javax.sql.DataSource

@Configuration
@EnableAuthorizationServer
class AuthorizationServerConfig(

        private val dataSource: DataSource,

        private val passwordEncoder: PasswordEncoder,

        private val authorizationCodeServices: AuthorizationCodeServices,

        @Qualifier("authenticationManagerBean")
        private val authenticationManager: AuthenticationManager,

        private val tokenStore: TokenStore,

        @Qualifier("userDetailsServiceImpl")
        private val userDetailsService: UserDetailsService

) : AuthorizationServerConfigurerAdapter() {

    override fun configure(clients: ClientDetailsServiceConfigurer) {
        clients
                .jdbc(dataSource)
                .passwordEncoder(passwordEncoder)
    }

    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        endpoints
                .authorizationCodeServices(authorizationCodeServices)
                .authenticationManager(authenticationManager)
                .tokenStore(tokenStore)
                .userDetailsService(userDetailsService)
    }

    override fun configure(security: AuthorizationServerSecurityConfigurer) {
        security
                .checkTokenAccess("isAuthenticated()")
    }
}
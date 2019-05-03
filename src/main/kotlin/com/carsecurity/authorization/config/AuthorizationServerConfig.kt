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

/**
 * Configuration of authorization server.
 *
 * @param dataSource is source where will be all auth data.
 * @param authenticationManager Processes an Authentication request.
 * @param authorizationCodeServices Services for issuing and storing authorization codes.
 * @param passwordEncoder encoder for password hashing.
 * @param tokenStore store for storing tokens.
 * @param userDetailsService Core interface which loads user-specific data.
 */
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

    /**
     * Description copied from interface: AuthorizationServerConfigurer
     * Configure the ClientDetailsService, e.g. declaring individual clients and their properties. Note that password
     * grant is not enabled (even if some clients are allowed it) unless an AuthenticationManager is supplied to the
     * AuthorizationServerConfigurer.configure(AuthorizationServerEndpointsConfigurer). At least one client, or a
     * fully formed custom ClientDetailsService must be declared or the server will not start.
     *
     * @param clients the client details configurer
     */
    override fun configure(clients: ClientDetailsServiceConfigurer) {
        clients
                .jdbc(dataSource)
                .passwordEncoder(passwordEncoder)
    }

    /**
     * Description copied from interface: AuthorizationServerConfigurer
     * Configure the non-security features of the Authorization Server endpoints, like token store, token
     * customizations, user approvals and grant types. You shouldn't need to do anything by default, unless you
     * need password grants, in which case you need to provide an AuthenticationManager.
     *
     * @param endpoints the endpoints configurer
     */
    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        endpoints
                .authorizationCodeServices(authorizationCodeServices)
                .authenticationManager(authenticationManager)
                .tokenStore(tokenStore)
                .userDetailsService(userDetailsService)
    }

    /**
     * Description copied from interface: AuthorizationServerConfigurer
     * Configure the security of the Authorization Server, which means in practical terms the /oauth/token endpoint.
     * The /oauth/authorize endpoint also needs to be secure, but that is a normal user-facing endpoint and should be
     * secured the same way as the rest of your UI, so is not covered here. The default settings cover the most common
     * requirements, following recommendations from the OAuth2 spec, so you don't need to do anything here to get a
     * basic server up and running.
     *
     * @param security a fluent configurer for security features
     */
    override fun configure(security: AuthorizationServerSecurityConfigurer) {
        security
                .checkTokenAccess("isAuthenticated()")
    }
}
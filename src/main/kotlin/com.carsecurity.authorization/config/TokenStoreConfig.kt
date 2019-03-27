package com.carsecurity.authorization.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore
import javax.sql.DataSource

/**
 * Configuration of JDBC store.
 *
 * @param dataSource is source where will be all auth data.
 */
@Configuration
class TokenStoreConfig(
        private val dataSource: DataSource
) {
    /**
     * Store for storing tokens in JDBC store.
     */
    @Bean
    fun tokenStore(): TokenStore = JdbcTokenStore(dataSource)

    /**
     * Services for issuing and storing authorization codes in JDBC store.
     */
    @Bean
    fun authorizationCodeServices(): AuthorizationCodeServices {
        return JdbcAuthorizationCodeServices(dataSource)
    }
}
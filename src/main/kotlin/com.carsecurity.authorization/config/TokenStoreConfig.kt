package com.carsecurity.authorization.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore
import javax.sql.DataSource

@Configuration
class TokenStoreConfig(
        private val dataSource: DataSource
) {
    @Bean
    fun tokenStore(): TokenStore = JdbcTokenStore(dataSource)

    @Bean
    fun authorizationCodeServices(): AuthorizationCodeServices {
        return JdbcAuthorizationCodeServices(dataSource)
    }
}
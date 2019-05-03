package com.carsecurity.authorization.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.TokenStore

/**
 * Configuration of resource server which is used for accessing additional users informations.
 *
 * @param resourceId server resource id which client requires for access this resources.
 * @param tokenStore store for storing tokens.
 */
@Configuration
@EnableResourceServer
class ResourceServerConfig(

        @Value("\${oauth2.resource-id}")
        private val resourceId: String,

        private val tokenStore: TokenStore

) : ResourceServerConfigurerAdapter() {

    /**
     * Description copied from interface: ResourceServerConfigurer
     * Use this to configure the access rules for secure resources. By default all resources not in "/oauth/\**" are
     * protected (but no specific rules about scopes are given, for instance). You also get an
     * OAuth2WebSecurityExpressionHandler by default.
     */
    override fun configure(http: HttpSecurity) {
        http
                .authorizeRequests()
                .anyRequest()
                .authenticated()
    }

    /**
     * Description copied from interface: ResourceServerConfigurer
     * Add resource-server specific properties (like a resource id). The defaults should work for many applications,
     * but you might want to change at least the resource id.
     *
     * @param resources for the resource server
     */
    override fun configure(resources: ResourceServerSecurityConfigurer) {
        resources
                .tokenStore(tokenStore)
                .resourceId(resourceId)
    }
}
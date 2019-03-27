package com.carsecurity.authorization.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * Configuration of web security.
 *
 * @param userDetailsService Core interface which loads user-specific data.
 */
@Configuration
@EnableWebSecurity
class WebSecurityConfiguration(
        @Qualifier("userDetailsServiceImpl")
        private val userDetailsService: UserDetailsService

) : WebSecurityConfigurerAdapter() {

    /**
     * Encoder for password hashing.
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    /**
     * Description copied from interface: WebSecurityConfigurerAdapter
     * Used by the default implementation of authenticationManager() to attempt to obtain an AuthenticationManager.
     * If overridden, the AuthenticationManagerBuilder should be used to specify the AuthenticationManager.
     *
     * @param auth the AuthenticationManagerBuilder to use
     */
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService)
    }

    /**
     * Configuration of security to required authentication on every request.
     *
     * @param http is security which will be configured.
     */
    override fun configure(http: HttpSecurity) {
        http
                .authorizeRequests()
                .anyRequest()
                .authenticated()
    }

    /**
     * Processes an Authentication request.
     */
    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

}
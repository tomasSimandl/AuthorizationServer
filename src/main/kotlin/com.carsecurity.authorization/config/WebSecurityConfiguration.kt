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
import javax.sql.DataSource

@Configuration
@EnableWebSecurity
class WebSecurityConfiguration(
        @Qualifier("userDetailsServiceImpl")
        private val userDetailsService: UserDetailsService,

        private val dataSource: DataSource

) : WebSecurityConfigurerAdapter() {

    @Bean
    fun passwordEncoder(): PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService)
    }

    override fun configure(http: HttpSecurity) {
        http
//                .cors().and()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

//    @Bean
//    fun oauth2TokenFilterRegistrationBean(): FilterRegistrationBean<OAuth2TokenFilter> {
//        val registry = FilterRegistrationBean<OAuth2TokenFilter>()
//        registry.filter = OAuth2TokenFilter()
//        registry.order = Ordered.HIGHEST_PRECEDENCE
//        registry.urlPatterns = listOf("/oauth/token")
//        return registry
//    }
}
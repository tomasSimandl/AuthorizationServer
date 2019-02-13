package com.carsecurity.authorization.service

import com.carsecurity.authorization.domain.Role
import com.carsecurity.authorization.domain.User
import com.carsecurity.authorization.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserDetailsServiceImpl(
        private val userRepository: UserRepository

) : UserDetailsService {

    private val logger = LoggerFactory.getLogger(UserDetailsServiceImpl::class.java)

    @Transactional
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
        if (!user.isPresent)
            throw UsernameNotFoundException("Username $username is not found")

        return user.get().copy(authorities = getGrantedAuthorities(user.get().roles))
    }

    private fun getGrantedAuthorities(roles: Collection<Role>): MutableCollection<out GrantedAuthority> {

        val authorities = ArrayList<GrantedAuthority>()
        roles.mapTo(authorities) { role ->
            logger.info("Granted permission is ${role.name}")
            SimpleGrantedAuthority(role.name)
        }

        return authorities
    }

    fun isOwner(userDetails: Any, userId: Long): Boolean{
        if(userDetails is User) {
            return userDetails.id == userId
        }

        return false
    }
}
package com.carsecurity.authorization.service.impl

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

/**
 * Implementation of service which specified operations which can be used with actual logged user.
 *
 * @param userRepository is role repository used for access user table in database.
 */
@Service
class UserDetailsServiceImpl(
        private val userRepository: UserRepository

) : UserDetailsService {

    /** Logger of this class */
    private val logger = LoggerFactory.getLogger(UserDetailsServiceImpl::class.java)

    /**
     * Method load user from database. When user is not found method throw [UsernameNotFoundException].
     * Founded user is extended about grand authorities.
     *
     * @param username of seach user.
     * @return user details which represents user.
     */
    @Transactional
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
        if (!user.isPresent)
            throw UsernameNotFoundException("Username $username is not found")

        return user.get().copy(authorities = getGrantedAuthorities(user.get().roles))
    }

    /**
     * Method map input roles to authorities which will return.
     *
     * @param roles of which will be created authorities.
     * @return created authorities.
     */
    private fun getGrantedAuthorities(roles: Collection<Role>): MutableCollection<out GrantedAuthority> {

        val authorities = ArrayList<GrantedAuthority>()
        roles.mapTo(authorities) { role ->
            logger.info("Granted permission is ${role.name}")
            SimpleGrantedAuthority(role.name)
        }

        return authorities
    }

    /**
     * Method check if is [userDetails] class [User] and if its and id is equals to [userId] than return true.
     *
     * @param userDetails is tested user.
     * @param userId required user id.
     * @return true if [userDetails] have [userId]
     */
    fun isOwner(userDetails: Any, userId: Long): Boolean {
        if (userDetails is User) {
            return userDetails.id == userId
        }
        return false
    }

    /**
     * Method check if is [userDetails] class [User] and if its username is equals to [username].
     *
     * @param userDetails is tested user.
     * @param username is required users name.
     * @return true if [userDetails] have [username]
     */
    fun isOwner(userDetails: Any, username: String): Boolean {
        if (userDetails is User) {
            return userDetails.username == username
        }

        return false
    }
}
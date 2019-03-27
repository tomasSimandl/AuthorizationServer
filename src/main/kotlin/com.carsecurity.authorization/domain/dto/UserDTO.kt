package com.carsecurity.authorization.domain.dto

import com.carsecurity.authorization.domain.User

/**
 * Data class used for transfer data about users over https requests.
 */
data class UserDTO(

        /** Identification number of user. */
        var id: Long = 0,
        /** Unique username. */
        var username: String = "",
        /** Users password. */
        val password: String = "",
        /** Users e-mail. */
        val email: String = "",
        /** List of users roles. */
        val roles: Set<String> = HashSet(),
        /** Identification if user is expired. */
        val nonExpired: Boolean = true,
        /** Identification if user is locked. */
        val nonLocked: Boolean = true,
        /** Identification if user is enabled. */
        val enabled: Boolean = true,
        /** Identification if user has expired credentials. */
        val credentialsNonExpired: Boolean = true
) {
    /**
     * Constructor which initialize this class with [user] object.
     *
     * @param user of which will be initialize this class.
     */
    constructor(user: User) : this(
            user.id,
            user.username,
            user.password,
            user.email,
            user.roles.map { role -> role.name }.toHashSet(),
            user.isAccountNonExpired,
            user.isAccountNonLocked,
            user.isEnabled,
            user.isCredentialsNonExpired)
}
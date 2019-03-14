package com.carsecurity.authorization.domain.dto

import com.carsecurity.authorization.domain.User

data class UserDTO(

        var id: Long = 0,
        var username: String = "",
        val password: String = "",
        val email: String = "",
        val roles: Set<String> = HashSet(),
        val nonExpired: Boolean = true,
        val nonLocked: Boolean = true,
        val enabled: Boolean = true,
        val credentialsNonExpired: Boolean = true
) {
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
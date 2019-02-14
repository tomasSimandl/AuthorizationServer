package com.carsecurity.authorization.domain.dto

import com.carsecurity.authorization.domain.Role

data class RoleDTO(
        var id: Long = 0,
        val name: String = "",
        val users: Set<String> = HashSet()

) {
    constructor(role: Role): this(
            role.id,
            role.name,
            role.users.map { user -> user.username }.toHashSet()
    )
}